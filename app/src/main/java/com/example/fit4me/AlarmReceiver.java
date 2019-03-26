package com.example.fit4me;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.fitness.Fitness;

public class AlarmReceiver extends BroadcastReceiver {
    private GoogleApiClient mApiClient;

    @Override
    public void onReceive(Context context, Intent intent){
        mApiClient = new GoogleApiClient.Builder(context) //Set up API client to read information
                .addApi(Fitness.HISTORY_API)
                .addScope(new Scope(Scopes.FITNESS_ACTIVITY_READ))
                .build();
        mApiClient.connect();
        new GetDailyStepCount().execute();

        Log.i("Alarm","The alarm has been called.");
    }

    private class GetDailyStepCount extends AsyncTask<Void, Void, Void> {
        //TODO check if goal has been reached, if not reset all stars and add to database,
        // if it has been reached make sure to give them the star and add to database
        protected Void doInBackground(Void... params) {
            int todaysSteps = HomePage.getSteps(mApiClient);
            Log.i(MainActivity.GOOGLE_FIT_TAG,"Steps walked today: " + todaysSteps);
            return null;
        }
    }
}
