package com.example.moija.schedule;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.moija.ApiExplorer;
import com.example.moija.CustomAdapter;
import com.example.moija.R;
import com.example.moija.fragment.MapFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class IntercityBus extends AppCompatActivity {

    private Button getScheduleButton;
    private TextView busScheduleTextView;
    private TextView startStationTextView;
    private TextView destStationTextView;

    ArrayList Station;
    private List<Integer> BusCityCode;
    private List<String> BusLocalBlID;
    private List<String> BusNo;
    private List<Integer> BusID;
    Integer index;

    public static int convertDpToPixel(float dp, Context context) {
        return (int) (dp * context.getResources().getDisplayMetrics().density);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intercitybus);

//        getScheduleButton = findViewById(R.id.getScheduleButton);
        busScheduleTextView = findViewById(R.id.busScheduleTextView);
        startStationTextView = findViewById(R.id.startStationTextView);
        destStationTextView = findViewById(R.id.destStationTextView);

        Intent intent = getIntent();
        MapFragment.BusData busData = (MapFragment.BusData) intent.getSerializableExtra("key");
        index = intent.getIntExtra("index", 0);
        BusCityCode = busData.getIntegerList();
        BusLocalBlID = busData.getBusLocalBlID();
        BusID=busData.getBusID();
        BusNo = busData.getBusNo();

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
                        Intent intent = new Intent(IntercityBus.this, IntercityBus.class);
                        intent.putExtra("key", busData);
                        intent.putExtra("index", BusCityCode.indexOf(value));
                        Log.d("value_index", index.toString());
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent(IntercityBus.this, CityBus.class);
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

        String startStationId = "4000159";
        String destStationId = "4000064";

        // AsyncTask를 사용하여 스케줄 데이터를 가져와 UI에 표시
        new GetBusScheduleTask(startStationId, destStationId).execute();

//        getScheduleButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                String startStationId = "4000159";
//                String destStationId = "4000064";
//
//                // AsyncTask를 사용하여 스케줄 데이터를 가져와 UI에 표시
//                new GetBusScheduleTask(startStationId, destStationId).execute();
//            }
//        });
    }

    private class GetBusScheduleTask extends AsyncTask<Void, Void, List<String>> {
        private String startTerminal;
        private String destTerminal;
        private final String startStationId;
        private final String destStationId;

        public GetBusScheduleTask(String startStationId, String destStationId){
            this.startStationId = startStationId;
            this.destStationId = destStationId;
        }
        @Override
        protected List<String> doInBackground(Void... params) {
            List<String> scheduleList = new ArrayList<>();

            try {

//                String apiKey = "fXCWmI16V2ggA9Y9OhTrVMSiPw/YHkDXoHmKjpLG7l8";
                String apiKey = "6WN7AcWOFR1SJnfFVFKVtoIBidc4AoB2nj6qPmjXbPc";

                String apiUrl = "https://api.odsay.com/v1/api/intercityServiceTime?apiKey=" + apiKey +
                        "&startStationID=" + startStationId + "&endStationID=" + destStationId;

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

                    JSONObject jsonResponse = new JSONObject(stringBuilder.toString());
                    JSONObject result = jsonResponse.getJSONObject("result");
                    JSONArray stationArray = result.getJSONArray("station");

                    if (stationArray.length() > 0) {
                        JSONObject station = stationArray.getJSONObject(0);
                        String schedule = station.getString("schedule");
                        startTerminal = station.getString("startTerminal");
                        destTerminal = station.getString("destTerminal");

                        String[] scheduleArray = schedule.split("/");
                        for (String time : scheduleArray) {
                            scheduleList.add(time.trim());
                        }
                    }
                } finally {
                    urlConnection.disconnect();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return scheduleList;
        }

        @Override
        protected void onPostExecute(List<String> scheduleList) {
            // UI에 스케줄 데이터를 표시
            StringBuilder scheduleText = new StringBuilder();

            for (String time : scheduleList) {
                scheduleText.append(time).append("\n");
            }

            busScheduleTextView.setText(scheduleText.toString());

            startStationTextView.setText(startTerminal);
            destStationTextView.setText(destTerminal);

        }
    }
}