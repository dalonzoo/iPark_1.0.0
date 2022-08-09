package com.example.ipark30;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class fetchData extends AsyncTask {


    private String data = "";
    private String[] parcheggi = new String[3];
    private String singleParsed;
    private int liberi = 0,occupati = 0,progresso = 0;

    @Override
    protected Object doInBackground(Object[] objects) {
        try {

            URL url = new URL("http://80.17.15.50:49875/iPark/recive.php?mod=out");
            HttpURLConnection http = null;
            if (url.getProtocol().toLowerCase().equals("https")) {
                trustAllHosts();
                HttpsURLConnection https = (HttpsURLConnection) url.openConnection();

                https.setHostnameVerifier(DO_NOT_VERIFY);
                http = https;
            } else {
                http = (HttpURLConnection) url.openConnection();
            }

            if (http.getResponseCode() == 200){
                InputStream inputStream = http.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String line = "";
                Log.d("Json", "");
                while (line != null) {
                    line = bufferedReader.readLine();
                    data = data + line;
                }

                JSONObject JA = new JSONObject(data);


                for (int i = 0; i < JA.length(); i++) {
                    ;
                    parcheggi[0] = JA.get("A") + "";
                    parcheggi[1] = JA.get("B") + "";
                    parcheggi[2] = JA.get("C") + "";
                }
                conteggioPark();
            }else{
                parcheggi[0] = "server in manutenzione";
            }
            } catch(MalformedURLException e){
                e.printStackTrace();
            } catch(IOException e){
                e.printStackTrace();
            } catch(JSONException e){
                e.printStackTrace();
            }






// Utilizziamo il metodo addToRequestQueue per aggiungere la richiesta JSON alla RequestQueue

        return null;
    }

    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);

        MainActivity.stati = parcheggi;
        SplashActivity.dati = parcheggi;
        MainActivity.liberi = liberi;
        MainActivity.occupati = occupati;

    }

    // always verify the host - dont check for certificate
    final static HostnameVerifier DO_NOT_VERIFY = new HostnameVerifier() {
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    };

    /**
     * Trust every server - dont check for any certificate
     */
    private static void trustAllHosts() {
        // Create a trust manager that does not validate certificate chains
        TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[] {};
            }

            public void checkClientTrusted(X509Certificate[] chain,
                                           String authType) throws CertificateException {
            }

            public void checkServerTrusted(X509Certificate[] chain,
                                           String authType) throws CertificateException {
            }
        } };

        // Install the all-trusting trust manager
        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection
                    .setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    public void conteggioPark()
    {
        liberi = 0;
        occupati = 0;

        for(int i = 0; i < 3;i++)
        {



            if(parcheggi[i].contains("OCCUPATO"))
            {
                occupati++;
                Log.d("UPDATES", "PARCHEGGIO OCCUPATO RILEVATO");
            }
            else
            {
                liberi++;
                Log.d("UPDATES", "PARCHEGGIO LIBERO RILEVATO");
            }
        }
        progresso = (liberi * 100) / 3;
    }
}

