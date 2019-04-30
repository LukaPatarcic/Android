package com.luka.backgroundtoggle;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;

public class MainActivity extends AppCompatActivity {

    View view;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Switch bgToggle = (Switch) findViewById(R.id.bgSwitch);
        view = this.getWindow().getDecorView();
        bgToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
             if(isChecked){
                 view.setBackgroundResource(R.color.dark);

             }else{
                 view.setBackgroundResource(R.drawable.blue_background);
             }
            }
        });
    }
}
