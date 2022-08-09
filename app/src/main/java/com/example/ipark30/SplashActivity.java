package com.example.ipark30;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Window;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.model.LatLng;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class SplashActivity extends AppCompatActivity {

    public static int DURATION = 3000;
    public static String [] dati = new String[3];
    private Vector listaParcheggi = new Vector();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);



        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(getResources().getColor(R.color.barColor));
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent homeIntent = new Intent(SplashActivity.this,MainActivity.class);
                Intent gpsIntent = new Intent(SplashActivity.this,SplashActivityGps.class);
                fetchData process = new fetchData();
                process.execute();

                LocationManager locationManager = (LocationManager) getBaseContext().getSystemService(Context.LOCATION_SERVICE);

                homeIntent.putExtra("dati",dati);


                caricaParcheggi();
                Connections connections = new Connections();
                Connection con = connections.CONN();

                String initQuery = "DELETE FROM parcheggi;";

                Statement stmt = null;
                try {
                    stmt = con.createStatement();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
                try {
                    stmt.executeUpdate(initQuery);
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }

                for(int i = 0; i < listaParcheggi.size();i++){

                    String query="INSERT INTO `sql11439426`.`parcheggi` (`nome_parcheggio`) VALUES ('" + listaParcheggi.elementAt(i) + " ');";

                    Statement stmt2 = null;
                    try {
                        stmt2 = con.createStatement();
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }
                    try {
                        stmt2.executeUpdate(query);
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }

                }


                caricaParcheggi();
                if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
                    startActivity(homeIntent);
                }else{
                    startActivity(gpsIntent);
                }

                finish();

            }
        },DURATION);
    }


    private void caricaParcheggi(){



        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Document doc = Jsoup.connect("https://www.myparking.it/parcheggio_roma.php").userAgent("mozilla/17.0").get();
                    Elements temp = doc.select("div.table-responsive");

                    temp = temp.select("td a");

                    for(Element parkingList : temp)
                    {

                        listaParcheggi.add("" + parkingList.select("a").text());
                        Log.d("parcheggi lista",""  + listaParcheggi.size());
                        System.out.println(listaParcheggi.size());
                    }

                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

/*
                runOnUiThread(new Runnable() {
                    @Override public void run() {
                        //here you can modify UI here
                        //modifiying UI element happen here and at the end you cancel the progress dialog

                        Geocoder geocoder = new Geocoder(SplashActivity.this);

                        Connections connections = new Connections();
                        Connection con = connections.CONN();

                        for(int i = 0;i < listaParcheggi.size();i++)
                        {


                            Log.d("parcheggio",listaParcheggi.get(i).toString() + "");
                            try {
                                List<Address> list = geocoder.getFromLocationName(listaParcheggi.get(i).toString(),1,39.00000,11.000000,43.00000,14.000000);
                                if(list.isEmpty())
                                {
                                    System.out.println("nessun risultato trovato");
                                }else {


                                    String s = list.get(0).toString();


                                    StringUtils stringUtils = new StringUtils();

                                    double latitude = Double.valueOf(stringUtils.substringBetween(s, "hasLatitude=true,latitude=", ",hasLongitude=true"));
                                    Log.d("parcheggi","" + latitude);
                                    double longitude = Double.valueOf(StringUtils.substringBetween(s,"longitude=",",phone=null"));


                                    String query="INSERT INTO `sql11439426`.`parcheggi` (`nome_parcheggio`,`latidute`,`longitude`) VALUES ('" + listaParcheggi.elementAt(i) + "', '" + latitude + "','" + longitude + "');";

                                    Statement stmt2 = null;
                                    try {
                                        stmt2 = con.createStatement();
                                    } catch (SQLException throwables) {
                                        throwables.printStackTrace();
                                    }
                                    try {
                                        stmt2.executeUpdate(query);
                                    } catch (SQLException throwables) {
                                        throwables.printStackTrace();
                                    }
                                    LatLng latLng = new LatLng(latitude,longitude);
                                }


                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }




                });
*/


            }




        }).start();

    }




}