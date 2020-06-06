package com.luka.jsondomaci;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    String json = "{\"list\":[{\"dt\":1471255200,\"temp\":{\"day\":30.04,\"min\":17.35,\"max\":30.04 },\"pressure\":1018.83,\"humidity\":35,\"weather\":[{\"id\":802,\"main\":\"Clouds\",\"description\":\"scattered clouds\",\"icon\":\"03d\"}],\"speed\":2.66, \"deg\":314, \"clouds\":48},{\"dt\":1471341600,\"temp\":{\"day\":29.48,\"min\":19.3,\"max\":29.95 },\"pressure\":1018.63,\"humidity\":100,\"weather\":[{\"id\":501,\"main\":\"Rain\",\"description\":\"moderate rain\",\"icon\":\"10d\"}],\"speed\":2.46,\"deg\":28, \"clouds\":92,\"rain\":8.5},{\"dt\":1471428000,\"temp\":{\"day\":28.96,\"min\":17.84,\"max\":297.54},\"pressure\":1012.1,\"humidity\":95,\"weather\":[{\"id\":501,\"main\":\"Rain\",\"description\":\"moderate rain\",\"icon\":\"10d\"}],\"speed\":2.27,\"deg\":118, \"clouds\":88,\"rain\":5.55},{\"dt\":1471514400,\"temp\":{\"day\":26.89,\"min\":17.84,\"max\":28.95},\"pressure\":1014.27,\"humidity\":83,\"weather\":[{\"id\":500,\"main\":\"Rain\",\"description\":\"light rain\",\"icon\":\"10d\"}],\"speed\":1.98,\"deg\":93, \"clouds\":20},{\"dt\":1471600800,\"temp\":{\"day\":30.04,\"min\":21.87,\"max\":30.04},\"pressure\":1014.65,\"humidity\":0,\"weather\":[{\"id\":500,\"main\":\"Rain\",\"description\":\"light rain\",\"icon\":\"10d\"}],\"speed\":1.66,\"deg\":194, \"clouds\":11, \"rain\":0.22},{\"dt\":1471687200,\"temp\":{\"day\":31.3,\"min\":22.56,\"max\":32.3},\"pressure\":1018.6,\"humidity\":0,\"weather\":[{\"id\":500,\"main\":\"Rain\",\"description\":\"light rain\",\"icon\":\"10d\"}],\"speed\":1.69,\"deg\":180, \"clouds\":16, \"rain\":0.77} ,{\"dt\":1471773600,\"temp\":{\"day\":31.77,\"min\":21.67, \"max\":30.77},\"pressure\":1016.93,\"humidity\":0,\"weather\":[{\"id\":500,\"main\":\"Rain\",\"description\":\"light rain\",\"icon\":\"10d\"}],\"speed\":2.49,\"deg\":227, \"clouds\":14,\"rain\":2.2}]}";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ListView listView=(ListView)findViewById(R.id.list_view);

        ArrayList<String> data_array = new ArrayList<String>();
        try {

            JSONObject jsonObject=new JSONObject(json);
            JSONArray jsonArray = jsonObject.getJSONArray("list");
            int length = jsonArray.length();

            for (int i =0; i<length; i++) {
                JSONObject jsonArrayObject =  jsonArray.getJSONObject(i);
                JSONObject jsonTempObject = jsonArrayObject.getJSONObject("temp");
                int min = jsonTempObject.getInt("min");
                int max = jsonTempObject.getInt("max");
                double speed = jsonArrayObject.getDouble("speed");
                JSONArray jsonWeatherArray = jsonArrayObject.getJSONArray("weather");
                JSONObject jsonWeatherObject = jsonWeatherArray.getJSONObject(0);
                String main = jsonWeatherObject.getString("main");
                String msg = max+"/"+min+", "+main+", Wind="+speed;
                data_array.add(msg);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        ArrayAdapter<String> adapter=new ArrayAdapter<String>(
                this,
                R.layout.listitem,
                data_array
        );
        listView.setAdapter(adapter);
    }
}

