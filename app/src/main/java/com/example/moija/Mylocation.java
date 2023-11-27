package com.example.moija;

import android.location.Location;

//현재위치 Lastlocation과 
//목적지 selectedPlace를 담고있는데
//나눠서 작성한 이유는 액티비티가 바뀔 때마다 변수가 초기화 되는 불편함때문에 따로 클래스를 만들어
//값을 저장해놓고 액티비티가 바뀌어도 다시 불러올 수 있도록 하기 위함
public class Mylocation{
    public static Location Lastlocation;
    public static MapFragment.Place selectedPlace;
    public static MapFragment.Place StartPlace;
}