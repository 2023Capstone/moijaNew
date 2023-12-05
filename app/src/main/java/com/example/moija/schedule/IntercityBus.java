package com.example.moija.schedule;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.moija.ApiExplorer;
import com.example.moija.CustomAdapter;
import com.example.moija.MainPage;
import com.example.moija.R;
import com.example.moija.fragment.MapFragment;
import com.google.common.collect.Table;

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

    private TableLayout tableLayout;
    private ImageButton backBtn;
    ArrayList Station;
    private List<Integer> BusCityCode;
    private List<String> BusLocalBlID;
    private List<String> BusNo;
    private List<Integer> BusID;

    private List<Integer> StartID;
    private List<Integer> EndID;
    Integer index;

    public static int convertDpToPixel(float dp, Context context) {
        return (int) (dp * context.getResources().getDisplayMetrics().density);
    }
    @Override
    public void onBackPressed(){
        Intent intent = new Intent(IntercityBus.this, MainPage.class);
        startActivity(intent);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intercitybus);
        backBtn=findViewById(R.id.backBtn);
//        getScheduleButton = findViewById(R.id.getScheduleButton);
        startStationTextView = findViewById(R.id.startStationTextView);
        destStationTextView = findViewById(R.id.destStationTextView);
        tableLayout=findViewById(R.id.tableLayout);
        Intent intent = getIntent();
        MapFragment.BusData busData = (MapFragment.BusData) intent.getSerializableExtra("key");
        index = intent.getIntExtra("index", 0);
        BusCityCode = busData.getIntegerList();
        BusLocalBlID = busData.getBusLocalBlID();
        BusID=busData.getBusID();
        BusNo = busData.getBusNo();
        StartID=busData.getStartID();
        EndID=busData.getEndID();
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
            Log.d("BusCitycode",BusCityCode.toString());
            Log.d("StartID",StartID.toString());
            Log.d("EndID",EndID.toString());
            backBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(IntercityBus.this, MainPage.class);
                    startActivity(intent);
                }
            });

            // 클릭 이벤트 설정
            int value = i;
            boolean isintercitybus=BusCityCode.get(i).equals(0);
            singleBusLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(isintercitybus) {
                        Intent intent = new Intent(IntercityBus.this, IntercityBus.class);
                        intent.putExtra("key", busData);
                        intent.putExtra("index", value);
                        Log.d("value_index", Integer.toString(value));
                        startActivity(intent);
                    }else{
                        Intent intent = new Intent(IntercityBus.this, CityBus.class);
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

        String startStationId = StartID.get(index).toString();
        String destStationId = EndID.get(index).toString();

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

        private TextView busInfoTextView=findViewById(R.id.busInfoTextView);
        public GetBusScheduleTask(String startStationId, String destStationId){
            this.startStationId = startStationId;
            this.destStationId = destStationId;
        }
        @Override
        protected List<String> doInBackground(Void... params) {
            List<String> scheduleList = new ArrayList<>();
            String apiUrl=null;
            try {
                busInfoTextView.setText(BusNo.get(index));
                if(BusNo.get(index).equals("시외버스")) {
                    apiUrl = "https://api.odsay.com/v1/api/intercityServiceTime?apiKey=" + MainPage.OdsayAPI_KEY +
                            "&startStationID=" + startStationId + "&endStationID=" + destStationId;
                }else if(BusNo.get(index).equals("고속버스")){
                    apiUrl="https://api.odsay.com/v1/api/expressServiceTime?apiKey=" + MainPage.OdsayAPI_KEY +
                            "&startStationID=" + startStationId + "&endStationID=" + destStationId;
                }
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
            for (int i=0;i<scheduleList.size();i++) {
                TableRow tableRow=new TableRow(getApplicationContext());
                TableLayout.LayoutParams tableRowParams = new TableLayout.LayoutParams(
                        TableLayout.LayoutParams.MATCH_PARENT,
                        TableLayout.LayoutParams.WRAP_CONTENT
                );
                tableRow.setLayoutParams(tableRowParams);
                tableRow.setBackgroundColor(Color.WHITE);
                TextView textview = new TextView(getApplicationContext());
                TableRow.LayoutParams textViewParams = new TableRow.LayoutParams(
                        TableRow.LayoutParams.MATCH_PARENT,
                        TableRow.LayoutParams.MATCH_PARENT,
                        1.0f
                );
                textViewParams.setMargins(3, 3, 3, 3);
                textview.setLayoutParams(textViewParams);
                textview.setTextSize(50);
                textview.setBackgroundColor(Color.parseColor("#F7F7F7"));
                textview.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                textview.setGravity(Gravity.CENTER);
                textview.setText(scheduleList.get(i));
                tableRow.addView(textview);
                tableLayout.addView(tableRow);
            }
            startStationTextView.setText(startTerminal);
            destStationTextView.setText(destTerminal);

        }
    }
}