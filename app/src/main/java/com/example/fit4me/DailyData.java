package com.example.fit4me;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class DailyData extends AppCompatActivity {

    private String starID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_data);

        //find out which star was selected and display data for corresponding day
        starID = getIntent().getStringExtra("star ID");
        TextView starIDText = findViewById(R.id.starIDText);
        starIDText.setText(starID);
    }
}
