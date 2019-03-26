package com.example.fit4me;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.fitness.Fitness;

public class AlarmReceiver extends BroadcastReceiver {
    int total;
    int goal;
    GoogleApiClient mApiClient;
    @Override
    public void onReceive(Context context, Intent intent){
        HomePage goalCheck = new HomePage();
        mApiClient = new GoogleApiClient.Builder(context) //Set up API client to read information
                .addApi(Fitness.HISTORY_API)
                .addScope(new Scope(Scopes.FITNESS_ACTIVITY_READ))
                .build();
        mApiClient.connect();

        long toPrint = HomePage.getSteps(mApiClient);
        Log.i("total steps : " + toPrint,".");

       //check if goal has been reached, if not reset all stars and add to database, if it has been reached dont update stars and add to database
        Log.i("alarm worked","alarm worked");

    }
}
