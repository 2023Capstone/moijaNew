package com.example.moija.schedule;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.moija.ApiExplorer;
import com.example.moija.CustomAdapter;
import com.example.moija.MainPage;
import com.example.moija.R;
import com.example.moija.fragment.MapFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CityBus extends AppCompatActivity {

    private ImageButton backBtn;
    private Button getBusScheduleButton;
    private TextView busInfoTextView;
    private TextView busScheduleTextView;
    private ScrollView busScheduleView;
    private ListView stationNamesListView;
    private CustomAdapter adapter; // 커스텀 어댑터
    private ApiExplorer apiExplorer;
    ArrayList Station;
    private List<Integer> BusCityCode;
    private List<String> BusLocalBlID;
    private List<String> BusNo;
    Integer index;
    int state;
    private List<Integer> BusID;

    boolean isBusSchedule=false;

    class BusSchedule {

        private String firstTime;
        private String lastTime;
        private String interval;

        public String getFirstTime() {
            return firstTime;
        }

        public void setFirstTime(String firstTime) {
            this.firstTime = firstTime;
        }

        public String getLastTime() {
            return lastTime;
        }

        public void setLastTime(String lastTime) {
            this.lastTime = lastTime;
        }

        public String getInterval() {
            return interval;
        }

        public void setInterval(String interval) {
            this.interval = interval;
        }
    }

    class BusInfo {
        private String busNo;
        private ArrayList<String> stationNames;

        public String getBusNo() {
            return busNo;
        }

        public void setBusNo(String busNo) {
            this.busNo = busNo;
        }

        public ArrayList<String> getStationNames() {
            return stationNames;
        }

        public void setStationNames(ArrayList<String> stationNames) {
            this.stationNames = stationNames;
        }
    }
    private Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            List<String> nodeNames = apiExplorer.getNodeNames(); // ApiExplorer에서 nodeNames 가져오기
            int totalCount = msg.getData().getInt("totalCount");
            if(Station!=null && nodeNames!=null) {
                state=stationNamesListView.getFirstVisiblePosition();
                adapter = new CustomAdapter(CityBus.this, Station, nodeNames);
                Log.d("hMessage", Station.toString());
                Log.d("hMessage", nodeNames.toString());
                stationNamesListView.setAdapter(adapter);
                stationNamesListView.setSelection(state);
                adapter.notifyDataSetChanged();
            }
        }
    };

    // DP를 픽셀 단위로 변환하는 메소드
    public static int convertDpToPixel(float dp, Context context) {
        return (int) (dp * context.getResources().getDisplayMetrics().density);
    }

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_citybus);

        backBtn = findViewById(R.id.backBtn);
        getBusScheduleButton = findViewById(R.id.getBusScheduleButton);
        busInfoTextView = findViewById(R.id.busInfoTextView);
        stationNamesListView = findViewById(R.id.stationNamesListView);
        busScheduleTextView = findViewById(R.id.busScheduleTextView);
        busScheduleView=findViewById(R.id.busScheduleView);
        Intent intent = getIntent();
        MapFragment.BusData busData = (MapFragment.BusData) intent.getSerializableExtra("key");
        index = intent.getIntExtra("index", 0);
        BusCityCode = busData.getIntegerList();
        BusLocalBlID = busData.getBusLocalBlID();
        BusID=busData.getBusID();
        BusNo = busData.getBusNo();

        Log.d("yourlog", BusCityCode.toString());
        Log.d("yourlog", BusLocalBlID.toString());
        apiExplorer = new ApiExplorer(handler);  // Handler 전달
        apiExplorer.BusCityCode = BusCityCode;
        apiExplorer.BusLocalBlIDs = BusLocalBlID;
        apiExplorer.index = index;

        Log.d("hMessage", BusCityCode.toString());
        Log.d("hMessage", BusLocalBlID.toString());

        new Thread(apiExplorer).start();
        new GetStationNamesTask().execute();

        // Toolbar에 이미지 뷰와 텍스트 뷰 추가
        LinearLayout busLayout = findViewById(R.id.busLayout); // Toolbar가 LinearLayout 또는 RelativeLayout 내에 있어야 함
        busLayout.setWeightSum(BusCityCode.size()); // 모든 뷰가 균등하게 배치되도록 weightSum 설정

        for (int i = 0; i < BusCityCode.size(); i++) {
            // 각 이미지와 텍스트를 위한 LinearLayout 생성
            LinearLayout singleBusLayout = new LinearLayout(this);
            singleBusLayout.setOrientation(LinearLayout.VERTICAL); // 수직 방향 설정
            LinearLayout.LayoutParams singleBusLayoutParams = new LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    1.0f // weight 설정
            );
            singleBusLayout.setLayoutParams(singleBusLayoutParams);

            // ImageView 생성 및 설정
            ImageView imageView = new ImageView(this);
            imageView.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    convertDpToPixel(50, this)
            ));
            imageView.setImageResource(R.drawable.bus);

            // TextView 생성 및 설정
            TextView textView = new TextView(this);
            textView.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            ));
            textView.setText(BusNo.get(i)); // 버스 번호 설정
            textView.setGravity(Gravity.CENTER);

            // LinearLayout에 ImageView와 TextView 추가
            singleBusLayout.addView(imageView);
            singleBusLayout.addView(textView);

            // 클릭 이벤트 설정
            int value = i;
            boolean isintercitybus=BusCityCode.get(i).equals(0);
            singleBusLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(isintercitybus) {
                        Intent intent = new Intent(CityBus.this, IntercityBus.class);
                        intent.putExtra("key", busData);
                        intent.putExtra("index", value);
                        Log.d("value_index", Integer.toString(value));
                        startActivity(intent);
                    }else{
                        Intent intent = new Intent(CityBus.this, CityBus.class);
                        intent.putExtra("key", busData);
                        intent.putExtra("index", value);
                        Log.d("value_index", Integer.toString(value));
                        startActivity(intent);
                    }
                }
            });

            // 메인 LinearLayout에 singleBusLayout 추가
            busLayout.addView(singleBusLayout);
        }

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CityBus.this, MainPage.class);
                startActivity(intent);
            }
        });

        getBusScheduleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isBusSchedule){
                    new GetBusScheduleTask().execute();
                    isBusSchedule=true;
                    getBusScheduleButton.setText("버스 정류장");
                }else{
                    new GetStationNamesTask().execute();
                    isBusSchedule=false;
                    getBusScheduleButton.setText("버스 시간표");
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        apiExplorer.start(); // 액티비티 시작 시 스레드 시작
    }

    @Override
    protected void onStop() {
        super.onStop();
        apiExplorer.stop(); // 액티비티 중단 시 스레드 중단
    }
    private class GetBusScheduleTask extends AsyncTask<Void, Void, BusSchedule> {
        @Override

        protected BusSchedule doInBackground(Void... voids) {
            try {

                String busID = BusID.get(index).toString();
                String apiUrl = "https://api.odsay.com/v1/api/busLaneDetail?lang=&busID=" + busID + "&apiKey=" + MainPage.OdsayAPI_KEY;
                URL url = new URL(apiUrl);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                try {int responseCode = urlConnection.getResponseCode();
                    if (responseCode != HttpURLConnection.HTTP_OK) {
                        return null;
                    }
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;

                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line).append("\n");
                    }
                    JSONObject jsonResponse = new JSONObject(stringBuilder.toString());
                    JSONObject result = jsonResponse.getJSONObject("result");
                    if(result == null){
                        return null;
                    }
                    String firstTime = result.optString("busFirstTime", "N/A");
                    String lastTime = result.optString("busLastTime", "N/A");
                    String interval = result.optString("busInterval", "N/A");
                    BusSchedule busSchedule = new BusSchedule();
                    busSchedule.setFirstTime(firstTime);
                    busSchedule.setLastTime(lastTime);
                    busSchedule.setInterval(interval);
                    return busSchedule;
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                    return null;
                } finally {
                    urlConnection.disconnect();
                }
            }catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        public ArrayList<String> generateBusSchedule(String busFirstTime, String busLastTime, String busInterval) {
            // 시간 형식을 파싱하여 Date 객체로 변환
            SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
            Date firstTime;
            Date lastTime;
            try {
                firstTime = dateFormat.parse(busFirstTime);
                lastTime = dateFormat.parse(busLastTime);
            } catch (ParseException e) {
                e.printStackTrace();
                return null;
            }

            // 운행 간격을 분 단위로 변환
            int interval = Integer.parseInt(busInterval);

            ArrayList<String> busSchedule = new ArrayList<>();
            Date currentTime = new Date(firstTime.getTime());
            while (currentTime.compareTo(lastTime) <= 0) {
                busSchedule.add(dateFormat.format(currentTime)+"\n");
                currentTime.setTime(currentTime.getTime() + interval * 60 * 1000);
            }

            return busSchedule;
        }

        @Override

        protected void onPostExecute(BusSchedule result) {


            if (result != null) {
                stationNamesListView.setVisibility(View.GONE);
                ArrayList<String> resultSchedule = generateBusSchedule(result.getFirstTime(), result.getLastTime(), result.getInterval());
                busScheduleTextView.setText(resultSchedule.toString());
                busScheduleView.setVisibility(View.VISIBLE);
                busScheduleTextView.setVisibility(View.VISIBLE);
            } else {
                // Handle the case where data retrieval failed
            }
        }
    }

    private class GetStationNamesTask extends AsyncTask<Void, Void, BusInfo> {
        @Override
        protected BusInfo doInBackground(Void... voids) {
            try {
                // TODO: 여기에 버스 노선 상세 조회에서 얻은 busID를 입력하세요.
                String busID = BusID.get(index).toString();

                // API 호출을 위한 URL
                String apiUrl = "https://api.odsay.com/v1/api/busLaneDetail?lang=&busID=" + busID + "&apiKey=" + MainPage.OdsayAPI_KEY;

                URL url = new URL(apiUrl);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

                try {
                    int responseCode = urlConnection.getResponseCode();
                    if (responseCode != HttpURLConnection.HTTP_OK) {
                        return null;
                    }

                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line).append("\n");
                    }
                    bufferedReader.close();

                    // JSON 파싱
                    JSONObject jsonResponse = new JSONObject(stringBuilder.toString());
                    JSONObject result = jsonResponse.getJSONObject("result");
                    JSONArray stationsArray = result.getJSONArray("station");

                    String busNo = result.getString("busNo");

                    ArrayList<String> stationNames = new ArrayList<>();
                    for (int i = 0; i < stationsArray.length(); i++) {
                        JSONObject station = stationsArray.getJSONObject(i);
                        String stationName = station.getString("stationName");
                        stationNames.add(stationName);
                    }

                    BusInfo busInfo = new BusInfo();
                    busInfo.setBusNo(busNo);
                    busInfo.setStationNames(stationNames);

                    return busInfo;
                } finally {
                    urlConnection.disconnect();
                }
            } catch (Exception e) {
                return null;
            }
        }

        protected void onPostExecute(BusInfo result) {
            // UI에 결과를 표시
            if (result != null) {
                busScheduleView.setVisibility(View.GONE);
                busScheduleTextView.setVisibility(View.GONE);

                busInfoTextView.setText("Bus No: " + result.getBusNo());
                Station = result.getStationNames();
                Log.d("CityBus",result.getStationNames().toString());
                adapter = new CustomAdapter(CityBus.this, Station, Station);
                stationNamesListView.setAdapter(adapter);

                stationNamesListView.setVisibility(View.VISIBLE);
            } else {
                // Handle the case where data retrieval failed
            }
        }
    }
}



