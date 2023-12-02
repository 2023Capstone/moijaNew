package com.example.moija;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.moija.fragment.MapFragment;

import java.util.List;

public class busPointGPS extends AppCompatActivity{
    private TextView textViewResult;
    private ProgressBar progressBar;
    private ApiExplorer apiExplorer;
    private ListView listView;
    private List<Integer> BusCityCode;
    private List<String> BusLocalBlID;

    private List<Integer> BusID;
    private CustomAdapter adapter; // 커스텀 어댑터

    Parcelable state; // 상태 저장 변수



    // UI 스레드에서 메시지를 처리할 Handler 구현
    private Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            List<String> nodeNames = apiExplorer.getNodeNames(); // ApiExplorer에서 nodeNames 가져오기
            int totalCount = msg.getData().getInt("totalCount");
            listView.setAdapter(adapter); // 어댑터 설정
            textViewResult.setText(totalCount + "개의 시내버스가 운행되고 있습니다.");

            if (state != null) { // 리스트뷰 상태가 있는 경우
                listView.onRestoreInstanceState(state);// 리스트뷰 스크롤 위치 복구
            }else{state = listView.onSaveInstanceState();}

            progressBar.setVisibility(View.GONE);
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bus_point_gps);

        textViewResult = findViewById(R.id.textViewResult);
        progressBar = findViewById(R.id.progressBar); // 프로그레스 바 찾기
        listView = findViewById(R.id.listView);
        Intent intent = getIntent();
        MapFragment.BusData busData = (MapFragment.BusData) intent.getSerializableExtra("key");
        BusCityCode= busData.getIntegerList();
        BusLocalBlID = busData.getBusLocalBlID();
        BusID=busData.getBusID();
        Log.d("yourlog",BusCityCode.toString());
        Log.d("yourlog",BusLocalBlID.toString());
        Log.d("yourlog",BusID.toString());
        apiExplorer = new ApiExplorer(handler);  // Handler 전달
        apiExplorer.BusCityCode=BusCityCode;
        apiExplorer.BusLocalBlIDs=BusLocalBlID;

        progressBar.setVisibility(View.VISIBLE); // 데이터 로딩 전 프로그레스 바 표시
        // ApiExplorer 스레드 시작
        new Thread(apiExplorer).start();
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

}