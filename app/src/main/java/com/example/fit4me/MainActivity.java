package com.example.fit4me;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;

import android.content.IntentSender;
import android.util.Log;
import android.view.animation.AlphaAnimation;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.FitnessStatusCodes;
import com.google.android.gms.fitness.data.DataType;

import java.util.Calendar;


public class MainActivity extends AppCompatActivity
        implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    public static final String GOOGLE_FIT_TAG = "Google Fit API";
    private static final int REQUEST_OAUTH = 1;
    private static final int LOAD_TIME = 3000;
    private static final String AUTH_PENDING = "auth_state_pending";

    private boolean authInProgress = false;
    private GoogleApiClient mApiClient;
    private boolean initialized = false;
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DatabaseHandler database = new DatabaseHandler(this,null);
        int goal = database.getGoal();
        if(goal > 0){ //If profile already created
            Log.i("Database", "Returning user");
            Intent intent = new Intent(MainActivity.this, HomePage.class);
            initialized = true;
            startActivity(intent);
        }

        if (savedInstanceState != null)
            authInProgress = savedInstanceState.getBoolean(AUTH_PENDING);
        setContentView(R.layout.activity_main);

        //logo animation
        AlphaAnimation fadeIn = new AlphaAnimation(0.0f, 1.0f);
        TextView logo = findViewById(R.id.logo);
        logo.startAnimation(fadeIn);
        fadeIn.setDuration(1500);
        fadeIn.setFillAfter(true);

        //setting up alarmManager
        AlarmManager alarmMgr = (AlarmManager)getSystemService(Context.ALARM_SERVICE); //TODO fix bug where alarmManager is getting called when the app is created
        Intent intent = new Intent(this, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this,0,intent,0);
        Calendar time = Calendar.getInstance();
        time.set(Calendar.HOUR_OF_DAY, 23);
        time.set(Calendar.MINUTE, 55);
        time.set(Calendar.SECOND, 0);
        alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, time.getTimeInMillis(),AlarmManager.INTERVAL_DAY,pendingIntent);

        mApiClient = new GoogleApiClient.Builder(this)
                .addApi(Fitness.RECORDING_API)
                .addScope(new Scope(Scopes.FITNESS_ACTIVITY_READ))
                .addConnectionCallbacks(this)
                .enableAutoManage(this, 0, this)
                .build();
        mApiClient.connect();
    }

    protected void onStart() {
        super.onStart();
        if(initialized){      //Prevent users from returning to the page
           Intent intent = new Intent(MainActivity.this, HomePage.class);
           startActivity(intent);
        }
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
    }

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
}
