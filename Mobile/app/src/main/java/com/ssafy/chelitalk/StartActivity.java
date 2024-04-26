package com.ssafy.chelitalk;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;

public class StartActivity extends AppCompatActivity {

    // Firebase 인증 객체
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        // Firebase 인증 객체 초기화 , 로그인 체크
        auth = FirebaseAuth.getInstance();
        auth.signOut(); // 테스트용 로그아웃

        if (auth.getCurrentUser() != null) {
            // 사용자가 이미 로그인 되어 있다면 바로 Splash로 이동
            startActivity(new Intent(this, SplashActivity.class));
            finish();
        } else {
            // 로그인되지 않은 사용자는 Signup으로 이동
            startActivity(new Intent(this, SigninActivity.class));
            finish();
        }

    }

}