package com.example.moija.fragment;

import com.example.moija.R;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.moija.busPointGPS;
import com.example.moija.data.PathInfo;
import com.example.moija.map.Mylocation;
import com.example.moija.map.RouteDrawer;
import com.example.moija.schedule.CityBus;
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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MapFragment extends Fragment {
    FloatingActionButton RemoveMarkerbtn;
    //kakaoMap을 OnMapReady가 아닌 다른곳에서도 호출할 수 있게 선언
    public static Context context;
    //후에 추가할 실시간 위치를 끄기 기능을 키고 스스로 검색하여 출발지를 정할때 사용할 변수
    public boolean getLastLocation =true;
    //내 위치를 나타내는 마커(라벨)
    private Label MyLabel;
    //시작 위치를 나타내는 마커
    public Label startLabel;
    //도착 위치를 나타내는 마커
    public Label goalLabel;
    public LinearLayout busInfoLayout;
    public List<String> BusNo=new ArrayList<>();
    public List<String> BusLocalBlID=new ArrayList<>();

    public List<Integer> BusID=new ArrayList<>();
    public List<Integer> BusCityCode=new ArrayList<>();
    public List<Integer> BusOrder=new ArrayList<>();
    public PathInfo Selectedpath;
    //길 그려주는 RouteDrawer 클래스 선언
    public RouteDrawer routeDrawer;
    LabelStyles styles;
    LabelOptions options;
    LabelLayer layer;
    List<Pair<String, String>> busList=new ArrayList<>();
    KakaoMap thiskakaoMap;

    public static class BusData implements Serializable {
        private List<Integer> integerList;
        private List<String> busLocalBlID;
        private List<Integer> busID;
        public BusData(List<Integer> integerList,List<String> busLocalBlID,List<Integer> busID) {
            this.integerList = integerList;
            this.busLocalBlID = busLocalBlID;
            this.busID=busID;
        }

        public List<Integer> getIntegerList() {
            return integerList;
        }
        public List<String> getBusLocalBlID() {
            return busLocalBlID;
        }
        public List<Integer> getBusID(){return busID;}
    }
    @Nullable
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootview = (ViewGroup)inflater.inflate(R.layout.activity_map_fragment,container,false);
        context=requireContext();
        MapView mapView = rootview.findViewById(R.id.map_view);
        busInfoLayout = rootview.findViewById(R.id.bus_info_layout);
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

        // busInfoLayout에 OnClickListener 설정
        busInfoLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BusData busData = new BusData(BusCityCode,BusLocalBlID,BusID);
                // 인텐트 생성 및 액티비티 시작
                Intent intent2 = new Intent(getActivity(), CityBus.class);
                intent2.putExtra("key", busData);
                startActivity(intent2);
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
    // 버스 정보를 추가하는 메서
    //시작점 좌표,도착점 좌표와 대중교통 종류에 따라 그리기 예시
    //오디세이 결과값에 따라 그리도록 수정 필요함
    public void Draw(){

        int blidcitycodeindex=0;
        int busidindex=0;
        busList.clear();
        BusNo.clear();
        BusOrder.clear();
        BusCityCode.clear();
        BusLocalBlID.clear();
        BusID.clear();
        Log.d("busids",Selectedpath.getBusIDs().toString());
        for(int i=0; i<Selectedpath.getBusNos().size(); i++)
        {
            if(!Selectedpath.getBusNos().get(i).contains("도보")){
                BusNo.add(Selectedpath.getBusNos().get(i).get(0));
            }
        }
        for(int i=0; i<Selectedpath.getTrafficType().size(); i++)
        {
            if(Selectedpath.getTrafficType().get(i).equals(2) || Selectedpath.getTrafficType().get(i).equals(6)){
                BusOrder.add(Selectedpath.getTrafficType().get(i));
            }
        }
        for(int i=0; i<BusOrder.size();i++)
        {
            if(BusOrder.get(i)==2){
                BusLocalBlID.add(Selectedpath.getBusLocalBlIDs().get(blidcitycodeindex).get(0));
                BusCityCode.add(Selectedpath.getBusCityCodes().get(blidcitycodeindex).get(0));
                BusID.add(Selectedpath.getBusIDs().get(busidindex).get(0));
                blidcitycodeindex++;
                busidindex++;
            }else if(BusOrder.get(i)==6){
                BusLocalBlID.add("시외버스");
                BusID.add(0);
                BusCityCode.add(0);
                busidindex++;
            }
        }
        for(int i=0; i<BusOrder.size();i++){
            if(BusOrder.get(i)==2){
                busList.add(new Pair<>("city",BusNo.get(i).toString()));
                Log.d("mylog", BusOrder.get(i).toString());
            }
            else if(BusOrder.get(i)==6){
                busList.add(new Pair<>("intercity","시외버스"));
            }
        }

        for (int i = 0; i < busList.size(); i++) {
            Pair<String, String> bus = busList.get(i);
            // ">" 기호를 추가할지 결정 (마지막 버스 정보가 아닌 경우에만 추가)
            boolean addArrow = i < busList.size() - 1;
            if ("city".equals(bus.first)) {
                // 도시 버스일 경우의 처리
                addBusInfo(R.drawable.colorful_city_bus, bus.second, addArrow);
            } else if ("intercity".equals(bus.first)) {
                // 시외 버스일 경우의 처리
                addBusInfo(R.drawable.intercity_bus, bus.second, addArrow);
            }
        }
        Log.d("youlog",BusOrder.toString());
        Log.d("youlog",BusNo.toString());
        Log.d("youlog",busList.toString());
        Log.d("youlog",BusLocalBlID.toString());
        Log.d("youlog",BusID.toString());
        Log.d("youlog",BusCityCode.toString());
        for(int i = 0; i< Selectedpath.getBusNos().size(); i++)
        {
            if(routeDrawer!=null) {
                routeDrawer.draw(Selectedpath.getstartx().get(i), Selectedpath.getstarty().get(i), Selectedpath.getendx().get(i), Selectedpath.getendy().get(i), Selectedpath.getTrafficType().get(i));
            }
        }
        AddGoalMarker();
        AddStartMarker();
        //카메라를 현재 위치로 바꿈
        double middlelatitude=(Mylocation.StartPlace.getY()+Mylocation.GoalPlace.getY())/2;
        double middlelongitude=(Mylocation.StartPlace.getX()+Mylocation.GoalPlace.getX())/2;
        CameraUpdate cameraUpdate = CameraUpdateFactory.newCenterPosition(LatLng.from(middlelatitude, middlelongitude),5);
        thiskakaoMap.moveCamera(cameraUpdate);
    }
    // 버스 정보를 추가하는 메서드
    private void addBusInfo(int imageResId, String busNumber, boolean addArrow) {
        if (busInfoLayout != null) {
            // 각 버스 정보를 담을 새로운 수직 LinearLayout 생성
            LinearLayout busItemLayout = new LinearLayout(getContext());
            busItemLayout.setOrientation(LinearLayout.VERTICAL);
            busItemLayout.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            ));
            busItemLayout.setGravity(Gravity.CENTER_HORIZONTAL);

            // 버스 이미지 뷰 생성 및 설정
            ImageView imageView = new ImageView(getContext());
            imageView.setImageResource(imageResId);
            imageView.setLayoutParams(new LinearLayout.LayoutParams(
                    getResources().getDimensionPixelSize(R.dimen.bus_image_width),
                    getResources().getDimensionPixelSize(R.dimen.bus_image_height)
            ));
            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            busItemLayout.addView(imageView);

            // 버스 번호 텍스트 뷰 생성 및 설정
            TextView textView = new TextView(getContext());
            textView.setText(busNumber);
            textView.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            ));
            textView.setGravity(Gravity.CENTER);
            busItemLayout.addView(textView);

            // 생성된 busItemLayout을 부모 busInfoLayout에 추가
            busInfoLayout.addView(busItemLayout);

            // ">" 기호를 추가해야 하는 경우
            if (addArrow) {
                TextView arrowTextView = new TextView(getContext());
                arrowTextView.setText(" > ");
                arrowTextView.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                ));
                arrowTextView.setGravity(Gravity.CENTER);
                busInfoLayout.addView(arrowTextView);
            }
        } else {
            Log.e("MapFragment", "busInfoLayout is null");
        }
    }
}