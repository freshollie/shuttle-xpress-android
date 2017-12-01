package com.freshollie.shuttlexpress;

import android.app.Notification;
import android.app.NotificationManager;
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
import java.util.ArrayList;

/**
 * Created by obell on 30.11.17.
 */

public class ShuttleXpressConnection {
    public final static String TAG = ShuttleXpressConnection.class.getSimpleName();

    private final static int WAIT_FOR_ATTACH_TIMEOUT = 5000;

    private final static int NOTIFICATION_ID = 1;
    private final static String NOTIFICATION_CHANNEL = "None";

    private static final String ACTION_USB_PERMISSION =
            "com.freshollie.shuttlexpressdriver.DriverService.action.USB_PERMISSION";

    private final Context context;

    private final NotificationCompat.Builder notificationBuilder;
    private final PendingIntent usbPermissionIntent;

    private final IntentFilter intentFilter;

    private final UsbManager usbManager;
    private final NotificationManager notificationManager;

    private Handler mainThread;

    private UsbDeviceConnection usbDeviceConnection;
    private UsbRequest inUsbRequest;
    private int inMaxPacketSize;

    private boolean running = false;
    private boolean showNotification = false;
    private boolean connected = false;

    private ByteBuffer dataReadBuffer;
    private ShuttleXpressDevice shuttleXpressDevice;

    private DataReadThread dataReadThread;
    private StartConnectionThread startConnectionThread;

    private ArrayList<ConnectionChangeListener> connectionChangeListeners = new ArrayList<>();

    public interface ConnectionChangeListener {
        void onConnected();
        void onDisconnected();
    }

    /**
     * This receiver is called when a usb device is detached and when a usb
     * permission is granted or denied.
     */
    private BroadcastReceiver usbBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.v(TAG, "Intent Received: " + intent.getAction());

            if (ACTION_USB_PERMISSION.equals(intent.getAction())) {
                synchronized (this) {
                    UsbDevice device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        if (device != null) {
                            Log.v(TAG, "Permission for device granted");
                            attemptConnection();
                        }
                    }
                }
            } else if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(intent.getAction())) {
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
        notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);

        shuttleXpressDevice = new ShuttleXpressDevice();
        mainThread = new Handler(context.getMainLooper());

        notificationBuilder = new NotificationCompat.Builder(context)
                .setContentTitle(context.getString(R.string.notification_title))
                .setContentText(context.getString(R.string.notification_connected_text))
                .setSmallIcon(R.drawable.ic_gamepad_black_24dp)
                .setOngoing(true);


        usbPermissionIntent =
                PendingIntent.getBroadcast(context, 0, new Intent(ACTION_USB_PERMISSION), 0);

        intentFilter = new IntentFilter(ACTION_USB_PERMISSION);
        intentFilter.addAction(ACTION_USB_PERMISSION);
        intentFilter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
    }

    public void setShowNotifications(boolean show) {
        showNotification = show;
    }

    private void showConnectedNotification() {
        if (showNotification) {
            Notification notification = notificationBuilder
                    .setContentText(context.getString(R.string.notification_connected_text))
                    .setOngoing(true)
                    .build();
            notificationManager.notify(NOTIFICATION_ID, notification);
        }
    }

    private void showConnectionIssuesNotification() {
        if (showNotification) {
            Notification notification = notificationBuilder
                    .setContentText("Connection issues, attempting to reconnect")
                    .build();
            notificationManager.notify(NOTIFICATION_ID, notification);
        }
    }

    private void cancelNotification() {
        notificationManager.cancel(NOTIFICATION_ID);
    }

    private UsbDevice getUsbDevice() {
        for (UsbDevice device: usbManager.getDeviceList().values()) {
            if (device.getProductId() == ShuttleXpressDevice.PRODUCT_ID &&
                    device.getVendorId() == ShuttleXpressDevice.VENDOR_ID) {
                return device;
            }
        }

        return null;
    }

    private void attemptConnection() {
        if (startConnectionThread == null || !startConnectionThread.isAlive()) {
            startConnectionThread = new StartConnectionThread();
            startConnectionThread.start();
        }
    }

    /**
     * Run a quite reconnect without informing the user
     */
    private void attemptReopenConnection() {
        Log.v(TAG, "Attempting to reconnect");

        showConnectionIssuesNotification();

        closeConnection();

        attemptConnection();
    }

    private void closeConnection() {
        Log.v(TAG, "Closing connection");

        if (startConnectionThread != null && startConnectionThread.isAlive()) {
            startConnectionThread.interrupt();
        }
        startConnectionThread = null;

        if (dataReadThread != null && dataReadThread.isAlive()) {
            dataReadThread.interrupt();
        }
        dataReadThread = null;

        if (usbDeviceConnection != null) {
            usbDeviceConnection.close();
        }
        usbDeviceConnection = null;

        if (inUsbRequest != null && inUsbRequest.cancel()) {
            // Only close the in request if we manage to cancel the current queue operation
            inUsbRequest.close();
        }

        inUsbRequest = null;




    }

    public boolean isDeviceAttached() {
        return getUsbDevice() != null;
    }

    public void open() {
        // Start device service if the device service is currently not running
        if (!running) {
            Log.v(TAG, "Opening");
            context.registerReceiver(usbBroadcastReceiver, intentFilter);
            running = true;
            attemptConnection();
        }
    }

    /**
     * Stops the  and closes device connection
     */
    public void close() {
        if (running) {
            running = false;
            Log.v(TAG, "Closing");

            context.unregisterReceiver(usbBroadcastReceiver);

            if (connected) {
                connected = false;
                closeConnection();
                notifyDisconnected();
            }

            cancelNotification();
        }
    }

    public boolean isRunning() {
        return running;
    }

    public boolean isConnected() {
        return connected;
    }

    public ShuttleXpressDevice getDevice() {
        return shuttleXpressDevice;
    }

    public void registerConnectionChangeListener(ConnectionChangeListener listener) {
        connectionChangeListeners.add(listener);
    }

    public void unregisterConnectionChangeListener(ConnectionChangeListener listener) {
        connectionChangeListeners.remove(listener);
    }

    private void notifyConnected() {
        synchronized (connectionChangeListeners) {
            for (final ConnectionChangeListener changeListener: connectionChangeListeners) {
                mainThread.post(new Runnable() {
                    @Override
                    public void run() {
                        changeListener.onConnected();
                    }
                });
            }
        }
    }

    private void notifyDisconnected() {
        synchronized (connectionChangeListeners) {
            for (final ConnectionChangeListener changeListener: connectionChangeListeners) {
                mainThread.post(new Runnable() {
                    @Override
                    public void run() {
                        changeListener.onDisconnected();
                    }
                });
            }
        }
    }

    private class StartConnectionThread extends Thread {
        @Override
        public void run() {
            long startTime = System.currentTimeMillis();

            while ((System.currentTimeMillis() - startTime) < WAIT_FOR_ATTACH_TIMEOUT && !Thread.interrupted()) {
                if (isDeviceAttached()) {
                    // The device is already marked as connected, so this is probably a reconnect
                    if (connected) {
                        // Wait 500ms as this will help with reconnection
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            return;
                        }
                    }

                    requestConnection();
                    return;
                }
            }

            Log.v(TAG, "Waited too long for device to attach, closing connection");

            close();
        }

        private void requestConnection() {
            startConnectionThread = null;

            Log.v(TAG, "Requesting connection to device");

            UsbDevice device = getUsbDevice();
            if (device != null) {
                if (usbManager.hasPermission(device)) {
                    if (openConnection(device)) {
                        notifyConnected();
                    }
                } else {
                    Log.v(TAG, "Requesting permission for device");
                    usbManager.requestPermission(device, usbPermissionIntent);
                }
                return;
            }

            Log.v(TAG, "No devices found, closing");
            close();
        }

        private boolean openConnection(UsbDevice usbDevice) {
            if (!running) {
                return false;
            }

            Log.v(TAG, "Opening connection to device");
            if (usbDevice != null) {
                UsbInterface usbInterface = usbDevice.getInterface(0);
                UsbEndpoint usbEndpoint = usbInterface.getEndpoint(0);
                inMaxPacketSize = usbEndpoint.getMaxPacketSize();

                usbDeviceConnection = usbManager.openDevice(usbDevice);
                usbDeviceConnection.claimInterface(usbInterface, true);

                inUsbRequest = new UsbRequest();
                inUsbRequest.initialize(usbDeviceConnection, usbEndpoint);
                dataReadBuffer = ByteBuffer.allocate(inMaxPacketSize);

                if (inUsbRequest.queue(dataReadBuffer, inMaxPacketSize)) {
                    connected = true;

                    dataReadThread = new DataReadThread();
                    dataReadThread.start();

                    showConnectedNotification();

                    return true;
                }
            }

            Log.d(TAG, "Error opening connection, closing");
            close();
            return false;
        }
    }

    private class DataReadThread extends Thread {
        @Override
        public void run() {
            Log.v(TAG, "Data read thread started");

            while (isConnected() && !Thread.interrupted() && usbDeviceConnection != null) {
                // Wait for data to be received
                UsbRequest usbResponse = usbDeviceConnection.requestWait();


                // If the type of data received is correct
                if (usbResponse != null &&
                        usbResponse == inUsbRequest &&
                        isConnected()) {

                    // Copy the received data buffer and set that as the current state of the device
                    final ByteBuffer receivedData = dataReadBuffer.duplicate();

                    // If the next request doesn't work then the device is probably dead and the
                    // last input was not valid so don't let the device know it was changed.
                    if (inUsbRequest.queue(dataReadBuffer, inMaxPacketSize)) {

                        // Let the device know it has new data
                        mainThread.post(new Runnable() {
                            @Override
                            /**
                             * Make sure any functions with callbacks are run inside the main
                             * thread
                             */
                            public void run() {
                                shuttleXpressDevice.parseNewData(receivedData);
                            }
                        });
                    }
                } else if (usbResponse == null) { // device disconnected
                    Log.v(TAG, "Data read thread interrupted: Device disconnected");
                    attemptReopenConnection();
                    return;
                }
            }

            if (dataReadThread != null && dataReadThread == this) {
                Log.v(TAG, "Data read thread stopped");
                close();
            }
        }
    }
}
