package com.example.moija;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class Fragment_Chat_Map extends AppCompatActivity {
    Button btn_map, btn_chat;

    private ChatFragment chatFragment;
    private MapFragment mapFragment;
    private FragmentManager fragmentManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment_chat_map);

        fragmentManager = getSupportFragmentManager();

        btn_chat = findViewById(R.id.btn_chat);
        btn_map = findViewById(R.id.btn_map);

        // ChatFragment와 MapFragment 인스턴스 초기화
        chatFragment = new ChatFragment();
        mapFragment = new MapFragment();

        btn_map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //로그 메시지 출력 (맵 버튼 클릭 시)
                Log.d("MyApp", "맵버튼이 클릭되었습니다.");
                fragmentManager.beginTransaction().replace(R.id.fragmentContainer, mapFragment).commit();

            }
        });

        btn_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //로그 메시지 출력 ( 채팅 버튼 클릭 시)
                Log.d("MyApp", "채팅버튼이 클릭되었습니다.");
                fragmentManager.beginTransaction().replace(R.id.fragmentContainer, chatFragment).commit();

            }
        });
    }
}