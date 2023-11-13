package com.example.moija.map;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.moija.R;
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
    private EditText startEditText;
    private EditText goalEditText;
    //시작점을 검색하는건지 도착지점을 검색하는지 나누는 변수 (0: 시작점 1: 도착점)
    private int Searchcode=0;
    public static final String API_KEY = "8661fab6b43b9d4005d9eb9a06b10449";
    //api 기본 URL
    public static final String BASE_URL = "https://dapi.kakao.com/";
    private Button mylocbtn;
    //시작점을 정했는지
    private boolean Startsearched=false;
    //도착지점을 정했는지
    private boolean Goalsearched=false;
    //검색기록
    ArrayList<Mylocation> searchHistory=new ArrayList<>();
    //검색결과를 담을 리스트뷰
    private ListView resultListView;
    //검색기록을 담을 리스트뷰
    private ListView historyView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);
        startEditText = findViewById(R.id.startEditText);
        goalEditText=findViewById(R.id.goalEditText);
        resultListView = findViewById(R.id.resultListView);
        historyView=findViewById(R.id.historyListView);
        mylocbtn=findViewById(R.id.mylocbtn);
        //키보드 제어
        InputMethodManager Keyboardmanager = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);

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
                        startEditText.setText(""); //편의를 위해 EditText의 텍스트를 모두 제거
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
                        startEditText.setText("출발 위치: " + selected.getPlaceName());
                        Startsearched=true;
                        Mylocation.StartPlace=selected;
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
                            /*//인텐트를 이용해 메인액티비티로 넘어가는데
                            Intent myIntent=new Intent(MainPage.this, Fragment_Chat_Map.class);
                            //FindGoal이라는 String Key를 넘겨줌
                            //메인액티비티에서 이 Key를 확인하고 길찾기 메서드를 실행시킴
                            myIntent.putExtra("key","FindGoal");
                            //선택했던 장소에 대한 정보를 메인으로 넘김
                            //액티비티 이동
                            startActivity(myIntent);*/
                        }
                        Startsearched=true;
                    }
                    //도착점을 찾는중이었다면
                    else if(Searchcode==1)
                    {
                        //도착위치를 선택한 위치로 결정
                        goalEditText.setText("도착 위치: " + selected.getPlaceName());
                        Goalsearched=true;
                        Mylocation.selectedPlace=selected;
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
                            /*//인텐트를 이용해 메인액티비티로 넘어가는데
                            Intent myIntent=new Intent(MainPage.this, Fragment_Chat_Map.class);
                            //FindGoal이라는 String Key를 넘겨줌
                            //메인액티비티에서 이 Key를 확인하고 길찾기 메서드를 실행시킴
                            myIntent.putExtra("key","FindGoal");
                            //선택했던 장소에 대한 정보를 메인으로 넘김
                            //액티비티 이동
                            startActivity(myIntent);*/
                        }

                        Goalsearched=true;
                    }
                resultListView.setVisibility(View.INVISIBLE);
                historyView.setVisibility(View.VISIBLE);
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

    //ListView의 양식을 나타낼 CustomAdapter
    public class CustomAdapter extends ArrayAdapter<Place> {
        private LayoutInflater inflater;

        public CustomAdapter(Context context, List<Place> places) {
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
            Place place = getItem(position);

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
                if(Mylocation.Lastlocation!=null)
                {
                    float distancetoFind=Mylocation.Lastlocation.distanceTo(findplace)/1000;
                    String distancetoString=String.format("%.1f",distancetoFind);
                    //현재위치와의 거리를 나타냄 (시작점을 검색으로 하면 시작점과의 거리로 바꿔야할수도 있음)
                    placedistance.setText(distancetoString+"km");
                }

            }

            return view;
        }
    }
    public void FindNearPlace(String query){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        KakaoApi kakaoapi = retrofit.create(KakaoApi.class);
        Call<SearchResults> call=null;
        call=kakaoapi.searchNearPlace("KakaoAK "+API_KEY, query,Mylocation.Lastlocation.getLongitude(),Mylocation.Lastlocation.getLatitude(),50);
        call.enqueue(new Callback<SearchResults>() {
            @Override
            public void onResponse(Call<SearchResults> call, Response<SearchResults> response) {
                if (response.isSuccessful()) {

                    SearchResults searchResults = response.body();
                    List<Place> places=searchResults.getPlaces();
                    if(places.get(0).getPlaceName()!=null)
                    {
                        String Json = new Gson().toJson(places.get(0).getPlaceName());
                        startEditText.setText("출발 위치 : "+places.get(0).getPlaceName());
                        Mylocation.StartPlace=places.get(0);
                        Log.d("mylog",Json);
                        Startsearched=true;
                    }
                }
            }


            @Override
            public void onFailure(Call<SearchResults> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }
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
                    if(searchResults.getDocuments().get(0).getRoad_address()!=null)
                    {
                        String roadAddressJson = new Gson().toJson(searchResults.getDocuments().get(0).getRoad_address());
                        startEditText.setText("출발 위치 : "+searchResults.getDocuments().get(0).getRoad_address().getBuilding_name());
                        Log.d("mylog",roadAddressJson);
                        Startsearched=true;
                    }
                    else {
                        String AddressJson = new Gson().toJson(searchResults.getDocuments().get(0).getAddress());
                        //도로명주소인 RoadAddress가 null값이 나올수도 있음
                        //따라서 그럴 경우에 주소를 넘겨주어 주소를 통해 가까운 건물을 검색하는 메서드를 호출함
                        FindNearPlace(searchResults.getDocuments().get(0).getAddress().getAddress_name());
                        Log.d("mylog",AddressJson);
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
                                if (!(searchplace.getPlaceName().equals(Mylocation.selectedPlace.getPlaceName()))) {
                                    filteredplace.add(searchplace);
                                }
                            }
                            if (searchResults != null && searchResults.getPlaces() != null) {
                                CustomAdapter adapter = new CustomAdapter(MainPage.this, filteredplace);
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
                                CustomAdapter adapter = new CustomAdapter(MainPage.this, filteredplace);
                                resultListView.setAdapter(adapter);
                            }
                            resultListView.setVisibility(View.VISIBLE);
                        }
                        else {
                            if (searchResults != null && searchResults.getPlaces() != null) {
                                CustomAdapter adapter = new CustomAdapter(MainPage.this, searchResults.getPlaces());
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