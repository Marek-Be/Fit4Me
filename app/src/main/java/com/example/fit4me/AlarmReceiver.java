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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AlarmReceiver extends BroadcastReceiver {
    private GoogleApiClient mApiClient;
    private Context context;

    @Override
    public void onReceive(Context context, Intent intent){
        this.context = context;
        mApiClient = new GoogleApiClient.Builder(context) //Set up API client to read information
                .addApi(Fitness.HISTORY_API)
                .addScope(new Scope(Scopes.FITNESS_ACTIVITY_READ))
                .build();
        mApiClient.connect();
        new GetDailyStepCount().execute();
    }

    private class GetDailyStepCount extends AsyncTask<Void, Void, Void> {

        protected Void doInBackground(Void... params) {
            DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            Date date = new Date();
            String todaysDate = dateFormat.format(date);
            todaysDate = todaysDate.substring(0,10);

            DatabaseHandler database = new DatabaseHandler(context,null);
            int todaysSteps = HomePage.getSteps(mApiClient);
            Log.i(MainActivity.GOOGLE_FIT_TAG,"Steps walked today: " + todaysSteps);

            int goal = database.getGoal();
            database.addDay(todaysDate,todaysSteps,goal);
            if(!database.getGoalReached()) {
                if (todaysSteps >= goal) {
                    int star = database.getStars();
                    if (star < 5)
                        database.setStars(star + 1);
                } else
                    database.setStars(0);
            }
            database.setGoalReached(false);
            return null;
        }
    }

}
