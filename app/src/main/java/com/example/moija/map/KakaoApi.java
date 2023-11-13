package com.example.moija.map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

public interface KakaoApi {
    //위치 검색(내 위치에서 가까운 순서대로 출력)
    @GET("/v2/local/search/keyword.json")
    Call<SearchResults> searchPlacesWithMyLocation(
            @Header("Authorization") String apiKey,
            @Query("query") String query,
            @Query("x") double x,
            @Query("y") double y
    );
    //위치 검색(가장 가까운 위치에 있는 건물 검색)
    @GET("/v2/local/search/keyword.json")
    Call<SearchResults> searchNearPlace(
            @Header("Authorization") String apiKey,
            @Query("query") String query,
            @Query("x") double x,
            @Query("y") double y,
            @Query("radius") int radius //검색할 거리
    );
    //현재위치 상관없이 검색(내 위치가 받아지지 않았을때 사용)
    @GET("/v2/local/search/keyword.json")
    Call<SearchResults> searchPlaces(
            @Header("Authorization") String apiKey,
            @Query("query") String query
    );
    //현재 좌표를 통해 주소명을 받아옴
    @GET("/v2/local/geo/coord2address.json")
    Call<SearchResults.LoctoAddResult> getAddressWithLocation(
            @Header("Authorization") String apiKey,
            @Query("x") double x,
            @Query("y") double y
    );
}
