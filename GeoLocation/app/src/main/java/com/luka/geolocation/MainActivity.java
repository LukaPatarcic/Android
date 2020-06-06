package com.luka.geolocation;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private LocationManager locationManager;
    double longitude;
    double latitude;
    TextView textView;
    String deviceName;
    final private int REQUEST_CODE_ASK_PERMISSIONS = 123;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getSupportActionBar().hide();
        setContentView(R.layout.activity_main);
        deviceName = android.os.Build.MANUFACTURER + " " + android.os.Build.MODEL;
        textView = findViewById(R.id.loading);
    }

    private final LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            longitude = location.getLongitude();
            latitude = location.getLatitude();
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        textView.setVisibility(View.GONE);
        checkAppState();
    }

    private void checkAppState() {
        if (AppStatus.isLocationEnabled(this) && AppStatus.getInstance(this).isOnline()) {
            setCurrentLocation();
            if (longitude == 0.0 || latitude == 0.0) {
                Toast.makeText(this, "Could not find your location", Toast.LENGTH_LONG).show();
            } else {
                String latAndLong = latitude + ";" + longitude;
                SendDataToServer sendDataToServer = new SendDataToServer();
                sendDataToServer.execute(latAndLong);
            }

        } else {
            Toast.makeText(this, "Please turn on Wi-Fi and Locations", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    checkAppState();
                } else {
                    Toast.makeText(getApplicationContext(), "Permission denied", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }


    protected void setCurrentLocation() {

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat
                    .requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_ASK_PERMISSIONS);
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        Location location = getLastKnownLocation();
        longitude = location.getLongitude();
        latitude = location.getLatitude();
    }

    private Location getLastKnownLocation() {
        locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
        List<String> providers = locationManager.getProviders(true);
        Location bestLocation = null;

        for (String provider : providers) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat
                        .requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_ASK_PERMISSIONS);
            }
            Location l = locationManager.getLastKnownLocation(provider);
            if (l == null) {
                continue;
            }
            if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                // Found best last known location: %s", l);
                bestLocation = l;
            }
        }
        return bestLocation;
    }

    private class SendDataToServer extends AsyncTask<String,Void,String> {


        @SuppressLint("StaticFieldLeak")


        @Override
        protected String doInBackground(String... strings) {
            String response = getServerResponse(strings[0]);
            return response;
        }

        @Override
        protected void onPostExecute(String s) {

            Intent i = new Intent(MainActivity.this, MapActivity.class);
            i.putExtra("key",s);
            startActivity(i);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            textView.setVisibility(View.VISIBLE);
        }

        private String getServerResponse(String latAndLong) {


            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String serverResponse = null;
            String MAC_ADDRESS = AppStatus.getMacAddress();
            String[] array;
            String latitude;
            String longitude;
            array = latAndLong.split(";");
            latitude = array[0];
            longitude = array[1];

            final String URL = "https://luka.secondsection.in.rs/index.php";

            try {

                String data =
                        URLEncoder.encode("mac", "UTF-8") + "=" + URLEncoder.encode(MAC_ADDRESS, "UTF-8") + "&" +
                        URLEncoder.encode("latitude","UTF-8") + "=" + URLEncoder.encode(latitude,"UTF-8") + "&" +
                        URLEncoder.encode("longitude","UTF-8") + "=" + URLEncoder.encode(longitude,"UTF-8") + "&" +
                        URLEncoder.encode("name","UTF-8") + "=" + URLEncoder.encode(deviceName,"UTF-8")
                        ;
                URL url = new URL(URL);

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setDoOutput(true);
                urlConnection.setRequestMethod("POST");
                OutputStreamWriter wr = new OutputStreamWriter(urlConnection.getOutputStream());
                wr.write(data);
                wr.flush();
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;

                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }

                if (buffer.length() == 0) {
                    return null;
                }
                serverResponse = buffer.toString();


            } catch (IOException e) {
                Log.e("Server Response", "Error: ", e);
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e("Server Response", "IO Exception", e);
                    }
                }
            }
            return serverResponse;
        }
    }

}


