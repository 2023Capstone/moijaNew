package com.example.moija.schedule;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.moija.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class CityBus extends AppCompatActivity {

    private Button getStationNamesButton;
    private TextView busInfoTextView;

    // TODO: 사용자가 발급받은 odsay lab API 키를 입력하세요.
    private static final String API_KEY = "Bk3FXTpa4bUs3dxTOsUxSFvLGFYhTaoBDPKfSPOLdwI";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_citybus);

        getStationNamesButton = findViewById(R.id.getStationNamesButton);
        busInfoTextView = findViewById(R.id.busInfoTextView);

        getStationNamesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // AsyncTask를 사용하여 stationName 데이터를 가져와 UI에 표시
                new GetStationNamesTask().execute();
            }
        });
    }

    private class GetStationNamesTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... voids) {
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
                    bufferedReader.close();

                    // JSON 파싱
                    JSONObject jsonResponse = new JSONObject(stringBuilder.toString());
                    JSONObject result = jsonResponse.getJSONObject("result");
                    JSONArray stationsArray = result.getJSONArray("station");

                    String busNo = result.getString("busNo");

                    StringBuilder stationNames = new StringBuilder();
                    for (int i = 0; i < stationsArray.length(); i++) {
                        JSONObject station = stationsArray.getJSONObject(i);
                        String stationName = station.getString("stationName");
                        stationNames.append(stationName).append("\n");
                    }

                    return "Bus No: " + busNo + "\n" + String.join(", ", stationNames);
                } finally {
                    urlConnection.disconnect();
                }
            } catch (Exception e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(String result) {
            // UI에 결과를 표시
            if (result != null) {
                busInfoTextView.setText(result);
            } else {
                busInfoTextView.setText("Failed to fetch data");
            }
        }
    }
}