package com.example.moija.map;

import android.graphics.Color;

import com.example.moija.R;
import com.kakao.vectormap.KakaoMap;
import com.kakao.vectormap.LatLng;
import com.kakao.vectormap.route.RouteLine;
import com.kakao.vectormap.route.RouteLineLayer;
import com.kakao.vectormap.route.RouteLineManager;
import com.kakao.vectormap.route.RouteLineOptions;
import com.kakao.vectormap.route.RouteLinePattern;
import com.kakao.vectormap.route.RouteLineSegment;
import com.kakao.vectormap.route.RouteLineStyle;
import com.kakao.vectormap.route.RouteLineStyles;
import com.kakao.vectormap.route.RouteLineStylesSet;

import java.util.ArrayList;
import java.util.Arrays;


//길을 그려주는 클래스
public class RouteDrawer {

    RouteLineLayer routeLineLayer;
    RouteLineStylesSet stylesSet;
    RouteLineSegment segment;
    KakaoMap kakaoMap;
    RouteLineManager routeLineManager;
     public ArrayList<RouteLine> GoalRoutes=new ArrayList<>();
    public RouteDrawer(KakaoMap kakaoMap) {
        this.kakaoMap=kakaoMap;
    }

    //대중교통 길 그리기(길 신경쓰지 않고 직선으로 그림)
    //traffictype은 오디세이 api의 결과값중 하나로 대중교통의 종류를 의미
    public void draw(double startx,double starty,double endx,double endy,int traffictype){
        routeLineManager=kakaoMap.getRouteLineManager();
        routeLineLayer=routeLineManager.getLayer();
        //선의 색깔을 결정하는 변수
        RouteLineStyles styles1 = RouteLineStyles.from(RouteLineStyle.from(16, Color.YELLOW));
        RouteLineStyles styles2 = RouteLineStyles.from(RouteLineStyle.from(16, Color.RED));
        RouteLineStyles styles3 = RouteLineStyles.from(RouteLineStyle.from(16, Color.BLUE));
        RouteLineStyles styles4 = RouteLineStyles.from(RouteLineStyle.from(16, Color.GREEN));
        stylesSet = RouteLineStylesSet.from(styles1,styles2,styles3,styles4);

        //시작점의 위도,경도 저장
        LatLng startPoint = LatLng.from(starty, startx);
        //도착점의 위도,경도 저장
        LatLng endPoint = LatLng.from(endy, endx);
        //기차는 노란색
        if(traffictype==4)
        {
            segment = RouteLineSegment.from(Arrays.asList(startPoint, endPoint))
                    .setStyles(stylesSet.getStyles(0));
        }
        //고속버스는 빨간색
        else if(traffictype==5)
        {
            segment = RouteLineSegment.from(Arrays.asList(startPoint, endPoint))
                    .setStyles(stylesSet.getStyles(1));
        }
        //시외버스는 파란색
        else if(traffictype==6)
        {
            segment = RouteLineSegment.from(Arrays.asList(startPoint, endPoint))
                    .setStyles(stylesSet.getStyles(2));
        }
        //항공기는 초록색
        else if(traffictype==7)
        {
            segment = RouteLineSegment.from(Arrays.asList(startPoint, endPoint))
                    .setStyles(stylesSet.getStyles(3));
        }
        //경로선을 그림
        RouteLineOptions routeoptions = RouteLineOptions.from(segment)
                .setStylesSet(stylesSet);
        RouteLine line = routeLineLayer.addRouteLine(routeoptions);
        //GoalRoutes는 그렸던 경로선들을 저장해줌 (나중에 다른 목적지 그릴때 지우기 위함
        GoalRoutes.add(line);
    }
    //개인 자동차 경로(길에 맞추어서) 그려주는 메서드
    public void drawRoute(ArrayList<Double> vertexes) {
        //vertexes 는 위도,경도를 일차원 배열로 쭉 나열한 것이기때문에 나름의 가공 방식이 필요
        //즉 시작점의 위도,경도 목적지의 위도,경도가 각각 필요하므로 vertexes 안의 정보가 4개 이상이면 그릴 수 있음
        if (vertexes != null && vertexes.size() >= 4) {
            stylesSet = RouteLineStylesSet.from("blueStyles",
                    RouteLineStyles.from(RouteLineStyle.from(16, Color.RED)));
            //선이 그려질 레이어

            
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
