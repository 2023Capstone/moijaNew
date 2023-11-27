package com.example.moija;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
//카카오 모빌리티api에서 받아오는 정보들을 저장하는 객체
public class KakaoMobilityclasses {

    public class Bound{
        public double min_x;
        public double min_y;
        public double max_x;
        public double max_y;
    }

    public class Destination{
        public String name;
        public double x;
        public double y;
    }

    public class Fare{
        public int taxi;
        public int toll;
    }

    public class Guide{
        public String name;
        public double x;
        public double y;
        public int distance;
        public int duration;
        public int type;
        public String guidance;
        public int road_index;
    }

    public class Origin{
        public String name;
        public double x;
        public double y;
    }

    public class Road{
        public String name;
        public int distance;
        public int duration;
        public int traffic_speed;
        public int traffic_state;
        @SerializedName("vertexes")
        public ArrayList<Double> vertexes;

        public ArrayList<Double> getVertexes() {
            return vertexes;
        }
    }

    public class Root{
        public String trans_id;
        @SerializedName("routes")
        public ArrayList<Route> routes;

        public ArrayList<Route> getRoutes() {
            return routes;
        }
    }

    public class Route{
        public int result_code;
        public String result_msg;
        public Summary summary;
        @SerializedName("sections")
        public ArrayList<Section> sections;

        public ArrayList<Section> getSections() {
            return sections;
        }
    }

    public class Section{
        public int distance;
        public int duration;
        public Bound bound;
        @SerializedName("roads")
        public ArrayList<Road> roads;

        public ArrayList<Road> getRoads() {
            return roads;
        }

        public ArrayList<Guide> guides;
    }

    public class Summary{
        public Origin origin;
        public Destination destination;
        public ArrayList<Object> waypoints;
        public String priority;
        public Bound bound;
        public Fare fare;
        public int distance;
        public int duration;
    }


}
