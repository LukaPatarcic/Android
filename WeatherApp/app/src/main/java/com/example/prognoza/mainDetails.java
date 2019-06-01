package com.example.prognoza;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class mainDetails extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        Intent i=getIntent();
        String msg=i.getStringExtra("string");
        TextView txt=(TextView)findViewById(R.id.textView);
        txt.setText(msg);
    }
}
