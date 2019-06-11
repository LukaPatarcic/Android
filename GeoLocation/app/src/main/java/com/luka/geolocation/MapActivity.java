package com.luka.geolocation;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    Double lat;
    Double lng;
    String name;
    String[] data;
    GoogleMap map;
    Integer counter = 0;
    Integer counter2 = 0;
    int markerLength;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        this.getSupportActionBar().show();

//        final ActionBar actionBar = getActionBar();
//        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);


        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String value = extras.getString("key");
            data = value.split(";");
            lat = Double.parseDouble(data[0]);
            lng = Double.parseDouble(data[1]);
            name = data[2];
        }
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        new Timer().scheduleAtFixedRate(new TimerTask(){
            @Override
            public void run(){
                GetDataFromServer getDataFromServer = new GetDataFromServer();
                getDataFromServer.execute();
            }
        },0,5000);


    }

    @Override
    protected void onPause() {
        if (isApplicationSentToBackground(this)){
            SetDeviceStatus setDeviceStatus = new SetDeviceStatus();
            setDeviceStatus.execute();
            finish();
        }
        super.onPause();
    }

    public boolean isApplicationSentToBackground(final Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasks = am.getRunningTasks(1);
        if (!tasks.isEmpty()) {
            ComponentName topActivity = tasks.get(0).topActivity;
            if (!topActivity.getPackageName().equals(context.getPackageName())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_refresh) {
            startActivity(new Intent(this, MainActivity.class));
            return true;
        }
        if(id==R.id.action_about){
            startActivity(new Intent(this, AboutActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private class GetDataFromServer extends AsyncTask<String,Void,String> {


        @SuppressLint("StaticFieldLeak")
        @Override
        protected String doInBackground(String... strings) {
            String response = getServerResponse();
            return response;
        }

        @Override
        protected void onPostExecute(String s) {

            LatLng myLocation = new LatLng(lat, lng);
            map.clear();
            map.addMarker(new MarkerOptions().position(myLocation).title("My Location"));
            if(counter == 0) {
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat,lng), 13));

                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(new LatLng(lat, lng))
                        .zoom(12)
                        .build();
                map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                counter++;
            }

            ArrayList<String> locations = new ArrayList<String>();
            locations =  formatJsonData(s);
            int len = locations.size();
            for(int i = 0; i<len; i++) {
                String data = locations.get(i);
                String[] dataArray = data.split(";");
                Double lat = Double.parseDouble(dataArray[0]);
                Double lng = Double.parseDouble(dataArray[1]);
                String name = dataArray[2];
                LatLng OtherLocation = new LatLng(lat,lng);
                map.addMarker(new MarkerOptions().position(OtherLocation).title(name));
            }
        }

        private ArrayList<String> formatJsonData(String data) {

            ArrayList<String> locations = new ArrayList<String>();
            try {
                JSONObject jsonObject = new JSONObject(data);
                JSONArray jsonArray = jsonObject.getJSONArray("locations");
                markerLength = jsonArray.length();
                for (int i = 0; i<markerLength; i++) {
                    String lat;
                    String lon;
                    String name;

                    JSONObject location = jsonArray.getJSONObject(i);
                    lat = location.getString("latitude");
                    lon = location.getString("longitude");
                    name = location.getString("device_name");
                    locations.add(lat+";"+lon+";"+name);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return locations;

        }


        private String getServerResponse() {

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String serverResponse = null;
            String MAC_ADDRESS = AppStatus.getMacAddress();

            final String URL = "https://luka.secondsection.in.rs/get_all_active_devices.php";

            try {

                String data =
                        URLEncoder.encode("mac", "UTF-8") + "=" + URLEncoder.encode(MAC_ADDRESS, "UTF-8");

                java.net.URL url = new URL(URL);

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
    private class SetDeviceStatus extends AsyncTask<String,Void,String> {


        @SuppressLint("StaticFieldLeak")


        @Override
        protected String doInBackground(String... strings) {
            String response = setStatusToZero();
            return response;
        }

        @Override
        protected void onPostExecute(String s) {

        }

        private String setStatusToZero() {

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String serverResponse = null;
            String MAC_ADDRESS = AppStatus.getMacAddress();

            final String URL = "https://luka.secondsection.in.rs/change_status.php";

            try {

                String data =
                        URLEncoder.encode("mac", "UTF-8") + "=" + URLEncoder.encode(MAC_ADDRESS, "UTF-8");

                java.net.URL url = new URL(URL);

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


