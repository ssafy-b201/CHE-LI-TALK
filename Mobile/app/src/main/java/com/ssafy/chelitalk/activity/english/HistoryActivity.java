package com.ssafy.chelitalk.activity.english;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import android.view.View;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
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

import java.util.ArrayList;
import java.util.List;

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


    @RequiresApi(api = Build.VERSION_CODES.O)
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
                    Log.e("HistoryActivity", "오류"+t);
                    Toast.makeText(getApplicationContext(), "통신오류: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }else{
//            textView.setText("사용자 정보가 없습니다");
        }


        setupHomeNavigation();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void setupHomeNavigation() {
        ImageView goToHome = findViewById(R.id.goToHome);
        goToHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HistoryActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }
}