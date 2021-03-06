package com.example.fit4me;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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
    private static final int GET_ACTIVITIES = 1;
    private DatabaseHandler database;
    private GoogleApiClient mApiClient;

    private int goal;
    private List<ImageView> stars;

    private HandlerThread thread;
    private Handler stepsUpdateHandler;

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

        database = new DatabaseHandler(this, null);
        goal = database.getGoal(); //load goal from database

        ProgressBar progress = findViewById(R.id.determinateBar);
        progress.setMax(goal); //progress bar

        //edit profile button
        Button editButton = findViewById(R.id.editprofile);
        editButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(HomePage.this, CreateProfile.class);
                startActivity(intent);
            }
        });

        //activity button
        Button activityButton = findViewById(R.id.addactivity);
        activityButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(HomePage.this, AddActivity.class);
                startActivityForResult(intent, GET_ACTIVITIES);
            }
        });

        //track progress button
        Button progressButton = findViewById(R.id.trackprogress);
        progressButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(HomePage.this, DailyData.class);
                startActivity(intent);
            }
        });

        //initialise star image arrayList
        stars = new ArrayList<ImageView>(star_IDs.length);
        for(int i:star_IDs) {
            ImageView star = findViewById(i);
            stars.add(star);
        }

        int starCount = database.getStars();//initially all days of goal reached are false
        for(int i = 0; i < starCount; i++)
            stars.get(i).setImageResource(android.R.drawable.btn_star_big_on);

        thread = new HandlerThread("MyHandlerThread");
        thread.start();
        stepsUpdateHandler = new Handler(thread.getLooper());
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
                        if(TOTAL_DAILY_STEPS > goal) {
                            Toast.makeText(getApplicationContext(), "Congratulations. You have reached your goal.  Steps taken : "
                                    + TOTAL_DAILY_STEPS, Toast.LENGTH_SHORT).show();
                            if(!(database.getGoalReached())) {
                                int currentStar = database.getStars() + 1;
                                if (currentStar <= star_IDs.length) {
                                    database.setStars(currentStar); //change star image for the day
                                    database.setGoalReached(true);
                                    stars.get(currentStar-1).setImageResource(android.R.drawable.btn_star_big_on); 
                                }
                            }
                        }
                        else
                            Toast.makeText(getApplicationContext(), "Steps taken : " + TOTAL_DAILY_STEPS, Toast.LENGTH_SHORT).show();
                        ProgressBar progress = findViewById(R.id.determinateBar);
                        progress.setProgress(TOTAL_DAILY_STEPS);
                        // TODO: avatar movement needs testing. Get avatar X coordinates moving with progress bar
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i("Activities", "Successfully called functions");
        if (resultCode == RESULT_OK && requestCode == GET_ACTIVITIES) {
            Log.i("Activities", "Well that is something");
            ImageView [] setStickers = {findViewById(R.id.footballsticker),findViewById(R.id.swimsticker),findViewById(R.id.bballsticker), findViewById(R.id.cyclesticker)};

            for(int i = 0; i < setStickers.length; i++) //initially reset activities stickers to white
                setStickers[i].setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.colorPrimary));
            boolean[] activities = data.getBooleanArrayExtra("Activities");

            for(int i = 0; i < activities.length; i++) { //set selected activities stickers to green
                if(activities[i]) {
                    Log.i("Activities", "Activity marked " + i);
                    setStickers[i].setColorFilter(ContextCompat.getColor(getApplicationContext(),R.color.colorAccent));
                }
            }
        }
    }

}