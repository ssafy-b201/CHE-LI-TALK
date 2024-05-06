package com.ssafy.chelitalk.activity.english;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ssafy.chelitalk.R;
import com.ssafy.chelitalk.activity.common.MainActivity;
import com.ssafy.chelitalk.activity.login.SignupActivity;
import com.ssafy.chelitalk.api.chat.Message;
import com.ssafy.chelitalk.api.chat.MessageAdapter;
import com.ssafy.chelitalk.api.member.MemberData;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.util.ArrayList;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ChattingActivity extends AppCompatActivity {

    private EditText etMessage;
    private RecyclerView recyclerView;
    private MessageAdapter adapter;
    private TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_chatting);

        recyclerView = findViewById(R.id.recyclerView);
        adapter = new MessageAdapter();
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        Intent intent = getIntent();
        String keyword = intent.getStringExtra("keyword");
        String userEmail = intent.getStringExtra("userEmail");

        tv = findViewById(R.id.tv_keyword); // 여기로 이동
        tv.setText(keyword + "을(를) 주제로 채팅을 진행합니다.");

        // 선택한 키워드로 채팅 시작
        startChat(userEmail, keyword);

        // 메시지 전송
        etMessage = findViewById(R.id.etMessage);

        Button btnSend = findViewById(R.id.btnSend);
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = etMessage.getText().toString();
                if (!message.isEmpty()) {
                    sendMessage(userEmail, message);
                    etMessage.setText("");
                    runOnUiThread(() -> {
                        // 서버 응답 메시지를 RecyclerView에 추가합니다.
                        adapter.addMessage(new Message(message, true));
                    });
                }
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //채팅 종료
        Button btn_finish = (Button) findViewById(R.id.btn_finish);
        btn_finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dlg = new AlertDialog.Builder(ChattingActivity.this);
                LayoutInflater inflater = getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.dialog_chat_finish, null);
                dlg.setView(dialogView);

                TextView tvDialogContent = dialogView.findViewById(R.id.tvDialogContent);
                Button btnDialogOk = dialogView.findViewById(R.id.btnDialogOk);
                Button btnDialogCancel = dialogView.findViewById(R.id.btnDialogCancel);

                AlertDialog alertDialog = dlg.create();
                alertDialog.getWindow().setBackgroundDrawableResource(R.drawable.rectangle_style);

                tvDialogContent.setText("정말 채팅을 끝내시겠어요?");

                btnDialogCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });

                btnDialogOk.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(ChattingActivity.this, CheckActivity.class);
                        startActivity(intent);
                    }
                });

                alertDialog.show();
            }
        });
    }

    private void startChat(String email, String keyword) {
        try {

            OkHttpClient client = getSecureOkHttpClient();

            String url = "https://k10b201.p.ssafy.io/cherry/api/chat/begin";

            MediaType mediaType = MediaType.parse("application/json");
            String requestBody = "{\"memberEmail\":\"" + email + "\", \"content\":\"" + keyword + "\"}";
            RequestBody body = RequestBody.create(requestBody, mediaType);

            Request request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .build();

            client.newCall(request).enqueue(new okhttp3.Callback() {

                @Override
                public void onFailure(okhttp3.Call call, IOException e) {
                    Log.e("ChattingActivity", "서버 통신 실패", e);
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        // 서버로부터의 응답 처리
                        Log.d("ChattingActivity", "메시지 전송 성공");
                        String responseMessage = response.body().string();

                        runOnUiThread(() -> {
                            adapter.addMessage(new Message(responseMessage, false));
                        });
                    } else {
                        Log.e("ChattingActivity", "서버 오류: " + response.code());
                    }
                }
            });
        }catch (Exception e) {
            Log.e("ChattingActivity", "///// OkHttp Client Error /////", e);
        }
    }

    private void sendMessage(String email, String message) {
        try {

            OkHttpClient client = getSecureOkHttpClient();

            String url = "https://k10b201.p.ssafy.io/cherry/api/chat";

            MediaType mediaType = MediaType.parse("application/json");
            String requestBody = "{\"memberEmail\":\"" + email + "\", \"content\":\"" + message + "\"}";
            RequestBody body = RequestBody.create(requestBody, mediaType);

            Request request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .build();

            client.newCall(request).enqueue(new okhttp3.Callback() {

                @Override
                public void onFailure(okhttp3.Call call, IOException e) {
                    Log.e("ChattingActivity", "서버 통신 실패", e);
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        // 서버로부터의 응답 처리
                        Log.d("ChattingActivity", "메시지 전송 성공");
                            String responseMessage = response.body().string();
                        runOnUiThread(() -> {
                            adapter.addMessage(new Message(responseMessage, false));
                        });
                    } else {
                        // 서버 오류 처리
                        Log.e("ChattingActivity", "서버 오류: " + response.code());
                    }
                }
            });
        }catch (Exception e) {
            Log.e("ChattingActivity", "///// OkHttp Client Error /////", e);
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