package com.ssafy.chelitalk.activity.english;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
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

    private String userEmail = user.getEmail();

    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private static final int WRITE_EXTERNAL_STORAGE = 200;
    private static final int READ_EXTERNAL_STORAGE = 200;

    private ImageButton btnSpeak;
    private ImageButton btnChatting;

    private ImageView imageFamily;
    private ImageView imagePet;
    private ImageView imageMovie;
    private ImageView imageHobby;
    private ImageView imageFood;
    private ImageView imageMusic;
    private ImageView imageWorkout;
    private ImageView imageReading;
    private ImageView imageTravel;

    private View selectedFamily;
    private View selectedPet;
    private View selectedMovie;
    private View selectedHobby;
    private View selectedFood;
    private View selectedMusic;
    private View selectedWorkout;
    private View selectedReading;
    private View selectedTravel;

    private View selectedSpeaking;
    private View selectedChatting;

    private Button btnStart;
    private Button btnUnselected;

    private ImageView selectedKeyword = null;
    private ImageButton selectedType = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_select);

        btnSpeak = (ImageButton) findViewById(R.id.btn_speak);
        btnChatting = (ImageButton) findViewById(R.id.btn_chatting);

        imageFamily = findViewById(R.id.image_family);
        imagePet = findViewById(R.id.image_pet);
        imageMovie = findViewById(R.id.image_movie);
        imageHobby = findViewById(R.id.image_hobby);
        imageFood = findViewById(R.id.image_food);
        imageMusic = findViewById(R.id.image_music);
        imageWorkout = findViewById(R.id.image_workout);
        imageReading = findViewById(R.id.image_reading);
        imageTravel = findViewById(R.id.image_travel);

        selectedFamily = findViewById(R.id.selected_family);
        selectedPet = findViewById(R.id.selected_pet);
        selectedMovie = findViewById(R.id.selected_movie);
        selectedHobby = findViewById(R.id.selected_hobby);
        selectedFood = findViewById(R.id.selected_food);
        selectedMusic = findViewById(R.id.selected_music);
        selectedWorkout = findViewById(R.id.selected_workout);
        selectedReading = findViewById(R.id.selected_reading);
        selectedTravel = findViewById(R.id.selected_travel);

        selectedFamily.setVisibility(View.GONE);
        selectedPet.setVisibility(View.GONE);
        selectedMovie.setVisibility(View.GONE);
        selectedHobby.setVisibility(View.GONE);
        selectedFood.setVisibility(View.GONE);
        selectedMusic.setVisibility(View.GONE);
        selectedWorkout.setVisibility(View.GONE);
        selectedReading.setVisibility(View.GONE);
        selectedTravel.setVisibility(View.GONE);

        selectedSpeaking = findViewById(R.id.selected_speaking);
        selectedChatting = findViewById(R.id.selected_chatting);

        selectedSpeaking.setVisibility(View.GONE);
        selectedChatting.setVisibility(View.GONE);

        btnStart = findViewById(R.id.btnStart);
        btnUnselected = findViewById(R.id.btnUnselected);

        btnStart.setEnabled(false);
        btnStart.setVisibility(View.GONE);

        btnUnselected.setEnabled(false);
        btnUnselected.setVisibility(View.VISIBLE);

        setupButtons();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, REQUEST_RECORD_AUDIO_PERMISSION);
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_EXTERNAL_STORAGE);
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, READ_EXTERNAL_STORAGE);
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

    }

    private void setupButtons() {
        View.OnClickListener keywordListener = v -> {
            if (selectedKeyword != null && selectedKeyword != v) {
                selectedKeyword.setSelected(false);
            }

            selectedKeyword = (ImageView) v;
            v.setSelected(!v.isSelected());
            updateIntentData();
            checkAllSelected();
        };

        View.OnClickListener typeListener = v -> {
            if (selectedType != null && selectedType != v) {
                selectedType.setSelected(false);
            }
            selectedType = (ImageButton) v;
            v.setSelected(!v.isSelected());
            updateIntentData();
            checkAllSelected();
        };

        btnSpeak.setOnClickListener(typeListener);
        btnChatting.setOnClickListener(typeListener);

        imageFamily.setOnClickListener(keywordListener);
        imagePet.setOnClickListener(keywordListener);
        imageMovie.setOnClickListener(keywordListener);
        imageHobby.setOnClickListener(keywordListener);
        imageFood.setOnClickListener(keywordListener);
        imageMusic.setOnClickListener(keywordListener);
        imageWorkout.setOnClickListener(keywordListener);
        imageReading.setOnClickListener(keywordListener);
        imageTravel.setOnClickListener(keywordListener);
    }

    private void updateIntentData() {
        String conversationType = null;
        String keyword = null;

        if (btnSpeak.isSelected()){
            conversationType = "Speaking";
            selectedSpeaking.setVisibility(View.VISIBLE);
        } else {
            selectedSpeaking.setVisibility(View.GONE);
        }

        if (btnChatting.isSelected()){
            conversationType = "Chatting";
            selectedChatting.setVisibility(View.VISIBLE);
        } else {
            selectedChatting.setVisibility(View.GONE);
        }

        if (imageFamily.isSelected()) {
            keyword = "가족";
            selectedFamily.setVisibility(View.VISIBLE);
        } else {
            selectedFamily.setVisibility(View.GONE);
        }

        if (imagePet.isSelected()) {
            keyword = "반려동물";
            selectedPet.setVisibility(View.VISIBLE);
        } else {
            selectedPet.setVisibility(View.GONE);
        }

        if (imageMovie.isSelected()) {
            keyword = "영화";
            selectedMovie.setVisibility(View.VISIBLE);
        } else {
            selectedMovie.setVisibility(View.GONE);
        }

        if (imageHobby.isSelected()) {
            keyword = "취미";
            selectedHobby.setVisibility(View.VISIBLE);
        } else {
            selectedHobby.setVisibility(View.GONE);
        }

        if (imageFood.isSelected()) {
            keyword = "음식";
            selectedFood.setVisibility(View.VISIBLE);
        } else {
            selectedFood.setVisibility(View.GONE);
        }

        if (imageMusic.isSelected()) {
            keyword = "음악";
            selectedMusic.setVisibility(View.VISIBLE);
        } else {
            selectedMusic.setVisibility(View.GONE);
        }

        if (imageWorkout.isSelected()) {
            keyword = "운동";
            selectedWorkout.setVisibility(View.VISIBLE);
        } else {
            selectedWorkout.setVisibility(View.GONE);
        }

        if (imageReading.isSelected()) {
            keyword = "독서";
            selectedReading.setVisibility(View.VISIBLE);
        } else {
            selectedReading.setVisibility(View.GONE);
        }

        if (imageTravel.isSelected()) {
            keyword = "여행";
            selectedTravel.setVisibility(View.VISIBLE);
        } else {
            selectedTravel.setVisibility(View.GONE);
        }

        if (conversationType != null && keyword != null) {
            Intent intent;

            if (conversationType.equals("Speaking")) {
                intent = new Intent(this, SpeakingActivity.class);
                intent.putExtra("keyword", keyword);
                intent.putExtra("userEmail", userEmail);
            } else {
                intent = new Intent(this, ChattingActivity.class);
                intent.putExtra("keyword", keyword);
                intent.putExtra("userEmail", userEmail);
            }

            btnStart.setOnClickListener(v -> {
                if (intent != null) {
                    startActivity(intent);
                }
            });

        }
    }

    private void checkAllSelected() {
        if (selectedType != null && selectedType.isSelected() &&
                selectedKeyword != null && selectedKeyword.isSelected()) {
            btnStart.setEnabled(true);
            btnStart.setVisibility(View.VISIBLE);
            btnUnselected.setVisibility(View.GONE);
        } else {
            btnStart.setEnabled(false);
            btnStart.setVisibility(View.GONE);
            btnUnselected.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onBackPressed() { // 요거 주석처리 하지 말기
        Intent intent = new Intent(SelectActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish(); // 현재 활동 종료

    }

}