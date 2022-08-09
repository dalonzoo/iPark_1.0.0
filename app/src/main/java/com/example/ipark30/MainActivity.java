package com.example.ipark30;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback{

    DrawerLayout drawerLayout;
    private RequestQueue mQueue;
    private ListView lista;
    public static String[] stati = new String[3];
    private String[] parcheggi = new String[3];
    private fetchData process = new fetchData();
    private String urlString = "http://80.17.15.50:49875/iPark/recive.php?mod=out";
    private GoogleMap map;
    private Location curreLocation;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private static final int REQUEST_CODE = 101;
    public static int liberi = 3, occupati = 0;
    private Button btnLiberi;
    private Button btnOcc;
    private ProgressBar progressBar;
    public static int progresso = 50;
    private TextView txtPosti;
    private MaterialSearchView searchView;
    private ArrayList<String> array = new ArrayList();
    private final LatLng lngRosselli = new LatLng(41.5974, 12.6659);
    private final LatLng lngMatteotti = new LatLng(41.59209807098149, 12.662053118315498);
    private LatLng lngAttuale = new LatLng(0, 0);
    private Handler handler = new Handler();
    private Handler handler3 = new Handler();
    private Handler secondHandler = new Handler();
    private Runnable runnable;
    private int delay = 1 * 1000;
    private String selezionato = "";
    private boolean stop = false;
    private LocationManager locationManager;
    private SupportMapFragment mapFragment;
    private final String TAG = MainActivity.class.getSimpleName();
    private Location locationGained;
    private TextView txtTitle;
    private TextView txtPosti2;
    private int height = 1470;
    private Vector<String> listaParcheggi = new Vector<String>();
    private boolean finito = false;
    private int parcheggiTrovati = 0;
    private ProgressBar progressBar2;
    private Vector listaCoordinate = new Vector();
    private Boolean done = false;
    private Vector posizioni = new Vector();
    private int count = 0;
    private ArrayList<MarkerOptions> mMarkerArray = new ArrayList<MarkerOptions>();
    private Location nearToYou;
    private String nome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        drawerLayout = findViewById(R.id.drawer_layout);


        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        Activity activity = MainActivity.this;
        Window window = activity.getWindow();

// clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

// finally change the color
        window.setStatusBarColor(ContextCompat.getColor(activity,R.color.barColor));


        progressBar = (ProgressBar) findViewById(R.id.progressBar4);
        progressBar2 = (ProgressBar) findViewById(R.id.progressBar);

        txtPosti = (TextView) findViewById(R.id.txtParcheggio);
        txtPosti2 = (TextView) findViewById(R.id.txtPosti);
        progressBar.setMax(100);
        progressBar.setProgress(0);

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        ViewGroup.LayoutParams params = mapFragment.getView().getLayoutParams();
        params.height = 1880;
        mapFragment.getView().setLayoutParams(params);

        txtPosti.setVisibility(View.INVISIBLE);
        txtPosti2.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.INVISIBLE);

        txtTitle = (TextView) findViewById(R.id.txtTitle);

        array.add("Rosselli parking (Aprilia)");
        array.add("Matteotti parking (Aprilia)");


        searchViewCode();


        mQueue = Volley.newRequestQueue(this);


        locationManager = (LocationManager) MainActivity.this.getSystemService(Context.LOCATION_SERVICE);
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);



        int SDK_INT = android.os.Build.VERSION.SDK_INT;
        if (SDK_INT > 8) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);
            //your codes here

        }





                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {


                        showWarningDialog();
                    }
                },7500);


    }




    public void ClickMenu(View view)
    {
        openDrawer(drawerLayout);
       ;
    }



    public static void openDrawer(DrawerLayout drawerLayout)
    {

        drawerLayout.openDrawer(GravityCompat.START);
    }


    public void ClickLogo(View view){

        closeDrawer(drawerLayout);
    }

    public static void closeDrawer(DrawerLayout drawerLayout){
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }
    }

    public void ClickHome(View view)
    {
        closeDrawer(drawerLayout);
    }


    public void ClickDashboard(View view){
        redirectActivity(this, SegnalaProblema.class);
    }

    public void ClickAboutUs(View view)
    {
        redirectActivity(this,AboutUs.class);
    }

    public static void redirectActivity(Activity activity, Class aClass) {


        Intent intent = new Intent(activity,aClass);

        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);

        activity.startActivity(intent);
    }



    @Override
    protected void onResume() {
        //start handler as activity become visible

        Log.d(TAG,"onResume method");
        handler.postDelayed(runnable = new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.R)
            public void run() {
                //do something

                Intent gpsIntent = new Intent(MainActivity.this,SplashActivityGps.class);

                if(stop)
                {

                }else
                {
                if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){

                }else{
                    startActivity(gpsIntent);
                }
                fetchData process2 = new fetchData();
                if (process2.getStatus() == AsyncTask.Status.RUNNING || process.getStatus() == AsyncTask.Status.RUNNING) {

                } else {
                    if (isNetworkAvailable()) {

                        stop = false;


                        process2.execute();
                        conteggioPark();
                        Log.d("provba", selezionato + "");
                        if (selezionato == "Rosselli parking") {
                            progressBar.setProgress(progresso);
                            txtPosti.setText("Rosselli parking (Aprilia)" + "\n" + "\n" +
                                    "posti : " + liberi + "/6");
                            txtPosti2.setText(" posti liberi : " + liberi + "/6" );
                            selezionato = "Rosselli parking";
                        } else if (selezionato == "Matteotti parking") {
                            txtPosti.setText("Matteotti parking (Aprilia)" + "\n" + "\n" +
                                    "posti : " + 4 + "/8");
                            txtPosti2.setText(" posti liberi : 4/8" );
                            selezionato = "Matteotti parking";
                        }else{

                        }

                        while (stati == null) {

                        }


                    } else {
                        Toast.makeText(MainActivity.this, "Connessione a internet assente , attivare i dati", Toast.LENGTH_LONG).show();
                        stop = true;
                    }


                }
                    handler.postDelayed(runnable, delay);
                }
            }
        }, delay);

        super.onResume();
    }


// If onPause() is not included the threads will double up when you
// reload the activity

    private void caricaParcheggi(){


/*
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





                runOnUiThread(new Runnable() {
                    @Override public void run() {
                        //here you can modify UI here
                        //modifiying UI element happen here and at the end you cancel the progress dialog


                    }




                });



            }
        }).start();
*/

        new Thread(new Runnable() {
            @Override
            public void run() {
                Geocoder geocoder = new Geocoder(MainActivity.this);

                Log.d("AggiornaMappa","sono entrato");

                Log.d("lista parcheggi 2","" + listaParcheggi.size());

                   for (int i = 0; i < listaParcheggi.size(); i++) {

                       Log.d("lista parcheggi ciclo", "" + listaParcheggi.size());
                        try {
                           List<Address> list = geocoder.getFromLocationName(listaParcheggi.elementAt(i).toString(), 1, 39.00000, 11.000000, 43.00000, 14.000000);
                           if (list.isEmpty()) {
                               System.out.println("nessun risultato trovato");

                           } else {


                               if(listaParcheggi.elementAt(i).toString().equals("Rosselli parking (Aprilia)") || listaParcheggi.elementAt(i).toString().equals("Matteotti parking (Aprilia)"))
                               {

                               }else {
                                   String s = list.get(0).toString();


                                   StringUtils stringUtils = new StringUtils();

                                   double latitude = Double.valueOf(stringUtils.substringBetween(s, "hasLatitude=true,latitude=", ",hasLongitude=true"));
                                   Log.d("parcheggi", "" + latitude);
                                   double longitude = Double.valueOf(StringUtils.substringBetween(s, "longitude=", ",phone=null"));

                                   LatLng latLng = new LatLng(latitude, longitude);
                                   caricaLuogoBackground(listaParcheggi.elementAt(i) + " (Roma)", latLng);

                               }
                               Log.d("ciclo", "aggiungo posizione");
                           }


                       } catch (IOException e) {
                           e.printStackTrace();
                       }
                   }

                   System.out.println("aggiungo");
               }


        }).start();



    }




    public void SearchClick(View view)
    {

        searchView.showSearch(true);
    }
    @Override
    protected void onPause() {
        handler.removeCallbacks(runnable); //stop handler when activity not visible
        super.onPause();
        Log.d(TAG,"onPause method");
        closeDrawer(drawerLayout);
    }


    public void getData()
    {

        Statement s=null;
        ResultSet rs=null;

        Connections connections = new Connections();
        Connection con = connections.CONN();


        String query = "SELECT * FROM parcheggi";
        try{
            s = con.createStatement();

            rs = s.executeQuery(query);
            while(rs.next())
            {
                listaParcheggi.add(rs.getString(2)); //here you can get data, the '1' indicates column number based on your query

             //   caricaLuogo(listaParcheggi.lastElement().toString() + "",new LatLng(Double.valueOf(rs.getString(3)),Double.valueOf(rs.getString(4))));
                Log.d("parcheggioTrovato",listaParcheggi.lastElement().toString());

            }

        }
        catch(Exception e)
        {
            System.out.println("Error in getData"+e);
        }
        Log.d("parcheggioTrovato",listaParcheggi.size() + "");



    }
    private void searchViewCode() {
        searchView = (MaterialSearchView) findViewById(R.id.search_view);


        getData();
        listaParcheggi.add("Rosselli parking (Aprilia)");
        listaParcheggi.add("Matteotti parking (Aprilia)");
        String[] suggestions = listaParcheggi.toArray(new String[listaParcheggi.size()]);






        Drawable icon = getResources().getDrawable(R.drawable.ic_action_name);
        searchView.setSuggestionIcon(icon);
        searchView.setSuggestions(suggestions);
        searchView.setEllipsize(true);
        searchView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                searchView.dismissSuggestions();
                searchView.closeSearch();

                if (parent.getItemAtPosition(position).toString().equals("Matteotti parking (Aprilia)")) {
                    progressBar.setProgress(50);
                    ViewGroup.LayoutParams params = mapFragment.getView().getLayoutParams();
                    params.height =height;
                    mapFragment.getView().setLayoutParams(params);
                    progressBar.setVisibility(View.VISIBLE);
                    txtPosti.setVisibility(View.VISIBLE);
                    txtPosti2.setVisibility(View.VISIBLE);
                    txtPosti.setText("Matteotti parking (Aprilia)" + "\n" +
                            "posti : " + 4 + "/8");
                    txtPosti2.setText(" posti liberi : 4/8" );
                    caricaLuogo("Matteotti parking", lngMatteotti);
                } else if (parent.getItemAtPosition(position).toString().equals("Rosselli parking (Aprilia)")) {
                    conteggioPark();
                    ViewGroup.LayoutParams params = mapFragment.getView().getLayoutParams();
                    params.height = height;
                    mapFragment.getView().setLayoutParams(params);
                    progressBar.setVisibility(View.VISIBLE);
                    txtPosti.setVisibility(View.VISIBLE);
                    txtPosti2.setVisibility(View.VISIBLE);
                    progressBar.setProgress(progresso);
                    txtPosti.setText("Rosselli parking (Aprilia)" + "\n" +
                            "posti : " + liberi + "/6");
                    txtPosti2.setText(" posti liberi : " + liberi + "/6" );
                    caricaLuogo("Rosselli parking", lngRosselli);
                }
            }
        });
        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Toast.makeText(getApplicationContext(), query, Toast.LENGTH_SHORT).show();
                if (query.equals("Rosselli parking (Aprilia)")) {
                    conteggioPark();
                    progressBar.setProgress(progresso);
                    txtPosti.setText("Rosselli parking (Aprilia)" + "\n" +
                            "posti : " + liberi + "/6");
                    txtPosti2.setText(" posti liberi : " + liberi + "/6" );
                    caricaLuogo(query.toString(), lngRosselli);
                } else if (query.equals("Matteotti parking (Aprilia)")) {
                    progressBar.setProgress(50);
                    txtPosti.setText("Matteotti parking (Aprilia)" + "\n" +
                            "posti : " + 4 + "/8");
                    txtPosti2.setText(" posti liberi : 4/8" );
                    caricaLuogo(query.toString(), lngMatteotti);

                }else{
                    ViewGroup.LayoutParams params = mapFragment.getView().getLayoutParams();
                    params.height = height;
                    mapFragment.getView().setLayoutParams(params);
                    txtPosti.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.VISIBLE);
                    txtPosti2.setVisibility(View.VISIBLE);
                    progressBar.setProgress(progresso);
                    txtPosti.setText("" + query + "\n" + "presto disponibile...");
                    txtPosti2.setText("   Dati N/D" );

                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        searchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {
            }

            @Override
            public void onSearchViewClosed() {
            }
        });

    }

    private void fetchLastLocation() {


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);

            return;
        }


    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int voceScelta = item.getItemId();
        switch (voceScelta) {
            case R.id.search_action:
                return super.onOptionsItemSelected(item);
        }
        return true;

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuItem item = menu.findItem(R.id.search_action);
        searchView.setMenuItem(item);
        Log.d(TAG,"onCreateOptionMenu method");
        return true;
    }


    public static JSONObject getJSONObjectFromURL(String urlString) throws IOException, JSONException {
        HttpURLConnection urlConnection = null;
        URL url = new URL(urlString);
        urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestMethod("GET");
        urlConnection.setReadTimeout(10000 /* milliseconds */);
        urlConnection.setConnectTimeout(15000 /* milliseconds */);
        urlConnection.setDoOutput(true);
        urlConnection.connect();

        BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
        StringBuilder sb = new StringBuilder();

        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line + "\n");
        }
        br.close();

        String jsonString = sb.toString();
        System.out.println("JSON: " + jsonString);

        return new JSONObject(jsonString);
    }


    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {

        map = googleMap;



        Log.d(TAG,"ONmapReasy method");

        boolean gps_enabled = false;
        boolean network_enabled = false;



        try {
            gps_enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
        }

        try {
            network_enabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ex) {
        }

        if (!isNetworkAvailable()) {

            new AlertDialog.Builder(MainActivity.this)
                    .setMessage("Connessione internet disattivata")
                    .setPositiveButton("Impostazioni", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                            Intent intent = new Intent(Intent.ACTION_MAIN);
                            startActivity(new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS));

                        }
                    })
                    .setNegativeButton("Cancel", null)
                    .show();



        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            }, 0);
            return;
        } else {
            // Toast.makeText(MainActivity.this,"permessi già ottenuti",Toast.LENGTH_SHORT).show();
        }


        map.getUiSettings().setZoomControlsEnabled(true);

        map.setMyLocationEnabled(true);
        map.getUiSettings().setMyLocationButtonEnabled(true);


        MarkerOptions markerOptions = new MarkerOptions().position(lngRosselli).title("Rosselli parking").icon(BitmapFromVector(getApplicationContext(), R.drawable.ic_action_name));



        //    googleMap.moveCamera(CameraUpdateFactory.newLatLng(lngRosselli));
        //    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lngRosselli, 16));
        googleMap.addMarker(markerOptions);
        mMarkerArray.add(markerOptions);



//secondo parcheggio


        MarkerOptions markerOptions2 = new MarkerOptions().position(lngMatteotti).title("Matteotti parking").icon(BitmapFromVector(getApplicationContext(), R.drawable.ic_action_name));
        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(@NonNull Marker marker) {
                conteggioPark();
                if (marker.getTitle().equals("Matteotti parking")) {
                    conteggioPark();
                    ViewGroup.LayoutParams params = mapFragment.getView().getLayoutParams();
                    params.height = height;
                    mapFragment.getView().setLayoutParams(params);
                    txtPosti.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.VISIBLE);
                    txtPosti2.setVisibility(View.VISIBLE);
                    progressBar.setProgress(50);
                    txtPosti.setText("Matteotti parking (Aprilia)" + "\n" +
                            "posti : " + 4 + "/8");
                    txtPosti2.setText(" posti liberi : 4/8" );
                    selezionato = "Matteotti parking";

                } else if(marker.getTitle().equals("Rosselli parking")){
                    conteggioPark();
                    ViewGroup.LayoutParams params = mapFragment.getView().getLayoutParams();
                    params.height = height;
                    mapFragment.getView().setLayoutParams(params);
                    txtPosti.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.VISIBLE);
                    txtPosti2.setVisibility(View.VISIBLE);
                    progressBar.setProgress(progresso);
                    txtPosti.setText("Rosselli parking (Aprilia)" + "\n" +
                            "posti : " + liberi + "/6");
                    txtPosti2.setText(" posti liberi : " + liberi + "/6" );
                    selezionato = "Rosselli parking";

                }
                else{
                    ViewGroup.LayoutParams params = mapFragment.getView().getLayoutParams();
                    params.height = height;
                    mapFragment.getView().setLayoutParams(params);
                    txtPosti.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.VISIBLE);
                    txtPosti2.setVisibility(View.VISIBLE);
                    progressBar.setProgress(0);
                    txtPosti.setText("" + marker.getTitle().toString() + "\n" + "presto disponibile...");
                    txtPosti2.setText("   Dati N/D" );
                    selezionato = "" + marker.getTitle();


                }
                return false;
            }
        });
        googleMap.addMarker(markerOptions2);
        mMarkerArray.add(markerOptions2);

        //    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lngAttuale, 14));



        getLastLocation();

        if (locationGained == null)
        {
            secondHandler.postDelayed(new Runnable() {

                @Override
                public void run() {

                    getLastLocation();

                    Log.d("rilevazione posizione","" + locationGained);
                }
            }, 3000);
        }

        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(@NonNull LatLng latLng) {
                selezionato = "mappa";
                ViewGroup.LayoutParams params = mapFragment.getView().getLayoutParams();
                params.height = 1880;
                mapFragment.getView().setLayoutParams(params);
                progressBar.setVisibility(View.INVISIBLE);
                txtPosti.setVisibility(View.INVISIBLE);
                txtPosti2.setVisibility(View.INVISIBLE);
            }
        });




        ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setTitle("Attendere");
        progressDialog.setMessage("Caricando nuovi parcheggi");
        progressDialog.setProgress(parcheggiTrovati);
        progressDialog.setMax(46);

        caricaParcheggi();

    }


    private BitmapDescriptor BitmapFromVector(Context context, int vectorResId) {
        // below line is use to generate a drawable.
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);

        // below line is use to set bounds to our vector drawable.
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());

        // below line is use to create a bitmap for our
        // drawable which we have added.
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);

        // below line is use to add bitmap in our canvas.
        Canvas canvas = new Canvas(bitmap);

        // below line is use to draw our
        // vector drawable in canvas.
        vectorDrawable.draw(canvas);

        // after generating our bitmap we are returning our bitmap.
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }


    public void conteggioPark() {
        liberi = 3;
        occupati = 0;

        for (int i = 0; i < 3; i++) {

            Log.d("prova", stati[i] + "");

            if (stati[0] == "server in manutenzione") {


                /*
                // Toast.makeText(MainActivity.this, "Server in manutenzione, riprovare piu tardi", Toast.LENGTH_LONG).show();
                new AlertDialog.Builder(MainActivity.this)
                        .setMessage("Server in manutenzione")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface paramDialogInterface, int paramInt) {

                            }
                        })
                        .show();
                stop = true;

*/

                break;



            } else if(stati[i] == null)
            {
                /*
                new AlertDialog.Builder(MainActivity.this)
                        .setMessage("Server in manutenzione")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface paramDialogInterface, int paramInt) {

                            }
                        })
                        .show();

                stop = true;
*/
                break;
            }
            else
            {
                if (stati[i].contains("OCCUPATO")) {
                    Log.d("prova", stati[i] + "");
                    occupati++;
                } else {
                    liberi++;
                }

                progresso = (liberi * 100) / 6;
            }
        }

    }



    @Override
    public void onBackPressed() {
        if (searchView.isSearchOpen()) {
            searchView.closeSearch();
        } else {
            super.onBackPressed();
        }
    }


    public void caricaLuogoBackground(String name, LatLng lng) {
        GoogleMap googleMap = map;


        if (name.contains("Rosselli")) {
            selezionato = "Rosselli parking";
        } else if(name.contains("Matteotti")){
            selezionato = "Matteotti parking";
        }else{
            selezionato = name;
        }

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                MarkerOptions markerOptions = new MarkerOptions().position(lng).title(name + "").icon(BitmapFromVector(getApplicationContext(), R.drawable.ic_action_name));

                MainActivity.this.map.addMarker(markerOptions);
                mMarkerArray.add(markerOptions);
                Log.d("totale",mMarkerArray.size() + "," + name);


            }
        });


    }

    public void caricaLuogo(String name, LatLng lng) {
        GoogleMap googleMap = map;


        if (name.contains("Rosselli")) {
            selezionato = "Rosselli parking";
        } else if(name.contains("Matteotti")){
            selezionato = "Matteotti parking";
        }else{
            selezionato = name;
        }

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                MarkerOptions markerOptions = new MarkerOptions().position(lng).title(name + "").icon(BitmapFromVector(getApplicationContext(), R.drawable.ic_action_name));

                MainActivity.this.map.addMarker(markerOptions).showInfoWindow();
                mMarkerArray.add(markerOptions);
                Log.d("totale",mMarkerArray.size() + "," + name);

                if(name.contains("Matteotti")) {
                    progressBar.setProgress(50);
                    ViewGroup.LayoutParams params = mapFragment.getView().getLayoutParams();
                    params.height = height;
                    mapFragment.getView().setLayoutParams(params);
                    progressBar.setVisibility(View.VISIBLE);
                    txtPosti.setVisibility(View.VISIBLE);
                    txtPosti2.setVisibility(View.VISIBLE);
                    txtPosti.setText("Matteotti parking (Aprilia)" + "\n" +
                            "posti : " + 4 + "/8");
                    txtPosti2.setText(" posti liberi : 4/8");

                    googleMap.moveCamera(CameraUpdateFactory.newLatLng(lng));
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lng, 16));
                }else if(name.contains("Rosselli")){
                    ViewGroup.LayoutParams params = mapFragment.getView().getLayoutParams();
                    params.height = height;
                    mapFragment.getView().setLayoutParams(params);
                    progressBar.setVisibility(View.VISIBLE);
                    txtPosti.setVisibility(View.VISIBLE);
                    txtPosti2.setVisibility(View.VISIBLE);
                    progressBar.setProgress(progresso);
                    txtPosti.setText("Rosselli parking (Aprilia)" + "\n" +
                            "posti : " + liberi + "/6");
                    txtPosti2.setText(" posti liberi : " + liberi + "/6" );
                    googleMap.moveCamera(CameraUpdateFactory.newLatLng(lng));
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lng, 16));
                }else{
                    googleMap.moveCamera(CameraUpdateFactory.newLatLng(lng));
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lng, 16));
                }
            }
        });


    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void getLastLocation() {
        FusedLocationProviderClient client = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }



        Task<Location> task = client.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {

                locationGained = location;
                if(location != null)
                {
                    mapFragment.getMapAsync(new OnMapReadyCallback() {
                        @Override
                        public void onMapReady(@NonNull GoogleMap googleMap) {
                            map = googleMap;
                            lngAttuale = new LatLng(location.getLatitude(),location.getLongitude());


                            map.animateCamera(CameraUpdateFactory.newLatLngZoom(lngAttuale,15));

                        }
                    });
                }
                else{
                    Log.d("gettingLocation","location is null!");
                }



            }
        });


    }


    private void trovaPiuVicino(){




        double limit = 100000000;

        for(int i = 0;i < mMarkerArray.size();i++){

            MarkerOptions markerOptions2 = mMarkerArray.get(i);

            LatLng latLng2 = markerOptions2.getPosition();


            Location location2 = new Location(LocationManager.GPS_PROVIDER);;
            location2.setLongitude(latLng2.longitude);
            location2.setLatitude(latLng2.latitude);
            if(locationGained.distanceTo(location2) < limit){


                limit = locationGained.distanceTo(location2);
                nearToYou = location2;
                nome = markerOptions2.getTitle();

            }



        }




    }


    private void showWarningDialog(){
        AlertDialog.Builder builder =
                new AlertDialog.Builder
                        (MainActivity.this, R.style.Theme_AppCompat_Dialog_Alert);
        View view = LayoutInflater.from(MainActivity.this).inflate(
                R.layout.message_dialog_layout,
                (ConstraintLayout)findViewById(R.id.layoutDialogContainer)
        );
        builder.setView(view);
        ((TextView) view.findViewById(R.id.textMessage))
                .setText("Desidera trovare il parcheggio piu vicino a lei  ?");
        ((Button) view.findViewById(R.id.buttonYes))
                .setText("Si");
        ((Button) view.findViewById(R.id.buttonNo))
                .setText("No");
        final AlertDialog alertDialog = builder.create();
        view.findViewById(R.id.buttonYes).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                trovaPiuVicino();
                Log.d("quanti",nearToYou.getLatitude() + "" + nearToYou.getLongitude() + nome);
                LatLng latLng = new LatLng(nearToYou.getLatitude(),nearToYou.getLongitude());
                caricaLuogo(nome,latLng);

                alertDialog.dismiss();

            }
        });
        view.findViewById(R.id.buttonNo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();

            }
        });
        if (alertDialog.getWindow() != null){
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        alertDialog.show();
    }

}