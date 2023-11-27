package com.example.moija;

import static com.example.moija.MapFragment.API_KEY;
import static com.example.moija.MapFragment.BASE_URL;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.kakao.vectormap.KakaoMap;

import java.util.ArrayList;
import java.util.List;

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
    private int Searchcode=0;
    private boolean Startsearched=false;
    private boolean Goalsearched=false;
    private MapFragment.Place Startplace;
    private MapFragment.Place Goalplace;
    private EditText goalEditText;
    //검색결과를 담을 리스트뷰
    private ListView resultListView;
    //검색버튼
    private Button findbtn;
    private ImageButton backbutton;
    //카카오맵 (MainActivity의 kakaoMap과 연계하기위함)
    KakaoMap kakaoMap;
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_page);
        startEditText = findViewById(R.id.startEditText);
        goalEditText=findViewById(R.id.goalEditText);
        resultListView = findViewById(R.id.resultListView);
        backbutton=findViewById(R.id.backbutton);
        kakaoMap = MapFragment.thiskakaoMap;

        backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent=new Intent(SearchPage.this, Fragment_Chat_Map.class);
                startActivity(myIntent);
            }
        });
        startEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    // EditText에 포커스가 주어진 경우
                    String text =startEditText.getText().toString();
                    // 특정 조건을 확인하여 글자를 제거
                    if (Startsearched==true) {
                        Startsearched=false;
                        startEditText.setText(""); // EditText의 텍스트를 모두 제거
                    }
                    if(Goalsearched==false){
                        goalEditText.setText("");
                    }
                    resultListView.setVisibility(View.INVISIBLE);
                }
            }
        });
        goalEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    // EditText에 포커스가 주어진 경우
                    String text =goalEditText.getText().toString();
                    // 특정 조건을 확인하여 글자를 제거
                    if (Goalsearched==true) {
                        Goalsearched=false;
                        goalEditText.setText(""); // EditText의 텍스트를 모두 제거
                    }
                    if(Startsearched==false){
                        startEditText.setText("");
                    }
                    resultListView.setVisibility(View.INVISIBLE);
                }
            }
        });
        startEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                Searchcode=0;
                search(startEditText,0);
                return true;
            }
        });
        goalEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                Searchcode=1;
                search(goalEditText,1);
                return true;
            }
        });
        //검색결과중 하나를 누르면 
        resultListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(Searchcode==0)
                {
                    MapFragment.Place selected=(MapFragment.Place) resultListView.getItemAtPosition(position);
                    startEditText.setText("출발 위치: " + selected.getPlaceName());
                    Startplace=selected;
                    Startsearched=true;
                    Mylocation.StartPlace=selected;
                    if(Goalsearched==false)
                    {
                        goalEditText.requestFocus();
                        goalEditText.setText("");
                    }
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
                else if(Searchcode==1)
                {
                    MapFragment.Place selected=(MapFragment.Place) resultListView.getItemAtPosition(position);
                    goalEditText.setText("도착 위치: " + selected.getPlaceName());
                    Goalplace=selected;
                    Goalsearched=true;
                    Mylocation.selectedPlace=selected;
                    resultListView.setVisibility(View.INVISIBLE);
                    if(Startsearched==false)
                    {
                        startEditText.requestFocus();
                        startEditText.setText("");
                    }
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
                placeAddressTextView.setText(place.getAddressName());
                Location myplace=new Location("my location");
                Location findplace=new Location("finded location");
                findplace.setLatitude(place.getY());
                findplace.setLongitude(place.getX());
                placedistance.setText(Math.floor(Mylocation.Lastlocation.distanceTo(findplace)/1000)+"km");
            }

            return view;
        }
    }

    public void search(EditText searchbox,int searchcode) {

        String query = searchbox.getText().toString();
        if (!query.isEmpty()) {
            // Retrofit2를 사용하여 카카오맵 REST API에 검색 요청을 보냅니다.
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

                        SearchResponse searchResponse = response.body();
                        List<MapFragment.Place> searchedplace = searchResponse.getDocuments();
                        List<MapFragment.Place> filteredplace = new ArrayList<>();
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