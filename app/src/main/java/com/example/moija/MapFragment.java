package com.example.moija;

import static android.content.Intent.getIntent;

import static androidx.core.content.ContentProviderCompat.requireContext;

import static org.apache.commons.lang3.ClassUtils.getPackageName;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;
import com.kakao.vectormap.KakaoMap;
import com.kakao.vectormap.KakaoMapReadyCallback;
import com.kakao.vectormap.LatLng;
import com.kakao.vectormap.MapLifeCycleCallback;
import com.kakao.vectormap.MapView;
import com.kakao.vectormap.camera.CameraAnimation;
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

    LinearLayout setGoalLayout;

    ImageButton busbtn,outbusbtn,trainbtn;

    //kakaoMap을 OnMapReady가 아닌 다른곳에서도 호출할 수 있게 선언
    KakaoMap thiskakaoMap;
    public static Context context;
    //후에 추가할 실시간 위치를 끄기 기능을 키고 스스로 검색하여 출발지를 정할때 사용할 변수
    public boolean searchingwithMine=false;
    //카카오모빌리티가 받아온 정보를 저장하는 변수
    KakaoMobilityclasses.Root directionResponse;
    //내 위치를 나타내는 마커(라벨)
    private Label MyLabel;
    //처음에만 내 위치로 카메라가 이동되도록 하기 위한 변수
    private boolean MovecameraFirst=false;
    //시작 위치를 나타내는 마커
    public Label startLabel;
    //도착 위치를 나타내는 마커
    public Label goalLabel;
    MapProvider mapProvider;
    //길 그려주는 RouteDrawer 클래스 선언
    RouteDrawer routeDrawer;
    //카카오맵 클래스 선언(사용하고 있는 카카오맵을 담기 위함)
    //API 키
    protected static final String API_KEY = "44bdf179ef832c51f0a780b3f0154b53";

    protected static final String OdsayAPI_KEY="Bk3FXTpa4bUs3dxTOsUxSFvLGFYhTaoBDPKfSPOLdwI";
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
        setGoalLayout=rootview.findViewById(R.id.setGoalLayout);
        FloatingActionButton menubtn=rootview.findViewById(R.id.menubtn);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        DrawerLayout drawerLayout = activity.findViewById(R.id.drawer_layout);
        NavigationView navigationView = activity.findViewById(R.id.nav_view);
        FloatingActionButton searchbtn=rootview.findViewById(R.id.searchbtn);
        busbtn=rootview.findViewById(R.id.busbtn);
        outbusbtn=rootview.findViewById(R.id.outbusbtn);
        trainbtn=rootview.findViewById(R.id.trainbtn);
        searchbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent Movetosearch = new Intent(getActivity(), SearchPage.class);
                startActivity(Movetosearch);
            }
        });
        // 메뉴바의 아이템들을 눌렀을때
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                // 아이템을 선택했을 때 실행할 동작 구현
                int itemId = item.getItemId();
                if (itemId == R.id.menuitem1) {
                    Toast.makeText(getActivity().getApplicationContext(), "버스노선 확인", Toast.LENGTH_SHORT).show();
                } else if (itemId == R.id.menuitem2) {
                    Toast.makeText(getActivity().getApplicationContext(), "다른 메뉴", Toast.LENGTH_SHORT).show();
                }
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });
        busbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setGoalLayout.setVisibility(View.INVISIBLE);
            }
        });
        outbusbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setGoalLayout.setVisibility(View.INVISIBLE);
            }
        });
        trainbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setGoalLayout.setVisibility(View.INVISIBLE);
            }
        });


        menubtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDrawer();
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
                //Fragment_Chat_Map 액티비티에서 보낸 번들을 받아 키를 확인하고, 있으면 길찾는 FindGoal() 메서드를 실행
                Bundle args = getArguments();
                if (args != null) {
                    String value = args.getString("key");
                    if(value!=null)
                    {
                        FindGoal();
                    }
                }
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

    //길찾기 메서드
    public void FindGoal(){
                //경로를 그리는 메서드 호출
                GetRoute();
                //시작지점과 끝지점에 마커를 찍는 코드
                if(thiskakaoMap!=null)
                {
                    LabelLayer layer = thiskakaoMap.getLabelManager().getLayer();
                    //중복 시작점마커 제거
                    if (startLabel != null) {
                        layer.remove(startLabel);
                    }
                    //중복 도착점마커 제거
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
                    Mylocation.startLocation.setLatitude(Mylocation.StartPlace.getY());
                    Mylocation.startLocation.setLongitude(Mylocation.StartPlace.getX());
                    Mylocation.selectedLocation.setLatitude(Mylocation.selectedPlace.getY());
                    Mylocation.selectedLocation.setLongitude(Mylocation.selectedPlace.getX());
                    float distance = Mylocation.startLocation.distanceTo(Mylocation.selectedLocation);
                    int zoomLevel;
                    if (distance < 1000) {
                        zoomLevel = 15;  // 거리가 1km 미만이면 확대 수준 15
                    } else if (distance < 5000) {
                        zoomLevel = 13;  // 거리가 5km 미만이면 확대 수준 13
                    } else {
                        zoomLevel = 10;  // 그 외의 경우 확대 수준 10
                    }
                    //카메라를 시작 지점, 끝지점 사이 중간 쪽으로 이동
                    setGoalLayout.setVisibility(View.VISIBLE);

                    CameraUpdate cameraUpdatetoGoal = CameraUpdateFactory.newCenterPosition(LatLng.from((Mylocation.StartPlace.getY()+Mylocation.selectedPlace.getY())/2,(Mylocation.StartPlace.getX()+Mylocation.selectedPlace.getX())/2),zoomLevel);
                    thiskakaoMap.moveCamera(cameraUpdatetoGoal, CameraAnimation.from(500, true, true));

                }
    }


    private void openDrawer() {
        // 부모 액티비티의 참조를 얻어옵니다.
        AppCompatActivity activity = (AppCompatActivity) getActivity();

        // 부모 액티비티의 DrawerLayout을 찾습니다.
        DrawerLayout drawerLayout = activity.findViewById(R.id.drawer_layout);

        // 드로어를 엽니다.
        drawerLayout.openDrawer(GravityCompat.START); // START는 왼쪽 드로어, END는 오른쪽 드로어를 열도록 지정합니다.
    }
    private void closeDrawer() {
        // 부모 액티비티의 참조를 얻어옵니다.
        AppCompatActivity activity = (AppCompatActivity) getActivity();

        // 부모 액티비티의 DrawerLayout을 찾습니다.
        DrawerLayout drawerLayout = activity.findViewById(R.id.drawer_layout);

        // 드로어를 닫습니다.
        drawerLayout.closeDrawer(GravityCompat.START); // START는 왼쪽 드로어, END는 오른쪽 드로어를 닫도록 지정합니다.
    }


    //경로를 그려주는 함수
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