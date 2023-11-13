package com.example.moija.map;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class SearchResults {
    //주소 검색시엔 결과를 Place로 받는다
    private List<Place> documents;

    public List<Place> getPlaces(){
        return documents;
    }

    //좌표를 사용하여 주소를 받는 검색시엔 LoctoAddResult를 별개로 사용해야함(Json 구조가 다름)
    public class LoctoAddResult {
        @SerializedName("documents")
        private ArrayList<Document> documents;
        public ArrayList<Document> getDocuments() {
            return documents;
        }
        public class Document {

            @SerializedName("address")
            private Address address;

            public Address getAddress(){
                return address;
            }
            @SerializedName("road_address")
            private RoadAddress road_address;

            public RoadAddress getRoad_address() {
                return road_address;
            }
        }
        public class RoadAddress {
            @SerializedName("address_name")
            private String address_name;

            @SerializedName("building_name")
            private String building_name;
            public String getAddress_name() {
                return address_name;
            }
            public String getBuilding_name() {
                return building_name;
            }
        }
        public class Address{
            @SerializedName("address_name")
            private String address_name;

            public String getAddress_name(){
                return address_name;
            }
        }
    }
}
