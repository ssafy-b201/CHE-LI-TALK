package com.ssafy.chelitalk.activity.common;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.ssafy.chelitalk.R;
import com.ssafy.chelitalk.activity.login.SigninActivity;
import com.ssafy.chelitalk.activity.login.SignupActivity;
import com.ssafy.chelitalk.api.member.MemberData;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SplashActivity extends AppCompatActivity {

    // Firebase 인증 객체
    private FirebaseAuth auth;
    private static final int SPLASH_TIME_OUT = 4000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        auth = FirebaseAuth.getInstance();

        ImageView imageView = findViewById(R.id.imageView);
        Glide.with(this)
                .asGif()
                .load(R.drawable.logo_move)
                .into(imageView);

        new Handler().postDelayed(() -> {
            FirebaseUser currentUser = auth.getCurrentUser();
            if (currentUser != null) {
                // 이미 로그인된 사용자
                checkUserRegistration(currentUser);
            } else {
                // 로그인 되어있지 않은 사용자
                promptLogin();
            }
        }, SPLASH_TIME_OUT);
    }

    private void checkUserRegistration(FirebaseUser user) {
        String userEmail = user.getEmail();
        sendEmailToServer(userEmail);
    }

    private void sendEmailToServer(String email) {
        try {

            OkHttpClient client = getSecureOkHttpClient();

            HttpUrl.Builder urlBuilder = HttpUrl.parse("https://k10b201.p.ssafy.io/cherry/api/member/valid").newBuilder();
            urlBuilder.addQueryParameter("memberEmail", email);
            String url = urlBuilder.build().toString();

            Request request = new Request.Builder().url(url).build();
            client.newCall(request).enqueue(new okhttp3.Callback() {

                @Override
                public void onFailure(okhttp3.Call call, IOException e) {
                    Log.e("SplashActivity", "Email 검증 실패", e);
                    promptLogin();
                }

                @Override
                public void onResponse(okhttp3.Call call, Response response) throws IOException {
                    if (!response.isSuccessful()) {
                        runOnUiThread(() -> promptLogin());
                        return;
                    }
                    try {
                        String jsonResponse = response.body().string();
                        JSONObject jsonObject = new JSONObject(jsonResponse);
                        JSONObject data = jsonObject.getJSONObject("data");
                        boolean isRegistered = data.getBoolean("isRegistered");
                        String nickname = data.getString("nickname");

                        runOnUiThread(() -> {
                            if (isRegistered) {
                                proceedToMain(nickname);
                            } else {
                                promptSignup();
                            }
                        });
                    } catch (JSONException e) {
                        Log.e("SplashActivity", "JSON 파싱 오류", e);
                        runOnUiThread(() -> promptLogin());
                    }
                }
            });
        } catch (Exception e) {
            Log.e("SplashActivity", "///// OkHttp Client Error /////", e);
        }
    }

    private OkHttpClient getSecureOkHttpClient() throws Exception {
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        InputStream caInput = getResources().openRawResource(R.raw.cheli);
        Certificate ca;
        try {
            ca = cf.generateCertificate(caInput);
        } finally {
            caInput.close();
        }

        String keyStoreType = KeyStore.getDefaultType();
        KeyStore keyStore = KeyStore.getInstance(keyStoreType);
        keyStore.load(null, null);
        keyStore.setCertificateEntry("ca", ca);

        String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
        TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
        tmf.init(keyStore);

        X509TrustManager trustManager = (X509TrustManager) tmf.getTrustManagers()[0];

        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, new javax.net.ssl.TrustManager[]{trustManager}, null);

        return new OkHttpClient.Builder()
                .sslSocketFactory(sslContext.getSocketFactory(), trustManager)
                .build();
    }

    private void proceedToMain(String nickname) {
        Intent intent = new Intent(SplashActivity.this, MainActivity.class);

        MemberData.getInstance().setNickname(nickname);

        startActivity(intent);
        finish();
    }

    private void promptLogin() {
        startActivity(new Intent(SplashActivity.this, SigninActivity.class));
        finish();
    }

    private void promptSignup() {
        startActivity(new Intent(SplashActivity.this, SignupActivity.class));
        finish();
    }
}
