package com.example.moija.fragment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.example.moija.R;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.kakao.vectormap.LatLng;
import com.kakao.vectormap.camera.CameraUpdate;
import com.kakao.vectormap.camera.CameraUpdateFactory;

public class Fragment_Chat_Map extends AppCompatActivity {
    Button btn_map, btn_chat,setGoalyesbtn,setGoalnobtn;
    LinearLayout goallayout;

    TextView routeinfo;
    private ChatFragment chatFragment;
    private MapFragment mapFragment;
    private FragmentManager fragmentManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment_chat_map);
        fragmentManager = getSupportFragmentManager();
        goallayout=findViewById(R.id.setGoalLayout);
        setGoalyesbtn=findViewById(R.id.yesbtn);
        setGoalnobtn=findViewById(R.id.nobtn);
        btn_chat = findViewById(R.id.btn_chat);
        btn_map = findViewById(R.id.btn_map);
        routeinfo=findViewById(R.id.Routeinfo);
        // ChatFragment와 MapFragment 인스턴스 초기화
        chatFragment = new ChatFragment();
        mapFragment = new MapFragment(goallayout,routeinfo);
        fragmentManager.beginTransaction().replace(R.id.fragmentContainer, chatFragment).commit();
        fragmentManager.beginTransaction().add(R.id.fragmentContainer, mapFragment).commit();

        //만약 검색창에서 결과를 클릭하고넘어온 경우에는 경로를 그려줘야함
        // SearchPage에서 보낸 인텐트를 받아서 key가 있는지 확인함 (SearchPage 참조)
        Intent intent=getIntent();
        //해당 인텐트의 Key를 확인하고, key가 있으면 MapFragment에 같은 번들(key)을 보냄
        if (intent != null) {
            String message = intent.getStringExtra("key");
            if (message != null) {
                //길찾기 메서드
                fragmentManager.beginTransaction().show(mapFragment).commit();
                Bundle bundle = new Bundle();
                bundle.putString("key", "FindGoal");
                mapFragment.setArguments(bundle);
            }
        }

        btn_map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //로그 메시지 출력 (맵 버튼 클릭 시)
                Log.d("MyApp", "맵버튼이 클릭되었습니다.");
                //프래그먼트를 바꿔도 프래그먼트 상태가 유지되도록 함
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
                //프래그먼트를 바꿔도 프래그먼트 상태가 유지되도록 함
                if(chatFragment == null) {
                    fragmentManager.beginTransaction().add(R.id.fragmentContainer, chatFragment).commit();
                }
                if(chatFragment != null) fragmentManager.beginTransaction().show(chatFragment).commit();
                if(mapFragment != null) fragmentManager.beginTransaction().hide(mapFragment).commit();



            }
        });
        setGoalyesbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"설정합니다",Toast.LENGTH_SHORT);
                goallayout.setVisibility(View.INVISIBLE);
            }
        });
        setGoalnobtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"설정안합니다",Toast.LENGTH_SHORT);
                goallayout.setVisibility(View.INVISIBLE);
            }
        });
    }
}