package com.example.moija.map;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Place {
        private String place_name; //장소명

        private String address_name; //주소명
        private double x; // 경도
        private double y; // 위도

        public String getPlaceName() {
            return place_name;
        }
        public String getAddressName() {
            return address_name;
        }
        public double getX() {
            return x;
        }
        public double getY() {
            return y;
        }

}
