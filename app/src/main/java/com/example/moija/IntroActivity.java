package com.example.moija;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.moija.map.Mylocation;

public class IntroActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.intro_activity); //xml , java 소스 연결
        LocationListener myLocationListener = new LocationListener() {
            //장소가 바뀌었으면
            public void onLocationChanged(Location location) {
                Log.d("mylog","위치 업데이트");
                Mylocation.Lastlocation=location;
            }
            public void onStatusChanged(String provider, int status, Bundle extras) {
            }
            public void onProviderEnabled(String provider) {
            }
            public void onProviderDisabled(String provider) {
            }
        };
        LocationManager map_lm = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        if ( Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission( IntroActivity.this.getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED ) {
            ActivityCompat.requestPermissions( IntroActivity.this, new String[] {  android.Manifest.permission.ACCESS_FINE_LOCATION  },
                    0 );
        }
        else{
            map_lm.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    0,
                    1,
                    myLocationListener);
            map_lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                    0,
                    1,
                    myLocationListener);
        }
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(getApplicationContext(), MainPage.class);
                startActivity(intent); //인트로 실행 후 바로 LoginActivity 넘어감.
                finish();
            }
        }, 2000); //2초 후 인트로 실행
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
}
