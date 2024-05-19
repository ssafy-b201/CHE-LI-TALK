package com.ssafy.chelitalk.activity.english;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.ListPopupWindow;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.ssafy.chelitalk.R;
import com.ssafy.chelitalk.activity.common.MainActivity;

import java.lang.reflect.Field;

public class SelectActivity extends AppCompatActivity {

    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser user = auth.getCurrentUser();

    private Spinner spinner;
    private String userEmail = user.getEmail();
    private String keyword;

    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private static final int WRITE_EXTERNAL_STORAGE = 200;
    private static final int READ_EXTERNAL_STORAGE = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_select);

        spinner = findViewById(R.id.spinner_keywords);

        // to speaking / to chatting
        final ImageButton btn_speak = (ImageButton) findViewById(R.id.btn_speak);
        final ImageButton btn_chatting = (ImageButton) findViewById(R.id.btn_chatting);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, REQUEST_RECORD_AUDIO_PERMISSION);
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_EXTERNAL_STORAGE);
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, READ_EXTERNAL_STORAGE);
        }

        btn_speak.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (keyword != null && !keyword.isEmpty()) {
                    Intent intent = new Intent(SelectActivity.this, SpeakingActivity.class);
                    intent.putExtra("keyword", keyword);
                    intent.putExtra("userEmail", userEmail);
                    startActivity(intent);
                } else {
                    // 키워드가 설정되지 않은 경우 오류 메시지를 표시하거나 처리
                    Toast.makeText(SelectActivity.this, "키워드를 선택하세요.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btn_chatting.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                if (keyword != null && !keyword.isEmpty()) {
                    Intent intent = new Intent(SelectActivity.this, ChattingActivity.class);
                    intent.putExtra("keyword", keyword);
                    intent.putExtra("userEmail", userEmail);
                    startActivity(intent);
                } else {
                    // 키워드가 설정되지 않은 경우 오류 메시지를 표시하거나 처리
                    Toast.makeText(SelectActivity.this, "키워드를 선택하세요.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>(this, R.layout.spinner_selected_layout, getResources().getStringArray(R.array.select_keywords)) {
            @NonNull
            @Override
            public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view = convertView;
                if (view == null) {
                    view = getLayoutInflater().inflate(R.layout.spinner_dropdown_layout, parent, false);
                }
                TextView textView = view.findViewById(R.id.text_view_spinner_item);
                textView.setText(getItem(position));
                return view;
            }
        };

        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                keyword = (String) parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }

        });

        setupHomeNavigation();


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

    }

    private void setupHomeNavigation() {
        ImageView goToHome = findViewById(R.id.goToHome);
        goToHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SelectActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }
}