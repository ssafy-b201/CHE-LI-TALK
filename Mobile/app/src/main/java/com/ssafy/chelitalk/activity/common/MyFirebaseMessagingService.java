package com.ssafy.chelitalk.activity.common;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.ssafy.chelitalk.api.alarm.AlarmService;
import com.ssafy.chelitalk.api.alarm.TokenBody;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        //토큰 서버로 전송
        Log.d("FCM", "New token: " + token);
        System.out.println("토큰:"+token);
        sendRegistrationToServer(token);
    }

    private void sendRegistrationToServer(String token) {
        Retrofit retrofit = NetworkClient.getRetrofitClient();
        AlarmService alarmService = retrofit.create(AlarmService.class);

        TokenBody tokenBody = new TokenBody(token);
        Call<Void> call = alarmService.registerToken(tokenBody);
        call.enqueue(new retrofit2.Callback<Void>(){

            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if(response.isSuccessful()){
                    Log.d("FCM", "Token successfully registered to server");
                }else{
                    Log.d("FCM","Failed to register token");
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("FCM", "Error registering token:" + t.getMessage());
            }
        });
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        //수신한 메시지 처리
        if (remoteMessage.getData().size() > 0) {
            Log.d("FCM", "Message data payload: " + remoteMessage.getData());
        }

        if (remoteMessage.getNotification() != null) {
            Log.d("FCM", "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }
    }
}
