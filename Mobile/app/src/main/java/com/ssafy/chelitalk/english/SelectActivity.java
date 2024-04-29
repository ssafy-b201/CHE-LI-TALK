package com.ssafy.chelitalk.english;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.Spinner;
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
import com.ssafy.chelitalk.common.MainActivity;

public class SelectActivity extends AppCompatActivity {

    private Spinner spinner;
    private TextView tv_result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_select);

        // to speaking / to chatting
        final ImageButton btn_speak = (ImageButton) findViewById(R.id.btn_speak);
        final ImageButton btn_chatting = (ImageButton) findViewById(R.id.btn_chatting);


        btn_speak.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                /**
                 * todo : gpt
                 */
                Intent intent = new Intent(SelectActivity.this, SpeakingActivity.class);
                startActivity(intent);

            }
        });

        btn_chatting.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SelectActivity.this, ChattingActivity.class);
                startActivity(intent);
            }
        });





        //메뉴 dialog(modal)
        final ImageButton button1 = (ImageButton) findViewById(R.id.imageButton);
        button1.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                AlertDialog.Builder dlg = new AlertDialog.Builder(SelectActivity.this);
                dlg.setTitle("Menu");
                ListAdapter adapter = new ArrayAdapter<String>(
                        SelectActivity.this, R.layout.dialog_item, R.id.text, new String[]{"HOME", "STUDY", "LIKE","HISTORY", "CHECK"}){
                    @NonNull
                    @Override
                    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent){
                        View view = super.getView(position, convertView, parent);
                        ImageView img = view.findViewById(R.id.icon);
                        switch (position){
                            case 0: img.setImageResource(R.drawable.home_icon); break;
                            case 1: img.setImageResource(R.drawable.study_icon); break;
                            case 2: img.setImageResource(R.drawable.like_icon); break;
                            case 3: img.setImageResource(R.drawable.history_icon); break;
                            case 4: img.setImageResource(R.drawable.check_icon); break;
                            default:img.setImageResource(R.drawable.cherry); break;
                        }
                        return view;
                    }
                };

                dlg.setAdapter(adapter, new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent;
                        switch(which){
                            case 0:
                                intent = new Intent(SelectActivity.this, MainActivity.class);
                                startActivity(intent);
                                break;
                            case 1:
                                intent = new Intent(SelectActivity.this, SelectActivity.class);
                                startActivity(intent);
                                break;
                            case 2:
                                intent = new Intent(SelectActivity.this, LikeActivity.class);
                                startActivity(intent);
                                break;
                            case 3:
                                intent = new Intent(SelectActivity.this, HistoryActivity.class);
                                startActivity(intent);
                                break;
                            case 4:
                                intent = new Intent(SelectActivity.this, CheckActivity.class);
                                startActivity(intent);
                                break;
                        }
                    }
                });
                dlg.show();
            }
        });


        spinner = findViewById(R.id.spinner_keywords);

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

                /**
                 * todo : gpt
                 */
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }

        });


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}