package com.example.moija;

import android.app.Application;


import com.kakao.sdk.common.KakaoSdk;


public class KakoApplication extends Application {


    @Override
    public void onCreate() {

        super.onCreate();

        KakaoSdk.init(this, "44bdf179ef832c51f0a780b3f0154b53");


    }



}


