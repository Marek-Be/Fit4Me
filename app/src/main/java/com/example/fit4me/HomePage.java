package com.example.fit4me;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.result.DailyTotalResult;

import java.util.concurrent.TimeUnit;

public class HomePage extends AppCompatActivity {

    private static final String GOOGLE_FIT_TAG = "Google Fit API";
    private GoogleApiClient mApiClient;
    private String [] extras;
    private int totalSteps;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        mApiClient = new GoogleApiClient.Builder(this)  //Set up API client to read information
                .addApi(Fitness.HISTORY_API)
                .addScope(new Scope(Scopes.FITNESS_ACTIVITY_READ))
                .build();
        mApiClient.connect();

        //receive arguments from CreateProfile Activity
        extras = getIntent().getStringArrayExtra("arguments");
        if(extras == null)
            return;
        String userName = extras[0];
        String dailyGoal = extras[1];
        TextView nameText = findViewById(R.id.nameText);
        nameText.setText("Go " + userName + "!");
        TextView goalText = findViewById(R.id.goalText);
        goalText.setText(dailyGoal);

        ProgressBar progress = findViewById(R.id.determinateBar);
        int dailySteps = 0;
        if(dailyGoal != null && dailyGoal.matches("\\d"))   //check dailyGoal contains an int
            dailySteps = Integer.parseInt(dailyGoal);
        progress.setMax(dailySteps);
        progress.setProgress(totalSteps);
        updateProgressBar();
    }

    private void updateProgressBar(){    //Access the API and use it to update the progress bar.
        new GetDailyStepCount().execute();
    }

    //display data from Google Fit API
    private class GetDailyStepCount extends AsyncTask<Void, Void, Void> {

        protected Void doInBackground(Void... params) {
            long total = 0;

            PendingResult<DailyTotalResult> result = Fitness.HistoryApi.readDailyTotal(mApiClient, DataType.TYPE_STEP_COUNT_DELTA);
            DailyTotalResult totalResult = result.await(30, TimeUnit.SECONDS);
            if (totalResult.getStatus().isSuccess()) {
                DataSet totalSet = totalResult.getTotal();
                if(totalSet != null && !totalSet.isEmpty()){
                    total = totalSet.getDataPoints().get(0).getValue(Field.FIELD_STEPS).asInt();
                    Log.i(GOOGLE_FIT_TAG, "Daily total-" + total);
                }
            }
            else
                Log.w(GOOGLE_FIT_TAG, "There was a problem getting the step count.");

            Log.i(GOOGLE_FIT_TAG, "Total steps: " + total);
            //totalSteps = (int) total;
            final int TOTAL_DAILY_STEPS = (int) total;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), "Steps taken : " + TOTAL_DAILY_STEPS, Toast.LENGTH_SHORT).show();
                    ProgressBar progress = findViewById(R.id.determinateBar);
                    progress.setProgress(TOTAL_DAILY_STEPS);
                }
            });
            return null;
        }
    }
}

