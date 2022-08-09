package com.example.ipark30;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class AboutUs extends AppCompatActivity {

    DrawerLayout drawerLayout;
    private ImageView searchView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(getResources().getColor(R.color.barColor));
        }
        drawerLayout = findViewById(R.id.drawer_layout);

        searchView = (ImageView) findViewById(R.id.search_action);

        searchView.setVisibility(View.INVISIBLE);
    }

    public void ClickMenu(View view)
    {
        MainActivity.openDrawer(drawerLayout);
    }


    public void ClickLogo(View view)
    {
        MainActivity.closeDrawer(drawerLayout);
    }




    public void ClickHome(View view)
    {
        MainActivity.redirectActivity(this,MainActivity.class);
    }

    public void ClickDashboard(View view){
        MainActivity.redirectActivity(this, SegnalaProblema.class);
    }

    public void ClickAboutUs(View view) {

        MainActivity.closeDrawer(drawerLayout);
    }


    @Override
    protected void onPause() {
        super.onPause();
        MainActivity.closeDrawer(drawerLayout);
    }
}