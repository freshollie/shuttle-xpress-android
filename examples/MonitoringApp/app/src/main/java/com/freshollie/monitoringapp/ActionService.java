package com.freshollie.monitoringapp;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by freshollie on 12/28/16.
 */

public class ActionService extends Service {
    public static String TAG = ActionService.class.getSimpleName();

    @Override
    public void onCreate() {

    }

    @Override
    public int onStartCommand(Intent intent, int flag, int startId) {
        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

}
