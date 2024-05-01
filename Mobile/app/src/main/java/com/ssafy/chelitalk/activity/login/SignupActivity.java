package com.ssafy.chelitalk.activity.login;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.ssafy.chelitalk.activity.common.MainActivity;
import com.ssafy.chelitalk.R;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SignupActivity extends AppCompatActivity {

    private EditText et_nickname;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        Button btn_start = findViewById(R.id.btn_start);
        et_nickname = findViewById(R.id.et_nickname);
        auth = FirebaseAuth.getInstance();

        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUser user = auth.getCurrentUser();

                if (user != null) {
                    String email = user.getEmail();
                    String nickname = et_nickname.getText().toString();

                    if (!nickname.isEmpty()) {
                        sendUserInfoToServer(email, nickname);
                    } else {
                        Toast.makeText(SignupActivity.this, "체리톡에서 사용할 영어이름을 입력해주세요.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.d("SignupActivity", "사용자 정보 없음");
                    Toast.makeText(SignupActivity.this, "로그인 되어 있지 않습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void sendUserInfoToServer(String email, String nickname) {
        try {

            OkHttpClient client = getSecureOkHttpClient();

            String url = "https://k10b201.p.ssafy.io/cherry/api/member/signup";

            MediaType mediaType = MediaType.parse("application/json");
            String requestBody = "{\"memberEmail\":\"" + email + "\", \"memberNickname\":\"" + nickname + "\"}";
            RequestBody body = RequestBody.create(requestBody, mediaType);

            Request request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .build();

            Log.d("SignupActivity", "Request URL: " + url);
            Log.d("SignupActivity", "Request Body: " + requestBody);

            client.newCall(request).enqueue(new okhttp3.Callback() {

                @Override
                public void onFailure(okhttp3.Call call, IOException e) {
                    Log.e("SignupActivity", "서버 통신 실패", e);
                }

                @Override
                public void onResponse(okhttp3.Call call, Response response) throws IOException {
                    Log.d("HTTP Status Code", String.valueOf(response.code()));
                    if (response.isSuccessful()) {
                        runOnUiThread(() -> {
                            Toast.makeText(SignupActivity.this, "회원가입 성공!", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(SignupActivity.this, MainActivity.class));
                            Log.d("HTTP Status Code", String.valueOf(response.code()));
                            finish();
                        });
                    } else {
                        runOnUiThread(() -> {
                            Toast.makeText(SignupActivity.this, "회원가입 실패", Toast.LENGTH_SHORT).show();
                            Log.d("HTTP Status Code", String.valueOf(response.code()));
                            Log.d("HTTP Status Message", response.message());
                        });
                    }
                    response.close();
                }
            });
        }catch (Exception e) {
            Log.e("SignupActivity", "///// OkHttp Client Error /////", e);
        }
    }

    private OkHttpClient getSecureOkHttpClient() throws Exception {
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        InputStream caInput = getResources().openRawResource(R.raw.k10b201);
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

}