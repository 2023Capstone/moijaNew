package com.example.moija;

import static android.content.Intent.getIntent;

import static androidx.core.content.ContentProviderCompat.requireContext;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
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

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

public class MapFragment extends Fragment {
    //상태를 보존할 변수
    private Bundle savedState;

    public static Context context;
    public boolean searchingwithMine=false;
    KakaoMobilityclasses.Root directionResponse;
    //내 위치를 나타내는 마커(라벨)
    private Label MyLabel;
    private boolean MovecameraFirst=false;
    //목적지 위치를 나타내는 마커(라벨)
    public Label startLabel;
    public Label goalLabel;
    MapProvider mapProvider;
    //길 그려주는 RouteDrawer 클래스 선언
    RouteDrawer routeDrawer;
    //카카오맵 클래스 선언(사용하고 있는 카카오맵을 담기 위함)
    static KakaoMap thiskakaoMap;
    //API 키
    protected static final String API_KEY = "8661fab6b43b9d4005d9eb9a06b10449";
    //api 기본 URL
    protected static final String BASE_URL = "https://dapi.kakao.com/";
    //REST api의 장소 정보를 담기 위한 객체
    //장소 검색 시에 받아온 정보를 Place 형태로 리스트로 담아 저장
    public class Place {
        private String place_name; //장소명
        private String address_name; //주소명
        private double x; // 경도
        private double y; // 위도
        public String getPlaceName() {
            return place_name;
        }
        public String getAddressName() {
            return address_name;
        }
        public double getX() {
            return x;
        }
        public double getY() {
            return y;
        }
    }


    //길찾기 API를 가져오는 쿼리 셋팅
    public interface DirectionApi {
        @GET("/v1/directions")
        Call<KakaoMobilityclasses.Root> getDirections(
                @Header("Authorization") String apiKey,
                @Query("origin") String origin,
                @Query("destination") String destination,
                @Query("summary") Boolean summary
        );
    }

    @Nullable
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        ViewGroup rootview = (ViewGroup)inflater.inflate(R.layout.activity_map_fragment,container,false);

        context=requireContext();
        MapView mapView = rootview.findViewById(R.id.map_view);
        FloatingActionButton searchbtn=rootview.findViewById(R.id.searchbtn);

        //Retrofit을 이용해 api 데이터를 json 형태로 받고 해석
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

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
                mapProvider = new KakaoMapProvider(kakaoMap);
                routeDrawer=new RouteDrawer(mapProvider);
                thiskakaoMap=kakaoMap;
                if(Mylocation.Lastlocation!=null) {
                    //마커 스타일 설정
                    LabelStyles styles = kakaoMap.getLabelManager()
                            .addLabelStyles(LabelStyles.from(LabelStyle.from(R.drawable.redmarker).setApplyDpScale(false)));
                    //마커 위치 설정
                    LabelOptions options = LabelOptions.from(LatLng.from(Mylocation.Lastlocation.getLatitude(), Mylocation.Lastlocation.getLongitude()))
                            .setStyles(styles);
                    //마커를 그릴 레이어
                    LabelLayer layer = kakaoMap.getLabelManager().getLayer();
                    if(MyLabel!=null) {
                        //그렸던 마커 없애고
                        layer.remove(MyLabel);
                    }
                    //마커를 그리고 MyLabel에 집어넣음
                    MyLabel = layer.addLabel(options);
                    //마커 이미지 크기 조절
                    MyLabel.scaleTo(0.15f, 0.15f);
                    //카메라를 현재 위치로 바꿈
                    CameraUpdate cameraUpdate = CameraUpdateFactory.newCenterPosition(LatLng.from(Mylocation.Lastlocation.getLatitude(), Mylocation.Lastlocation.getLongitude()));
                    kakaoMap.moveCamera(cameraUpdate);
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

                //현재위치에 마커를 찍는 코드


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
        //검색(돋보기)버튼 누르면 검색페이지로 이동
        searchbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent Movetosearch=new Intent(getActivity(),SearchPage.class);
                startActivity(Movetosearch);
            }
        });
        return rootview;
    }
    //길찾기 메서드
    public void FindGoal(){
                //경로를 그리는 메서드 호출
                GetRoute();
                // 선택한 장소의 마커를 지도에 표시
                LabelLayer layer = thiskakaoMap.getLabelManager().getLayer();
                if (startLabel != null) {
                    layer.remove(startLabel);
                }
                 if (goalLabel != null) {
                     layer.remove(goalLabel);
                }
                LabelStyles startstyles = thiskakaoMap.getLabelManager()
                .addLabelStyles(LabelStyles.from(LabelStyle.from(R.drawable.goalmarker).setTextStyles(20, Color.BLACK).setApplyDpScale(false)));

                 LabelOptions startoptions = LabelOptions.from(LatLng.from(Mylocation.StartPlace.getY(), Mylocation.StartPlace.getX())).setTexts("출발").setStyles(startstyles);
                 startLabel = layer.addLabel(startoptions);
                 startLabel.scaleTo(0.15f, 0.15f);

                LabelStyles styles = thiskakaoMap.getLabelManager()
                        .addLabelStyles(LabelStyles.from(LabelStyle.from(R.drawable.goalmarker).setTextStyles(20, Color.BLACK).setApplyDpScale(false)));
                LabelOptions options = LabelOptions.from(LatLng.from(Mylocation.selectedPlace.getY(), Mylocation.selectedPlace.getX())).setTexts("도착").setStyles(styles);

                goalLabel = layer.addLabel(options);
                goalLabel.scaleTo(0.15f, 0.15f);
                CameraUpdate cameraUpdatetoGoal = CameraUpdateFactory.newCenterPosition(LatLng.from((Mylocation.Lastlocation.getLatitude()+Mylocation.selectedPlace.getY())/2,(Mylocation.Lastlocation.getLongitude()+Mylocation.selectedPlace.getX())/2),13);
                thiskakaoMap.moveCamera(cameraUpdatetoGoal);

    }
    public void GetRoute(){
        //모빌리티 api 정보를 retrofit으로 받아오기
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://apis-navi.kakaomobility.com/") // 카카오 모빌리티 API의 기본 URL
                .addConverterFactory(GsonConverterFactory.create()) // Gson 변환기 추가
                .build();

        DirectionApi directionApi = retrofit.create(DirectionApi.class);
        //카카오모빌리티api에서 쿼리로 정보받아옴
        Call<KakaoMobilityclasses.Root> call = directionApi.getDirections("KakaoAK " + API_KEY,
                Mylocation.StartPlace.getX()+","+Mylocation.StartPlace.getY(),
                Mylocation.selectedPlace.getX()+","+Mylocation.selectedPlace.getY(),
                false);

        call.enqueue(new Callback<KakaoMobilityclasses.Root>() {
            @Override
            public void onResponse(Call<KakaoMobilityclasses.Root> call, Response<KakaoMobilityclasses.Root> response) {
                if (response.isSuccessful()) {
                    //directionResponse에 받아온 정보들을 저장함 (.body();)
                    directionResponse=response.body();
                    String jsonResponse = new Gson().toJson(directionResponse); // 응답 데이터를 JSON 문자열로 변환
                    Log.d("directionResponse", jsonResponse); // 로그에 출력
                    //Road에 있는 경로 간의 좌표들을 담는 vertexes들을 받아와 그릴거기 때문에 Road를 선언하고 받아온 road 정보를 넣어줌
                    ArrayList<KakaoMobilityclasses.Road> roads=directionResponse.getRoutes().get(0).getSections().get(0).getRoads();
                    //이미 그려진 게 있으면
                    if(routeDrawer.GoalRoutes!=null)
                    {
                        //다 지우고
                        routeDrawer.clearRouteLines();
                    }
                    //다시 그림 (Road 각각에 대해 안에 있는 vertexes 토대로 경로그리기 시작
                    for (KakaoMobilityclasses.Road road : roads) {
                        ArrayList<Double> vertexes = road.getVertexes(); // Road 객체의 vertexes를 가져옵니다.
                        routeDrawer.drawRoute(vertexes);
                    }



                }else {
                    //연결 실패시
                    Log.d("retrofit_test","실패 코드"+response.code());
                    Log.d("retrofit_test","연결 주소"+response.raw().request().url().url());
                }
            }

            @Override
            public void onFailure(Call<KakaoMobilityclasses.Root> call, Throwable t) {
                t.printStackTrace();
            }
        });

    }
}