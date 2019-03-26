package com.example.fit4me;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;

import android.content.IntentSender;
import android.util.Log;
import android.view.animation.AlphaAnimation;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.FitnessStatusCodes;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.result.DailyTotalResult;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;


public class MainActivity extends AppCompatActivity
        implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final int REQUEST_OAUTH = 1;
    private static final int LOAD_TIME = 3000;
    private static final String AUTH_PENDING = "auth_state_pending";
    private static final String APP_INITIALIZED = "initialized";
    private static final String GOOGLE_FIT_TAG = "Google Fit API";

    private boolean authInProgress = false;
    private GoogleApiClient mApiClient;
    private boolean initialized = false;
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            initialized = savedInstanceState.getBoolean(APP_INITIALIZED);
            if(initialized){
                Intent intent = new Intent(MainActivity.this, HomePage.class);
                startActivity(intent);
            }
            authInProgress = savedInstanceState.getBoolean(AUTH_PENDING);
        }
        setContentView(R.layout.activity_main);

        //logo animation
        AlphaAnimation fadeIn = new AlphaAnimation(0.0f, 1.0f);
        TextView logo = findViewById(R.id.logo);
        logo.startAnimation(fadeIn);
        fadeIn.setDuration(1500);
        fadeIn.setFillAfter(true);

        //setting up alarmManager
        AlarmManager alarmMgr = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, alarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this,0,intent,0);
        Calendar time = Calendar.getInstance();
        time.set(Calendar.HOUR_OF_DAY, 14);
        time.set(Calendar.MINUTE, 36);
        time.set(Calendar.SECOND, 0);
        alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, time.getTimeInMillis(),AlarmManager.INTERVAL_DAY,pendingIntent);

        mApiClient = new GoogleApiClient.Builder(this)
                .addApi(Fitness.RECORDING_API)
                .addScope(new Scope(Scopes.FITNESS_ACTIVITY_READ))
                .addConnectionCallbacks(this)
                .enableAutoManage(this, 0, this)
                .build();
        mApiClient.connect();
        
        new Handler().postDelayed(new Runnable(){
            @Override
            public void run()
            {
                Intent i = new Intent(MainActivity.this, CreateProfile.class);
                startActivity(i);
            }
        }, LOAD_TIME);

    }
/*
>>>>>>> e071072a4d86e11e20a7be4dc3670dcabc0df316
    @Override
    protected void onStart() {
        super.onStart();
        //if(initialized){      //Uncomment to prevent returning to the main menu
         //   Intent intent = new Intent(MainActivity.this, HomePage.class);
        //    startActivity(intent);
        //}
        handler.postDelayed(new Runnable(){
            @Override
            public void run()
            {
                if(initialized) {
                    Intent intent = new Intent(MainActivity.this, CreateProfile.class);
                    startActivity(intent);
                }
                else {
                    Log.i(GOOGLE_FIT_TAG, "Setting up API!");
                    if(mApiClient.isConnected())
                        subscribe();
                    else if(!mApiClient.isConnecting())
                        mApiClient.connect();
                    handler.postDelayed(this, LOAD_TIME);
                }
            }
        }, LOAD_TIME);
    }*/

    @Override
    protected void onStop(){
        super.onStop();
        handler.removeCallbacksAndMessages(null);
    }
    
    @Override
    public void onConnected(Bundle bundle) {
        subscribe();
    }

    //Create a subscription to the step count to get the Google API to start recording it.
    private void subscribe() {
        Log.i(GOOGLE_FIT_TAG, "Attempting to subscribe");
        Fitness.RecordingApi.subscribe(mApiClient, DataType.TYPE_STEP_COUNT_CUMULATIVE)
                .setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {
                        if (status.isSuccess()) {
                            if (status.getStatusCode() == FitnessStatusCodes.SUCCESS_ALREADY_SUBSCRIBED)
                                Log.i(GOOGLE_FIT_TAG, "Existing subscription for activity detected.");
                            else
                                Log.i(GOOGLE_FIT_TAG, "Successfully subscribed!");
                            initialized = true;
                        }
                        else
                            Log.w(GOOGLE_FIT_TAG, "There was a problem subscribing.");
                    }
                });
    }


    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        if( !authInProgress ) {
            try {
                authInProgress = true;
                connectionResult.startResolutionForResult( MainActivity.this, REQUEST_OAUTH );
            } catch(IntentSender.SendIntentException e ) { }
        } else
            Log.e( GOOGLE_FIT_TAG, "authInProgress" );
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(AUTH_PENDING, authInProgress);
    }


    /*private class GetDailyStepCount extends AsyncTask<Void, Void, Void> { //not being called

        protected Void doInBackground(Void... params) {

            long total = 0;
            //HomePage progress = new HomePage();

            PendingResult<DailyTotalResult> result = Fitness.HistoryApi.readDailyTotal(mApiClient, DataType.TYPE_STEP_COUNT_DELTA);
            DailyTotalResult totalResult = result.await(30, TimeUnit.SECONDS);
            if (totalResult.getStatus().isSuccess()) {

                DataSet totalSet = totalResult.getTotal();
                if(totalSet == null || totalSet.isEmpty()) {
                    total = 0;
                }
                else{
                    total = totalSet.getDataPoints().get(0).getValue(Field.FIELD_STEPS).asInt();
                    Log.i("daily total", Integer.toString((int) total));
                    //progress.setData(total);
                }

            } else
                Log.w(TAG, "There was a problem getting the step count.");

            Log.i(TAG, "Total steps: " + total);
            dailySteps = total;
            final long TOTAL_DAILY_STEPS = total;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), "Steps taken : " + TOTAL_DAILY_STEPS, Toast.LENGTH_SHORT).show();
                }
            });
            return null;
        }
        outState.putBoolean(APP_INITIALIZED, initialized);
    }*/
}
