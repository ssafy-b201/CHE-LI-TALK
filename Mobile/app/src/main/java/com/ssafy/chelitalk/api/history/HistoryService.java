package com.ssafy.chelitalk.api.history;


import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface HistoryService{

    @GET("/chat/list")
    Call<List<History>> historyList(@Query("memberEmail") String memberEmail);

}
