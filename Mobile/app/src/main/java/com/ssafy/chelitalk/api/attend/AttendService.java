package com.ssafy.chelitalk.api.attend;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface AttendService {

    @GET("/attend/list")
    Call<List<Attend>> attendList(@Query("memberEmail") String memberEmail);

}
