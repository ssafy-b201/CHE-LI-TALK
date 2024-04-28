package com.ssafy.chelitalk;

import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.Buffer;

public class MainActivity extends AppCompatActivity {

    private static final String API_URL = "https://api.adviceslip.com/advice";
    private TextView adviceTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

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
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuilder buffer = new StringBuilder();
                if(inputStream == null){
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while((line = reader.readLine()) != null){
                    buffer.append(line).append("\n");
                }
                if(buffer.length()==0){
                    return null;
                }
                adviceJsonString = buffer.toString();
            }catch(IOException e){
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