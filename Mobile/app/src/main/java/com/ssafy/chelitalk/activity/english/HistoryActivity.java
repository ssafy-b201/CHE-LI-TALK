package com.ssafy.chelitalk.activity.english;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
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
import java.util.Collection;
import java.util.Collections;
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
    private Button deleteHistoryButton;
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

        deleteHistoryButton = findViewById(R.id.deleteBtn);
        deleteHistoryButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                deleteAllHistory();
            }
        });

        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();
        String email = currentUser != null ? currentUser.getEmail() : null;

        if(email!=null) {
            Call<List<History>> call = api.historyList(email);
            call.enqueue(new Callback<List<History>>() {
                @Override
                public void onResponse(Call<List<History>> call, Response<List<History>> response) {
                    if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                        historyList = response.body();
                        Collections.reverse(historyList);
                        adapter = new HistoryAdapter(HistoryActivity.this, historyList);
                        historyRecyclerView.setAdapter(adapter);
                    } else {
                        historyList = new ArrayList<>();  // 응답 실패 시 빈 리스트 할당
                        adapter = new HistoryAdapter(HistoryActivity.this, historyList);
                        historyRecyclerView.setAdapter(adapter);
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
        }


//        setupHomeNavigation();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void deleteAllHistory() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();
        String email = currentUser != null ? currentUser.getEmail() : null;

        if(email != null){
            Call<Void> call = api.deleteHistory(email);
            call.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if(response.isSuccessful()){
                        Toast.makeText(HistoryActivity.this, "히스토리가 모두 삭제되었습니다.", Toast.LENGTH_SHORT).show();
                        historyList.clear();
                        adapter.notifyDataSetChanged();
                    }else{
                        Toast.makeText(HistoryActivity.this, "히스토리 삭제 실패!", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Toast.makeText(HistoryActivity.this, "통신오류:"+t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

//    private void setupHomeNavigation() {
//        ImageView goToHome = findViewById(R.id.goToHome);
//        goToHome.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(HistoryActivity.this, MainActivity.class);
//                startActivity(intent);
//            }
//        });
//    }
}