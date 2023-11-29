package com.example.moija;

import static com.example.moija.time.DateTime.getCurrentDateTime;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.moija.api.KakaoApi;
import com.example.moija.api.ODsayService;
import com.example.moija.data.CallApiData;
import com.example.moija.data.OdsayData;
import com.example.moija.data.PathInfo;
import com.example.moija.fragment.MapFragment;
import com.example.moija.map.Mylocation;
import com.example.moija.map.Place;
import com.example.moija.map.SearchResults;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.stream.Collectors;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

//검색 페이지에 관한 내용
public class MainPage extends AppCompatActivity {
    private EditText startEditText, goalEditText;
    //시작점을 검색하는건지 도착지점을 검색하는지 나누는 변수 (0: 시작점 1: 도착점)
    private int Searchcode = 0;
    public static final String API_KEY = "ab4624b190ebccd6369144de2502ad14";

    public boolean pathsearched=false;
    public static final String OdsayAPI_KEY = "fXCWmI16V2ggA9Y9OhTrVMSiPw/YHkDXoHmKjpLG7l8";
    //api 기본 URL
    public static final String BASE_URL = "https://dapi.kakao.com/";

    public static final String BASE_URL2 = "https://api.odsay.com/";
    public static final String OdsayBASE_URL = "https://api.odsay.com/";
    private ImageButton mylocbtn;

    private ListView recordPlaceList;
    private RecordPlaceDB recordPlaceDB;
    private ArrayAdapter<String> recordAdapter;
    private Queue<String> dataList;
    private static final int MAX_QUEUE_SIZE = 10;
    private ImageButton dataclear;
    //시작점을 정했는지, 도착점을 정했는지
    private boolean Startsearched, Goalsearched = false;
    //검색결과를 담을 리스트뷰
    private ListView resultListView, searchPathListView;
    Fragment MapFragment;

    FrameLayout Mapframelayout;

    //데이터 저장
    private List<PathInfo> pathInfoList = new ArrayList<>();
    private ArrayAdapter<String> listViewadapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);
        startEditText = findViewById(R.id.startEditText);
        goalEditText = findViewById(R.id.goalEditText);
        resultListView = findViewById(R.id.resultListView);
        mylocbtn = findViewById(R.id.mylocbtn);
        Mapframelayout = findViewById(R.id.Mapframe);
        dataList = new LinkedList<>();
        dataclear = findViewById(R.id.dataclear);
        recordPlaceList = findViewById(R.id.recordPlaceList);
        searchPathListView = findViewById(R.id.searchPathListView);
        recordAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, new ArrayList<>(dataList));
        recordPlaceDB = new RecordPlaceDB(getApplicationContext());
        MapFragment = new MapFragment();
        recordPlaceList.setAdapter(recordAdapter);
        updateList();
        getSupportFragmentManager().beginTransaction().replace(R.id.Mapframe, MapFragment).commit();
        //키보드 제어
        InputMethodManager Keyboardmanager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

        listViewadapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, new ArrayList<>());

        //데이터베이스 삭제
        dataclear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SQLiteDatabase database = recordPlaceDB.getWritableDatabase();
                String deleteQuery = "DELETE FROM recordPlace_DB";
                database.execSQL(deleteQuery);
                updateList();
            }
        });
        //검색기록 누르면 자동 입력되게 함
        recordPlaceList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // 클릭한 아이템의 값을 가져와서 입력 필드에 표시
                Startsearched=false;
                Goalsearched=false;
                String selectedItem = dataList.toArray(new String[0])[position];
                String[] parts = selectedItem.split(" - ");
                pathsearched=false;
                if (parts.length == 2) {
                    searchAndSet(parts[0],"start",false);
                    searchAndSet(parts[1],"goal",false);

                }
            }
        });

        //시작점을 검색하는 EditText에 focus되었을때
        startEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    Searchcode = 0;
                    //다시 지우는 번거로움을 없애기 위해 자동으로 비운다
                    if (Startsearched == true) {
                        Startsearched = false;
                        startEditText.setText("");
                    }
                    if (Goalsearched == false) {
                        goalEditText.setText("");
                    }
                    //검색결과 숨김
                    Mapframelayout.setVisibility(View.GONE);
                    searchPathListView.setVisibility(View.GONE);
                    recordPlaceList.setVisibility(View.VISIBLE);
                    resultListView.setVisibility(View.GONE);
                }
            }
        });
        //도착점을 검색하는 EditText에 focus되었을때, startEditText와 같은 로직
        goalEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    Searchcode = 1;
                    if (Goalsearched == true) {
                        Goalsearched = false;
                        goalEditText.setText("");
                    }
                    if (Startsearched == false) {
                        startEditText.setText("");
                    }
                    //검색결과 숨김
                    Mapframelayout.setVisibility(View.GONE);
                    searchPathListView.setVisibility(View.GONE);
                    recordPlaceList.setVisibility(View.VISIBLE);
                    resultListView.setVisibility(View.GONE);
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
                Searchcode = 0;
                if(Startsearched==false)
                {
                    search(startEditText, 0);
                    recordPlaceList.setVisibility(View.GONE);
                    resultListView.setVisibility(View.VISIBLE);
                }
                if(startEditText.getText().toString().isEmpty())
                {
                    recordPlaceList.setVisibility(View.VISIBLE);
                    resultListView.setVisibility(View.GONE);
                }
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
                Searchcode = 1;
                if(Goalsearched==false)
                {
                    search(goalEditText, 1);
                    recordPlaceList.setVisibility(View.GONE);
                    resultListView.setVisibility(View.VISIBLE);
                }
                if(goalEditText.getText().toString().isEmpty())
                {
                    recordPlaceList.setVisibility(View.VISIBLE);
                    resultListView.setVisibility(View.GONE);
                }
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
        searchPathListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                PathInfo selectedpath=(PathInfo) searchPathListView.getItemAtPosition(position);
                Mapframelayout.setVisibility(View.VISIBLE);
                MapFragment mf = (MapFragment) getSupportFragmentManager().findFragmentById(R.id.Mapframe);
                mf.DrawingPath=selectedpath;
                if(mf.routeDrawer!=null) {
                    mf.routeDrawer.clearRouteLines();

                    mf.Draw();
                }else if(mf.routeDrawer==null){
                    return;
                }
            }
        });
        //검색결과중 하나를 누르면
        resultListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            //검색결과 중 하나 클릭하면
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Place selected = (Place) resultListView.getItemAtPosition(position);
                //출발위치를 선택한 위치로 결정
                if (Searchcode == 0) {
                    setStartPlace(selected);
                    //아직 도착점 안정했으면
                    if (Goalsearched == false) {
                        //도착점 검색창에 focus를 넘겨줌
                        goalEditText.requestFocus();
                        goalEditText.setText("");
                        //키보드 올림
                        Keyboardmanager.showSoftInput(goalEditText, InputMethodManager.SHOW_IMPLICIT);
                    }
                    //도착점 정해져있으면
                    else if (Goalsearched == true) {
                        addRecord();
                    }
                }
                //도착점을 찾는중이었다면
                else if (Searchcode == 1) {
                    //도착위치를 선택한 위치로 결정
                    setGoalPlace(selected);
                    //시작점 안정했으면
                    if (Startsearched == false) {
                        //시작점 검색창으로 focus 이동
                        startEditText.requestFocus();
                        startEditText.setText("");
                        //키보드 올림
                        Keyboardmanager.showSoftInput(startEditText, InputMethodManager.SHOW_IMPLICIT);
                    }
                    //시작점 정해져있으면
                    else if (Startsearched == true) {
                        //검색 결과 기록
                        addRecord();
                    }
                }
                String txt = String.valueOf(Startsearched);
                String txt2 = String.valueOf(Goalsearched);
                Log.d("mylog2", txt + txt2);
                if (Goalsearched && Startsearched) {
                    Log.d("pathSearch", "searchpath is being called");
                    searchpath();
                    pathsearched=true;
                    resultListView.setVisibility(View.GONE);
                    recordPlaceList.setVisibility(View.GONE);
                    searchPathListView.setVisibility(View.VISIBLE);
                }
            }
        });
        mylocbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Mylocation.Lastlocation != null) {
                    //위치에 따라 주소 찾고 설정하는 메서드 넣기
                    Mapframelayout.setVisibility(View.GONE);
                    FindMyAddress();
                }
            }
        });

    }

    //도착점 설정 메서드
    public void setGoalPlace(Place place) {
        goalEditText.setText(place.getPlaceName());
        Goalsearched = true;
        Mylocation.GoalPlace = place;
        resultListView.setVisibility(View.GONE);
    }

    //출발점 설정 메서드
    public void setStartPlace(Place place) {
        startEditText.setText(place.getPlaceName());
        Startsearched = true;
        Mylocation.StartPlace = place;
        resultListView.setVisibility(View.GONE);
    }

    //검색기록 추가
    public void addRecord() {

        String start = startEditText.getText().toString();
        String end = goalEditText.getText().toString();

        SQLiteDatabase database = recordPlaceDB.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("startPlace", start);
        values.put("endPlace", end);
        values.put("time", getCurrentDateTime());
        long newRowId = database.insert("recordPlace_DB", null, values);

        if (newRowId == -1) {
            // 데이터베이스에 추가 실패한 경우
            Toast.makeText(getApplicationContext(), "데이터베이스에 정보를 추가하는 데 실패했습니다.", Toast.LENGTH_SHORT).show();
        } else {
            // 데이터베이스에 성공적으로 추가한 경우
            Toast.makeText(getApplicationContext(), "데이터베이스에 정보를 추가했습니다.", Toast.LENGTH_SHORT).show();
            dataMaxRows(database);
            // 데이터베이스 업데이트 후 리스트 업데이트
            updateList();
        }
    }

    private void updateList() {
        dataList.clear();
        SQLiteDatabase database = recordPlaceDB.getReadableDatabase();

        Cursor cursor = database.query("recordPlace_DB", null, null, null, null, null, "time DESC", "10");

        int startPlaceIndex = cursor.getColumnIndex("startPlace");
        int endPlaceIndex = cursor.getColumnIndex("endPlace");

        if (cursor.moveToFirst()) {
            do {
                String startPlace = cursor.getString(startPlaceIndex);
                String endPlace = cursor.getString(endPlaceIndex);
                dataList.offer(startPlace + " - " + endPlace);

                if (dataList.size() > MAX_QUEUE_SIZE) {
                    dataList.poll();
                }
            } while (cursor.moveToNext());
        } else {
            // 데이터가 없음을 사용자에게 알림
            Toast.makeText(getApplicationContext(), "검색 결과가 없습니다.", Toast.LENGTH_SHORT).show();
            Log.d("updateList", "커서가 데이터를 가리키지 않습니다.");
        }
        cursor.close(); // 커서 사용 후 닫기

        // 어댑터 업데이트
        recordAdapter.clear();
        recordAdapter.addAll(dataList);
        recordAdapter.notifyDataSetChanged();
        Log.d("updateList", "리스트가 업데이트되었습니다: " + dataList.size());
    }

    private void dataMaxRows(SQLiteDatabase database) {
        Cursor cursor = database.rawQuery("SELECT COUNT(*) FROM recordPlace_DB", null);
        int rowCount = 0;
        if (cursor.moveToFirst()) {
            rowCount = cursor.getInt(0);
        }
        cursor.close();

        if (rowCount > MAX_QUEUE_SIZE) {
            String deleteQuery = "DELETE FROM recordPlace_DB WHERE time IN (SELECT MIN(time) FROM recordPlace_DB)";
            database.execSQL(deleteQuery);
        }
    }

    //검색한 결과를 바로 시작점/도착점으로 설정할 때 사용(내 위치를 시작점에 넣을때)
    //startorgoal(시작점인지 도착점인지) searchbymyloc(내 위치기준으로 검색하는지)
    public void searchAndSet(String query, String startorgoal, boolean searchbymyloc) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        KakaoApi kakaoapi = retrofit.create(KakaoApi.class);
        Call<SearchResults> call = null;
        //내 위치 기준으로 검색하는거면 현재위치에서 가장 가까운 1km 내의 건물명을 찾음
        if (searchbymyloc == true) {
            call = kakaoapi.searchNearPlace("KakaoAK " + API_KEY, query, Mylocation.Lastlocation.getLongitude(), Mylocation.Lastlocation.getLatitude(), 1000);
        } else {
            call = kakaoapi.searchPlaces("KakaoAK " + API_KEY, query);
        }
        call.enqueue(new Callback<SearchResults>() {
            @Override
            public void onResponse(Call<SearchResults> call, Response<SearchResults> response) {
                if (response.isSuccessful()) {
                    SearchResults searchResults = response.body();
                    List<Place> places = searchResults.getPlaces();
                    if (places.get(0).getPlaceName() != null) {
                        if (startorgoal.equals("start")) {
                            setStartPlace(places.get(0));
                        } else if (startorgoal.equals("goal")) {
                            setGoalPlace(places.get(0));
                        }
                    }
                    if(Startsearched && Goalsearched && !pathsearched){
                        searchpath();
                        pathsearched=true;
                        resultListView.setVisibility(View.GONE);
                        recordPlaceList.setVisibility(View.GONE);
                        searchPathListView.setVisibility(View.VISIBLE);
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
    public void FindMyAddress() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        KakaoApi kakaoapi = retrofit.create(KakaoApi.class);
        Call<SearchResults.LoctoAddResult> call = null;
        if (Mylocation.Lastlocation != null) {
            call = kakaoapi.getAddressWithLocation("KakaoAK " + API_KEY, Mylocation.Lastlocation.getLongitude(), Mylocation.Lastlocation.getLatitude());
        }
        call.enqueue(new Callback<SearchResults.LoctoAddResult>() {
            @Override
            public void onResponse(Call<SearchResults.LoctoAddResult> call, Response<SearchResults.LoctoAddResult> response) {
                if (response.isSuccessful()) {
                    SearchResults.LoctoAddResult searchResults = response.body();
                    SearchResults.LoctoAddResult.RoadAddress roadaddress = searchResults.getDocuments().get(0).getRoad_address();
                    //위치에 따라 도로명주소(roadaddress)가 null이 될때가 있음
                    //따라서 실행전에 null값인지 미리 확인한다
                    if (roadaddress != null) {
                        //주변 건물이 존재하면
                        if (roadaddress.getBuilding_name() != null && !roadaddress.getBuilding_name().isEmpty()) {
                            //해당 건물명을 searchAndSet을 통해 넘겨주고,시작점으로 결정
                            searchAndSet(roadaddress.getBuilding_name(), "start", true);
                        } else if (roadaddress == null || roadaddress.getBuilding_name() == null || roadaddress.getBuilding_name().isEmpty()) {
                            //건물이 없으면 주소명이 있는지 확인하고 searchAndSet에 넘겨주어 해당 주소에 가까이 있는 건물명을 찾고
                            //시작점으로 설정한다.
                            if (searchResults.getDocuments().get(0).getAddress() != null) {
                                searchAndSet(searchResults.getDocuments().get(0).getAddress().getAddress_name(), "start", true);
                            } else {
                                //그냥 주소명도 검색이 안될 경우에는 토스트 메시지로 검색이 안됨을 안내
                                Toast.makeText(getApplicationContext(), "검색된 장소가 없습니다.", Toast.LENGTH_SHORT);
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
    public void search(EditText searchbox, int searchcode) {
        String query = searchbox.getText().toString();
        if (!query.isEmpty()) {
            // Retrofit2를 사용하여 카카오맵 REST API에 검색 요청을 보냄
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            KakaoApi kakaoapi = retrofit.create(KakaoApi.class);
            Call<SearchResults> call;
            //현재위치가 정해져있으면 현재위치를 기준으로 가까운 곳을 검색한다.
            if (Mylocation.Lastlocation != null) {
                call = kakaoapi.searchPlacesWithMyLocation("KakaoAK " + API_KEY, query, Mylocation.Lastlocation.getLongitude(), Mylocation.Lastlocation.getLatitude());
            } //정해져있지 않으면 그냥 검색함
            else {
                call = kakaoapi.searchPlaces("KakaoAK " + API_KEY, query);
            }
            call.enqueue(new Callback<SearchResults>() {
                @Override
                public void onResponse(Call<SearchResults> call, Response<SearchResults> response) {
                    if (response.isSuccessful()) {
                        //SearchResults에 api 응답을 받고
                        SearchResults searchResults = response.body();
                        //검색된 장소를 searchedplace리스트에 Place형태로 저장함
                        List<Place> searchedplace = searchResults.getPlaces();
                        //filteredplace는 이미 검색한 장소를 제외한 장소들을 표시
                        List<Place> filteredplace = new ArrayList<>();
                        //도착점과 시작점 중복 설정 방지를 위한 코드들
                        if (searchcode == 0 && Goalsearched == true) {
                            for (Place searchplace : searchedplace) {
                                if (!(searchplace.getPlaceName().equals(Mylocation.GoalPlace.getPlaceName()))) {
                                    filteredplace.add(searchplace);
                                }
                            }
                            if (searchResults != null && searchResults.getPlaces() != null) {
                                SearchAdapter adapter = new SearchAdapter(MainPage.this, filteredplace);
                                resultListView.setAdapter(adapter);
                            }

                        }
                        if (searchcode == 1 && Startsearched == true) {

                            for (Place searchplace : searchedplace) {
                                if (!(searchplace.getPlaceName().equals(Mylocation.StartPlace.getPlaceName()))) {
                                    filteredplace.add(searchplace);
                                }
                            }
                            if (searchResults != null && searchResults.getPlaces() != null) {
                                SearchAdapter adapter = new SearchAdapter(MainPage.this, filteredplace);
                                resultListView.setAdapter(adapter);
                            }

                        } else {
                            if (searchResults != null && searchResults.getPlaces() != null) {
                                SearchAdapter adapter = new SearchAdapter(MainPage.this, searchResults.getPlaces());
                                resultListView.setAdapter(adapter);
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
    }

    //출발지와 도착지가 다 정해지면 발동하는 클래스
    public void searchpath() {
        //처음은 출발지, 도착지 | 위도 경도를 받는다
        callApi(Mylocation.StartPlace.getX(), Mylocation.StartPlace.getY(),
                Mylocation.GoalPlace.getX(), Mylocation.GoalPlace.getY()
                , null);
    }
    private void callApi(double startX, double startY, double endX, double endY, PathInfo MypathInfo) {
       HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY); // 로그 수준 설정
        // OkHttp 클라이언트에 로깅 인터셉터 추가
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL2)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();
        ODsayService odsayApi = retrofit.create(ODsayService.class);

        Call<OdsayData> call = odsayApi.searchPublicTransitPath(OdsayAPI_KEY, startX, startY, endX, endY);
        call.enqueue(new Callback<OdsayData>() {
            @Override
            public void onResponse(Call<OdsayData> call, Response<OdsayData> response) {
                if (response.isSuccessful()) {
                    pathInfoList.clear();
                    OdsayData searchResult = response.body();
                    busLogic(searchResult);
                }
            }

            @Override
            public void onFailure(Call<OdsayData> call, Throwable t) {
                t.printStackTrace();
                Log.d("odsay", "API call failed: " + t.getMessage());
            }
        });
    }

    private void callApi1(double startX, double startY, double endX, double endY, PathInfo MypathInfo) {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL2)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ODsayService odsayApi = retrofit.create(ODsayService.class);

        Call<OdsayData> call = odsayApi.searchPublicTransitPath(OdsayAPI_KEY, startX, startY, endX, endY);
        call.enqueue(new Callback<OdsayData>() {
            @Override
            public void onResponse(Call<OdsayData> call, Response<OdsayData> response) {
                if (response.isSuccessful()) {
                    OdsayData searchResult = response.body();
                    Gson json=new Gson();
                    String j=json.toJson(searchResult);
                    Log.d("mylog",j);

                    busLogicAdd(searchResult,MypathInfo,"start");
                }
            }

            @Override
            public void onFailure(Call<OdsayData> call, Throwable t) {
                t.printStackTrace();
                Log.d("odsay", "API call failed: " + t.getMessage());
            }
        });
    }
    private void callApi2(double startX, double startY, double endX, double endY, PathInfo MypathInfo) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL2)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ODsayService odsayApi = retrofit.create(ODsayService.class);
        Call<OdsayData> call = odsayApi.searchPublicTransitPath(OdsayAPI_KEY, startX, startY, endX, endY);
        call.enqueue(new Callback<OdsayData>() {
            @Override
            public void onResponse(Call<OdsayData> call, Response<OdsayData> response) {
                if (response.isSuccessful()) {
                    OdsayData searchResult = response.body();
                    Gson json=new Gson();
                    String j=json.toJson(searchResult);
                    Log.d("mylog",j);

                    busLogicAdd(searchResult,MypathInfo,"end");
                }
            }
            @Override
            public void onFailure(Call<OdsayData> call, Throwable t) {
                t.printStackTrace();
                Log.d("odsay", "API call failed: " + t.getMessage());
            }
        });
    }
    private void busLogicAdd(OdsayData searchResult,PathInfo myPathInfo,String startorend) {
        int count = 0;
            PathInfo pathInfo = new PathInfo();
            for (OdsayData.Path path : searchResult.getResult().getPath()) {
                if (path.getPathType() == 2) {
                    pathInfo.setTotalTime(path.getInfo().getTotalTime());
                    for (int i = 0; i < path.getSubPath().size(); i++) {
                        if (path.getSubPath().get(i).getTrafficType() == 3) {//도보
                            List<String> busNos = new ArrayList<>();
                            busNos.add("도보");
                            //도보는 x,y값은 없고 시간만 나타내고 있음
                            //따라서, x,y값을 받으려면 for문으로 index값을 통하여 x,y값을 받아오는 수밖에 없음
                            if (i == 0) {
                                if (startorend == "start") {
                                    pathInfo.setSubPath(busNos, Mylocation.StartPlace.getPlaceName(), path.getSubPath().get(i + 1).getStartName(), Mylocation.StartPlace.getX(), Mylocation.StartPlace.getY(), path.getSubPath().get(i + 1).getStartX(), path.getSubPath().get(i + 1).getStartY(), path.getSubPath().get(i).getTrafficType());
                                }
                                if (startorend == "end") {
                                    pathInfo.setSubPath(busNos, myPathInfo.getEndNames().get(myPathInfo.getEndNames().size() - 1), path.getSubPath().get(i + 1).getStartName(), myPathInfo.getendx().get(myPathInfo.getendx().size() - 1),
                                            myPathInfo.getendy().get(myPathInfo.getendy().size() - 1), path.getSubPath().get(i + 1).getStartX(), path.getSubPath().get(i + 1).getStartY(), path.getSubPath().get(i).getTrafficType());
                                }
                            } else if (i == path.getSubPath().size() - 1) {
                                if (startorend == "start") {
                                    pathInfo.setSubPath(busNos, path.getSubPath().get(i - 1).getEndName(), myPathInfo.getStartNames().get(0), path.getSubPath().get(i - 1).getEndX(), path.getSubPath().get(i - 1).getEndY(), myPathInfo.getstartx().get(0), myPathInfo.getstarty().get(0), path.getSubPath().get(i).getTrafficType());
                                }
                                if (startorend == "end") {
                                    pathInfo.setSubPath(busNos, path.getSubPath().get(i - 1).getEndName(), Mylocation.GoalPlace.getPlaceName(), path.getSubPath().get(i - 1).getEndX(), path.getSubPath().get(i - 1).getEndY(), Mylocation.GoalPlace.getX(), Mylocation.GoalPlace.getY(), path.getSubPath().get(i).getTrafficType());
                                }
                            } else {
                                pathInfo.setSubPath(busNos, path.getSubPath().get(i - 1).getEndName(), path.getSubPath().get(i + 1).getStartName(), path.getSubPath().get(i - 1).getEndX(), path.getSubPath().get(i - 1).getEndY(), path.getSubPath().get(i + 1).getStartX(), path.getSubPath().get(i + 1).getStartY(), path.getSubPath().get(i).getTrafficType());
                            }
                            count++;
                        } else if (path.getSubPath().get(i).getTrafficType() == 2) { // 버스 경로인 경우
                            List<String> busNos = path.getSubPath().get(i).getLane().stream()
                                    .map(OdsayData.Lane::getBusNo)
                                    .collect(Collectors.toList());
                            pathInfo.setSubPath(busNos, path.getSubPath().get(i).getStartName(), path.getSubPath().get(i).getEndName(), path.getSubPath().get(i).getStartX(), path.getSubPath().get(i).getStartY(), path.getSubPath().get(i).getEndX(), path.getSubPath().get(i).getEndY(), path.getSubPath().get(i).getTrafficType());
                            count++;
                        }
                    }
                    if (startorend.equals("start")) {
                        myPathInfo.addPathinfo(pathInfo, 0);
                    } else if (startorend.equals("end")) {
                        myPathInfo.addPathinfo(pathInfo, myPathInfo.getBusNos().size());
                        pathInfoList.add(myPathInfo);
                    }

                    if (count >= 3) {
                        break; // 최대 3개의 Path 객체만 처리하고 루프를 중단
                    }
                }
            }
        PathAdapter pathadapter=new PathAdapter(this,pathInfoList);
        searchPathListView.setAdapter(pathadapter);
    }
    private void busLogic(OdsayData searchResult) {
        pathInfoList.clear();
        int count = 0;
        for (OdsayData.Path path : searchResult.getResult().getPath()) {
            PathInfo pathInfo=new PathInfo();
            // 도시내 길찾기의 경우
            if (searchResult.getResult().getSearchType()==0) {
                pathInfo.setTotalTime(path.getInfo().getTotalTime());
                if (path.getPathType() == 2) {
                    for (int i = 0; i < path.getSubPath().size(); i++) {
                        if (path.getSubPath().get(i).getTrafficType() == 3) {//도보
                            List<String> busNos = new ArrayList<>();
                            busNos.add("도보");
                            //도보는 x,y값은 없고 시간만 나타내고 있음
                            //따라서, x,y값을 받으려면 for문으로 index값을 통하여 x,y값을 받아오는 수밖에 없음
                            if (i == 0) {
                                pathInfo.setSubPath(busNos, Mylocation.StartPlace.getPlaceName(), path.getSubPath().get(i + 1).getStartName(), Mylocation.StartPlace.getX(), Mylocation.StartPlace.getY(), path.getSubPath().get(i + 1).getStartX(), path.getSubPath().get(i + 1).getStartY(), path.getSubPath().get(i).getTrafficType());
                            } else if (i == path.getSubPath().size() - 1) {
                                pathInfo.setSubPath(busNos, path.getSubPath().get(i - 1).getEndName(), Mylocation.GoalPlace.getPlaceName(), path.getSubPath().get(i - 1).getEndX(), path.getSubPath().get(i - 1).getEndY(), Mylocation.GoalPlace.getX(), Mylocation.GoalPlace.getY(), path.getSubPath().get(i).getTrafficType());
                            } else {
                                pathInfo.setSubPath(busNos, path.getSubPath().get(i - 1).getEndName(), path.getSubPath().get(i + 1).getStartName(), path.getSubPath().get(i - 1).getEndX(), path.getSubPath().get(i - 1).getEndY(), path.getSubPath().get(i + 1).getStartX(), path.getSubPath().get(i + 1).getStartY(), path.getSubPath().get(i).getTrafficType());
                            }

                        } else if (path.getSubPath().get(i).getTrafficType() == 2) { // 버스 경로인 경우
                            List<String> busNos = path.getSubPath().get(i).getLane().stream()
                                    .map(OdsayData.Lane::getBusNo)
                                    .collect(Collectors.toList());
                            pathInfo.setSubPath(busNos, path.getSubPath().get(i).getStartName(), path.getSubPath().get(i).getEndName(), path.getSubPath().get(i).getStartX(), path.getSubPath().get(i).getStartY(), path.getSubPath().get(i).getEndX(), path.getSubPath().get(i).getEndY(), path.getSubPath().get(i).getTrafficType());


                        }
                    }
                    pathInfoList.add(pathInfo);
                    count++;
                    //도시간 길찾기의 경우
                }
            }else if (searchResult.getResult().getSearchType() == 1) {
                pathInfo.setTotalTime(path.getInfo().getTotalTime());
                //시외/고속버스 경로일때
                if(path.getPathType()==12) {
                    List<OdsayData.SubPath> subPath=path.getSubPath();
                    for (int i=0; i<subPath.size(); i++) {
                        List<String> busNos=new ArrayList<>();
                        busNos.add("시외버스");
                        pathInfo.setSubPath(busNos, subPath.get(i).getStartName(), subPath.get(i).getEndName(), subPath.get(i).getStartX(), subPath.get(i).getStartY(), subPath.get(i).getEndX(), subPath.get(i).getEndY(), subPath.get(i).getTrafficType());
                    }
                    pathInfoList.clear();
                    callApi1(Mylocation.StartPlace.getX(), Mylocation.StartPlace.getY(), pathInfo.getstartx().get(0), pathInfo.getstarty().get(0), pathInfo);
                    callApi2(pathInfo.getendx().get(pathInfo.getendx().size()-1),pathInfo.getendy().get(pathInfo.getendy().size()-1),Mylocation.GoalPlace.getX(),Mylocation.GoalPlace.getY(),pathInfo);
                    count++;
                }
                }

            // 처리한 Path 객체의 수 증가
            if (count >= 3) {
                break; // 최대 3개의 Path 객체만 처리하고 루프를 중단
            }
        }
        PathAdapter pathadapter = new PathAdapter(this, pathInfoList);
        searchPathListView.setAdapter(pathadapter);
    }

}