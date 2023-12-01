package com.example.moija.fragment;

import com.example.moija.ApiExplorer;
import com.example.moija.MainPage;
import com.example.moija.R;
import com.example.moija.busPointGPS;
import com.example.moija.data.PathInfo;

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

import com.example.moija.data.PathInfo;
import com.example.moija.map.Mylocation;
import com.example.moija.map.RouteDrawer;
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

import java.util.ArrayList;
import java.util.List;

public class MapFragment extends Fragment {

    //kakaoMap을 OnMapReady가 아닌 다른곳에서도 호출할 수 있게 선언
    public static Context context;
    //후에 추가할 실시간 위치를 끄기 기능을 키고 스스로 검색하여 출발지를 정할때 사용할 변수
    public boolean searchingwithMine=false;
    //내 위치를 나타내는 마커(라벨)
    private Label MyLabel;
    //처음에만 내 위치로 카메라가 이동되도록 하기 위한 변수
    private boolean MovecameraFirst=false;
    //시작 위치를 나타내는 마커
    public Label startLabel;
    //도착 위치를 나타내는 마커
    public Label goalLabel;
    //길 그려주는 RouteDrawer 클래스 선언
    RouteDrawer routeDrawer;

    private LinearLayout busInfoLayout;
    // 버스 정보 레이아웃

    @Nullable
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        ViewGroup rootview = (ViewGroup)inflater.inflate(R.layout.activity_map_fragment,container,false);
        context=requireContext();
        MapView mapView = rootview.findViewById(R.id.map_view);

        busInfoLayout = rootview.findViewById(R.id.bus_info_layout);
        // 버스 정보 레이아웃 잡기

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
                routeDrawer=new RouteDrawer(kakaoMap);
                if(Mylocation.Lastlocation!=null) {
                    //마커 스타일 설정
                    LabelStyles styles = kakaoMap.getLabelManager()
                            .addLabelStyles(LabelStyles.from(LabelStyle.from(R.drawable.redmarker).setApplyDpScale(false)));
                    //마커 위치 설정
                    LabelOptions options = LabelOptions.from(LatLng.from(Mylocation.Lastlocation.getLatitude(), Mylocation.Lastlocation.getLongitude()))
                            .setStyles(styles);
                    //마커를 그릴 레이어
                    LabelLayer layer = kakaoMap.getLabelManager().getLayer();
                    Draw(); //임시로 그리게 해놓은것이고 오디세이 결과값에 따라 그리도록 코드 수정 필요
                    if(MyLabel!=null) {
                        //그렸던 마커 없애고
                        layer.remove(MyLabel);
                    }
                    //마커를 그리고 MyLabel에 집어넣음
                    MyLabel = layer.addLabel(options);
                    //마커 이미지 크기 조절
                    MyLabel.scaleTo(0.15f, 0.15f);
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
                        if(!searchingwithMine)
                        {
                            Mylocation.Lastlocation=location;
                            if(Mylocation.Lastlocation!=null) {
                                //마커 스타일 설정
                                LabelStyles styles = kakaoMap.getLabelManager()
                                        .addLabelStyles(LabelStyles.from(LabelStyle.from(R.drawable.redmarker).setApplyDpScale(false)));
                                //마커 위치 설정
                                LabelOptions options = LabelOptions.from(LatLng.from(Mylocation.Lastlocation.getLatitude(), Mylocation.Lastlocation.getLongitude()))
                                        .setStyles(styles);
                                //마커를 그릴 레이어
                                LabelLayer layer = kakaoMap.getLabelManager().getLayer();
                                //그렸던 마커 없애고
                                if (MyLabel != null) {

                                    layer.remove(MyLabel);
                                }
                                //마커를 그리고 MyLabel에 집어넣음
                                MyLabel = layer.addLabel(options);
                                //마커 이미지 크기 조절
                                MyLabel.scaleTo(0.15f, 0.15f);

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

        // busInfoLayout에 OnClickListener 설정
        busInfoLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 인텐트 생성 및 액티비티 시작
                Intent intent = new Intent(getContext(), busPointGPS.class);
                startActivity(intent);
            }
        });

        List<Pair<String, String>> busList = new ArrayList<>();

        busList.add(new Pair<>("city", "160"));
        busList.add(new Pair<>("city", "290"));
        busList.add(new Pair<>("intercity", "진주행"));

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

        return rootview;
    }

    //시작점 좌표,도착점 좌표와 대중교통 종류에 따라 그리기 예시
    //오디세이 결과값에 따라 그리도록 수정 필요함
    public void Draw(){
        Log.d("mylog","drawed");
        routeDrawer.draw(128.11798,35.150715,127.587554,34.95649,5);
        routeDrawer.draw(127.589832,34.969587,127.379763,36.361512,6);
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