package com.example.moija.schedule;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.moija.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;

import java.net.URL;
import java.util.ArrayList;
import java.util.Locale;


public class CityBus2 extends AppCompatActivity {

    private Button getBusScheduleButton;

    private TextView busScheduleTextView;
    private TextView intervalTextView;
    private ListView infoListView;
    private static final String API_KEY = "Bk3FXTpa4bUs3dxTOsUxSFvLGFYhTaoBDPKfSPOLdwI";

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_citybus2);

        getBusScheduleButton = findViewById(R.id.getBusScheduleButton);
        busScheduleTextView = findViewById(R.id.busScheduleTextView);
        intervalTextView = findViewById(R.id.intervalTextView);
        infoListView = findViewById(R.id.infoListView);

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

                String busID = "2040055";


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
                    if (result == null) {
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
            } catch (IOException e) {
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

                ArrayList<String> timetable = result.getMinutesBetween();

                TimetableAdapter adapter = new TimetableAdapter(timetable);
                infoListView.setAdapter(adapter);
            } else {
            }
        }

        private class TimetableAdapter extends ArrayAdapter<String> {
            TimetableAdapter(ArrayList<String> timetable){

                super(CityBus2.this, R.layout.timetable_item, timetable);
            }

            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View itemView = convertView;
                if(itemView == null){
                    itemView = LayoutInflater.from(getContext()).inflate(R.layout.timetable_item, parent,false);
                }

                TextView timeTextView = itemView.findViewById(R.id.timeTextView);
                timeTextView.setText(getItem(position));

                return itemView;
            }
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

    private int convertTimeToMinutes(String time) {
        String[] parts = time.split(

                ":");
        int hours = Integer.parseInt(parts[0]);
        int minutes = Integer.parseInt(parts[1]);
        return hours * 60 + minutes;
    }

    public ArrayList<String> getMinutesBetween() {
        ArrayList<String> timetable = new ArrayList<>();
        try {
            int firstTimeMinutes = convertTimeToMinutes(firstTime);
            int lastTimeMinutes = convertTimeToMinutes(lastTime);
            Log.d("firstTimeMinutes", "firstTimeMinutes: "+firstTimeMinutes);
            String[] aa= interval.split("회");
            //간격 횟수
            int intervalTime =Integer.valueOf(aa[0]);
            // (마지막분 -시작분)/interval -> 간격분
            int time = (lastTimeMinutes-firstTimeMinutes)/intervalTime;

            Log.d("intervalTime", "intervalTime: "+interval);
//                        int time = (lastTimeMinutes-firstTimeMinutes)/intervalTime;
            // Generate timetable with 10-minute intervals
            int count=0;
            for (int i = firstTimeMinutes; i <= lastTimeMinutes; i += time) {
                timetable.add(convertMinutesToTime(i));
                count++;
                if(count==intervalTime+1){
                    break;
                }
            }
        }

        catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return timetable;
    }

    private String convertMinutesToTime(int minutes) {
        int hours = minutes / 60;
        int remainderMinutes = minutes % 60;
        return String.format(Locale.getDefault(), "%02d:%02d", hours, remainderMinutes);
    }

    private int extractFrequency(String interval) {
        try {
            // Extract the frequency from the interval string
            String[] parts = interval.split(" - ");
            String frequencyPart = parts[1].trim(); // assuming "106회 -" format
            return Integer.parseInt(frequencyPart);
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            e.printStackTrace();
            return 1; // Default to 1 if extraction fails or not present
        }
    }
}


