package com.ssafy.chelitalk.activity.english;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListAdapter;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.ssafy.chelitalk.R;
import com.ssafy.chelitalk.activity.common.MainActivity;
import com.ssafy.chelitalk.activity.common.NetworkClient;
import com.ssafy.chelitalk.api.history.History;
import com.ssafy.chelitalk.api.history.HistoryService;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

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
        if (retrofit == null) {
            throw new IllegalStateException("레트로핏 초기화 상태 안됨");
        }
        api = retrofit.create(HistoryService.class);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();
        String email = currentUser != null ? currentUser.getEmail() : null;

        LinearLayout container = findViewById(R.id.textContainer);

        if(email!=null) {
            Call<List<History>> call = api.historyList(email);
            call.enqueue(new Callback<List<History>>() {
                @Override
                public void onResponse(Call<List<History>> call, Response<List<History>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        List<History> historyList = response.body();
                        //날짜 포맷 지정
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.US);

                        for (History history : historyList) {
                            TextView textView = new TextView(HistoryActivity.this);
                            String dateText = dateFormat.format(history.getCreatedAt());
                            String timeText = "\n" + timeFormat.format(history.getCreatedAt());

                            SpannableString spannable = new SpannableString(dateText + timeText);
                            spannable.setSpan(new StyleSpan(Typeface.BOLD), 0, dateText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            spannable.setSpan(new RelativeSizeSpan(0.8f), dateText.length(), spannable.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                            textView.setText(spannable);
                            textView.setTextSize(16);
                            textView.setTextColor(ContextCompat.getColor(HistoryActivity.this, R.color.black));
                            Typeface typeface = ResourcesCompat.getFont(HistoryActivity.this, R.font.galmuri9);
                            textView.setTypeface(typeface);
                            textView.setGravity(Gravity.CENTER);
                            textView.setPadding(20, 10, 20, 10);
                            textView.setLayoutParams(new LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.MATCH_PARENT,
                                    LinearLayout.LayoutParams.WRAP_CONTENT));
                            container.addView(textView);
                        }
                    } else if(response.body() ==null){
                        Toast.makeText(getApplicationContext(), "목록이 존재하지 않습니다.", Toast.LENGTH_SHORT).show();
                    } else{
                        Toast.makeText(getApplicationContext(), "응답실패", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<List<History>> call, Throwable t) {
                    Toast.makeText(getApplicationContext(), "통신오류: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }else{
//            textView.setText("사용자 정보가 없습니다");
        }



        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //메뉴 dialog(modal)
        final ImageButton button1 = (ImageButton) findViewById(R.id.imageButton);
        button1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                AlertDialog.Builder dlg = new AlertDialog.Builder(HistoryActivity.this);
                dlg.setTitle("Menu");
                ListAdapter adapter = new ArrayAdapter<String>(
                        HistoryActivity.this, R.layout.dialog_item, R.id.text, new String[]{"HOME", "STUDY", "LIKE", "HISTORY", "CHECK"}) {
                    @NonNull
                    @Override
                    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                        View view = super.getView(position, convertView, parent);
                        ImageView img = view.findViewById(R.id.icon);
                        switch (position) {
                            case 0:
                                img.setImageResource(R.drawable.home_icon);
                                break;
                            case 1:
                                img.setImageResource(R.drawable.study_icon);
                                break;
                            case 2:
                                img.setImageResource(R.drawable.like_icon);
                                break;
                            case 3:
                                img.setImageResource(R.drawable.history_icon);
                                break;
                            case 4:
                                img.setImageResource(R.drawable.check_icon);
                                break;
                            default:
                                img.setImageResource(R.drawable.cherry);
                                break;
                        }
                        return view;
                    }
                };

                dlg.setAdapter(adapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent;
                        switch (which) {
                            case 0:
                                intent = new Intent(HistoryActivity.this, MainActivity.class);
                                startActivity(intent);
                                break;
                            case 1:
                                intent = new Intent(HistoryActivity.this, SelectActivity.class);
                                startActivity(intent);
                                break;
                            case 2:
                                intent = new Intent(HistoryActivity.this, LikeActivity.class);
                                startActivity(intent);
                                break;
                            case 3:
                                intent = new Intent(HistoryActivity.this, HistoryActivity.class);
                                startActivity(intent);
                                break;
                            case 4:
                                intent = new Intent(HistoryActivity.this, CheckActivity.class);
                                startActivity(intent);
                                break;
                        }
                    }
                });
                dlg.show();
            }
        });
    }
}