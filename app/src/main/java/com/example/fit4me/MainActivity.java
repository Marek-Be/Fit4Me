package com.example.fit4me;

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
        outState.putBoolean(APP_INITIALIZED, initialized);
    }
}
