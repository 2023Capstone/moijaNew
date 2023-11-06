package com.example.moija.fragment;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.FragmentManager;

import com.example.moija.R;

public class HomeFragment extends AppCompatActivity {
    Button btn_map, btn_favorite;
    LinearLayout goallayout;
    private static final String PRIMARY_CHANNEL_ID = "primary_notification_channel";
    private NotificationManager mNotificationManager;
    TextView routeinfo;
    private FavoriteFragment favoriteFragment;
    private MapFragment mapFragment;
    private FragmentManager fragmentManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        fragmentManager = getSupportFragmentManager();
        btn_favorite = findViewById(R.id.btn_chat);
        btn_map = findViewById(R.id.btn_map);

        // ChatFragment와 MapFragment 인스턴스 초기화
        favoriteFragment = new FavoriteFragment();
        mapFragment = new MapFragment();
        fragmentManager.beginTransaction().replace(R.id.fragmentContainer, favoriteFragment).commit();
        fragmentManager.beginTransaction().add(R.id.fragmentContainer, mapFragment).commit();

        //만약 검색창에서 결과를 클릭하고넘어온 경우에는 경로를 그려줘야함
        // SearchPage에서 보낸 인텐트를 받아서 key가 있는지 확인함 (SearchPage 참조)
        Intent intent=getIntent();

        createNotificationChannel();
        sendNotification();
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
                if(favoriteFragment != null) fragmentManager.beginTransaction().hide(favoriteFragment).commit();

            }
        });

        btn_favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //로그 메시지 출력 ( 채팅 버튼 클릭 시)
                Log.d("MyApp", "채팅버튼이 클릭되었습니다.");
                //프래그먼트를 바꿔도 프래그먼트 상태가 유지되도록 함
                if(favoriteFragment == null) {
                    fragmentManager.beginTransaction().add(R.id.fragmentContainer, favoriteFragment).commit();
                }
                if(favoriteFragment != null) fragmentManager.beginTransaction().show(favoriteFragment).commit();
                if(mapFragment != null) fragmentManager.beginTransaction().hide(mapFragment).commit();



            }
        });

    }
    public void sendNotification(){
        // Builder 생성
        NotificationCompat.Builder notifyBuilder = getNotificationBuilder();
        // Manager를 통해 notification 디바이스로 전달
        mNotificationManager.notify(0,notifyBuilder.build());
    }
    private NotificationCompat.Builder getNotificationBuilder() {
        NotificationCompat.Builder notifyBuilder = new NotificationCompat.Builder(this, PRIMARY_CHANNEL_ID)
                .setContentTitle("30분 뒤 출발")
                .setContentText("19:30~21:05 진주시외버스터미널")
                .setSmallIcon(R.drawable.bus);
        return notifyBuilder;
    }
    public void createNotificationChannel()
    {
        //notification manager 생성
        mNotificationManager = (NotificationManager)
                getSystemService(NOTIFICATION_SERVICE);
        // 기기(device)의 SDK 버전 확인 ( SDK 26 버전 이상인지 - VERSION_CODES.O = 26)
        if(android.os.Build.VERSION.SDK_INT
                >= android.os.Build.VERSION_CODES.O){
            //Channel 정의 생성자( construct 이용 )
            NotificationChannel notificationChannel = new NotificationChannel(PRIMARY_CHANNEL_ID
                    ,"Test Notification",mNotificationManager.IMPORTANCE_HIGH);
            //Channel에 대한 기본 설정
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableVibration(true);
            notificationChannel.setDescription("Notification from Mascot");
            // Manager을 이용하여 Channel 생성
            mNotificationManager.createNotificationChannel(notificationChannel);
        }

    }
}