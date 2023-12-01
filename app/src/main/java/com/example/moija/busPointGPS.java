 package com.example.moija;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;

 public class busPointGPS extends AppCompatActivity {
    private TextView textViewResult;
    private ProgressBar progressBar;
    private ApiExplorer apiExplorer;

     // UI 스레드에서 메시지를 처리할 Handler 구현
     private Handler handler = new Handler(Looper.getMainLooper()) {
         @Override
         public void handleMessage(Message msg) {
//             String nodeNames = msg.getData().getString("nodeNames");
             List<String> nodeNames = msg.getData().getStringArrayList("nodeNames");
             int totalCount = msg.getData().getInt("totalCount");

             StringBuilder sb = new StringBuilder();
             for (String nodeName : nodeNames) {
                 sb.append(nodeName).append("\n");
             }

             textViewResult.setText(sb.toString() + totalCount + "개의 시내버스가 운행되고 있습니다.");
             progressBar.setVisibility(View.GONE);
         }
     };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bus_point_gps);

        textViewResult = findViewById(R.id.textViewResult);
        progressBar = findViewById(R.id.progressBar); // 프로그레스 바 찾기

        apiExplorer = new ApiExplorer(handler);  // Handler 전달
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