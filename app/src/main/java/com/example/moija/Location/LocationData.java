package com.example.moija.Location;

public class LocationData {
    private double longitude = 0; // 위도
    private double latitude = 0; // 경도
    private double altitude = 0; // 고도

    // Getter method for longitude
    public double getLongitude() {
        return longitude;
    }

    // Setter method for longitude
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    // Getter method for latitude
    public double getLatitude() {
        return latitude;
    }

    // Setter method for latitude
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    // Getter method for altitude
    public double getAltitude() {
        return altitude;
    }

    // Setter method for altitude
    public void setAltitude(double altitude) {
        this.altitude = altitude;
    }
}
