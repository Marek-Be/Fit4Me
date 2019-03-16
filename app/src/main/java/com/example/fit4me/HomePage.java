package com.example.fit4me;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class HomePage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        //receive arguments from CreateProfile Activity
        String dailyGoal = getIntent().getStringExtra("stepGoalText");
        TextView goalText = findViewById(R.id.goalText);
        goalText.setText(dailyGoal);
    }

    //display data from Google Fit API
}
