package com.example.fit4me;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class alarmReceiver extends BroadcastReceiver {
    int total;
    int goal;
    @Override
    public void onReceive(Context context, Intent intent){
        HomePage goalCheck = new HomePage();
        total = goalCheck.getTotal();
        goal = goalCheck.getGoal();

       //check if goal has been reached, if not reset all stars and add to database, if it has been reached dont update stars and add to database
        
    }
}
