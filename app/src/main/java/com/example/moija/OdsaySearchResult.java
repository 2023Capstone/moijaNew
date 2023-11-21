package com.example.moija;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class OdsaySearchResult {
    @SerializedName("result")
    private Result result;
    public class Result{
        @SerializedName("searchType")
        private int searchType;

        @SerializedName("busCount")
        private int busCount;

        @SerializedName("trainCount")
        private int trainCount;

        @SerializedName("airCount")
        private int airCount;

        @SerializedName("mixedCount")
        private int mixedCount;

        @SerializedName("path")
        private List<Path> path;
    }

    public class Path {
        @SerializedName("pathType")
        private int pathType;

        @SerializedName("info")
        private Info info;

        @SerializedName("subPath")
        private List<SubPath> subPath;

        // getters and setters
    }
    public class Info {
        @SerializedName("totalTime")
        private int totalTime;

        @SerializedName("totalPayment")
        private int totalPayment;

        @SerializedName("transitCount")
        private int transitCount;

        @SerializedName("firstStartStation")
        private String firstStartStation;

        @SerializedName("lastEndStation")
        private String lastEndStation;

        @SerializedName("totalDistance")
        private int totalDistance;

        // getters and setters
    }

    public class SubPath {
        @SerializedName("trafficType")
        private int trafficType;

        @SerializedName("distance")
        private int distance;

        @SerializedName("sectionTime")
        private int sectionTime;

        @SerializedName("payment")
        private int payment;

        @SerializedName("startName")
        private String startName;

        @SerializedName("endName")
        private String endName;

        @SerializedName("startID")
        private int startID;

        @SerializedName("endID")
        private int endID;

        @SerializedName("startCityCode")
        private int startCityCode;

        @SerializedName("endCityCode")
        private int endCityCode;

        @SerializedName("startX")
        private double startX;

        @SerializedName("startY")
        private double startY;

        @SerializedName("endX")
        private double endX;

        @SerializedName("endY")
        private double endY;

        @SerializedName("trainType")
        private Integer trainType;

        @SerializedName("trainSpSeatYn")
        private String trainSpSeatYn;

        @SerializedName("trainSpSeatPayment")
        private Integer trainSpSeatPayment;

        // getters and setters
    }

    // getters and setters
}


