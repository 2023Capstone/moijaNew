package com.example.moija.schedule;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.moija.R;

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

    private EditText startStationEditText;
    private EditText endStationEditText;
    private Button getScheduleButton;
    private TextView busScheduleTextView;
    private TextView startStationTextView;
    private TextView destStationTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intercitybus);

        startStationEditText = findViewById(R.id.startStationEditText);
        endStationEditText = findViewById(R.id.endStationEditText);
        getScheduleButton = findViewById(R.id.getScheduleButton);
        busScheduleTextView = findViewById(R.id.busScheduleTextView);
        startStationTextView = findViewById(R.id.startStationTextView);
        destStationTextView = findViewById(R.id.destStationTextView);

        getScheduleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String startStation = startStationEditText.getText().toString().trim();
                String endStation = endStationEditText.getText().toString().trim();

                // AsyncTask를 사용하여 스케줄 데이터를 가져와 UI에 표시
                new GetBusScheduleTask().execute(startStation, endStation);
            }
        });
    }

    private class GetBusScheduleTask extends AsyncTask<String, Void, List<String>> {
        private String startTerminal;
        private String destTerminal;
        @Override
        protected List<String> doInBackground(String... params) {
            List<String> scheduleList = new ArrayList<>();

            try {
                String startStation = URLEncoder.encode(params[0], "UTF-8");
                String endStation = URLEncoder.encode(params[1], "UTF-8");
                String apiKey = "Bk3FXTpa4bUs3dxTOsUxSFvLGFYhTaoBDPKfSPOLdwI";

                String apiUrl = "https://api.odsay.com/v1/api/intercityServiceTime?apiKey=" + apiKey +
                        "&startStationID=" + startStation + "&endStationID=" + endStation;

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
                return null;
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