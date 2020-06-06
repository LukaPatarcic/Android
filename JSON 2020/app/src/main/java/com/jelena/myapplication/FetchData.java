package com.jelena.myapplication;

import android.net.Uri;
import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class FetchData extends AsyncTask<Void,Void,String> {
    String data = "";
    private String movie = "";
    private String year = "";
    public MainActivity mainActivity;

    public FetchData(String newMovie,String newYear, MainActivity activity) {
        year = newYear;
        movie = newMovie;
        mainActivity = activity;
    }

    @Override
    protected String doInBackground(Void... voids) {

        try {
            Uri builtUri = Uri.parse("https://omdbapi.com/?").buildUpon()
                    .appendQueryParameter("apikey", "2e576279")
                    .appendQueryParameter("y",year)
                    .appendQueryParameter("s",movie)
                    .build();
            URL url = new URL(builtUri.toString());

            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            InputStream inputStream = httpURLConnection.getInputStream();
            BufferedReader bufferedReader =  new BufferedReader(new InputStreamReader(inputStream));
            String line = "";
            while (line != null) {
                line = bufferedReader.readLine();
                data = data + line;
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }

    public String getData() {
        return data;
    }

    @Override
    protected void onPostExecute(String aVoid) {
        super.onPostExecute(aVoid);
        mainActivity.formatJSON(aVoid);
    }


}
