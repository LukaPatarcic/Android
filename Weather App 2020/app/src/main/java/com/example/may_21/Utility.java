package com.example.may_21;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.sql.Time;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class Utility {
    public static final String DATE_FORMAT = "yyyyMMdd";


    public static String getReadableDate(long time) {
        Calendar cal = Calendar.getInstance();
        TimeZone tz = cal.getTimeZone();

        Log.d("Time zone: ", tz.getDisplayName());
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        sdf.setTimeZone(tz);
        String localTime = sdf.format(new Date(time * 1000));
        return localTime;
    }

    public static String DayOfWeek() {
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE");
        Date d = new Date();
        String dayOfTheWeek = sdf.format(d);
        return dayOfTheWeek;
    }

    public static String formatTemperature(double temperature, boolean metric) {
        // Data stored in Celsius by default.  If user prefers to see in Fahrenheit, convert
        // the values here.

        if (!metric) {
            temperature = (temperature * 1.8) + 32;
         }
        long roundedTemp = Math.round(temperature);
        return String.valueOf(roundedTemp);
    }

    public static String getFormattedWind( double windSpeed, double degrees, boolean metric) {
        if (!metric) {
            windSpeed = .621371192237334f * windSpeed;
        }

        return String.format("%.2f", windSpeed);
    }

    public static int getWindPicture(double degrees) {
        int direction = R.drawable.compass;
        if (degrees >= 337.5 || degrees < 22.5) {
            direction = R.drawable.wind_n;
        } else if (degrees >= 22.5 && degrees < 67.5) {
            direction = R.drawable.wind_ne;
        } else if (degrees >= 67.5 && degrees < 112.5) {
            direction = R.drawable.wind_e;
        } else if (degrees >= 112.5 && degrees < 157.5) {
            direction = R.drawable.wind_se;
        } else if (degrees >= 157.5 && degrees < 202.5) {
            direction = R.drawable.wind_s;
        } else if (degrees >= 202.5 && degrees < 247.5) {
            direction = R.drawable.wind_sw;
        } else if (degrees >= 247.5 && degrees < 292.5) {
            direction = R.drawable.wind_w;
        } else if (degrees >= 292.5 && degrees < 337.5) {
            direction = R.drawable.wind_nw;
        }
        return direction;
    }

    public static String getCurrentTemperatureMeasure(Intent intent) {
        String temp = intent.getStringExtra(MainActivity.MY_UNIT);
        if(temp.equals("imperial")) {
            return "°F";
        } else {
            return "°C";
        }
    }

    public static String getCurrentWindSpeedMeasure(Intent intent) {
        String temp = intent.getStringExtra(MainActivity.MY_UNIT);
        if(temp.equals("imperial")) {
            return "mph";
        } else {
            return "km/h";
        }
    }

    public static String getWindPressure(Intent intent) {
        String temp = intent.getStringExtra(MainActivity.MY_UNIT);
        if(temp.equals("imperial")) {
            return "Pa";
        } else {
            return "mbar";
        }
    }
    /**
     * Helper method to provide the icon resource id according to the weather condition id returned
     * by the OpenWeatherMap call.
     * @param weatherId from OpenWeatherMap API response
     * @return resource id for the corresponding icon. -1 if no relation is found.
     */
    public static int getIconResourceForWeatherCondition(int weatherId) {
        // Based on weather code data found at:
        // https://openweathermap.org/weather-conditions#Weather-Condition-Codes-2
        if (weatherId >= 200 && weatherId <= 232) {
            return R.drawable.ic_storm;
        } else if (weatherId >= 300 && weatherId <= 321) {
            return R.drawable.ic_light_rain;
        } else if (weatherId >= 500 && weatherId <= 504) {
            return R.drawable.ic_rain;
        } else if (weatherId == 511) {
            return R.drawable.ic_snow;
        } else if (weatherId >= 520 && weatherId <= 531) {
            return R.drawable.ic_rain;
        } else if (weatherId >= 600 && weatherId <= 622) {
            return R.drawable.ic_snow;
        } else if (weatherId >= 701 && weatherId <= 761) {
            return R.drawable.ic_fog;
        } else if (weatherId == 761 || weatherId == 781) {
            return R.drawable.ic_storm;
        } else if (weatherId == 800) {
            return R.drawable.ic_clear;
        } else if (weatherId == 801) {
            return R.drawable.ic_light_clouds;
        } else if (weatherId >= 802 && weatherId <= 804) {
            return R.drawable.ic_cloudy;
        }
        return -1;
    }

    /**
     * Helper method to provide the art resource id according to the weather condition id returned
     * by the OpenWeatherMap call.
     * @param weatherId from OpenWeatherMap API response
     * @return resource id for the corresponding icon. -1 if no relation is found.
     */
    public static int getArtResourceForWeatherCondition(int weatherId) {
        // Based on weather code data found at:
       // https://websygen.github.io/owfont/
        // https://openweathermap.org/weather-conditions#Weather-Condition-Codes-2
        if (weatherId >= 200 && weatherId <= 232) {
            return R.drawable.art_storm;
        } else if (weatherId >= 300 && weatherId <= 321) {
            return R.drawable.art_light_rain;
        } else if (weatherId >= 500 && weatherId <= 504) {
            return R.drawable.art_rain;
        } else if (weatherId == 511) {
            return R.drawable.art_snow;
        } else if (weatherId >= 520 && weatherId <= 531) {
            return R.drawable.art_rain;
        } else if (weatherId >= 600 && weatherId <= 622) {
            return R.drawable.art_snow;
        } else if (weatherId >= 701 && weatherId <= 761) {
            return R.drawable.art_fog;
        } else if (weatherId == 761 || weatherId == 781) {
            return R.drawable.art_storm;
        } else if (weatherId == 800) {
            return R.drawable.art_clear;
        } else if (weatherId == 801) {
            return R.drawable.art_light_clouds;
        } else if (weatherId >= 802 && weatherId <= 804) {
            return R.drawable.art_clouds;
        }
        return -1;
    }

    static String formatDate(long dateInMilliseconds) {
        Date date = new Date(dateInMilliseconds);
        return DateFormat.getDateInstance().format(date);
    }
}
