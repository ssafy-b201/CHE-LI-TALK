package com.ssafy.chelitalk.api.check;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface CheckService {
    @POST("chat/check")
    Call<String> chatCheck(@Body Check dto);
}
