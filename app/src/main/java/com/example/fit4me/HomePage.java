package com.example.fit4me;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.ProgressBar;

public class HomePage extends AppCompatActivity {
    private int total;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        //receive arguments from CreateProfile Activity
        String dailyGoal = getIntent().getStringExtra("stepGoalText");
        TextView goalText = findViewById(R.id.goalText);
        ProgressBar progress = findViewById(R.id.determinateBar);
        progress.setMax(Integer.parseInt(dailyGoal));
        progress.setProgress(total);

        goalText.setText(dailyGoal);
    }

    public void setData(long stepTotal){
        total = (int) stepTotal;
    }

    //display data from Google Fit API
}
