package com.example.studenti.myapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button btn = (Button) findViewById(R.id.btn);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                TextView input = (TextView) findViewById(R.id.input);
                RadioButton celsius = (RadioButton) findViewById(R.id.c);
                RadioButton fahrenheit = (RadioButton) findViewById(R.id.f);
                String temp =  input.getText().toString();

                if(celsius.isChecked()){
                    int cResult = (Integer.parseInt(temp) - 32) * 5/9;
                    input.setText(cResult + "");
                }else if(fahrenheit.isChecked()){
                    int fResult = Integer.parseInt(temp) * 9/5 + 32;
                    input.setText(fResult + "");
                }
            }
        });



    }

}
