package com.example.moija.map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

public interface KakaoApi {
    @GET("/v2/local/search/keyword.json")
    Call<SearchResults> searchPlacesWithMyLocation(
            @Header("Authorization") String apiKey,
            @Query("query") String query,
            @Query("x") double x,
            @Query("y") double y
    );
    @GET("/v2/local/search/keyword.json")
    Call<SearchResults> searchNearPlace(
            @Header("Authorization") String apiKey,
            @Query("query") String query,
            @Query("x") double x,
            @Query("y") double y,
            @Query("radius") int radius
    );
    @GET("/v2/local/search/keyword.json")
    Call<SearchResults> searchPlaces(
            @Header("Authorization") String apiKey,
            @Query("query") String query
    );
    @GET("/v2/local/geo/coord2address.json")
    Call<SearchResults.LoctoAddResult> getAddressWithLocation(
            @Header("Authorization") String apiKey,
            @Query("x") double x,
            @Query("y") double y
    );
}
