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
import com.ssafy.chelitalk.api.check.Check;
import com.ssafy.chelitalk.api.check.CheckService;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class CheckActivity extends AppCompatActivity {
    private static Retrofit retrofit;
    private static CheckService api;
    private Check dto;
    private TextView responseTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_check);

        responseTextView = findViewById(R.id.text_response);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            retrofit = NetworkClient.getRetrofitClient(CheckActivity.this);
        }
        if (retrofit == null) {
            throw new IllegalStateException("레트로핏 초기화 상태 안됨");
        }
        api = retrofit.create(CheckService.class);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();
        String email = currentUser != null ? currentUser.getEmail() : null;

        if (email != null) {
            dto = new Check(email);
            Call<String> call = api.chatCheck(dto);
            call.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    if (response.isSuccessful()) {
                        responseTextView.setText(response.body());
                    } else {
                        try {
                            String errorResponse = response.errorBody().string();
                            Log.e("CheckActivity", "에러메시지: " + errorResponse);
                        } catch (IOException e) {
                            Log.e("CheckActivity", "Error reading error body", e);
                        }
                    }
                }

                private String formatToJson(String responseBody) {
                    String[] parts = responseBody.split(":");
                    if (parts.length == 2) {
                        return "{\"" + parts[0].trim() + "\":\"" + parts[1].trim() + "\"}";
                    }
                    return "{}";
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    responseTextView.setText("서버 연결 실패");
                    Log.e("CheckActivity", "서버 연결 실패" + t);
                }
            });

        } else {
            Toast.makeText(this, "사용자 로그인 오류", Toast.LENGTH_SHORT).show();
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        //메뉴 dialog(modal)
        final ImageButton button1 = (ImageButton) findViewById(R.id.imageButton);
        button1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                AlertDialog.Builder dlg = new AlertDialog.Builder(CheckActivity.this);
                dlg.setTitle("Menu");
                ListAdapter adapter = new ArrayAdapter<String>(
                        CheckActivity.this, R.layout.dialog_item, R.id.text, new String[]{"HOME", "STUDY", "LIKE", "HISTORY", "CHECK"}) {
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
                            case 4:
                                img.setImageResource(R.drawable.check_icon);
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
                                intent = new Intent(CheckActivity.this, MainActivity.class);
                                startActivity(intent);
                                break;
                            case 1:
                                intent = new Intent(CheckActivity.this, SelectActivity.class);
                                startActivity(intent);
                                break;
                            case 2:
                                intent = new Intent(CheckActivity.this, LikeActivity.class);
                                startActivity(intent);
                                break;
                            case 3:
                                intent = new Intent(CheckActivity.this, HistoryActivity.class);
                                startActivity(intent);
                                break;
                            case 4:
                                intent = new Intent(CheckActivity.this, CheckActivity.class);
                                startActivity(intent);
                                break;
                        }
                    }
                });
                dlg.show();
            }
        });
    }

    @Override
    public void onBackPressed() { // 요거 주석처리 하지 말기
        Intent intent = new Intent(CheckActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish(); // 현재 활동 종료

    }
}
