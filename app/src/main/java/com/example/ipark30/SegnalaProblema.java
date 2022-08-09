package com.example.ipark30;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

public class SegnalaProblema extends AppCompatActivity {

    DrawerLayout drawerLayout;
    private ImageView searchView;
    private Button btnSend;
    private EditText txtNome;
    private EditText txtProblem;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_segnalaproblema);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(getResources().getColor(R.color.barColor));
        }
        drawerLayout = findViewById(R.id.drawer_layout);
        searchView = (ImageView) findViewById(R.id.search_action);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        btnSend = (Button) findViewById(R.id.btnSend);
        txtNome = (EditText) findViewById(R.id.txtNome);
        txtProblem = (EditText) findViewById(R.id.txtProblem);

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(txtNome.getText().toString().equals("") && txtProblem.getText().toString().equals("")) {
                    txtNome.setError("Questo campo non puo essere vuoto!");
                    txtProblem.setError("Questo campo non puo essere vuoto!");
                }else if(txtNome.getText().toString().equals("")){
                    txtNome.setError("Questo campo non puo essere vuoto!");
                }else if(txtProblem.getText().toString().equals("")){
                    txtProblem.setError("Questo campo non puo essere vuoto!");

                }else{
                    sendEmail("iparkhelpservice@gmail.com","Parking2021","iparkhelpservice@gmail.com","Problema nell'APP da : " + txtNome.getText(),txtProblem.getText().toString());
                }
                }
        });


        searchView.setVisibility(View.INVISIBLE);
    }

    public void ClickMenu(View view)
    {
       openDrawer(drawerLayout);
    }


    public static void openDrawer(DrawerLayout drawerLayout)
    {

        drawerLayout.openDrawer(GravityCompat.START);
    }

    public void ClickLogo(View view)
    {
       closeDrawer(drawerLayout);
    }

    public static void closeDrawer(DrawerLayout drawerLayout){
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }
    }

    public void ClickHome(View view)
    {
        MainActivity.redirectActivity(this,MainActivity.class);
    }

    public void ClickDashboard(View view){
        closeDrawer(drawerLayout);
    }

    public void ClickAboutUs(View view) {

        MainActivity.redirectActivity(this,AboutUs.class);
    }


    @Override
    protected void onPause() {
        super.onPause();
        MainActivity.closeDrawer(drawerLayout);
    }


    private void sendEmail(final String Sender,final String Password,final String Receiver,final String Title,final String Message)
    {

        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    GMailSender sender = new GMailSender(Sender,Password);
                    sender.sendMail(Title, "<b>"+Message+"</b>", Sender, Receiver);
                    makeAlert();
                } catch (Exception e) {
                    Log.e("SendMail", e.getMessage(), e);
                }
            }

        }).start();
    }
    private void makeAlert(){
        this.runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(SegnalaProblema.this, "Grazie!", Toast.LENGTH_LONG).show();
            }
        });
    }


}