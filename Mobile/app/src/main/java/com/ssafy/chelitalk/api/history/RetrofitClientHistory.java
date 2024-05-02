package com.ssafy.chelitalk.api.history;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClientHistory {
    private static final String BASE_URL = "http://localhost:8060/cherry/api/";
    public static HistoryService createService(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit.create(HistoryService.class);
    }
}
