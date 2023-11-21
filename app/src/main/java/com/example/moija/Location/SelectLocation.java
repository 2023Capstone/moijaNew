package com.example.moija.Location;

public class SelectLocation {

    // 출발지의 위도와 경도
    private double startLatitude;
    private double startLongitude;

    // 목적지의 위도와 경도
    private double goalLatitude;
    private double goalLongitude;

    // 기본 생성자
    public SelectLocation() {
    }

    // 모든 위치 정보를 매개변수로 받는 생성자
    public SelectLocation(double startLat, double startLng, double goalLat, double goalLng) {
        this.startLatitude = startLat;
        this.startLongitude = startLng;
        this.goalLatitude = goalLat;
        this.goalLongitude = goalLng;
    }

    // 출발지 위도의 getter와 setter
    public double getStartLatitude() {
        return startLatitude;
    }

    public void setStartLatitude(double startLatitude) {
        this.startLatitude = startLatitude;
    }

    // 출발지 경도의 getter와 setter
    public double getStartLongitude() {
        return startLongitude;
    }

    public void setStartLongitude(double startLongitude) {
        this.startLongitude = startLongitude;
    }

    // 목적지 위도의 getter와 setter
    public double getGoalLatitude() {
        return goalLatitude;
    }

    public void setGoalLatitude(double goalLatitude) {
        this.goalLatitude = goalLatitude;
    }

    // 목적지 경도의 getter와 setter
    public double getGoalLongitude() {
        return goalLongitude;
    }

    public void setGoalLongitude(double goalLongitude) {
        this.goalLongitude = goalLongitude;
    }
}