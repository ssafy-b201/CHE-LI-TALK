package com.ssafy.chelitalk;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashActivity extends AppCompatActivity {

    // Firebase 인증 객체
    private FirebaseAuth auth;

    private static int SPLASH_TIME_OUT = 2000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash);

        // Firebase 인증 객체 초기화 , 로그인 체크
        auth = FirebaseAuth.getInstance();
//        auth.signOut(); // 테스트용 로그아웃

        new Handler().postDelayed(() -> {
            if (auth.getCurrentUser() != null) {
                // 사용자가 로그인 한 적이 있다면 메인 액티비티로 이동
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
                Log.i("SplashActivity", "token = + " + String.valueOf(getString(com.firebase.ui.auth.R.string.default_web_client_id)));
                finish();
            } else {
                // 로그인 한 적 없는 사용자는 회원가입 화면으로 이동
                startActivity(new Intent(SplashActivity.this, SigninActivity.class));
                finish();
            }
        }, SPLASH_TIME_OUT);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

    }
}