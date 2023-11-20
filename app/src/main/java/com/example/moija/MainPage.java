package com.example.moija;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.moija.api.KakaoApi;
import com.example.moija.fragment.MapFragment;
import com.example.moija.map.Mylocation;
import com.example.moija.map.Place;
import com.example.moija.map.SearchResults;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

//검색 페이지에 관한 내용
public class MainPage extends AppCompatActivity{
    private EditText startEditText,goalEditText;
    //시작점을 검색하는건지 도착지점을 검색하는지 나누는 변수 (0: 시작점 1: 도착점)
    private int Searchcode=0;
    public static final String API_KEY = "8661fab6b43b9d4005d9eb9a06b10449";
    //api 기본 URL
    public static final String BASE_URL = "https://dapi.kakao.com/";
    private Button mylocbtn;

    private Button makemapbtn;
    private FrameLayout Mapframe;
    //시작점을 정했는지, 도착점을 정했는지
    private boolean Startsearched,Goalsearched=false;
    //검색결과를 담을 리스트뷰
    private ListView resultListView;

    private boolean makedmap=false;
    Fragment MapFragment;

    FrameLayout Mapframelayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);
        startEditText = findViewById(R.id.startEditText);
        goalEditText=findViewById(R.id.goalEditText);
        resultListView = findViewById(R.id.resultListView);
        mylocbtn=findViewById(R.id.mylocbtn);
        makemapbtn=findViewById(R.id.makemapbtn);
        Mapframelayout=findViewById(R.id.Mapframe);
        MapFragment=new MapFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.Mapframe,MapFragment).commit();
        //키보드 제어
        InputMethodManager Keyboardmanager = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
        SharedPreferences prefs=getSharedPreferences("SearchHistoryPrefs",Context.MODE_PRIVATE);
        //원래는 검색 후에 결과들 중 하나 선택하면 맵이 띄워져야 하나 합치기 전이므로 일단 임시적으로 맵을 키고 끌 수 있는 버튼을 구현함
        makemapbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(makedmap==false)
                {
                    Mapframelayout.setVisibility(View.VISIBLE);
                    makedmap=true;
                }
                else {
                    Mapframelayout.setVisibility(View.GONE);
                    makedmap=false;
                }
            }
        });
        //시작점을 검색하는 EditText에 focus되었을때
        startEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    Searchcode=0;
                    //다시 지우는 번거로움을 없애기 위해 자동으로 비운다
                    if (Startsearched==true) {
                        Startsearched=false;
                        startEditText.setText("");
                    }
                    if(Goalsearched==false){
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
                    Searchcode=1;
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
        //edittext 안의 내용이 바뀔때마다 검색
        startEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Searchcode=0;
                search(startEditText,0);
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        //startEditText에서 엔터키 누르면
        startEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                //키보드 내림
                Keyboardmanager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                return true;
            }
        });
        //edittext 안의 내용이 바뀔때마다 검색
        goalEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Searchcode=1;
                search(goalEditText,1);
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        //goalEditText에서 엔터키 누르면
        goalEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                //키보드 내림
                Keyboardmanager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                return true;
            }
        });
        //검색결과중 하나를 누르면 
        resultListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            //검색결과 중 하나 클릭하면
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Place selected=(Place) resultListView.getItemAtPosition(position);
                    //출발위치를 선택한 위치로 결정
                    if(Searchcode==0)
                    {
                        setStartPlace(selected);
                        //아직 도착점 안정했으면
                        if(Goalsearched==false)
                        {
                            //도착점 검색창에 focus를 넘겨줌
                            goalEditText.requestFocus();
                            goalEditText.setText("");
                            //키보드 올림
                            Keyboardmanager.showSoftInput(goalEditText, InputMethodManager.SHOW_IMPLICIT);
                        }
                        //도착점 정해져있으면
                        else if(Goalsearched==true)
                        {
                            //검색 결과 띄우는 동작 수행
                        }
                    }
                    //도착점을 찾는중이었다면
                    else if(Searchcode==1)
                    {
                        //도착위치를 선택한 위치로 결정
                        setGoalPlace(selected);
                        //시작점 안정했으면
                        if(Startsearched==false)
                        {
                            //시작점 검색창으로 focus 이동
                            startEditText.requestFocus();
                            startEditText.setText("");
                            //키보드 올림
                            Keyboardmanager.showSoftInput(startEditText, InputMethodManager.SHOW_IMPLICIT);
                        }
                        //시작점 정해져있으면
                        else if(Startsearched==true)
                        {
                           //검색 결과 띄우는 동작 수행
                        }
                    }
                resultListView.setVisibility(View.INVISIBLE);
            }
        });
        mylocbtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if(Mylocation.Lastlocation!=null)
                {
                    //위치에 따라 주소 찾고 설정하는 메서드 넣기
                    FindMyAddress();
                }
            }
        });
    }
    //도착점 설정 메서드
    public void setGoalPlace(Place place){
        goalEditText.setText("도착 위치: " + place.getPlaceName());
        Goalsearched=true;
        Mylocation.GoalPlace=place;
    }
    //출발점 설정 메서드
    public void setStartPlace(Place place){
        startEditText.setText("출발 위치: " + place.getPlaceName());
        Startsearched=true;
        Mylocation.StartPlace=place;
    }
    //검색한 결과를 바로 시작점/도착점으로 설정할 때 사용(내 위치를 시작점에 넣을때)
    public void searchAndSet(String query, String startorgoal, boolean searchbymyloc){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        KakaoApi kakaoapi = retrofit.create(KakaoApi.class);
        Call<SearchResults> call=null;
        if(searchbymyloc==true)
        {call=kakaoapi.searchNearPlace("KakaoAK "+API_KEY,query,Mylocation.Lastlocation.getLongitude(),Mylocation.Lastlocation.getLatitude(),1000);}
        else {call=kakaoapi.searchPlaces("KakaoAK "+API_KEY,query);}
        call.enqueue(new Callback<SearchResults>() {
            @Override
            public void onResponse(Call<SearchResults> call, Response<SearchResults> response) {
                if (response.isSuccessful()) {
                    SearchResults searchResults = response.body();
                    List<Place> places=searchResults.getPlaces();
                    String Json2 = new Gson().toJson(searchResults);
                    Log.d("mylog",Json2);
                    String Json = new Gson().toJson(places);
                    Log.d("mylog",Json);
                    if(places.get(0).getPlaceName()!=null)
                    {
                        if(startorgoal.equals("start"))
                        {
                            setStartPlace(places.get(0));
                        }
                        else if(startorgoal.equals("goal"))
                        {
                            setGoalPlace(places.get(0));
                        }
                    }
                }
            }
            @Override
            public void onFailure(Call<SearchResults> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }
    //내 위치의 좌표를 받고 결과에 따라 searchAndSet을 통하여 시작점으로 설정한다
    public void FindMyAddress(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        KakaoApi kakaoapi = retrofit.create(KakaoApi.class);
        Call<SearchResults.LoctoAddResult> call=null;
        if(Mylocation.Lastlocation!=null) {
            call = kakaoapi.getAddressWithLocation("KakaoAK " + API_KEY, Mylocation.Lastlocation.getLongitude(), Mylocation.Lastlocation.getLatitude());
            Log.d("mylog",Double.toString(Mylocation.Lastlocation.getLongitude()) + Double.toString(Mylocation.Lastlocation.getLatitude()));
        }
        call.enqueue(new Callback<SearchResults.LoctoAddResult>() {
            @Override
            public void onResponse(Call<SearchResults.LoctoAddResult> call, Response<SearchResults.LoctoAddResult> response) {
                if (response.isSuccessful()) {
                    SearchResults.LoctoAddResult searchResults = response.body();
                    SearchResults.LoctoAddResult.RoadAddress roadaddress=searchResults.getDocuments().get(0).getRoad_address();
                    //위치에 따라 도로명주소(roadaddress)가 null이 될때가 있음
                    //따라서 실행전에 null값인지 미리 확인한다
                    if(roadaddress!=null)
                    {

                        String roadAddressJson = new Gson().toJson(roadaddress);
                        Log.d("mylog", roadAddressJson);
                        //주변 건물이 존재하면
                        if(roadaddress.getBuilding_name()!=null && !roadaddress.getBuilding_name().isEmpty()) {
                            //해당 건물명을 searchAndSet을 통해 넘겨주고,시작점으로 결정
                            searchAndSet(roadaddress.getBuilding_name(),"start",true);
                        }
                        else if(roadaddress==null || roadaddress.getBuilding_name()==null || roadaddress.getBuilding_name().isEmpty())
                        {
                            //건물이 없으면 주소명이 있는지 확인하고 searchAndSet에 넘겨주어 해당 주소에 가까이 있는 건물명을 찾고
                            //시작점으로 설정한다.
                            if(searchResults.getDocuments().get(0).getAddress()!=null) {
                                String AddressJson = new Gson().toJson(searchResults.getDocuments().get(0).getAddress());
                                Log.d("mylog", AddressJson);
                                searchAndSet(searchResults.getDocuments().get(0).getAddress().getAddress_name(), "start", true);
                            }
                            else {
                                //그냥 주소명도 검색이 안될 경우에는 토스트 메시지로 검색이 안됨을 안내
                            Toast.makeText(getApplicationContext(),"검색된 장소가 없습니다.",Toast.LENGTH_SHORT);
                        }
                        }
                    }
                    
                }
            }
            @Override
            public void onFailure(Call<SearchResults.LoctoAddResult> call, Throwable t) {
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
            KakaoApi kakaoapi = retrofit.create(KakaoApi.class);
            Call<SearchResults> call;
            if(Mylocation.Lastlocation!=null) {
                call = kakaoapi.searchPlacesWithMyLocation("KakaoAK " + API_KEY, query, Mylocation.Lastlocation.getLongitude(), Mylocation.Lastlocation.getLatitude());
            }
            else {
                call=kakaoapi.searchPlaces("KakaoAK "+API_KEY,query);
            }
            call.enqueue(new Callback<SearchResults>() {
                @Override
                public void onResponse(Call<SearchResults> call, Response<SearchResults> response) {
                    if (response.isSuccessful()) {
                        //SearchResults에 api 응답을 받고
                        SearchResults searchResults = response.body();
                        List<Place> searchedplace = searchResults.getPlaces();
                        List<Place> filteredplace = new ArrayList<>();
                        //도착점과 시작점 중복 설정 방지를 위한 코드들
                        if(searchcode==0 && Goalsearched==true) {
                            for (Place searchplace : searchedplace) {
                                if (!(searchplace.getPlaceName().equals(Mylocation.GoalPlace.getPlaceName()))) {
                                    filteredplace.add(searchplace);
                                }
                            }
                            if (searchResults != null && searchResults.getPlaces() != null) {
                                SearchAdapter adapter = new SearchAdapter(MainPage.this, filteredplace);
                                resultListView.setAdapter(adapter);
                            }
                            resultListView.setVisibility(View.VISIBLE);
                        }
                        if(searchcode==1 && Startsearched==true) {

                            for (Place searchplace : searchedplace) {
                                if (!(searchplace.getPlaceName().equals(Mylocation.StartPlace.getPlaceName()))) {
                                    filteredplace.add(searchplace);
                                }
                            }
                            if (searchResults != null && searchResults.getPlaces() != null) {
                                SearchAdapter adapter = new SearchAdapter(MainPage.this, filteredplace);
                                resultListView.setAdapter(adapter);
                            }
                            resultListView.setVisibility(View.VISIBLE);
                        }
                        else {
                            if (searchResults != null && searchResults.getPlaces() != null) {
                                SearchAdapter adapter = new SearchAdapter(MainPage.this, searchResults.getPlaces());
                                resultListView.setAdapter(adapter);
                            }
                            resultListView.setVisibility(View.VISIBLE);
                        }
                    }
                }
                @Override
                public void onFailure(Call<SearchResults> call, Throwable t) {
                    t.printStackTrace();
                }
            });
        }
    }
}