package com.example.moija.map;

import static com.example.moija.fragment.MapFragment.API_KEY;
import static com.example.moija.fragment.MapFragment.BASE_URL;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.moija.R;
import com.example.moija.fragment.Fragment_Chat_Map;
import com.example.moija.fragment.MapFragment;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

//검색 페이지에 관한 내용
public class SearchPage extends AppCompatActivity {

    //검색창
    private EditText startEditText;
    //시작점을 검색하는건지 도착지점을 검색하는지 나누는 변수 (0: 시작점 1: 도착점)
    private int Searchcode=0;
    //시작점을 정했는지
    private boolean Startsearched=false;
    //도착지점을 정했는지
    private boolean Goalsearched=false;
    private MapFragment.Place Startplace;
    private MapFragment.Place Goalplace;
    private EditText goalEditText;
    //검색결과를 담을 리스트뷰
    private ListView resultListView;

    private ImageButton backbutton;
    //REST API 에서 검색한 장소들을 Place 형태로 SearchResponse가 받음
    public class SearchResponse {
        private List<MapFragment.Place> documents;

        public List<MapFragment.Place> getDocuments() {return documents;}
    }
    //카카오맵 검색 API 쿼리셋팅
    public interface KakaoMapApi {
        @GET("/v2/local/search/keyword.json")
        Call<SearchResponse> searchPlaces(
                @Header("Authorization") String apiKey,
                @Query("query") String query,
                @Query("x") double x,
                @Query("y") double y

        );
    }
    //카카오 restapi에서 주소명을 담는 클래스
    public class KakaoAddressResponse {

        @SerializedName("documents")
        private ArrayList<Document> documents;

        public ArrayList<Document> getDocuments() {
            return documents;
        }

        public void setDocuments(ArrayList<Document> documents) {
            this.documents = documents;
        }
    }


    public class Document{
        @SerializedName("road_address")
        public RoadAddress road_address;
        public RoadAddress getRoadAddress(){
            return road_address;
        }
        public void setRoad_address(RoadAddress road_address){
            this.road_address=road_address;
        }

    }


    
    public class RoadAddress{
        //주소명
        @SerializedName("address_name")
        public String address_name;
        public String getAddress_name(){
            return address_name;
        }
        public void setAddress_name(String address_name){
            this.address_name=address_name;
        }
        @SerializedName("building_name")
        public String building_name;
        public String getBuilding_name(){
            return building_name;
        }
        public void setBuilding_name(String building_name){
            this.building_name=building_name;
        }
    }

    //현재 위치 위도,경도를 입력하면 가까운 주소(건물이름)을 반환하는 api를 쿼리로 요청(retrofit)
    public interface KakaoApiService {
        @GET("/v2/local/geo/coord2address.json")
        Call<KakaoAddressResponse> getAddress(
                @Header("Authorization") String authorization,
                @Query("x") double longitude,
                @Query("y") double latitude
        );
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_page);
        startEditText = findViewById(R.id.startEditText);
        goalEditText=findViewById(R.id.goalEditText);
        resultListView = findViewById(R.id.resultListView);
        backbutton=findViewById(R.id.backbutton);
        //1초마다 현재위치가 설정되어있는지 확인하고, 설정되있으면 현재 주소를 토대로 시작위치를 결정하도록 한다
        Timer timer = new Timer();
        TimerTask Findaddress= new TimerTask() {
            @Override
            public void run() {

                if(Mylocation.Lastlocation!=null && Startsearched==false && !startEditText.isFocused())
                {
                    FindMyAddress();
                    Log.d("mylog","실행");
                }
            }

        };
        timer.schedule(Findaddress, 0, 1000);

        //뒤로가기 버튼 누르면 맵 채팅 화면으로 이동
        backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent=new Intent(SearchPage.this, Fragment_Chat_Map.class);
                startActivity(myIntent);
            }
        });
        //시작점을 입력하는 EditText를 눌러 Focus되었을때
        startEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    // EditText에 포커스가 주어진 경우
                    String text =startEditText.getText().toString();
                    // 시작점을 정했으면 변경하려는것이기때문에
                    if (Startsearched==true) {
                        Startsearched=false;
                        startEditText.setText(""); // 편의를 위해 EditText의 텍스트를 모두 제거
                    }
                    //도착점이 안정해졌으면
                    if(Goalsearched==false){
                        //도착점 검색창에 쓰다만 것들 삭제
                        goalEditText.setText("");
                    }
                    //검색결과 숨김
                    resultListView.setVisibility(View.INVISIBLE);
                }
            }
        });
        //도착점을 검색하는 EditText에 focus되었을때, startEditText와 같은 로직
        goalEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    String text =goalEditText.getText().toString();
                    if (Goalsearched==true) {
                        Goalsearched=false;
                        goalEditText.setText("");
                    }
                    if(Startsearched==false){
                        startEditText.setText("");
                    }
                    //검색결과 숨김
                    resultListView.setVisibility(View.INVISIBLE);
                }
            }
        });
        //startEditText에서 엔터키 누르면
        startEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                //검색 실행
                Searchcode=0;
                search(startEditText,0);
                return true;
            }
        });
        //goalEditText에서 엔터키 누르면
        goalEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                //검색 실행
                Searchcode=1;
                search(goalEditText,1);
                return true;
            }
        });
        //검색결과중 하나를 누르면 
        resultListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            //검색결과 중 하나 클릭하면
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //만약 시작점을 찾는중이면
                if(Searchcode==0)
                {

                    MapFragment.Place selected=(MapFragment.Place) resultListView.getItemAtPosition(position);
                    //출발위치를 선택한 위치로 결정
                    startEditText.setText("출발 위치: " + selected.getPlaceName());
                    Startplace=selected;


                    Startsearched=true;
                    Mylocation.StartPlace=selected;

                    //아직 도착점 안정했으면
                    if(Goalsearched==false)
                    {
                        //도착점 검색창에 focus를 넘겨줌
                        goalEditText.requestFocus();
                        goalEditText.setText("");
                    }
                    //도착점 정해져있으면
                    else if(Goalsearched==true)
                    {
                        //인텐트를 이용해 메인액티비티로 넘어가는데
                        Intent myIntent=new Intent(SearchPage.this, Fragment_Chat_Map.class);
                        //FindGoal이라는 String Key를 넘겨줌
                        //메인액티비티에서 이 Key를 확인하고 길찾기 메서드를 실행시킴
                        myIntent.putExtra("key","FindGoal");
                        //선택했던 장소에 대한 정보를 메인으로 넘김

                        //액티비티 이동
                        startActivity(myIntent);
                    }
                    resultListView.setVisibility(View.INVISIBLE);

                    Startsearched=true;
                }
                //도착점을 찾는중이었으면
                else if(Searchcode==1)
                {
                    MapFragment.Place selected=(MapFragment.Place) resultListView.getItemAtPosition(position);
                    //도착위치를 선택한 위치로 결정
                    goalEditText.setText("도착 위치: " + selected.getPlaceName());
                    Goalplace=selected;
                    Goalsearched=true;
                    Mylocation.selectedPlace=selected;
                    resultListView.setVisibility(View.INVISIBLE);
                    //시작점 안정했으면
                    if(Startsearched==false)
                    {
                        //시작점 검색창으로 focus 이동
                        startEditText.requestFocus();
                        startEditText.setText("");
                    }
                    //시작점 정해져있으면
                    else if(Startsearched==true)
                    {
                        //인텐트를 이용해 메인액티비티로 넘어가는데
                        Intent myIntent=new Intent(SearchPage.this, Fragment_Chat_Map.class);
                        //FindGoal이라는 String Key를 넘겨줌
                        //메인액티비티에서 이 Key를 확인하고 길찾기 메서드를 실행시킴
                        myIntent.putExtra("key","FindGoal");
                        //선택했던 장소에 대한 정보를 메인으로 넘김

                        //액티비티 이동
                        startActivity(myIntent);
                    }

                    Goalsearched=true;
                }


            }

        });


    }
    //ListView의 CustomAdapter
    public class CustomAdapter extends ArrayAdapter<MapFragment.Place> {
        private LayoutInflater inflater;

        public CustomAdapter(Context context, List<MapFragment.Place> places) {
            super(context, R.layout.list_item_place, places);
            inflater = LayoutInflater.from(context);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = convertView;
            if (view == null) {
                view = inflater.inflate(R.layout.list_item_place, parent, false);
            }

            TextView placeNameTextView = view.findViewById(R.id.placeNameTextView);
            TextView placeAddressTextView = view.findViewById(R.id.placeAddressTextView);
            TextView placedistance=view.findViewById(R.id.distance);
            MapFragment.Place place = getItem(position);

            if (place != null) {
                // 장소 이름을 텍스트뷰에 설정
                placeNameTextView.setText(place.getPlaceName());
                placeNameTextView.setTextSize(20);
                //장소 주소를 설정
                placeAddressTextView.setText(place.getAddressName());
                //장소의 Location을 받아오고
                Location myplace=new Location("my location");
                Location findplace=new Location("finded location");
                findplace.setLatitude(place.getY());
                findplace.setLongitude(place.getX());
                float distancetoFind=Mylocation.Lastlocation.distanceTo(findplace)/1000;
                String distancetoString=String.format("%.1f",distancetoFind);
                //현재위치와의 거리를 나타냄 (시작점을 검색으로 하면 시작점과의 거리로 바꿔야할수도 있음)
                placedistance.setText(distancetoString+"km");
            }

            return view;
        }
    }
    //현재위치의 좌표를 통해 주소를 찾음
    public void FindMyAddress(){
            // Retrofit2를 사용하여 카카오맵 REST API에 검색 요청을 보냄
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            KakaoApiService kakaoApiservice = retrofit.create(KakaoApiService.class);

            Call<KakaoAddressResponse> call = kakaoApiservice.getAddress("KakaoAK " + API_KEY, Mylocation.Lastlocation.getLongitude(), Mylocation.Lastlocation.getLatitude());

            call.enqueue(new Callback<KakaoAddressResponse>() {
                @Override
                public void onResponse(Call<KakaoAddressResponse> call, Response<KakaoAddressResponse> response) {
                    if (response.isSuccessful()) {
                        KakaoAddressResponse addressResponse = response.body();
                        Log.d("mylog",addressResponse.toString());
                        if (addressResponse != null && addressResponse.getDocuments().size() > 0) {
                            if(addressResponse.getDocuments().get(0).getRoadAddress()!=null) {
                                if (!addressResponse.getDocuments().get(0).getRoadAddress().getBuilding_name().isEmpty()) {
                                    //startEditText.setText("현재위치 : " + addressResponse.getDocuments().get(0).getRoadAddress().getBuilding_name().toString());
                                    //주소를 찾고 출발지 설정을 위해 넘겨준다
                                    setStarttoMyaddress(addressResponse.getDocuments().get(0).getRoadAddress().getBuilding_name().toString());
                                    Startsearched = true;
                                } else if (addressResponse.getDocuments().get(0).getRoadAddress().getBuilding_name().isEmpty()) {
                                    //startEditText.setText("현재위치 : " + addressResponse.getDocuments().get(0).getRoadAddress().getAddress_name().toString());
                                    //주소를 찾고 출발지 설정을 위해 넘겨준다
                                    setStarttoMyaddress(addressResponse.getDocuments().get(0).getRoadAddress().getAddress_name().toString());
                                    Startsearched = true;
                                }


                            }
                        } else {
                            Log.d("KakaoAddressSearch", "주소를 찾을 수 없습니다.");
                        }
                    } else {
                        Log.d("KakaoAddressSearch", "API 호출 실패: " + response.message());
                    }
                }

                @Override
                public void onFailure(Call<KakaoAddressResponse> call, Throwable t) {
                    t.printStackTrace();
                }
            });

    }
    //현재 위치를 토대로 검색하고, 가장 가까운 위치를 찾아 그 위치를 시작점으로 설정해주는 코드
    public void setStarttoMyaddress(String MyAddress){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        KakaoMapApi kakaoMapApi = retrofit.create(KakaoMapApi.class);

        Call<SearchResponse> call = kakaoMapApi.searchPlaces("KakaoAK " + API_KEY, MyAddress,Mylocation.Lastlocation.getLongitude(),Mylocation.Lastlocation.getLatitude());

        call.enqueue(new Callback<SearchResponse>() {
            @Override
            public void onResponse(Call<SearchResponse> call, Response<SearchResponse> response) {
                if (response.isSuccessful()) {
                    //searchResponse에 api 응답을 받고
                    SearchResponse searchResponse = response.body();
                    //받은 응답의 장소들을 Place형태로 받아온 뒤에,
                    List<MapFragment.Place> searchedplace = searchResponse.getDocuments();
                    //가장 가까운(앞에 있는) 장소를 현재 위치로 결정한다.
                    startEditText.setText("현재 위치 : "+searchResponse.getDocuments().get(0).getPlaceName().toString());
                    Mylocation.StartPlace=searchedplace.get(0);
                }
            }

            @Override
            public void onFailure(Call<SearchResponse> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }
    //검색 로직
    public void search(EditText searchbox,int searchcode) {

        String query = searchbox.getText().toString();
        if (!query.isEmpty()) {
            // Retrofit2를 사용하여 카카오맵 REST API에 검색 요청을 보냄
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            KakaoMapApi kakaoMapApi = retrofit.create(KakaoMapApi.class);

            Call<SearchResponse> call = kakaoMapApi.searchPlaces("KakaoAK " + API_KEY, query,Mylocation.Lastlocation.getLongitude(),Mylocation.Lastlocation.getLatitude());

            call.enqueue(new Callback<SearchResponse>() {
                @Override
                public void onResponse(Call<SearchResponse> call, Response<SearchResponse> response) {
                    if (response.isSuccessful()) {
                        //searchResponse에 api 응답을 받고
                        SearchResponse searchResponse = response.body();
                        List<MapFragment.Place> searchedplace = searchResponse.getDocuments();
                        List<MapFragment.Place> filteredplace = new ArrayList<>();
                        //도착점과 시작점 중복 설정 방지를 위한 코드들
                        if(searchcode==0 && Goalsearched==true) {
                            for (MapFragment.Place searchplace : searchedplace) {
                                if (!(searchplace.getPlaceName().equals(Mylocation.selectedPlace.getPlaceName()))) {
                                    filteredplace.add(searchplace);
                                }
                            }
                            if (searchResponse != null && searchResponse.getDocuments() != null) {
                                CustomAdapter adapter = new CustomAdapter(SearchPage.this, filteredplace);
                                resultListView.setAdapter(adapter);
                            }
                            resultListView.setVisibility(View.VISIBLE);
                        }
                        if(searchcode==1 && Startsearched==true) {

                            for (MapFragment.Place searchplace : searchedplace) {
                                if (!(searchplace.getPlaceName().equals(Mylocation.StartPlace.getPlaceName()))) {
                                    filteredplace.add(searchplace);
                                }
                            }
                            if (searchResponse != null && searchResponse.getDocuments() != null) {
                                CustomAdapter adapter = new CustomAdapter(SearchPage.this, filteredplace);
                                resultListView.setAdapter(adapter);
                            }
                            resultListView.setVisibility(View.VISIBLE);
                        }
                        else {
                            if (searchResponse != null && searchResponse.getDocuments() != null) {
                                CustomAdapter adapter = new CustomAdapter(SearchPage.this, searchResponse.getDocuments());
                                resultListView.setAdapter(adapter);
                            }
                            resultListView.setVisibility(View.VISIBLE);
                        }
                    }
                }

                @Override
                public void onFailure(Call<SearchResponse> call, Throwable t) {
                    t.printStackTrace();
                }
            });

        }
    }

}