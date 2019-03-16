package com.example.fit4me;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class BackgroundAppService extends Service {
    public BackgroundAppService() {
        //Code for stepcount should go here
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
