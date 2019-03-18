package com.example.fit4me;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.view.View;

import android.content.IntentSender;
import android.util.Log;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
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

import java.util.concurrent.TimeUnit;


public class MainActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private static final int REQUEST_OAUTH = 1;
    private static final String AUTH_PENDING = "auth_state_pending";
    private boolean authInProgress = false;
    private GoogleApiClient mApiClient;
    private static final String TAG = "Google Record API";
    private long dailySteps;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //logo animation
        AlphaAnimation fadeIn = new AlphaAnimation(0.0f, 1.0f);
        TextView logo = findViewById(R.id.logo);
        logo.startAnimation(fadeIn);
        fadeIn.setDuration(1500);
        fadeIn.setFillAfter(true);

        //button functionality to CreateProfile activity
        Button button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Start the background stepcounter.
                //Intent intentBackground = new Intent(MainActivity.this, BackgroundAppService.class);
                //startService(intentBackground);

                Intent intent = new Intent(MainActivity.this, CreateProfile.class);
                startActivity(intent);
            }
        });

        Button stepsButton = findViewById(R.id.stepsButton);
        stepsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new GetDailyStepCount().execute();
            }
        });

        if (savedInstanceState != null)
            authInProgress = savedInstanceState.getBoolean(AUTH_PENDING);

        mApiClient = new GoogleApiClient.Builder(this)
                .addApi(Fitness.RECORDING_API)
                .addApi(Fitness.HISTORY_API)
                .addApi(Fitness.SENSORS_API)
                .addApi(Fitness.SESSIONS_API)
                .addScope(new Scope(Scopes.FITNESS_ACTIVITY_READ))
                .addConnectionCallbacks(this)
                .enableAutoManage(this, 0, this)
                .build();
        mApiClient.connect();
    }

    @Override
    public void onConnected(Bundle bundle) {
        subscribe();
    }

    public void subscribe() {
        Log.i(TAG, "Attempting to subscribe");
        Fitness.RecordingApi.subscribe(mApiClient, DataType.TYPE_STEP_COUNT_CUMULATIVE)
                .setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {
                        if (status.isSuccess()) {
                            if (status.getStatusCode() == FitnessStatusCodes.SUCCESS_ALREADY_SUBSCRIBED)
                                Log.i(TAG, "Existing subscription for activity detected.");
                            else
                                Log.i(TAG, "Successfully subscribed!");
                        }
                        else
                            Log.w(TAG, "There was a problem subscribing.");
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
            Log.e( "GoogleFit", "authInProgress" );
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if( requestCode == REQUEST_OAUTH ) {
            authInProgress = false;
            if( resultCode == RESULT_OK )
                if( !mApiClient.isConnecting() && !mApiClient.isConnected() )
                    mApiClient.connect();
            else if( resultCode == RESULT_CANCELED )
                Log.e( "GoogleFit", "RESULT_CANCELED" );
        }
        else
            Log.e("GoogleFit", "requestCode NOT request_oauth");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(AUTH_PENDING, authInProgress);
    }


    private class GetDailyStepCount extends AsyncTask<Void, Void, Void> {

        protected Void doInBackground(Void... params) {
            long total = 0;

            PendingResult<DailyTotalResult> result = Fitness.HistoryApi.readDailyTotal(mApiClient, DataType.TYPE_STEP_COUNT_DELTA);
            DailyTotalResult totalResult = result.await(30, TimeUnit.SECONDS);
            if (totalResult.getStatus().isSuccess()) {
                DataSet totalSet = totalResult.getTotal();
                if(totalSet == null || totalSet.isEmpty())
                    total = 0;
                else
                    total = totalSet.getDataPoints().get(0).getValue(Field.FIELD_STEPS).asInt();
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
    }
}
