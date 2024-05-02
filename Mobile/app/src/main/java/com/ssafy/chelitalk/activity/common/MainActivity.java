package com.ssafy.chelitalk.activity.common;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
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
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.ssafy.chelitalk.R;
import com.ssafy.chelitalk.activity.carousel.MyAdapter;
import com.ssafy.chelitalk.activity.english.CheckActivity;
import com.ssafy.chelitalk.activity.english.HistoryActivity;
import com.ssafy.chelitalk.activity.english.LikeActivity;
import com.ssafy.chelitalk.activity.english.SelectActivity;
import com.ssafy.chelitalk.api.attend.Attend;
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
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.util.Calendar;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import me.relex.circleindicator.CircleIndicator3;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private static final String API_URL = "https://api.adviceslip.com/advice";
    private TextView adviceTextView;
    private TextView greetingTextView;
    private ViewPager2 mPager;
    private FragmentStateAdapter pagerAdapter;
    private int num_page = 3;
    private CircleIndicator3 mIndicator;
    private FirebaseAuth auth;
    private TextView attendView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        //현재 로그인 된 사용자 가져오기
        auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();
        String email = currentUser != null ? currentUser.getEmail() : null;

        attendView = findViewById(R.id.attendView);

        OkHttpClient client = TrustOkHttpClientUtil.getUnsafeOkHttpClient();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://k10b201.p.ssafy.io/cherry/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();


        AttendService attendAPI = retrofit.create(AttendService.class);

        attendAPI.attendList(email).enqueue(new Callback<List<Attend>>() {
            @Override
            public void onResponse(Call<List<Attend>> call, Response<List<Attend>> response) {
                //성공적으로 응답하면 실행
                if(response.isSuccessful() && response.body() != null){
                    List<Attend> attends = response.body();
                    updateAttendView(attends);
                    Log.d("MainActivity", "Attendance Data: " + attends);
                }else{
                    Log.d("MainActivity", "Response not successful or empty: " + response.code() + " - " + response.message());
                    Toast.makeText(MainActivity.this, "출석 데이터 추출 실패! " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<List<Attend>> call, Throwable t) {
                Log.e("MainActivity", "API call failed: " + t.getMessage(), t);
                Toast.makeText(MainActivity.this, "API 호출 실패: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });



        //메뉴 dialog(modal)
        final ImageButton button1 = (ImageButton) findViewById(R.id.imageButton);
        button1.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                AlertDialog.Builder dlg = new AlertDialog.Builder(MainActivity.this);
                dlg.setTitle("Menu");
                ListAdapter adapter = new ArrayAdapter<String>(
                        MainActivity.this, R.layout.dialog_item, R.id.text, new String[]{"HOME", "STUDY", "LIKE","HISTORY", "CHECK"}){
                    @NonNull
                    @Override
                    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent){
                        View view = super.getView(position, convertView, parent);
                        ImageView img = view.findViewById(R.id.icon);

                        int width = 200;
                        int height = 200;

                        if(position ==1){
                            width=100;
                        }

                        ViewGroup.LayoutParams layoutParams = img.getLayoutParams();
                        layoutParams.width = width;
                        layoutParams.height = height;
                        img.setLayoutParams(layoutParams);
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
                                intent = new Intent(MainActivity.this, MainActivity.class);
                                startActivity(intent);
                                break;
                            case 1:
                                intent = new Intent(MainActivity.this, SelectActivity.class);
                                startActivity(intent);
                                break;
                            case 2:
                                intent = new Intent(MainActivity.this, LikeActivity.class);
                                startActivity(intent);
                                break;
                            case 3:
                                intent = new Intent(MainActivity.this, HistoryActivity.class);
                                startActivity(intent);
                                break;
                            case 4:
                                intent = new Intent(MainActivity.this, CheckActivity.class);
                                startActivity(intent);
                                break;
                        }
                    }
                });
                dlg.show();
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

    private void updateAttendView(List<Attend> attends) {
        StringBuilder sb = new StringBuilder();
        for(Attend attend : attends){
            sb.append(attend.getAttend() ? "출석": "출석안함");
        }
        attendView.setText(sb.toString());
        Log.d("MainActivity", "Updated Attendance View: " + sb.toString());
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