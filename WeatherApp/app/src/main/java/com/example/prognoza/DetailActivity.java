package com.example.prognoza;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Locale;

public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);


        Intent intent=getIntent();
        if(intent!=null && intent.hasExtra(Intent.EXTRA_TEXT));
        {

            TextView date = findViewById(R.id.date);
            TextView description = findViewById(R.id.description);
            ImageView picture = findViewById(R.id.picture);
            TextView max = findViewById(R.id.max);
            TextView min = findViewById(R.id.min);
            TextView humidity = findViewById(R.id.humidity);
            TextView pressure = findViewById(R.id.pressure);
            TextView wind = findViewById(R.id.wind);
            TextView city = findViewById(R.id.city);
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(DetailActivity.this);

            /**
             * [0] datum
             * [1] tip
             * [2] max
             * [3] min
             * [4] vlaznost
             * [5] pritisak
             * [6] brzina verta
             * [7] direkcija vetra
             * [8] id
             * [9] icon
             * [10] city
             */
            String forecaststr=intent.getStringExtra(Intent.EXTRA_TEXT);
            String[] dataArray;
            String delimiter = ";";
            dataArray = forecaststr.split(delimiter);

            String lang = Locale.getDefault().getDisplayLanguage();
            if(lang.equals("српски")){
                date.setText(dataArray[0]);
                description.setText(dataArray[1]);
                max.setText(dataArray[2]+"°");
                min.setText(dataArray[3]+"°");
                humidity.setText("Влажност ваздуха: "+dataArray[4]+" %");
                pressure.setText("Притисак: "+dataArray[5]+" hPa");
                city.setText(dataArray[10]);
                String test = prefs.getString(getString(R.string.pref_units_key),getString(R.string.pref_units_metric));

                if(test.equals("metric")) {
                    wind.setText("Ветар:"+dataArray[6]+" km/h " /*+ dataArray[7] */);
                } else {
                    wind.setText("Ветар: "+dataArray[6]+" mph " /*+ dataArray[7] */);
                }

                setPicture(dataArray[9], picture);
            } else {
                date.setText(dataArray[0]);
                description.setText(dataArray[1]);
                max.setText(dataArray[2]+"°");
                min.setText(dataArray[3]+"°");
                humidity.setText("Humidity "+dataArray[4]+" %");
                pressure.setText("Pressure: "+dataArray[5]+" hPa");
                city.setText(dataArray[10]);
                String test = prefs.getString(getString(R.string.pref_units_key),getString(R.string.pref_units_metric));

                if(test.equals("metric")) {
                    wind.setText("Wind: "+dataArray[6]+" km/h " /*+ dataArray[7] */);
                } else {
                    wind.setText("Wind: "+dataArray[6]+" mph " /*+ dataArray[7] */);
                }
                setPicture(dataArray[9], picture);
            }



        }
    }

    private void setPicture(String pictureId, ImageView img)
    {
        /**
         * clear sky
         */
        if(pictureId.equals("01d"))
            img.setImageResource(R.drawable.sunny);
        /**
         * few clouds
         */
        if(pictureId.equals("02d"))
            img.setImageResource(R.drawable.partly_sunny);
        /**
         * scattered clouds
         */
        if(pictureId.equals("03d"))
            img.setImageResource(R.drawable.cloudy);
        /**
         * broken clouds
         */
        if(pictureId.equals("04d"))
            img.setImageResource(R.drawable.cloudy);
        /**
         * shower rain
         */
        if(pictureId.equals("09d"))
            img.setImageResource(R.drawable.rainshower);
        /**
         * rain
         */
        if(pictureId.equals("10d"))
            img.setImageResource(R.drawable.rainy);
        /**
         * thunderstorm
         */
        if(pictureId.equals("11d"))
            img.setImageResource(R.drawable.thunderstorm);
        /**
         * snow
         */
        if(pictureId.equals("13d"))
            img.setImageResource(R.drawable.snowy);
        /**
         * mist
         */
        if(pictureId.equals("50d"))
            img.setImageResource(R.drawable.foggy);

    }
/*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            CharSequence text = "Hello toast!";
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(this, text, duration);
            toast.show();
            Intent intent=new Intent(this, com.example.prognoza.SettingsActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
*/
}

