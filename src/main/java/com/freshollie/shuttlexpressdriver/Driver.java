package com.freshollie.shuttlexpressdriver;

import android.content.Context;
import android.content.Intent;

/**
 * Created by freshollie on 1/3/17.
 */

public class Driver {
    Context context;

    public Driver(Context appContext) {
        context = appContext;
    }

    public ShuttleXpressDevice getDevice() {
        return ShuttleXpressDevice.getInstance();
    }

    private void startDriverService(String action) {
        context.startService(
                new Intent(context, DriverService.class)
                        .setAction(action)
        );
    }


    public void start() {
        startDriverService(DriverService.ACTION_CONNECT);
    }

    /**
     * Starts the driver service without a notification
     * if silent is true
     */
    public void startSilent() {
        startDriverService(DriverService.ACTION_CONNECT_SILENT);
    }

    public void stop() {
        startDriverService(DriverService.ACTION_DISCONNECT);
    }
}
