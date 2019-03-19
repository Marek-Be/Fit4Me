package com.example.fit4me;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.ProgressBar;

public class HomePage extends AppCompatActivity {
<<<<<<< HEAD

    private String [] extras;

=======
    private int total;
>>>>>>> c6dd646ab0623df0e9e2f42464de0942aed364bb
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        //receive arguments from CreateProfile Activity
        extras = getIntent().getStringArrayExtra("arguments");
        if(extras == null)
        {
            return;
        }
        String userName = extras[0];
        String dailyGoal = extras[1];
        TextView nameText = findViewById(R.id.nameText);
        nameText.setText(userName);
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
