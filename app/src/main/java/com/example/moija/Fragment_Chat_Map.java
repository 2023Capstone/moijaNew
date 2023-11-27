package com.example.moija;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.kakao.vectormap.LatLng;
import com.kakao.vectormap.camera.CameraUpdate;
import com.kakao.vectormap.camera.CameraUpdateFactory;

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
        fragmentManager.beginTransaction().replace(R.id.fragmentContainer,chatFragment).commit();
        fragmentManager.beginTransaction().add(R.id.fragmentContainer,mapFragment).commit();
        //만약 검색창에서 결과를 클릭하고넘어온 경우에는 경로를 그려줘야함
        // SearchPage에서 보낸 인텐트를 받아서 key가 있는지 확인함 (SearchPage 참조)
        Intent intent=getIntent();
        //해당 인텐트의 Key를 확인하고, key가 있으면 길찾기를 수행
        if (intent != null) {
            String message = intent.getStringExtra("key");
            if (message != null) {
                //길찾기 메서드
                mapFragment.FindGoal();
                fragmentManager.beginTransaction().show(mapFragment).commit();


            }
        }

        btn_map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //로그 메시지 출력 (맵 버튼 클릭 시)
                Log.d("MyApp", "맵버튼이 클릭되었습니다.");
                if(mapFragment == null) {
                    fragmentManager.beginTransaction().add(R.id.fragmentContainer, mapFragment).commit();
                }

                if(mapFragment != null) fragmentManager.beginTransaction().show(mapFragment).commit();
                if(chatFragment != null) fragmentManager.beginTransaction().hide(chatFragment).commit();

            }
        });

        btn_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //로그 메시지 출력 ( 채팅 버튼 클릭 시)
                Log.d("MyApp", "채팅버튼이 클릭되었습니다.");
                if(chatFragment == null) {
                    fragmentManager.beginTransaction().add(R.id.fragmentContainer, chatFragment).commit();
                }
                if(chatFragment != null) fragmentManager.beginTransaction().show(chatFragment).commit();
                if(mapFragment != null) fragmentManager.beginTransaction().hide(mapFragment).commit();



            }
        });
    }
}