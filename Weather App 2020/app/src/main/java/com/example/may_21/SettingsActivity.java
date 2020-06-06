package com.example.may_21;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;

public class SettingsActivity extends AppCompatActivity {
    int days;
    String unit="";
    String location;
    ListView lstView;
    Map<String, String> map;
    String selected_city;
    AutoCompleteTextView auto_text;
    SharedPreferences settings;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);


        settings = getDefaultSharedPreferences(getApplicationContext());
        auto_text=(AutoCompleteTextView)findViewById(R.id.auto_text_view);
        lstView =(ListView)findViewById(R.id.list1);

        map = new HashMap<String, String>();
        map.put("Subotica","3189595");
        map.put("Beograd","787607");
        map.put("Novi Sad","3194360");
        map.put("Budimpesta","3054638");
        map.put("Segedin","715429");

        List list_city=new ArrayList<String>();

        for(String key: map.keySet()){
            list_city.add(key);
        }

        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, list_city);
        auto_text.setAdapter(adapter1);

        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_checked, list_city);
        lstView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        lstView.setTextFilterEnabled(true);
        lstView.setAdapter(adapter2);

        lstView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                selected_city = (String) adapterView.getItemAtPosition(i);
                location= map.get(selected_city);
                auto_text.setText(selected_city);
            }
        });
    }

    public void onClick(View view) {
        RadioButton opt3day=(RadioButton)findViewById(R.id.opt_3days);
        RadioButton opt5day=(RadioButton)findViewById(R.id.opt_5_days);
        RadioButton opt7day=(RadioButton)findViewById(R.id.opt_7_days);
        RadioButton optMetric=(RadioButton)findViewById(R.id.opt_metric);
        RadioButton optImperial=(RadioButton)findViewById(R.id.opt_imperial);


        if (opt3day.isChecked()) days=3;
        if (opt5day.isChecked()) days=5;
        if (opt7day.isChecked()) days=7;
        if (optMetric.isChecked()) unit="metric";
        if (optImperial.isChecked()) unit="imperial";



        String auto_text2=auto_text.getEditableText().toString();
        if (auto_text2.length()>0 && map.containsKey(selected_city))
            location= map.get(auto_text2);

        SharedPreferences.Editor editor = settings.edit();

        String location_xml=settings.getString(MainActivity.MY_LOCATION, "");
        String number_of_days = settings.getString(MainActivity.MY_NUMBER_OF_DAYs,"");
        String unit_type = settings.getString(MainActivity.MY_UNIT,"");


        if (!location_xml.equals(location)&&location!=null)
            editor.putString(MainActivity.MY_LOCATION, location);
        if (!number_of_days.equals(days))
            editor.putString(MainActivity.MY_NUMBER_OF_DAYs, String.valueOf(days));
        if (!unit_type.equals(unit))
            editor.putString(MainActivity.MY_UNIT, unit);
        editor.commit();

        finish();
    }
}
