package com.ssafy.chelitalk.activity.english;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.ssafy.chelitalk.R;
import com.ssafy.chelitalk.activity.common.MainActivity;
import com.ssafy.chelitalk.api.attend.DateTypeAdapter;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Locale;

public class HistoryDetailActivity extends AppCompatActivity {

    private TextView createdAtTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_history_detail);

        createdAtTextView = findViewById(R.id.createdAtTextView);

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("history-createdAt")) {
            long createdAt = intent.getLongExtra("history-createdAt", 0);
            if (createdAt != 0) {
                String createdAtString = convertTimestampToString(createdAt);
                createdAtTextView.setText(createdAtString);
            } else {
                Log.e("HistoryDetailActivity", "Received invalid timestamp (0).");
            }
        } else {
            Log.e("HistoryDetailActivity", "Intent does not have 'history-createdAt' extra.");
        }



        //메뉴 dialog(modal)
        final ImageButton button1 = (ImageButton) findViewById(R.id.imageButton);
        button1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                AlertDialog.Builder dlg = new AlertDialog.Builder(HistoryDetailActivity.this);
                dlg.setTitle("Menu");
                ListAdapter adapter = new ArrayAdapter<String>(
                        HistoryDetailActivity.this, R.layout.dialog_item, R.id.text, new String[]{"HOME", "STUDY", "LIKE", "HISTORY"}) {
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
                                intent = new Intent(HistoryDetailActivity.this, MainActivity.class);
                                startActivity(intent);
                                break;
                            case 1:
                                intent = new Intent(HistoryDetailActivity.this, SelectActivity.class);
                                startActivity(intent);
                                break;
                            case 2:
                                intent = new Intent(HistoryDetailActivity.this, LikeActivity.class);
                                startActivity(intent);
                                break;
                            case 3:
                                intent = new Intent(HistoryDetailActivity.this, HistoryActivity.class);
                                startActivity(intent);
                                break;
                        }
                    }
                });
                dlg.show();
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private String convertTimestampToString(long timestamp) {
        System.out.println("타임스탬프"+timestamp);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        return dateFormat.format(new Date(timestamp));
    }
}