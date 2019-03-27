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

        String name = getIntent().getStringExtra("username");
        TextView text = findViewById(R.id.progress_text);
        text.setText(String.format("%s's Progress", name));

        //display data for current week in some sort of graphical way

    }
}
