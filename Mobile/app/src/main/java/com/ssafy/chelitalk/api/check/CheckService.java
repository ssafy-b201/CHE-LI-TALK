package com.ssafy.chelitalk.api.check;

import com.ssafy.chelitalk.api.historydetail.HistoryDetailResponseDto;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface CheckService {
    @POST("chat/check")
    Call<String> chatCheck(@Body Check dto);

    @GET("chat/recent")
    Call<List<HistoryDetailResponseDto>> chatRecent(@Query("memberEmail")String memberEmail);
}
