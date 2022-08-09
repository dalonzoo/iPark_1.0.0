package com.example.ipark30;


import android.annotation.SuppressLint;
import android.os.StrictMode;
import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Created by ABHI on 9/20/2016.
 */
public class Connections {


    String url;
    String ip;
    String un = "U90zetFmMG";
    String password = "Y0G6CnLaI0";




    @SuppressLint("NewApi")
    public Connection CONN(){



        url = "jdbc:mysql://remotemysql.com:3306/U90zetFmMG";
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);
        Connection conn = null;
        String ConnURL = null;
        try {

            Class.forName("com.mysql.jdbc.Driver");

            conn = DriverManager.getConnection(url, un, password);




        } catch (SQLException se) {
            Log.e("ERRO", se.getMessage());
        } catch (ClassNotFoundException e) {
           e.printStackTrace();
        } catch (Exception e) {
            Log.e("ERRO", e.getMessage());
        }
        return conn;
    }
}
