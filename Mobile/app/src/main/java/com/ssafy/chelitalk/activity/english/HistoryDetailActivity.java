package com.ssafy.chelitalk.activity.english;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.ssafy.chelitalk.R;
import com.ssafy.chelitalk.activity.common.MainActivity;
import com.ssafy.chelitalk.activity.common.NetworkClient;
import com.ssafy.chelitalk.api.historydetail.HistoryDetailRequestDto;
import com.ssafy.chelitalk.api.historydetail.HistoryDetailResponseDto;
import com.ssafy.chelitalk.api.historydetail.HistoryDetailService;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class HistoryDetailActivity extends AppCompatActivity {

    private TextView createdAtTextView;
    private static Retrofit retrofit;
    private static HistoryDetailService api;
    private HistoryDetailRequestDto requestDto;
    private TextView detailTextView;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_history_detail);

        createdAtTextView = findViewById(R.id.createdAtTextView);
        detailTextView = findViewById(R.id.historyDetailView);

        retrofit = NetworkClient.getRetrofitClient(HistoryDetailActivity.this);
        if(retrofit == null){
            throw new IllegalStateException("레트로핏 초기화 상태 안됨");
        }
        api = retrofit.create(HistoryDetailService.class);


        //날짜 넘겨 받기
        Intent intent = getIntent();
        LocalDateTime createdAtOtherForm = null; //api 연결시 필요
        if (intent != null && intent.hasExtra("history-createdAt")) {
            long createdAt = intent.getLongExtra("history-createdAt", 0);
            if (createdAt != 0) {
                String createdAtString = convertTimestampToString(createdAt);
                createdAtOtherForm = convertTimestampToStringWithOtherFormat(createdAt);
                createdAtTextView.setText(createdAtString);
            } else {
                Log.e("HistoryDetailActivity", "0 넘겨받음");
            }
        } else {
            Log.e("HistoryDetailActivity", "extra 존재 하지 않음");
        }

        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();
        String email = currentUser != null ? currentUser.getEmail() : null;

        if(email != null && createdAtOtherForm != null){
            requestDto = new HistoryDetailRequestDto(email,createdAtOtherForm);
            Call<List<HistoryDetailResponseDto>> call = api.historyDetail(requestDto.getMemberEmail(), requestDto.getCreatedAt());
            call.enqueue(new Callback<List<HistoryDetailResponseDto>>() {
                @Override
                public void onResponse(Call<List<HistoryDetailResponseDto>> call, Response<List<HistoryDetailResponseDto>> response) {
                    System.out.println("response:"+response);
                    if(response.isSuccessful() && response.body()!=null){
                        StringBuilder details = new StringBuilder();
                        for(HistoryDetailResponseDto item : response.body()){
                            String sender = item.getSentenceSender();
                            String sentence = item.getSentenceContent();
                            details.append("발신자 :").append(sender).append("\n")
                                    .append("메시지 :").append(sentence).append("\n\n");
                        }

                        detailTextView.setText(details.toString());
                    }else{
                        try{
                            String errorResponse = response.errorBody().string();
                            Log.e("HistoryDetailActivity", "에러 : " + errorResponse);
                            detailTextView.setText("에러 발생: " + errorResponse);
                        }catch (Exception e){
                            Log.e("HistoryDetailActivity", "Error reading error body", e);
                            detailTextView.setText("응답 처리 중 오류 발생");
                        }
                    }
                }

                @Override
                public void onFailure(Call<List<HistoryDetailResponseDto>> call, Throwable t) {
                    Log.e("HistoryDetailActivity", "서버 연결 실패"+t);
                            
                }
            });

        }else{
            Toast.makeText(this, "사용자 로그인 오류 혹은 날짜 오류", Toast.LENGTH_SHORT).show();
            Log.e("HistoryDetailActivity", "사용자 로그인 오류 혹은 날짜 오류");
        }

        //메뉴 dialog(modal)
        final ImageButton button1 = (ImageButton) findViewById(R.id.imageButton);
        button1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                AlertDialog.Builder dlg = new AlertDialog.Builder(HistoryDetailActivity.this);
                dlg.setTitle("Menu");
                ListAdapter adapter = new ArrayAdapter<String>(
                        HistoryDetailActivity.this, R.layout.dialog_item, R.id.text, new String[]{"HOME", "STUDY", "LIKE", "HISTORY"}) {
                    @NonNull
                    @Override
                    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                        View view = super.getView(position, convertView, parent);
                        ImageView img = view.findViewById(R.id.icon);
                        switch (position) {
                            case 0:
                                img.setImageResource(R.drawable.home_icon);
                                break;
                            case 1:
                                img.setImageResource(R.drawable.study_icon);
                                break;
                            case 2:
                                img.setImageResource(R.drawable.like_icon);
                                break;
                            case 3:
                                img.setImageResource(R.drawable.history_icon);
                                break;
                            default:
                                img.setImageResource(R.drawable.cherry);
                                break;
                        }
                        return view;
                    }
                };

                dlg.setAdapter(adapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent;
                        switch (which) {
                            case 0:
                                intent = new Intent(HistoryDetailActivity.this, MainActivity.class);
                                startActivity(intent);
                                break;
                            case 1:
                                intent = new Intent(HistoryDetailActivity.this, SelectActivity.class);
                                startActivity(intent);
                                break;
                            case 2:
                                intent = new Intent(HistoryDetailActivity.this, LikeActivity.class);
                                startActivity(intent);
                                break;
                            case 3:
                                intent = new Intent(HistoryDetailActivity.this, HistoryActivity.class);
                                startActivity(intent);
                                break;
                        }
                    }
                });
                dlg.show();
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private String convertTimestampToString(long timestamp) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        return dateFormat.format(new Date(timestamp));
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private LocalDateTime convertTimestampToStringWithOtherFormat(long timestamp) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneId.systemDefault());
    }
}