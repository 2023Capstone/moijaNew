package com.example.moija.map;

import com.kakao.vectormap.KakaoMap;
import com.kakao.vectormap.route.RouteLineLayer;

public class KakaoMapProvider implements MapProvider {
    public KakaoMap kakaoMap;

    public KakaoMapProvider(KakaoMap kakaoMap) {
        this.kakaoMap = kakaoMap;
    }

    @Override
    public RouteLineLayer getRouteLineLayer() {
        return kakaoMap.getRouteLineManager().getLayer();
    }

    // 다른 필요한 메서드들을 구현
}