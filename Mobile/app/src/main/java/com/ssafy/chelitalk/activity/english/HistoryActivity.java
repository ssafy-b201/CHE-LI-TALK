package com.ssafy.chelitalk.activity.english;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.ssafy.chelitalk.R;
import com.ssafy.chelitalk.activity.common.NetworkClient;
import com.ssafy.chelitalk.api.history.History;
import com.ssafy.chelitalk.api.history.HistoryService;
import com.ssafy.chelitalk.api.history.RetrofitClientHistory;

import java.util.List;

import okhttp3.OkHttp;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class HistoryActivity extends AppCompatActivity {

    private static Retrofit retrofit;
    private static HistoryService api;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_history);

        retrofit = NetworkClient.getRetrofitClient(HistoryActivity.this);
        api = retrofit.create(HistoryService.class);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();
        String email = currentUser != null ? currentUser.getEmail() : null;

        TextView textView = findViewById(R.id.history);

        if(email!=null) {
            Call<List<History>> call = api.historyList(email);
            call.enqueue(new Callback<List<History>>() {
                @Override
                public void onResponse(Call<List<History>> call, Response<List<History>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        List<History> historyList = response.body();
                        StringBuilder historyText = new StringBuilder();

                        for (History history : historyList) {
                            historyText.append(history.getCreatedAt()).append("\n");
                        }
                        textView.setText(historyText.toString());
                    } else {
                        Toast.makeText(getApplicationContext(), "목록이 존재하지 않습니다.", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<List<History>> call, Throwable t) {
                    Toast.makeText(getApplicationContext(), "통신오류: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }else{
            textView.setText("사용자 정보가 없습니다");
        }



        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}