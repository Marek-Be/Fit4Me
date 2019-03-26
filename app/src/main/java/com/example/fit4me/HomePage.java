package com.example.fit4me;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class HomePage extends AppCompatActivity{

    private static final String GOOGLE_FIT_TAG = "Google Fit API";
    private GoogleApiClient mApiClient;
    private String [] extras;
    private int goal;
    private int dailyTotal;
    private List<ImageButton> stars;
    private static final int[] star_IDs = {R.id.star1, R.id.star2, R.id.star3, R.id.star4, R.id.star5};
    private boolean [] goalReached;
    private int currentDay;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        //myProgress = progress;
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

        //progress bar functionality
        ProgressBar progress = findViewById(R.id.determinateBar);
        if(dailyGoal != null && dailyGoal.length() > 0)   //star_on dailyGoal contains an int
            goal = Integer.parseInt(dailyGoal);
        progress.setMax(goal);
        updateProgressBar();

        //star image buttons
        stars = new ArrayList<ImageButton>(star_IDs.length);
        for(int i:star_IDs)
        {
            ImageButton star = findViewById(i);
            //if star images are pressed -> bring to screen displaying that day's data - at the moment just brings to empty screen
            star.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(HomePage.this, DailyData.class);
                    int c = 0;
                    boolean found = false;
                    while(!found && c < star_IDs.length)
                    {
                        //pass in selected dayNumber as argument to access data for that day
                        if(star_IDs[c] == v.getId())
                        {
                            found = true;
                            String dayNum = Integer.toString(c);
                            intent.putExtra("star ID", dayNum);
                            startActivity(intent);
                        }
                        else
                        {
                            c++;
                        }
                    }

                }
            });
            stars.add(star);
        }

        //initally set all days of goal reached to false
        goalReached = new boolean[star_IDs.length];
        for(int i = 0; i < goalReached.length; i++)
        {
            goalReached[i] = false;
        }
        currentDay = 0;
    }

    private void updateProgressBar(){    //Access the API and use it to update the progress bar.
        new GetDailyStepCount().execute();
    }



    //display data from Google Fit API
    private class GetDailyStepCount extends AsyncTask<Void, Void, Void> {

        protected Void doInBackground(Void... params) {

            final int TOTAL_DAILY_STEPS = (int) getSteps(mApiClient);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    if(TOTAL_DAILY_STEPS > goal)
                    {        //TODO put code for when they have achieved their goal here
                        Toast.makeText(getApplicationContext(), "Congratulations. You have reached your goal.  Steps taken : " + TOTAL_DAILY_STEPS, Toast.LENGTH_SHORT).show();
                        //change star image for the day
                        if(currentDay < star_IDs.length)
                        {
                            goalReached[currentDay]=true;
                            //**NOTE: needs testing
                            stars.get(currentDay).setImageResource(R.drawable.star_on);
                            currentDay++;
                        }
                        //else celebration animation - weekly goal reached
                    }
                    else
                        Toast.makeText(getApplicationContext(), "Steps taken : " + TOTAL_DAILY_STEPS, Toast.LENGTH_SHORT).show();
                    ProgressBar progress = findViewById(R.id.determinateBar);
                    progress.setProgress(TOTAL_DAILY_STEPS);
                    // **NOTE: avatar movement needs testing
                    // get avatar X coordinates moving with progress bar
                    ImageView avatar = findViewById(R.id.avatar);
                    avatar.setTranslationX(avatar.getX() + TOTAL_DAILY_STEPS);
                }
            });
            return null;
        }


    }
    public static long getSteps(GoogleApiClient mApiClient){
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
        return total;
    }



}

