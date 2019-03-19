package com.example.fit4me;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.ProgressBar;
import android.view.View;
import android.widget.Toast;

public class HomePage extends AppCompatActivity {
    int total = 10;
    ProgressBar myProgress;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        //myProgress = progress;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        //receive arguments from CreateProfile Activity
        ProgressBar progress = findViewById(R.id.determinateBar);
        myProgress = progress;
        String dailyGoal = getIntent().getStringExtra("stepGoalText");
        TextView goalText = findViewById(R.id.goalText);
        progress.setMax(Integer.parseInt(dailyGoal));
        progress.setProgress(total);

        goalText.setText(dailyGoal);
    }

    public void increment(View view){
        total += 5;
        myProgress.setProgress(total);
    }



    //display data from Google Fit API
}
