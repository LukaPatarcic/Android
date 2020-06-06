package com.example.may_21;

public class DailyTemperature {

    long dateTime;
    double pressure;
    int humidity;
    double windSpeed;
    double windDirection;

    double high;
    double low;

    String description;
    int weatherId;
    String cityName;


    public DailyTemperature(long dateTime0, double pressure0,
            int humidity0,double windSpeed0,double windDirection0,
            double high0, double low0,
            String description0, int weatherId0, String cityName0){
        dateTime=dateTime0;
        pressure=pressure0;
        humidity=humidity0;
        windSpeed=windSpeed0;
        windDirection=windDirection0;
        high=high0;
        low=low0;
        description=description0;
        weatherId=weatherId0;
        cityName=cityName0;
    }

    public long getDateTime() {
        return dateTime;
    }

    public double getPressure() {
        return pressure;
    }

    public int getHumidity() {
        return humidity;
    }

    public double getWindSpeed() {
        return windSpeed;
    }

    public double getWindDirection() {
        return windDirection;
    }

    public double getHigh() {
        return high;
    }

    public double getLow() {
        return low;
    }

    public String getDescription() {
        return description;
    }

    public int getWeatherId() {
        return weatherId;
    }
    public String getCityName(){
        return cityName;
    };

}
