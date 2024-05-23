package com.ssafy.chelitalk.api.alarm;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface AlarmService {
    @POST("/fcm/send")
    Call<Void> registerToken(@Body TokenBody token);
}