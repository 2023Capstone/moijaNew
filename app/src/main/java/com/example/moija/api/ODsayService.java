package com.example.moija.api;

import com.example.moija.data.OdsayData;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.Call;
import retrofit2.http.Query;

// Retrofit 인터페이스 정의
public interface ODsayService {
    @GET("/v1/api/searchPubTransPathT.json")
    Call<OdsayData> searchPublicTransitPath(
            @Query("apiKey") String apiKey,
            @Query("SX") double startX,
            @Query("SY") double startY,
            @Query("EX") double endX,
            @Query("EY") double endY
    );
}