package com.ssafy.chelitalk.api.historydetail;

import java.time.LocalDateTime;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface HistoryDetailService {

    @GET("chat/list/detail")
    Call<List<HistoryDetailResponseDto>> historyDetail(
            @Query("memberEmail") String memberEmail,
            @Query("createdAt") LocalDateTime createdAt);
}
