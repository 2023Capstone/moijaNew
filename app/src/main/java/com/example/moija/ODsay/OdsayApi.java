package com.example.moija.ODsay;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface OdsayApi {
    @GET("/v1/api/searchPubTransPathT.json")
    Call<OdsaySearchResult> FindRoute(
            @Query("apiKey") String apiKey,
            @Query("lang") int lang,
            @Query("SX") double sx,
            @Query("SY") double sy,
            @Query("EX") double ex,
            @Query("EY") double ey
    );
}
