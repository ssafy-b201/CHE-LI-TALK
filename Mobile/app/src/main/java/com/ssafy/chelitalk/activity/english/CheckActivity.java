package com.ssafy.chelitalk.activity.english;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.ssafy.chelitalk.R;
import com.ssafy.chelitalk.activity.common.MainActivity;
import com.ssafy.chelitalk.activity.common.NetworkClient;
import com.ssafy.chelitalk.api.check.Check;
import com.ssafy.chelitalk.api.check.CheckAdapter;
import com.ssafy.chelitalk.api.check.CheckService;
import com.ssafy.chelitalk.api.check.GrammarAdapter;
import com.ssafy.chelitalk.api.historydetail.HistoryDetailResponseDto;


import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class CheckActivity extends AppCompatActivity {
    private static Retrofit retrofit;
    private static CheckService api;
    private RecyclerView recyclerView;
    private RecyclerView recyclerViewForCheck;
    private CheckAdapter adapter;
    private GrammarAdapter adapterForGrammar;
    private Check dto;
    private LottieAnimationView animationView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_check);

        recyclerView = findViewById(R.id.checkRecyclerView);
        recyclerViewForCheck = findViewById(R.id.grammarRecyclerView);
        animationView = findViewById(R.id.lottie);
        animationView.setAnimation("loading_loop.json");

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewForCheck.setLayoutManager(new LinearLayoutManager(this));

//        setupHomeNavigation();

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
            showLoading(true);
            dto = new Check(email);
            Call<String> call = api.chatCheck(dto);
            Call<List<HistoryDetailResponseDto>> callForChat = api.chatRecent(email);


            //문법체크
            call.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    showLoading(false);
                    if (response.isSuccessful()) {
                        List<String> grammarDetails = Arrays.asList(response.body().split("\\n"));
                        adapterForGrammar = new GrammarAdapter(grammarDetails);
                        recyclerViewForCheck.setAdapter(adapterForGrammar);
                    } else {
                        try {
                            String errorResponse = response.errorBody().string();
                            Log.e("CheckActivity", "에러메시지: " + errorResponse);
                        } catch (IOException e) {
                            Log.e("CheckActivity", "Error reading error body", e);
                        }
                    }
                }
                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    Log.e("CheckActivity", "서버 연결 실패" + t);
                }
            });

            callForChat.enqueue(new Callback<List<HistoryDetailResponseDto>>() {
                @Override
                public void onResponse(Call<List<HistoryDetailResponseDto>> call, Response<List<HistoryDetailResponseDto>> response) {
                    if(response.isSuccessful() && response.body() != null){
                        adapter = new CheckAdapter(response.body());
                        recyclerView.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                    }else{
                        try{
                            String errorResponse = response.errorBody().string();
                            Log.e("CheckActivity", "에러 : " + errorResponse);
                            Toast.makeText(CheckActivity.this, "서버 에러 발생: " + errorResponse, Toast.LENGTH_LONG).show();
                        }catch (Exception e){
                            Log.e("CheckActivity", "Error reading error body", e);
                            Toast.makeText(CheckActivity.this, "응답 처리 중 오류 발생", Toast.LENGTH_LONG).show();
                        }
                    }
                }

                @Override
                public void onFailure(Call<List<HistoryDetailResponseDto>> call, Throwable t) {
                    Log.e("CheckActivity", "서버 연결 실패"+t);
                    Toast.makeText(CheckActivity.this, "서버 연결 실패", Toast.LENGTH_LONG).show();
                }
            });

        } else {
            Toast.makeText(this, "사용자 로그인 오류", Toast.LENGTH_SHORT).show();
            Log.e("CheckActivity", "사용자 로그인 오류 혹은 날짜 오류");
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });



    }

    @Override
    public void onBackPressed() { // 요거 주석처리 하지 말기
        Intent intent = new Intent(CheckActivity.this, SelectActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish(); // 현재 활동 종료

    }

//    private void setupHomeNavigation() {
//        ImageView goToHome = findViewById(R.id.goToHome);
//        goToHome.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(CheckActivity.this, MainActivity.class);
//                startActivity(intent);
//            }
//        });
//    }

    private void showLoading(boolean show){
        if(show){
            animationView.setVisibility(View.VISIBLE);
            recyclerViewForCheck.setVisibility(View.GONE);
            animationView.playAnimation();
            animationView.setRepeatCount(3);
        }else{
            animationView.cancelAnimation();
            recyclerViewForCheck.setVisibility(View.VISIBLE);
            animationView.setVisibility(View.GONE);
        }
    }
}
