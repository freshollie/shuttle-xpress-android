package com.freshollie.shuttlexpressdriver;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.hardware.usb.UsbRequest;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.nio.ByteBuffer;

/**
 * Created by obell on 30.11.17.
 */

public class ShuttleXpressConnection {
    public static String TAG = DriverService.class.getSimpleName();
    private static int NOTIFICATION_ID = 1;

    private static final String ACTION_USB_PERMISSION =
            "com.freshollie.shuttlexpressdriver.DriverService.action.USB_PERMISSION";

    // Connects with a notification so that the service will not be killed in the background
    public static final String ACTION_CONNECT =
            "com.freshollie.shuttlexpressdriver.DriverService.action.CONNECT";

    // Connects without a notification,
    // for apps that only need the shuttle device while in the foreground
    public static final String ACTION_CONNECT_SILENT =
            "com.freshollie.shuttlexpressdriver.DriverService.action.CONNECT_SILENT";

    public static final String ACTION_DISCONNECT =
            "com.freshollie.shuttlexpressdriver.DriverService.action.DISCONNECT";

    private Context context;

    private PendingIntent usbPermissionIntent;

    private UsbManager usbManager;
    private UsbDevice usbDevice;
    private UsbDeviceConnection usbDeviceConnection;
    private UsbRequest inUsbRequest;
    private int inMaxPacketSize;

    private boolean running = false;

    private boolean showNotification = false;

    private Handler mainHandler;

    private NotificationCompat.Builder notificationBuilder;

    private ByteBuffer receivedDataBuffer;

    private ShuttleXpressDevice shuttleXpressDevice = ShuttleXpressDevice.getInstance();

    private int RECONNECT_TIMEOUT = 5000;

    // Used in case the device disconnects for 5000ms
    private Runnable reconnectRunnable = new Runnable() {
        @Override
        public void run() {
            Log.v(TAG, "Attempting to reconnect to device");

            long startTime = System.currentTimeMillis();

            while ((System.currentTimeMillis() - startTime) < RECONNECT_TIMEOUT && !Thread.interrupted()) {
                if (isDeviceAttached()) {
                    Log.v(TAG, "Success, reconnecting");
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        return;
                    }

                    start();
                    return;
                }
            }

            Log.v(TAG, "Reconnect attempt failed");

            stop();
        }
    };

    private Thread reconnectThread;

    private Thread inputListeningThread;

    private Runnable inputListeningRunnable = new Runnable() {
        @Override
        public void run() {
            Log.v(TAG, "Started input listener");

            while (shuttleXpressDevice.isConnected() && !Thread.interrupted()) {
                // Wait for data to be received
                UsbRequest usbRequest = usbDeviceConnection.requestWait();


                // If the type of data received is correct
                if (usbRequest == inUsbRequest &&
                        shuttleXpressDevice.isConnected() &&
                        isDeviceAttached()) {

                    ByteBuffer previousData = shuttleXpressDevice.getStateBuffer();

                    // Copy the received data buffer and set that as the current state of the device
                    shuttleXpressDevice.setStateBuffer(receivedDataBuffer.duplicate());

                    // If the next request doesn't work then the device is probably dead and the
                    // last input was not valid so don't let the device know it was changed.
                    if (usbRequest.queue(receivedDataBuffer, inMaxPacketSize)) {

                        // Let the device know it has new data
                        mainHandler.post(new Runnable() {
                            @Override
                            /**
                             * Make sure any functions with callbacks are run inside the main
                             * thread
                             */
                            public void run() {
                                shuttleXpressDevice.onNewData();
                            }
                        });
                    } else {
                        shuttleXpressDevice.setStateBuffer(previousData);
                    }



                } else if (usbRequest == null) { // device disconnected
                    Log.v(TAG, "Input thread interrupted: Device disconnected");
                    attemptReopenConnection();
                    return;
                }
            }
            Log.v(TAG, "Input thread interrupted");
        }
    };

    /**
     * This receiver is called when a usb device is detached and when a usb
     * permission is granted or denied.
     */
    private BroadcastReceiver usbBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.v(TAG, "Intent Received: " + intent.getAction());

            switch (intent.getAction()) {

                case (ACTION_USB_PERMISSION):
                    synchronized (this) {
                        UsbDevice device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                        if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                            if (device != null) {
                                Log.v(TAG, "Permission for device granted");
                                start();
                            }
                        }
                    }
                    return;

                case (UsbManager.ACTION_USB_DEVICE_DETACHED):
                    UsbDevice device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                    if (device != null && isRunning()) {
                        if (device.getVendorId() == ShuttleXpressDevice.VENDOR_ID &&
                                device.getProductId() == ShuttleXpressDevice.PRODUCT_ID) {

                        }
                    }

            }
        }
    };

    public ShuttleXpressConnection(Context appContext) {
        Log.v(TAG, "Created");

        context = appContext;

        usbManager = (UsbManager) context.getSystemService(Context.USB_SERVICE);
        usbPermissionIntent =
                PendingIntent.getBroadcast(context, 0, new Intent(ACTION_USB_PERMISSION), 0);

        mainHandler = new Handler(context.getMainLooper());

        notificationBuilder = new NotificationCompat.Builder(context)
                .setContentTitle(context.getString(R.string.driver_title))
                .setContentText(context.getString(R.string.driver_running_text))
                .setSmallIcon(R.drawable.ic_gamepad_black_24dp)
                .setOngoing(true);

        IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
        filter.addAction(ACTION_USB_PERMISSION);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        context.registerReceiver(usbBroadcastReceiver, filter);
    }

    public void setShowNotification(boolean show) {
        showNotification = show;
    }

    private void notifyRunning() {
        Notification notification = notificationBuilder
                .setContentText(context.getString(R.string.driver_running_text))
                .build();
    }

    private void notifyConnectionIssue() {
        Notification notification = notificationBuilder
                .setContentText("Connection issues, reconnecting")
                .build();
        mana
    }

    private boolean isDeviceAttached() {
        for (UsbDevice device : usbManager.getDeviceList().values()) {
            if (device.getProductId() == ShuttleXpressDevice.PRODUCT_ID &&
                    device.getVendorId() == ShuttleXpressDevice.VENDOR_ID) {
                return true;
            }
        }

        return false;
    }

    private void attemptReopenConnection() {
        Log.v(TAG, "Attempting to reconnect");

        notifyConnectionIssue();

        if (inputListeningThread != null) {
            inputListeningThread.interrupt();
        }

        if (reconnectThread != null) {
            reconnectThread.interrupt();
        }

        inUsbRequest.close();
        usbDeviceConnection.close();
        usbDevice = null;

        running = false;

        reconnectThread = new Thread(reconnectRunnable);
        reconnectThread.start();
    }

    private void requestConnection() {
        Log.v(TAG, "Requesting connection to device");
        for (UsbDevice device : usbManager.getDeviceList().values()) {
            Log.v(TAG, "Checking device:");
            Log.d(TAG, "ProductId: " + String.valueOf(device.getProductId()));
            Log.d(TAG, "VendorId: " + String.valueOf(device.getVendorId()));

            if (device.getProductId() == ShuttleXpressDevice.PRODUCT_ID &&
                    device.getVendorId() == ShuttleXpressDevice.VENDOR_ID) {
                Log.v(TAG, "Found device");

                if (usbManager.hasPermission(device)) {
                    usbDevice = device;
                    openConnection();
                } else {
                    Log.v(TAG, "Requesting permission for device");
                    usbManager.requestPermission(device, usbPermissionIntent);
                }
                return;
            }
        }

        Log.v(TAG, "No devices found, closing");
        stop();

    }

    private void openConnection() {
        Log.v(TAG, "Opening connection to device");
        if (usbDevice != null) {
            running = true;
            UsbInterface usbInterface = usbDevice.getInterface(0);
            UsbEndpoint usbEndpoint = usbInterface.getEndpoint(0);
            inMaxPacketSize = usbEndpoint.getMaxPacketSize();

            usbDeviceConnection = usbManager.openDevice(usbDevice);
            usbDeviceConnection.claimInterface(usbInterface, true);

            shuttleXpressDevice.resetDevice(inMaxPacketSize);

            inUsbRequest = new UsbRequest();
            inUsbRequest.initialize(usbDeviceConnection, usbEndpoint);
            receivedDataBuffer = ByteBuffer.allocate(inMaxPacketSize);

            if (inUsbRequest.queue(receivedDataBuffer, inMaxPacketSize)) {
                shuttleXpressDevice.setConnected();

                if (showNotification) {
                    notifyRunning();
                }

                inputListeningThread = new Thread(inputListeningRunnable);
                inputListeningThread.start();
            } else {
                Log.d(TAG, "Error requesting data, stopping");
                stop();
            }
        }
    }

    public void closeConnection() {
        if (shuttleXpressDevice.isConnected()) {
            Log.v(TAG, "Closing connection");

            if (inputListeningThread != null) {
                inputListeningThread.interrupt();
            }

            if (reconnectThread != null) {
                reconnectThread.interrupt();
            }

            if (showNotification) {

            }

            inUsbRequest.close();
            usbDeviceConnection.close();
            usbDevice = null;
            shuttleXpressDevice.setDisconnected();
        }

    }

    public void start() {
        // Start device service if the device service is currently not running
        if (!running) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Log.v(TAG, "Starting");
                    requestConnection();
                }
            }).start();

        }
    }

    /**
     * stops the service and closes device connection
     */
    public void stop() {
        Log.v(TAG, "Stopping");

        running = false;
        closeConnection();

        if (inputListeningThread != null) {
            inputListeningThread.interrupt();
        }

        if (reconnectThread != null) {
            reconnectThread.interrupt();
        }
    }

    public boolean isRunning() {
        return running;
    }
}
