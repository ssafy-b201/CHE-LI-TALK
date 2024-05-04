package com.ssafy.chelitalk.activity.common;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ssafy.chelitalk.api.history.DateTypeAdapter;

import java.time.LocalDate;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class NetworkClient {

    public static Retrofit retrofit;
    private static final String BASE_URL = "https://k10b201.p.ssafy.io/cherry/api/";

    public static Retrofit getRetrofitClient(Context context){
        if(retrofit == null){
            Gson gson = new GsonBuilder()
                    .setLenient()
                    .registerTypeAdapter(Date.class, new DateTypeAdapter())
//                    .registerTypeAdapter(Date.class, new com.ssafy.chelitalk.api.attend.DateTypeAdapter())
                    .create();

            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient httpClient = new OkHttpClient.Builder().connectTimeout(1, TimeUnit.MINUTES)
                    .readTimeout(1, TimeUnit.MINUTES)
                    .writeTimeout(1, TimeUnit.MINUTES)
                    .addInterceptor(loggingInterceptor)
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(httpClient)
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .addConverterFactory(new NullOnEmptyConverterFactory())
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
        }
        return retrofit;
    }
}
