package com.example.ipark30;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

public class SplashActivityGps extends AppCompatActivity {

    private Button btnAttiva;
    private Handler handler = new Handler();
    private Runnable runnable;
    private int delay = 500;
    private LocationManager locationManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_gps);


        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(getResources().getColor(R.color.barColor));
        }
            locationManager = (LocationManager) SplashActivityGps.this.getSystemService(Context.LOCATION_SERVICE);
            btnAttiva = (Button) findViewById(R.id.btnAttiva);



            btnAttiva.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SplashActivityGps.this.startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                }
            });
        }


    @Override
    protected void onResume() {
        //start handler as activity become visible

        handler.postDelayed(runnable = new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.R)
            public void run() {
                //do something

                Intent homeIntent = new Intent(SplashActivityGps.this,MainActivity.class);
                if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
                    startActivity(homeIntent);
                }else{

                }


            }
        }, delay);

        super.onResume();
    }

// If onPause() is not included the threads will double up when you
// reload the activity

    @Override
    protected void onPause() {
        handler.removeCallbacks(runnable); //stop handler when activity not visible
        super.onPause();
    }

}