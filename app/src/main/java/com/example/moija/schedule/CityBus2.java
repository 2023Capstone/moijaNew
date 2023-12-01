package com.example.moija.schedule;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;

import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.moija.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;

import java.net.URL;


public class CityBus2 extends AppCompatActivity {

    private Button getBusScheduleButton;

    private TextView busScheduleTextView;

    private static final String API_KEY = "fXCWmI16V2ggA9Y9OhTrVMSiPw/YHkDXoHmKjpLG7l8";

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_citybus2);

        getBusScheduleButton = findViewById(R.id.getBusScheduleButton);
        busScheduleTextView = findViewById(R.id.busScheduleTextView);




        getBusScheduleButton.setOnClickListener(

                new View.OnClickListener() {

                    @Override

                    public void onClick(View view) {

                        new GetBusScheduleTask().execute();
                    }
                });
    }


    private class GetBusScheduleTask extends AsyncTask<Void, Void, BusSchedule> {
        @Override

        protected BusSchedule doInBackground(Void... voids) {
            try {

// TODO: 여기에 버스 노선 상세 조회에서 얻은 busID를 입력하세요.

                String busID = "2040148";


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


// JSON 파싱


                    JSONObject jsonResponse = new JSONObject(stringBuilder.toString());

                    JSONObject result = jsonResponse.getJSONObject("result");
                    if(result == null){
                        return null;
                    }

                    String firstTime = result.optString("busFirstTime", "N/A");
                    String lastTime = result.optString("busLastTime", "N/A");
                    String interval = result.optString("busInterval", "N/A");
// 추가 정보 가져오기


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


        @Override

        protected void onPostExecute(BusSchedule result) {


            if (result != null) {
                busScheduleTextView.setText(


                        "First Time: " + result.getFirstTime() +


                                "\nLast Time: " + result.getLastTime() +


                                "\nInterval: " + result.getInterval());
            } else {
                // Handle the case where data retrieval failed
            }
        }
    }


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
}


