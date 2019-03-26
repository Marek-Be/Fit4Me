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

        if(total < goal){
            Toast.makeText(context,"you have not reached your goal", Toast.LENGTH_LONG).show();
        }
        else if(total == goal){
            Toast.makeText(context,"you reached your goal", Toast.LENGTH_LONG).show();
        }
        else{
            Toast.makeText(context, "alarm went off", Toast.LENGTH_LONG).show();
        }
    }
}
