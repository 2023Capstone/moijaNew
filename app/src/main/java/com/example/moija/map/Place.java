package com.example.moija.map;

public class Place {
        private String place_name; //장소명

        private String address_name; //주소명
        private double x; // 경도
        private double y; // 위도

        public String getPlaceName() {
            return place_name;
        }
        public void setPlaceName(String name){this.place_name=name;}
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
