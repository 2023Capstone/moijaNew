package com.example.moija.schedule;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

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

    private Button getScheduleButton;
    private TextView busScheduleTextView;
    private TextView startStationTextView;
    private TextView destStationTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intercitybus);

        getScheduleButton = findViewById(R.id.getScheduleButton);
        busScheduleTextView = findViewById(R.id.busScheduleTextView);
        startStationTextView = findViewById(R.id.startStationTextView);
        destStationTextView = findViewById(R.id.destStationTextView);

        getScheduleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String startStationId = "4000159";
                String destStationId = "4000064";

                // AsyncTask를 사용하여 스케줄 데이터를 가져와 UI에 표시
                new GetBusScheduleTask(startStationId, destStationId).execute();
            }
        });
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

                String apiKey = "fXCWmI16V2ggA9Y9OhTrVMSiPw/YHkDXoHmKjpLG7l8";

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