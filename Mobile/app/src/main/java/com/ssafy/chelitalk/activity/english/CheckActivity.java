package com.ssafy.chelitalk.activity.english;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.ssafy.chelitalk.R;
import com.ssafy.chelitalk.activity.common.NetworkClient;
import com.ssafy.chelitalk.api.check.Check;
import com.ssafy.chelitalk.api.check.CheckService;

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

        retrofit = NetworkClient.getRetrofitClient(CheckActivity.this);
        if(retrofit == null){
            throw new IllegalStateException("레트로핏 초기화 상태 안됨");
        }
        api = retrofit.create(CheckService.class);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();
        String email = currentUser != null ? currentUser.getEmail() : null;

        if(email != null){
            dto = new Check(email);
            Call<String> call = api.chatCheck(dto);
            call.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    if(response.isSuccessful() && response.body() != null){
                        responseTextView.setText(response.body());
                    }else{
                        responseTextView.setText("데이터 없음");
                        Log.d("CheckActivity", "데이터 없음"+response.code());
                    }
                }
    
                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    responseTextView.setText("서버 연결 실패");
                    Log.e("CheckActivity","서버 연결 실패"+t);
                }
            });

        }else{
            Toast.makeText(this, "사용자 로그인 오류", Toast.LENGTH_SHORT).show();
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}