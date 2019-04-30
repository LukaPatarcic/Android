package com.luka.prviprojekat;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView scrollText;

        scrollText = findViewById(R.id.textAbout);
        scrollText.setMovementMethod(new ScrollingMovementMethod());
    }
}
