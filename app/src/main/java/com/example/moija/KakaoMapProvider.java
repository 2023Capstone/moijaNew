package com.example.moija;

import com.kakao.vectormap.KakaoMap;
import com.kakao.vectormap.route.RouteLineLayer;

public class KakaoMapProvider implements MapProvider {
    private KakaoMap kakaoMap;

    public KakaoMapProvider(KakaoMap kakaoMap) {
        this.kakaoMap = kakaoMap;
    }

    @Override
    public RouteLineLayer getRouteLineLayer() {
        return kakaoMap.getRouteLineManager().getLayer();
    }

    // 다른 필요한 메서드들을 구현
}