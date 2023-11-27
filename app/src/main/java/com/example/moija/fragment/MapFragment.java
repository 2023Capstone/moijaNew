package com.example.moija.fragment;

import com.example.moija.R;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.moija.data.PathInfo;
import com.example.moija.map.Mylocation;
import com.example.moija.map.RouteDrawer;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.kakao.vectormap.KakaoMap;
import com.kakao.vectormap.KakaoMapReadyCallback;
import com.kakao.vectormap.LatLng;
import com.kakao.vectormap.MapLifeCycleCallback;
import com.kakao.vectormap.MapView;
import com.kakao.vectormap.camera.CameraUpdate;
import com.kakao.vectormap.camera.CameraUpdateFactory;
import com.kakao.vectormap.label.Label;
import com.kakao.vectormap.label.LabelLayer;
import com.kakao.vectormap.label.LabelOptions;
import com.kakao.vectormap.label.LabelStyle;
import com.kakao.vectormap.label.LabelStyles;

public class MapFragment extends Fragment {

    FloatingActionButton RemoveMarkerbtn;
    //kakaoMap을 OnMapReady가 아닌 다른곳에서도 호출할 수 있게 선언
    public static Context context;
    //후에 추가할 실시간 위치를 끄기 기능을 키고 스스로 검색하여 출발지를 정할때 사용할 변수
    public boolean getLastLocation =true;
    //내 위치를 나타내는 마커(라벨)
    private Label MyLabel;
    //처음에만 내 위치로 카메라가 이동되도록 하기 위한 변수
    private boolean MovecameraFirst=false;
    //시작 위치를 나타내는 마커
    public Label startLabel;
    //도착 위치를 나타내는 마커
    public Label goalLabel;


    public PathInfo DrawingPath;
    //길 그려주는 RouteDrawer 클래스 선언
    public RouteDrawer routeDrawer;
    LabelStyles styles;
    LabelOptions options;
    LabelLayer layer;

    KakaoMap thiskakaoMap;
    @Nullable
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        ViewGroup rootview = (ViewGroup)inflater.inflate(R.layout.activity_map_fragment,container,false);
        context=requireContext();
        MapView mapView = rootview.findViewById(R.id.map_view);
        RemoveMarkerbtn=rootview.findViewById(R.id.removemarkerbtn);
        RemoveMarkerbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(getLastLocation && MyLabel!=null) {
                        //마커 삭제
                        getLastLocation = false;
                        layer.remove(MyLabel);
                }
                else if (!getLastLocation){
                    getLastLocation=true;
                    UpdateMarker();
                }
            }
        });
        mapView.start(new MapLifeCycleCallback() {
            @Override
            public void onMapDestroy() {
                // 지도 API 가 정상적으로 종료될 때 호출됨
            }

            @Override
            public void onMapError(Exception error) {
                // 인증 실패 및 지도 사용 중 에러가 발생할 때 호출됨
            }
        }, new KakaoMapReadyCallback() {
            @Override
            //지도 API가 준비되었을때
            public void onMapReady(KakaoMap kakaoMap) {
                thiskakaoMap=kakaoMap;
                routeDrawer=new RouteDrawer(kakaoMap);
                Draw();
                if(Mylocation.Lastlocation!=null) {
                    UpdateMarker();
                    if(MovecameraFirst==false) {
                        //카메라를 현재 위치로 바꿈
                        CameraUpdate cameraUpdate = CameraUpdateFactory.newCenterPosition(LatLng.from(Mylocation.Lastlocation.getLatitude(), Mylocation.Lastlocation.getLongitude()));
                        kakaoMap.moveCamera(cameraUpdate);
                        MovecameraFirst=true;
                    }
                }
                final LocationListener gpsLocationListener = new LocationListener() {
                    //장소가 바뀌었으면
                    public void onLocationChanged(Location location) {
                        if(getLastLocation)
                        {
                            Mylocation.Lastlocation=location;
                            if(Mylocation.Lastlocation!=null) {
                                //마커 스타일 설정
                                UpdateMarker();
                            }
                        }
                        //최초 내위치 카메라 이동
                        if(MovecameraFirst==false)
                        {
                            CameraUpdate cameraUpdate = CameraUpdateFactory.newCenterPosition(LatLng.from(Mylocation.Lastlocation.getLatitude(), Mylocation.Lastlocation.getLongitude()));
                            kakaoMap.moveCamera(cameraUpdate);
                            MovecameraFirst=true;
                        }

                    }
                    public void onStatusChanged(String provider, int status, Bundle extras) {
                    }
                    public void onProviderEnabled(String provider) {
                    }

                    public void onProviderDisabled(String provider) {
                    }

                };
                //실시간 위치를 제공하기 위해 권한을 물어봄
                final LocationManager map_lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
                if ( Build.VERSION.SDK_INT >= 23 &&
                        ContextCompat.checkSelfPermission( getActivity().getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED ) {
                    ActivityCompat.requestPermissions( getActivity(), new String[] {  android.Manifest.permission.ACCESS_FINE_LOCATION  },
                            0 );
                }
                else{
                    map_lm.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                            1000,
                            1,
                            gpsLocationListener);
                    map_lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                            1000,
                            1,
                            gpsLocationListener);
                }
            }
        });
        return rootview;
    }
    public void AddStartMarker(){
        styles = thiskakaoMap.getLabelManager()
                .addLabelStyles(LabelStyles.from(LabelStyle.from(R.drawable.start).setApplyDpScale(false)));
        //마커 위치 설정
        options = LabelOptions.from(LatLng.from(Mylocation.StartPlace.getY(), Mylocation.StartPlace.getX()))
                .setStyles(styles);
        //마커를 그릴 레이어
        layer = thiskakaoMap.getLabelManager().getLayer();
        //그렸던 마커 없애고
        if (startLabel != null) {

            layer.remove(startLabel);
        }
        //마커를 그리고 MyLabel에 집어넣음
        startLabel = layer.addLabel(options);
        //마커 이미지 크기 조절
        startLabel.scaleTo(0.15f, 0.15f);

    }
    public void AddGoalMarker(){
        styles = thiskakaoMap.getLabelManager()
                .addLabelStyles(LabelStyles.from(LabelStyle.from(R.drawable.goal).setApplyDpScale(false)));
        //마커 위치 설정
        options = LabelOptions.from(LatLng.from(Mylocation.GoalPlace.getY(), Mylocation.GoalPlace.getX()))
                .setStyles(styles);
        //마커를 그릴 레이어
        layer = thiskakaoMap.getLabelManager().getLayer();
        //그렸던 마커 없애고
        if (goalLabel != null) {

            layer.remove(goalLabel);
        }
        //마커를 그리고 MyLabel에 집어넣음
        goalLabel = layer.addLabel(options);
        //마커 이미지 크기 조절
        goalLabel.scaleTo(0.15f, 0.15f);
    }
    public void UpdateMarker(){
        styles = thiskakaoMap.getLabelManager()
                .addLabelStyles(LabelStyles.from(LabelStyle.from(R.drawable.redmarker).setApplyDpScale(false)));
        //마커 위치 설정
        options = LabelOptions.from(LatLng.from(Mylocation.Lastlocation.getLatitude(), Mylocation.Lastlocation.getLongitude()))
                .setStyles(styles);
        //마커를 그릴 레이어
        layer = thiskakaoMap.getLabelManager().getLayer();
        //그렸던 마커 없애고
        if (MyLabel != null) {

            layer.remove(MyLabel);
        }
        //마커를 그리고 MyLabel에 집어넣음
        MyLabel = layer.addLabel(options);
        //마커 이미지 크기 조절
        MyLabel.scaleTo(0.15f, 0.15f);

    }
    //시작점 좌표,도착점 좌표와 대중교통 종류에 따라 그리기 예시
    //오디세이 결과값에 따라 그리도록 수정 필요함
    public void Draw(){
        for(int i=0;i<DrawingPath.getBusNos().size();i++)
        {
            if(routeDrawer!=null) {
                routeDrawer.draw(DrawingPath.getstartx().get(i), DrawingPath.getstarty().get(i), DrawingPath.getendx().get(i), DrawingPath.getendy().get(i), DrawingPath.getTrafficType().get(i));
            }
        }
        AddGoalMarker();
        AddStartMarker();
    }
}