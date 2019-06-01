package com.example.prognoza;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class MainActivity extends AppCompatActivity {
    private ArrayAdapter<String> ForecastAdapter;
    Intent intent;
    String unit;
    String[] weatherData;
    String location = "3189595"; //ID Subotice

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().setDisplayShowHomeEnabled(true);

        ForecastAdapter = new ArrayAdapter<String>(this,
                R.layout.list_item, R.id.list_item_text,
                new ArrayList<String>());
        ListView listView = (ListView) findViewById(R.id.listView_weather);
        listView.setAdapter(ForecastAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                String weekForecastDetails = weatherData[position];
                intent = new Intent(getApplicationContext(), com.example.prognoza.DetailActivity.class);
                intent.putExtra(Intent.EXTRA_TEXT, weekForecastDetails);
                startActivity(intent);
            }

        });
    }

    private class WeatherTask extends AsyncTask<String, Void, String[]> {

        private final String LOG_TAG = WeatherTask.class.getSimpleName();

        private String getReadableDate(long time) {
            Calendar cal = Calendar.getInstance();
            TimeZone tz = cal.getTimeZone();

          //  Log.d("Time zone: ", tz.getDisplayName());
            SimpleDateFormat sdf = new SimpleDateFormat("dd MMM YYYY");
            sdf.setTimeZone(tz);
            String localTime = sdf.format(new Date(time * 1000));
            return localTime;
        }

        private String[] getDataFromJson(String forecastJsonStr, int numDays) throws JSONException {
            final String OWM_LIST = "list";
            final String OWM_WEATHER = "weather";
            final String OWM_TEMPERATURE = "temp";
            final String OWM_MIN = "min";
            final String OWM_MAX = "max";
            final String OWM_DESCRIPTION = "description";
            final String OWM_DT = "dt";
            final String OWM_WINDSPEED = "speed";
            final String OWM_PRESSURE = "pressure";
            final String OWM_HUMIDITY = "humidity";
            final String OWM_WIND_DIRECTION = "deg";
            final String OWM_WEATHER_ID = "id";
            final String OWM_ICON = "icon";

            String[] resultStr = new String[numDays];
            weatherData = new String[numDays];

            try {
                JSONObject forecastJson = new JSONObject(forecastJsonStr);
                JSONArray forecastArray = forecastJson.getJSONArray(OWM_LIST);

                int length = forecastArray.length();

                for (int i = 0; i < length; i++) {
                    String day;
                    String description;
                    String high_low;
                    String icon;

                    int weatherId;

                    JSONObject dayForcast = forecastArray.getJSONObject(i);
                    double windSpeed = dayForcast.getDouble(OWM_WINDSPEED);
                    double windDirection = dayForcast.getDouble(OWM_WIND_DIRECTION);
                    double humidity = dayForcast.getDouble(OWM_HUMIDITY);
                    double pressure = dayForcast.getDouble(OWM_PRESSURE);

                    String city;
                    JSONObject cityObject = forecastJson.getJSONObject("city");
                    city = cityObject.getString("name") + ", " + cityObject.getString("country");

                    long dateTime;
                    dateTime = dayForcast.getLong(OWM_DT);

                    JSONObject weatherObj2 = dayForcast.getJSONArray(OWM_WEATHER).getJSONObject(0);
                    description = weatherObj2.getString(OWM_DESCRIPTION);
                    weatherId = weatherObj2.getInt(OWM_WEATHER_ID);
                    icon = weatherObj2.getString(OWM_ICON);
                    JSONObject temperatureObj = dayForcast.getJSONObject(OWM_TEMPERATURE);
                    day = getReadableDate(dateTime);


                    int highT = temperatureObj.getInt(OWM_MAX);

                    int lowT = temperatureObj.getInt(OWM_MIN);

                    high_low=formatTemp(highT,lowT);

                    resultStr[i] = day + " - " + description + " - " + high_low;
                    weatherData[i] = day + ";" + description + ";" + (highT) + ";" + (lowT) + ";"
                            + (humidity) + ";" + (pressure) + ";"
                            + (windSpeed) + ";" + (windDirection) + ";"
                            + (weatherId) + ";" + icon + ";" + city;
                    Log.d("ostatak: ", weatherData[i]);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
            return resultStr;
        }
        @Override
        protected String [] doInBackground(String... params) {
        if(params.length==0){
            return null;
        }
        HttpURLConnection urlConnection=null;
        BufferedReader reader=null;
        String forecastJsonStr=null;

        String format="json";
        int numDays=7;
        String code="5459d1266c4c46f65063264f743f6b15";
        String lang = Locale.getDefault().getDisplayLanguage();
        if(lang.equals("српски")){

        try {
            final String FORECAST_BASE_URL = "https://api.openweathermap.org/data/2.5/forecast/daily?";
            final String QUERY_PARAM = "id";
            final String FORMAT_PARAM = "mode";
            final String UNITS_PARAM = "units";
            final String DAYS_PARAM = "cnt";
            final String CODE_PARAM = "APPID";
            final String LANGUAGE = "lang";

            Uri builtUri = Uri.parse(FORECAST_BASE_URL).buildUpon()
                    .appendQueryParameter(QUERY_PARAM, params[0])
                    .appendQueryParameter(FORMAT_PARAM, format)
                    .appendQueryParameter(UNITS_PARAM, params[1])
                    .appendQueryParameter(LANGUAGE, "sr")
                    .appendQueryParameter(DAYS_PARAM, Integer.toString(numDays))
                    .appendQueryParameter(CODE_PARAM, code)
                    .build();
            URL url = new URL(builtUri.toString());
            Log.v(LOG_TAG, "Built params1:" + params[0]);
            Log.v(LOG_TAG, "Built params1:" + params[1]);
            Log.v(LOG_TAG, "Built URI:  " + builtUri.toString());

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line + "\n");
            }
            if (buffer.length() == 0) {
                return null;
            }
            forecastJsonStr = buffer.toString();
            Log.v(LOG_TAG, "JSON string: " + forecastJsonStr);
        } catch(IOException e){
            Log.e("MainActivity", "Greska 123", e);
            return null;
        } finally {
            if(urlConnection!=null){
                urlConnection.disconnect();
            }
            if(reader!=null){
                try{
                    reader.close();
                }catch(final IOException e){
                    Log.e("MainActivity", "IO Exception", e);
                }
            }
        }
        }else {
            try {
                final String FORECAST_BASE_URL = "https://api.openweathermap.org/data/2.5/forecast/daily?";
                final String QUERY_PARAM = "id";
                final String FORMAT_PARAM = "mode";
                final String UNITS_PARAM = "units";
                final String DAYS_PARAM = "cnt";
                final String CODE_PARAM = "APPID";


                Uri builtUri = Uri.parse(FORECAST_BASE_URL).buildUpon()
                        .appendQueryParameter(QUERY_PARAM, params[0])
                        .appendQueryParameter(FORMAT_PARAM, format)
                        .appendQueryParameter(UNITS_PARAM, params[1])
                        .appendQueryParameter(DAYS_PARAM, Integer.toString(numDays))
                        .appendQueryParameter(CODE_PARAM, code)
                        .build();
                URL url = new URL(builtUri.toString());
                Log.v(LOG_TAG, "Built params1:" + params[0]);
                Log.v(LOG_TAG, "Built params1:" + params[1]);
                Log.v(LOG_TAG, "Built URI:  " + builtUri.toString());

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }
                if (buffer.length() == 0) {
                    return null;
                }
                forecastJsonStr = buffer.toString();
                Log.v(LOG_TAG, "JSON string: " + forecastJsonStr);
            } catch(IOException e){
                Log.e("MainActivity", "Greska 123", e);
                return null;
            } finally {
                if(urlConnection!=null){
                    urlConnection.disconnect();
                }
                if(reader!=null){
                    try{
                        reader.close();
                    }catch(final IOException e){
                        Log.e("MainActivity", "IO Exception", e);
                    }
                }
            }
        }
        String []result=null;
        try{
            result = getDataFromJson(forecastJsonStr, numDays);
        }
        catch(JSONException e){
            e.printStackTrace();
        }
        return result;
        }

        @Override
        protected void onPostExecute(String [] result) {

            Log.e("MainActivity", "IO" + result);
            if (result != null)
                ForecastAdapter.clear();
            for (String dayForecastStr : result) {
                ForecastAdapter.add(dayForecastStr);
            }
        }
        }

    public void updateWeather(){
        WeatherTask w=new WeatherTask();
        SharedPreferences prefs=PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        location=prefs.getString(getString(R.string.pref_location_key),
                getString(R.string.pref_location_default));
        unit = prefs.getString("units", "default");
        Log.d("Unit", unit);
        Log.d("Lokacija", location);
        w.execute(location,unit);
    }


    @Override
    public void onStart(){
        super.onStart();
      updateWeather();
    }
//****
    private String formatTemp(double high, double low){
        SharedPreferences prefs=PreferenceManager.getDefaultSharedPreferences((getApplication()));
        if(unit.equals("imperial")){
            high=high;
            low=low;
        }
        else if ( !unit.equals(R.string.pref_units_metric)  ) {
            Log.d("Ima problema!", "Nema takvog tipa");
        }
        long roundedHigh=Math.round(high);
        long roundedLow=Math.round(low);
        String hiLo=roundedHigh+"/"+roundedLow;
        return hiLo;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.menu,menu);
        return super.onCreateOptionsMenu(menu);
    }
public void reLoad(){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
}

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_refresh) {
            updateWeather();
            return true;
        }
        if(id==R.id.action_settings){
            startActivity(new Intent(this, com.example.prognoza.SettingsActivity.class));
            return true;
        }
        if(id==R.id.action_about){
            startActivity(new Intent(this, com.example.prognoza.AboutActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //     SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        // Boolean prefCheckBox = sharedPreferences.getBoolean("PREF_CHECKBOX", false);
        // settingCheckBox.setText("CHECKBOX preference = " + prefCheckBox.toString());
        //    String prefList = sharedPreferences.getString("location", "no selection");
        //     String prefEditText = sharedPreferences.getString("units", "default");
        //   reLoad();
        Log.v("Location:", "refList");
    }

//    private void openPrefferedLocationInMap(){
//        SharedPreferences prefs=PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
//        location=prefs.getString(getString(R.string.pref_location_key), getString(R.string.pref_location_default));
//        /************************/
//
//        String posLong= "19.66667";
//        String posLat = "46.099998";
//        Uri geoLocation2 = Uri.parse("geo:" + posLat + "," + posLong);
//
//        /************************/
//
//        location="subotica";
//        Uri geoLocation=Uri.parse("geo:0,0?").buildUpon()
//                .appendQueryParameter("q", location)
//                .build();
//
//
//        Intent i=new Intent((Intent.ACTION_VIEW));
//        i.setData(geoLocation);
//
//        if (i.resolveActivity(getPackageManager())!=null)
//            startActivity(i);
//        else
//            Log.d("Baj van", "A helyszin nem fejtheto vissza");
//
//    }

}