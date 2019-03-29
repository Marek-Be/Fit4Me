package com.example.fit4me;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
    private static final int[] star_IDs = {R.id.star1, R.id.star2, R.id.star3, R.id.star4, R.id.star5};
    private static final int STEP_UPDATE_TIME = 20000;  //Every 20 seconds update progress bar.

    private GoogleApiClient mApiClient;
    private String [] extras;

    private int goal;
    private List<ImageView> stars;
    private boolean [] goalReached;
    private int currentStar;

    HandlerThread thread;
    private Handler stepsUpdateHandler;


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
        final String userName = extras[0];
        String dailyGoal = extras[1];
        TextView nameText = findViewById(R.id.nameText);
        nameText.setText(String.format("Go %s!", userName));    //C way of printing

        //progress bar functionality
        ProgressBar progress = findViewById(R.id.determinateBar);
        if(dailyGoal != null && dailyGoal.length() > 0)   //star_on dailyGoal contains an int
            goal = Integer.parseInt(dailyGoal);
        progress.setMax(goal);

        //activity button
        Button activityButton = findViewById(R.id.addactivity);
        activityButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(HomePage.this, AddActivity.class);
                intent.putExtra("username", userName);
                startActivity(intent);
            }
        });

        //trackprogress button
        Button progressButton = findViewById(R.id.trackprogress);
        progressButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(HomePage.this, DailyData.class);
                intent.putExtra("username", userName);
                startActivity(intent);
            }
        });

        //initialise star image arrayList
        stars = new ArrayList<ImageView>(star_IDs.length);
        for(int i:star_IDs)
        {
            ImageView star = findViewById(i);
            stars.add(star);
            thread = new HandlerThread("MyHandlerThread");
            thread.start();
            stepsUpdateHandler = new Handler(thread.getLooper());
        }

        //initially set all days of goal reached to false
        goalReached = new boolean[star_IDs.length];
        for(int i = 0; i < goalReached.length; i++)
        {
            goalReached[i] = false;
        }
        currentStar = 0;
    }

    @Override
    protected void onStart() {
        super.onStart();

        stepsUpdateHandler.postDelayed(new Runnable(){
            @Override
            public void run()
            {
                final int TOTAL_DAILY_STEPS = getSteps(mApiClient);
                stepsUpdateHandler.postDelayed(this, STEP_UPDATE_TIME);     //Queue next timer

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(TOTAL_DAILY_STEPS > goal) {   //TODO put code for when they have achieved their goal here
                            Toast.makeText(getApplicationContext(), "Congratulations. You have reached your goal.  Steps taken : "
                                    + TOTAL_DAILY_STEPS, Toast.LENGTH_SHORT).show();
                            //change star image for the day
                            if(currentStar < star_IDs.length) {
                                goalReached[currentStar]=true;
                                //**NOTE: needs testing
                                stars.get(currentStar).setImageResource(android.R.drawable.btn_star_big_on);
                                currentStar++;
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
                        ObjectAnimator animator = ObjectAnimator.ofFloat(avatar, "translationX", 350f);
                        animator.setDuration(5000);
                        animator.start();
                    }
                });
            }
        }, 100);
    }

    @Override
    protected void onStop(){
        super.onStop();
        stepsUpdateHandler.removeCallbacksAndMessages(null);
    }


    public static int getSteps(GoogleApiClient mApiClient){
        long total = 0;

        PendingResult<DailyTotalResult> result = Fitness.HistoryApi.readDailyTotal(mApiClient, DataType.TYPE_STEP_COUNT_DELTA);
        DailyTotalResult totalResult = result.await(30, TimeUnit.SECONDS);
        if (totalResult.getStatus().isSuccess()) {
            DataSet totalSet = totalResult.getTotal();
            if(totalSet != null && !totalSet.isEmpty())
                total = totalSet.getDataPoints().get(0).getValue(Field.FIELD_STEPS).asInt();
        }
        else
            Log.w(GOOGLE_FIT_TAG, "There was a problem getting the step count.");
        Log.i(GOOGLE_FIT_TAG, "Steps taken: " + total);
        return (int) total;
    }


    //resetStars function - called if daily goal is not reached by midnight
    public void resetStars (ArrayList<ImageView> stars)
    {
        for(int i = 0; i < stars.size(); i++)
        {
            stars.get(i).setImageResource(android.R.drawable.btn_star_big_off);
        }
        currentStar = 0;
    }

}

