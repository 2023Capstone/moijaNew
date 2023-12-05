package com.example.moija.Location;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

//import com.odsay.odsayandroidsdk.API;
//import com.odsay.odsayandroidsdk.ODsayData;
//import com.odsay.odsayandroidsdk.ODsayService;
//import com.odsay.odsayandroidsdk.OnResultCallbackListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class BusScheduleActivity extends AppCompatActivity {

    private EditText departureEditText;
    private EditText destinationEditText;
    private ListView busScheduleListView;

    private ArrayList<String> busScheduleList;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity1);

        departureEditText = findViewById(R.id.departure_edit_text);
        destinationEditText = findViewById(R.id.destination_edit_text);
        busScheduleListView = findViewById(R.id.bus_schedule_list_view);

        busScheduleList = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, busScheduleList);
        busScheduleListView.setAdapter(adapter);
    }

    public void searchBusSchedule(View view) {
        String departure = departureEditText.getText().toString();
        String destination = destinationEditText.getText().toString();

        // ODsay API를 통해 시외버스 시간표를 가져오는 AsyncTask를 실행
        new GetBusScheduleTask().execute(departure, destination);
    }

    private class GetBusScheduleTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {
            String departure = params[0];
            String destination = params[1];

            ODsayService oDsayService = ODsayService.init(BusScheduleActivity.this, "Bk3FXTpa4bUs3dxTOsUxSFvLGFYhTaoBDPKfSPOLdwI");

            oDsayService.requestSearchSTN(departure, "bus", 1, 1, new OnResultCallbackListener() {
                @Override
                public void onSuccess(ODsayData oDsayData, API api) {
                    try {
                        JSONObject result = oDsayData.getJson().getJSONObject("result");
                        JSONArray stationArray = result.getJSONArray("station");

                        if (stationArray.length() > 0) {
                            String stationID = stationArray.getJSONObject(0).getString("stationID");

                            oDsayService.requestExpressServiceTime(stationID, destination, new OnResultCallbackListener() {
                                @Override
                                public void onSuccess(ODsayData oDsayData, API api) {
                                    try {
                                        JSONObject result = oDsayData.getJson().getJSONObject("result");
                                        JSONArray busScheduleArray = result.getJSONArray("expressServiceTime");

                                        // 버스 시간표를 리스트에 추가
                                        busScheduleList.clear();
                                        for (int i = 0; i < busScheduleArray.length(); i++) {
                                            String time = busScheduleArray.getString(i);
                                            busScheduleList.add(time);
                                        }

                                        // UI 업데이트
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                adapter.notifyDataSetChanged();
                                            }
                                        });

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }

                                @Override
                                public void onError(int i, String s, API api) {
                                    // 에러 처리
                                }
                            });
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onError(int i, String s, API api) {
                    // 에러 처리
                }
            });

            return null;
        }
    }
}
