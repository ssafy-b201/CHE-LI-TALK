package com.ssafy.chelitalk.activity.english;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListAdapter;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.ssafy.chelitalk.R;
import com.ssafy.chelitalk.activity.common.MainActivity;
import com.ssafy.chelitalk.activity.common.NetworkClient;
import com.ssafy.chelitalk.api.history.History;
import com.ssafy.chelitalk.api.history.HistoryAdapter;
import com.ssafy.chelitalk.api.history.HistoryService;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class HistoryActivity extends AppCompatActivity {

    private static Retrofit retrofit;
    private static HistoryService api;
    private RecyclerView historyRecyclerView;
    private HistoryAdapter adapter;
    private List<History> historyList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_history);

        historyRecyclerView = findViewById(R.id.historyRecyclerView);
        historyRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        retrofit = NetworkClient.getRetrofitClient(HistoryActivity.this);
        if (retrofit == null) {
            throw new IllegalStateException("레트로핏 초기화 상태 안됨");
        }
        api = retrofit.create(HistoryService.class);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();
        String email = currentUser != null ? currentUser.getEmail() : null;

        if(email!=null) {
            Call<List<History>> call = api.historyList(email);
            call.enqueue(new Callback<List<History>>() {
                @Override
                public void onResponse(Call<List<History>> call, Response<List<History>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        historyList = response.body();
                        adapter = new HistoryAdapter(HistoryActivity.this, historyList);
                        historyRecyclerView.setAdapter(adapter);
                    } else {
                        historyList = new ArrayList<>();  // 응답 실패 시 빈 리스트 할당
                        Log.d("HistoryActivity", "No data received or error in response");
                    }
                }

                @Override
                public void onFailure(Call<List<History>> call, Throwable t) {
                    historyList = new ArrayList<>();
                    Toast.makeText(getApplicationContext(), "통신오류: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }else{
//            textView.setText("사용자 정보가 없습니다");
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
                AlertDialog.Builder dlg = new AlertDialog.Builder(HistoryActivity.this);
                dlg.setTitle("Menu");
                ListAdapter adapter = new ArrayAdapter<String>(
                        HistoryActivity.this, R.layout.dialog_item, R.id.text, new String[]{"HOME", "STUDY", "LIKE", "HISTORY", "CHECK"}) {
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
                                intent = new Intent(HistoryActivity.this, MainActivity.class);
                                startActivity(intent);
                                break;
                            case 1:
                                intent = new Intent(HistoryActivity.this, SelectActivity.class);
                                startActivity(intent);
                                break;
                            case 2:
                                intent = new Intent(HistoryActivity.this, LikeActivity.class);
                                startActivity(intent);
                                break;
                            case 3:
                                intent = new Intent(HistoryActivity.this, HistoryActivity.class);
                                startActivity(intent);
                                break;
                            case 4:
                                intent = new Intent(HistoryActivity.this, CheckActivity.class);
                                startActivity(intent);
                                break;
                        }
                    }
                });
                dlg.show();
            }
        });
    }
}