package com.ssafy.chelitalk.activity.common;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.ssafy.chelitalk.R;
import com.ssafy.chelitalk.activity.carousel.MyAdapter;
import com.ssafy.chelitalk.activity.english.HistoryActivity;
import com.ssafy.chelitalk.activity.english.LikeActivity;
import com.ssafy.chelitalk.activity.english.SelectActivity;
import com.ssafy.chelitalk.api.attend.Attend;
import com.ssafy.chelitalk.api.attend.AttendAdapter;
import com.ssafy.chelitalk.api.attend.AttendListDto;
import com.ssafy.chelitalk.api.attend.AttendService;
import com.ssafy.chelitalk.api.member.MemberData;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


import me.relex.circleindicator.CircleIndicator3;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MainActivity extends AppCompatActivity {

    private static final String API_URL = "https://api.adviceslip.com/advice";
    private TextView adviceTextView;
    private TextView greetingTextView;
    private ViewPager2 mPager;
    private FragmentStateAdapter pagerAdapter;
    private int num_page = 3;
    private CircleIndicator3 mIndicator;
    private FirebaseAuth auth;
    private static Retrofit retrofit;
    private static AttendService api;
    private Attend dto;
    private List<AttendListDto> attendList = new ArrayList<>();
//    private RecyclerView attendRecyclerView;
    private AttendAdapter attendAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
//        attendRecyclerView.setLayoutManager(layoutManager);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            retrofit = NetworkClient.getRetrofitClient(MainActivity.this);
        }
        if(retrofit == null){
            throw new IllegalStateException("레트로핏 초기화 상태 안됨");
        }
        api = retrofit.create(AttendService.class);

        //현재 로그인 된 사용자 가져오기
        auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();
        String email = currentUser != null ? currentUser.getEmail() : null;

        if(email != null){
            dto = new Attend(email);
            Call<List<AttendListDto>> call = api.attendList(dto.getMemberEmail());
            call.enqueue(new Callback<List<AttendListDto>>() {
                @Override
                public void onResponse(Call<List<AttendListDto>> call, Response<List<AttendListDto>> response) {
                    if(response.isSuccessful() && response.body() != null){
                        attendAdapter = new AttendAdapter(response.body());
//                        attendRecyclerView.setAdapter(attendAdapter);
//                        attendRecyclerView.setAdapter(attendAdapter);
//                        attendList = response.body();
//                        StringBuilder builder = new StringBuilder();
//                        for(AttendListDto attend : attendList){
//                            builder.append(attend.toString()).append("\n");
//                         }
//                        attendView.setText(builder.toString());
                        Log.d("MainActivity", "성공"+response.body());
                    }else{
                        try{
                            String errorResponse = response.errorBody().string();
                            Log.e("MainActivity", "에러메시지: "+errorResponse);
                        }catch (IOException e){
                            Log.e("MainActivity", "Error", e);
                        }
                    }
                }

                @Override
                public void onFailure(Call<List<AttendListDto>> call, Throwable t) {
//                    attendView.setText("서버 연결 실패");
                    Log.e("MainActivity", "서버 연결"+t);
                }
            });
        }else{
            Toast.makeText(this, "로그인오류", Toast.LENGTH_SHORT).show();
        }

        //menu
        LinearLayout goToStudy = findViewById(R.id.goToStudy);
        LinearLayout goToHistory = findViewById(R.id.goToHistory);
        goToStudy.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SelectActivity.class);
                startActivity(intent);
            }
        });

        goToHistory.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, HistoryActivity.class);
                startActivity(intent);
            }
        });

        //인사말
        greetingTextView = findViewById(R.id.greetingTextView);
        setGreetingBasedOnTime();

        //캐러셀
        mPager = findViewById(R.id.viewpager);
        pagerAdapter = new MyAdapter(this, num_page);
        mPager.setAdapter(pagerAdapter);
        mIndicator = findViewById(R.id.indicator);
        mIndicator.setViewPager(mPager);
        mIndicator.createIndicators(num_page,0);
        mPager.setOrientation(ViewPager2.ORIENTATION_HORIZONTAL);
        mPager.setCurrentItem(1000);
        mPager.setOffscreenPageLimit(3);

        mPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
                if(positionOffsetPixels == 0){
                    mPager.setCurrentItem(position);
                }
            }

            @Override
            public void onPageSelected(int position){
                super.onPageSelected(position);
                mIndicator.animatePageSelected(position%num_page);
            }
        });

        final float pageMargin = getResources().getDimensionPixelOffset(R.dimen.pageMargin);
        final float pageOffset = getResources().getDimensionPixelOffset(R.dimen.offset);

        mPager.setPageTransformer(new ViewPager2.PageTransformer() {
            @Override
            public void transformPage(@NonNull View page, float position) {
                float myOffset = position * -(2*pageOffset+pageMargin);
                if(mPager.getOrientation()==ViewPager2.ORIENTATION_HORIZONTAL){
                    if(ViewCompat.getLayoutDirection(mPager)==ViewCompat.LAYOUT_DIRECTION_RTL){
                        page.setTranslationX(-myOffset);
                    }else{
                        page.setTranslationX(myOffset);
                    }
                }else{
                    page.setTranslationY(myOffset);
                }
            }
        });

        //화면에 진입하면 바로 명언띄우기
        adviceTextView  = findViewById(R.id.adviceTextView);

        new FetchAdviceTask().execute();

        //알람 토글
        Switch alarmToggle = findViewById(R.id.toggle);
        alarmToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    Toast.makeText(getApplicationContext(), "알람이 켜졌습니다.", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getApplicationContext(), "알람이 꺼졌습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void setGreetingBasedOnTime() {
        Calendar c = Calendar.getInstance();
        int timeOfDay = c.get(Calendar.HOUR_OF_DAY);

        // Singleton에서 닉네임 가져오기
        String nickname = MemberData.getInstance().getNickname();

        if (nickname == null) {
            nickname = "Guest";
        }

        if(timeOfDay >= 6 && timeOfDay < 12){
            greetingTextView.setText("Good Morning, " + nickname);
        }else if(timeOfDay >= 12 && timeOfDay < 17){
            greetingTextView.setText("Good Afternoon, " + nickname);
        }else if(timeOfDay >= 17 && timeOfDay < 21){
            greetingTextView.setText("Good Evening, " + nickname);
        }else{
            greetingTextView.setText("Good Night, " + nickname);
        }
    }

    private class FetchAdviceTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... voids){
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String adviceJsonString = null;

            try{
                URL url = new URL(API_URL);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                while(true){
                    urlConnection.connect();
                    InputStream inputStream = urlConnection.getInputStream();
                    StringBuilder buffer = new StringBuilder();
                    if(inputStream == null){
                        break;
                    }
                    reader = new BufferedReader(new InputStreamReader(inputStream));
                    String line;
                    while((line = reader.readLine()) != null){
                        buffer.append(line).append("\n");
                    }
                    if(buffer.length()==0){
                        break;
                    }
                    adviceJsonString = buffer.toString();

                    JSONObject adviceJson = new JSONObject(adviceJsonString);
                    JSONObject slip = adviceJson.getJSONObject("slip");
                    String advice = slip.getString("advice");

                    if(advice.length() < 70){
                        break;
                    }
                }
            }catch(IOException | JSONException e){
                e.printStackTrace();
            }finally{
                if(urlConnection != null){
                    urlConnection.disconnect();
                }
                if(reader != null){
                    try{
                        reader.close();
                    }catch (final IOException e){
                        e.printStackTrace();
                    }
                }
            }
            return adviceJsonString;
        }

        @Override
        protected void onPostExecute(String adviceJsonString){
            if(adviceJsonString != null){
                try{
                    JSONObject adviceJson = new JSONObject(adviceJsonString);
                    JSONObject slip = adviceJson.getJSONObject("slip");
                    String advice = slip.getString("advice");
                    adviceTextView.setText(advice);
                }catch (JSONException e){
                    e.printStackTrace();
                }
            }else{
                Toast.makeText(getApplicationContext(), "명언을 가져오는데 실패했습니다.", Toast.LENGTH_SHORT).show();
            }
        }
    }

}