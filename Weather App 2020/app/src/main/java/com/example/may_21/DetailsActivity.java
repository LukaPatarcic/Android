package com.example.may_21;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

public class DetailsActivity extends AppCompatActivity {

    private ImageView mIconView;
    private ImageView mWindPictureView;
    private TextView mFriendlyDateView;
    private TextView mDateView;
    private TextView mDescriptionView;
    private TextView mHighTempView;
    private TextView mLowTempView;
    private TextView mHumidityView;
    private TextView mWindView;
    private TextView mPressureView;

    private String mForecast; //

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        mIconView = (ImageView) findViewById(R.id.detail_icon);
        mDateView = (TextView) findViewById(R.id.detail_date_textview);
        mFriendlyDateView = (TextView) findViewById(R.id.detail_day_textview);
        mDescriptionView = (TextView) findViewById(R.id.detail_forecast_textview);
        mHighTempView = (TextView) findViewById(R.id.detail_high_textview);
        mLowTempView = (TextView) findViewById(R.id.detail_low_textview);
        mHumidityView = (TextView) findViewById(R.id.detail_humidity_textview);
        mWindView = (TextView) findViewById(R.id.detail_wind_textview);
        mPressureView = (TextView) findViewById(R.id.detail_pressure_textview);
        mWindPictureView = (ImageView) findViewById(R.id.detail_wind_direction);
        displayData();

    }


    private void displayData() {
        Intent intent = getIntent();
        if (intent != null) {
            // Read weather condition ID from cursor
            int weatherId = intent.getIntExtra(MainActivity.OWM_WEATHER_ID,200);

            // Use weather art image
            mIconView.setImageResource(Utility.getArtResourceForWeatherCondition(weatherId));

            // Read date from cursor and update views for day of week and date

            long date = intent.getLongExtra(MainActivity.OWM_DT, 12345678);

            String friendlyDateText = Utility.getReadableDate(date);
            String dateText = Utility.DayOfWeek();
            mFriendlyDateView.setText(friendlyDateText);
            mDateView.setText(dateText);
            String description = intent.getStringExtra(MainActivity.OWM_DESCRIPTION);
            mDescriptionView.setText(description);

            mIconView.setContentDescription(description);

            boolean isMetric=true;
            if ((intent.getStringExtra(MainActivity.MY_UNIT)).equals("imperial"))
                isMetric=false;

            double high = intent.getDoubleExtra(MainActivity.OWM_MAX, 50);

            String highString = Utility.formatTemperature(high, isMetric);
            mHighTempView.setText(highString + Utility.getCurrentTemperatureMeasure(intent));

            double low = intent.getDoubleExtra(MainActivity.OWM_MIN, 50);;

            String lowString = Utility.formatTemperature(low,isMetric);
            mLowTempView.setText(lowString + Utility.getCurrentTemperatureMeasure(intent));

            int humidity = intent.getIntExtra(MainActivity.OWM_HUMIDITY, 50);
            mHumidityView.setText("Humidity: " + String.valueOf(humidity) + "%");

            double windSpeedStr =intent.getDoubleExtra(MainActivity.OWM_WINDSPEED,1);

            double windDirStr = intent.getDoubleExtra(MainActivity.OWM_WIND_DIRECTION,20);

            mWindView.setText("Wind Speed: " + Utility.getFormattedWind(windSpeedStr, windDirStr, isMetric) + " " + Utility.getCurrentWindSpeedMeasure(intent));
            mWindPictureView.setImageResource(Utility.getWindPicture(windDirStr));


            double pressure = intent.getDoubleExtra(MainActivity.OWM_PRESSURE, 1000);

            mPressureView.setText("Pressure: " + pressure + " " + Utility.getWindPressure(intent));
            //getActivity().getString(R.string.format_pressure, pressure));

            }
    }

}
