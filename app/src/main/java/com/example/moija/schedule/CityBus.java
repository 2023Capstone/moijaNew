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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.moija.ApiExplorer;
import com.example.moija.CustomAdapter;
import com.example.moija.MainPage;
import com.example.moija.R;
import com.example.moija.busPointGPS;
import com.example.moija.data.PathInfo;
import com.example.moija.fragment.MapFragment;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class CityBus extends AppCompatActivity {
    private ImageButton backBtn;
    private Button getStationNamesButton;
    private TextView busInfoTextView;
    private ListView stationNamesListView;
    private CustomAdapter adapter; // 커스텀 어댑터
    private ApiExplorer apiExplorer;
    ArrayList Station;
    private List<Integer> BusCityCode;
    private List<String> BusLocalBlID;
    private List<String> BusNo;
    Integer index;

    private List<Integer> BusID;
    private Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            List<String> nodeNames = apiExplorer.getNodeNames(); // ApiExplorer에서 nodeNames 가져오기
            int totalCount = msg.getData().getInt("totalCount");
            if(Station!=null && nodeNames!=null) {
                adapter = new CustomAdapter(CityBus.this, Station, nodeNames);
                Log.d("hMessage", Station.toString());
                Log.d("hMessage", nodeNames.toString());
                stationNamesListView.setAdapter(adapter);
            }
        }
    };

    // DP를 픽셀 단위로 변환하는 메소드
    public static int convertDpToPixel(float dp, Context context) {
        return (int) (dp * context.getResources().getDisplayMetrics().density);
    }

    // TODO: 사용자가 발급받은 odsay lab API 키를 입력하세요.
//    private static final String API_KEY = "fXCWmI16V2ggA9Y9OhTrVMSiPw/YHkDXoHmKjpLG7l8";
    private static final String API_KEY = "6WN7AcWOFR1SJnfFVFKVtoIBidc4AoB2nj6qPmjXbPc";
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_citybus);

        backBtn = findViewById(R.id.backBtn);
        getStationNamesButton = findViewById(R.id.getStationNamesButton);
        busInfoTextView = findViewById(R.id.busInfoTextView);
        stationNamesListView = findViewById(R.id.stationNamesListView);

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
            final int value = BusCityCode.get(i);
            singleBusLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (value == 0) {
                        Intent intent = new Intent(CityBus.this, IntercityBus.class);
                        intent.putExtra("key", busData);
                        intent.putExtra("index", BusCityCode.indexOf(value));
                        Log.d("value_index", index.toString());
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent(CityBus.this, CityBus.class);
                        intent.putExtra("key", busData);
                        intent.putExtra("index", BusCityCode.indexOf(value));
                        Log.d("value_index", index.toString());
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

        getStationNamesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // AsyncTask를 사용하여 stationName 데이터를 가져와 UI에 표시
//                new GetStationNamesTask().execute();
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

    private class GetStationNamesTask extends AsyncTask<Void, Void, BusInfo> {
        @Override
        protected BusInfo doInBackground(Void... voids) {
            try {
                // TODO: 여기에 버스 노선 상세 조회에서 얻은 busID를 입력하세요.
                String busID = BusID.get(index).toString();

                // API 호출을 위한 URL
                String apiUrl = "https://api.odsay.com/v1/api/busLaneDetail?lang=&busID=" + busID + "&apiKey=" + API_KEY;

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
                busInfoTextView.setText("Bus No: " + result.getBusNo());
                Station = result.getStationNames();
                Log.d("CityBus",result.getStationNames().toString());
                adapter = new CustomAdapter(CityBus.this, Station, Station);
                stationNamesListView.setAdapter(adapter);
            } else {
                // Handle the case where data retrieval failed
            }
        }
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