package com.example.may_21;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Time;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    SharedPreferences sh;
    public static final String MY_PREF = "pref_general";
    public static final String MY_LOCATION = "location";
    public static final String MY_UNIT = "units";
    public static final String MY_NUMBER_OF_DAYs = "days";



    // Location information
    public static final  String OWM_CITY = "city";
    public static final  String OWM_CITY_NAME = "name";
    public static final  String OWM_COORD = "coord";

    // Location coordinate
    public static final  String OWM_LATITUDE = "lat";
    public static final  String OWM_LONGITUDE = "lon";

    // Weather information.  Each day's forecast info is an element of the "list" array.
    public static final  String OWM_LIST = "list";

    public static final  String OWM_PRESSURE = "pressure";
    public static final  String OWM_HUMIDITY = "humidity";
    public static final  String OWM_WINDSPEED = "speed";
    public static final  String OWM_WIND_DIRECTION = "deg";

    // All temperatures are children of the "temp" object.
    public static final  String OWM_TEMPERATURE = "temp";
    public static final  String OWM_MAX = "max";
    public static final  String OWM_MIN = "min";

    public static final  String OWM_WEATHER = "weather";
    public static final  String OWM_DESCRIPTION = "main";
    public static final  String OWM_WEATHER_ID = "id";

    public static final  String  OWM_DT="dt";




    DailyTemperature [] dailyTemperature=null;
    private ListView list;
    private ArrayAdapter<String> listAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        list=(ListView)findViewById(R.id.list);
        ArrayList<String> arrayList=new ArrayList<String>();
        listAdapter= new ArrayAdapter<String>(
                this,
                R.layout.list_layout, arrayList);
        list.setAdapter(listAdapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent= new Intent(MainActivity.this, DetailsActivity.class);
                intent.putExtra(OWM_DT, dailyTemperature[i].getDateTime());
                intent.putExtra(OWM_PRESSURE,dailyTemperature[i].getPressure() );
                intent.putExtra(OWM_HUMIDITY, dailyTemperature[i].getHumidity());
                intent.putExtra(OWM_WINDSPEED, dailyTemperature[i].getWindSpeed());
                intent.putExtra(OWM_WIND_DIRECTION, dailyTemperature[i].getWindDirection());
                intent.putExtra(OWM_MAX, dailyTemperature[i].getHigh() );
                intent.putExtra(OWM_MIN, dailyTemperature[i].getLow());
                intent.putExtra(OWM_DESCRIPTION, dailyTemperature[i].getDescription());
                intent.putExtra(OWM_WEATHER_ID, dailyTemperature[i].getWeatherId());
                intent.putExtra(MY_UNIT, sh.getString(MY_UNIT,"metric")); //******************************
                startActivity(intent);
            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();
        updateWeather();
    }
    @Override
    protected void onResume() {
        super.onResume();
        updateWeather();
    }

    private void updateWeather(){

        sh = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

          String location = sh.getString(MY_LOCATION,"3189595");
        String number_of_days = sh.getString(MY_NUMBER_OF_DAYs,"7");
        String unit_type = sh.getString(MY_UNIT,"metric");
        String location2=sh.getString(MY_LOCATION, "");
        String number2 = sh.getString(MY_NUMBER_OF_DAYs,"");
        String unit2 = sh.getString(MY_UNIT,"");
        WeatherDownload wd=new WeatherDownload();
        wd.execute(location, number_of_days, unit_type);

    }

    private String formatTemp(double high, double low){
        String unit = sh.getString(MY_UNIT, "metric");

        if(unit.equals("imperial")){
            high=(high*1.8)+32;
            low=(low*1.8)+32;
        }
        else if ( unit.equals(R.string.pref_units_metric)  ) {

        }
        long roundedHigh=Math.round(high);
        long roundedLow=Math.round(low);
        String hiLo=roundedHigh+"/"+roundedLow;
        return hiLo;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.main_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_refresh) {
            updateWeather();
            return true;
        }
        if(id==R.id.action_settings){
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }
        if(id==R.id.action_about){
            startActivity(new Intent(this, AboutActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class WeatherDownload extends AsyncTask<String, Void, String>{
        private String [] fetchJson(String forecastJsonStr){
            String [] data=null;

            String high_low_temp;

            try {
                JSONObject forecastJson = new JSONObject(forecastJsonStr);
                JSONArray weatherArray = forecastJson.getJSONArray(OWM_LIST);

                JSONObject cityJson = forecastJson.getJSONObject(OWM_CITY);
                String cityName = cityJson.getString(OWM_CITY_NAME);

                JSONObject cityCoord = cityJson.getJSONObject(OWM_COORD);
                double cityLatitude = cityCoord.getDouble(OWM_LATITUDE);
                double cityLongitude = cityCoord.getDouble(OWM_LONGITUDE);

                int length=weatherArray.length();
                data=new String[length];
                dailyTemperature=new DailyTemperature[length];
                for (int i = 0; i < length; i++) {
                    // These are the values that will be collected.
                    long dateTime=0;
                    double pressure;
                    int humidity;
                    double windSpeed;
                    double windDirection;

                    double high;
                    double low;

                    String description;
                    int weatherId;
                    String day;
                    // Get the JSON object representing the day
                    JSONObject dayForecast = weatherArray.getJSONObject(i);

                    // Cheating to convert this to UTC time, which is what we want anyhow
                    //     dateTime = dayTime.setJulianDay(julianStartDay+i);

                    pressure = dayForecast.getDouble(OWM_PRESSURE);
                    humidity = dayForecast.getInt(OWM_HUMIDITY);
                    windSpeed = dayForecast.getDouble(OWM_WINDSPEED);
                    windDirection = dayForecast.getDouble(OWM_WIND_DIRECTION);

                    dateTime = dayForecast.getLong(OWM_DT);
                    day = Utility.getReadableDate(dateTime);

                    // Description is in a child array called "weather", which is 1 element long.
                    // That element also contains a weather code.
                    JSONObject weatherObject =
                            dayForecast.getJSONArray(OWM_WEATHER).getJSONObject(0);
                    description = weatherObject.getString(OWM_DESCRIPTION);
                    weatherId = weatherObject.getInt(OWM_WEATHER_ID);

                    // Temperatures are in a child object called "temp".  Try not to name variables
                    // "temp" when working with temperature.  It confuses everybody.
                    JSONObject temperatureObject = dayForecast.getJSONObject(OWM_TEMPERATURE);
                    high = temperatureObject.getDouble(OWM_MAX);
                    low = temperatureObject.getDouble(OWM_MIN);
                    high_low_temp=formatTemp(high,low);
                    data[i] = high_low_temp + " " + description + " wind speed=" + windSpeed;
                    dailyTemperature[i]=new DailyTemperature(dateTime, pressure, humidity, windSpeed, windDirection, high, low, description, weatherId, cityName);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return  data;
        }

        private String buildUrl(String city, String days, String units){
            String format="json";
            //String units="metric";
            int nDays=Integer.valueOf(days);
            String code="5459d1266c4c46f65063264f743f6b15";


            final String FORECAST_BASE_URL="https://api.openweathermap.org/data/2.5/forecast/daily?";
            final String  QUERY_PARAM="id";
            final String FORMAT_PARAM="mode";
            final String UNITS_PARAM="units";
            final String DAYS_PARAM="cnt";
            final String CODE_PARAM="APPID";

            Uri builtUri=Uri.parse(FORECAST_BASE_URL).buildUpon()
                    .appendQueryParameter(QUERY_PARAM,city)
                    .appendQueryParameter(FORMAT_PARAM, format)
                    .appendQueryParameter(UNITS_PARAM, units)
                    .appendQueryParameter(DAYS_PARAM,Integer.toString(nDays))
                    .appendQueryParameter(CODE_PARAM, code)
                    .build();

            return builtUri.toString();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            String [] result=null; //X

            result=fetchJson(s);
            //Toast.makeText(MainActivity.this, s, Toast.LENGTH_LONG).show();

            if(s!=null)
                listAdapter.clear();

            for (String dayStr : result) {
                listAdapter.add(dayStr);
            }

            // listAdapter.add(s);
        }

        @Override
        protected String doInBackground(String... params) {
            String weather_string=null;
            HttpURLConnection urlConnection=null;
            BufferedReader reader=null;
            try {
                URL url = new URL(buildUrl(params[0], params[1], params[2]));

                getRequestedOrientation();
                urlConnection=(HttpURLConnection)url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                StringBuffer buffer = new StringBuffer();

                InputStream inputStream= urlConnection.getInputStream();
                if (inputStream == null){
                    return null;
                }
                reader = new BufferedReader( new InputStreamReader(inputStream));
                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }
                if (buffer.length()==0){
                    return null;
                }
                weather_string=buffer.toString();



            } catch (IOException e) {
                e.printStackTrace();
            }
            finally {
                //close()
                if(urlConnection!=null){
                    urlConnection.disconnect();
                }
                if(reader!=null){
                    try{
                        reader.close();
                    }catch(final IOException e){

                    }
                }
            }

            return weather_string;
        }
    }
}

//api.openweathermap.org/data/2.5/forecast?q={city name},{state},{country code}&appid={your api key}

//http://api.openweathermap.org/data/2.5/forecast/daily?id=3189595&mode=json&units=metric&cnt=7&appid=5459d1266c4c46f65063264f743f6b15


//{"cod":"200","message":0,"city":{"geoname_id":524901,"name":"Moscow","lat":55.7522,"lon":37.6156,"country":"RU","iso2":"RU","type":"city","population":0},"cnt":7,"list":[{"dt":1485766800,"temp":{"day":262.65,"min":261.41,"max":262.65,"night":261.41,"eve":262.65,"morn":262.65},"pressure":1024.53,"humidity":76,"weather":[{"id":800,"main":"Clear","description":"sky is clear","icon":"01d"}],"speed":4.57,"deg":225,"clouds":0,"snow":0.01}]}