package com.example.moija;

import android.graphics.Color;

import com.kakao.vectormap.LatLng;
import com.kakao.vectormap.route.RouteLine;
import com.kakao.vectormap.route.RouteLineLayer;
import com.kakao.vectormap.route.RouteLineOptions;
import com.kakao.vectormap.route.RouteLineSegment;
import com.kakao.vectormap.route.RouteLineStyle;
import com.kakao.vectormap.route.RouteLineStyles;
import com.kakao.vectormap.route.RouteLineStylesSet;

import java.util.ArrayList;
import java.util.Arrays;


//길을 그려주는 클래스
public class RouteDrawer {

    private MapProvider mapProvider=new MapProvider() {
        @Override
        public RouteLineLayer getRouteLineLayer() {
            return routeLineLayer;
        }
    };
    RouteLineLayer routeLineLayer;

    ArrayList<RouteLine> GoalRoutes=new ArrayList<>();
    public RouteDrawer(MapProvider mapProvider) {
        this.mapProvider = mapProvider;
    }

    //경로를 그려주는 메서드
    public void drawRoute(ArrayList<Double> vertexes) {
        //vertexes 는 위도,경도를 일차원 배열로 쭉 나열한 것이기때문에 나름의 가공 방식이 필요
        //즉 시작점의 위도,경도 목적지의 위도,경도가 각각 필요하므로 vertexes 안의 정보가 4개 이상이면 그릴 수 있음
        if (vertexes != null && vertexes.size() >= 4) {
            RouteLineStylesSet stylesSet = RouteLineStylesSet.from("blueStyles",
                    RouteLineStyles.from(RouteLineStyle.from(16, Color.RED)));
            //선이 그려질 레이어
            routeLineLayer = mapProvider.getRouteLineLayer();
            
            if (routeLineLayer != null) {
                for (int i = 0; i < vertexes.size() - 3; i += 2) {
                    //시작점의 위도,경도를 경도, 위도 순으로 바꾸고 저장
                    LatLng startPoint = LatLng.from(vertexes.get(i + 1), vertexes.get(i));
                    //도착점의 위도,경도를 경도, 위도 순으로 바꾼 뒤에 저장
                    LatLng endPoint = LatLng.from(vertexes.get(i + 3), vertexes.get(i + 2));
                    //경로선의 시작점과 끝점을 정의
                    RouteLineSegment segment = RouteLineSegment.from(Arrays.asList(startPoint, endPoint))
                            .setStyles(stylesSet.getStyles(0));
                    //경로선을 그림
                    RouteLineOptions routeoptions = RouteLineOptions.from(segment)
                            .setStylesSet(stylesSet);

                    RouteLine line = routeLineLayer.addRouteLine(routeoptions);
                    //GoalRoutes는 그렸던 경로선들을 저장해줌 (나중에 다른 목적지 그릴때 지우기 위함
                    GoalRoutes.add(line);
                }
            }
        }
    }
    public void clearRouteLines() {
        //GoalRoutes를 비워 그렸던 경로선들을 모두 삭제
        if(routeLineLayer!=null){
            for (RouteLine line : GoalRoutes) {
                routeLineLayer.remove(line);
            }
            GoalRoutes.clear();
        }

    }
}
