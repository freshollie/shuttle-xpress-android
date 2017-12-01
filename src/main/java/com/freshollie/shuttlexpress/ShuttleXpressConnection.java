package com.freshollie.shuttlexpress;

import android.app.Notification;
import android.app.NotificationChannel;
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
import android.os.Build;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.nio.ByteBuffer;
import java.util.ArrayList;

/**
 * Created by obell on 30.11.17.
 */

public class ShuttleXpressConnection {
    public static final String TAG = ShuttleXpressConnection.class.getSimpleName();

    private static final int WAIT_FOR_ATTACH_TIMEOUT = 5000;

    public static final int STATE_CONNECTED = 23947234;
    public static final int STATE_CONNECTING = 34532455;
    public static final int STATE_RECONNECTING = 4657465;
    public static final int STATE_DISCONNECTED = 3289489;

    private static final int NOTIFICATION_ID = 1;
    private static final String NOTIFICATION_CHANNEL_ID = "USB_CONNECTION";

    private static final String ACTION_USB_PERMISSION =
            "com.freshollie.shuttlexpressdriver.DriverService.action.USB_PERMISSION";

    private final Context context;

    private final NotificationCompat.Builder notificationBuilder;
    private final PendingIntent usbPermissionIntent;

    private final IntentFilter intentFilter;

    private final UsbManager usbManager;
    private final NotificationManager notificationManager;

    private Handler mainThread;

    private boolean running = false;
    private boolean showNotification = false;
    private int connectionState;

    private ShuttleXpressDevice shuttleXpressDevice;

    private ShuttleXpressReadThread readThread;
    private ShuttleXpressConnectThread openConnectionThread;

    private final ArrayList<ConnectionStateChangeListener> connectionStateChangeListeners = new ArrayList<>();

    public interface ConnectionStateChangeListener {
        void onConnected();
        void onReconnecting();
        void onConnecting();
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

        connectionState = STATE_DISCONNECTED;

        shuttleXpressDevice = new ShuttleXpressDevice();
        mainThread = new Handler(context.getMainLooper());

        // Notification channel for android devices larger than Oreo
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && notificationManager != null) {
            CharSequence name = "Shuttle Xpress connection status";
            String description = "Provides status of the usb connection to the Shuttle Xpress device";

            NotificationChannel channel = new NotificationChannel(
                    NOTIFICATION_CHANNEL_ID,
                    name,
                    NotificationManager.IMPORTANCE_LOW
            );
            channel.setDescription(description);
            channel.setShowBadge(false);
            channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);

            notificationManager.createNotificationChannel(channel);
        }

        notificationBuilder = new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
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
        if (openConnectionThread == null || !openConnectionThread.isAlive()) {
            openConnectionThread = new ShuttleXpressConnectThread();
            openConnectionThread.start();
        }
    }

    /**
     * Run a quite reconnect without informing the user
     */
    private void attemptReopenConnection() {
        connectionState = STATE_RECONNECTING;

        notifyReconnecting();
        closeConnection();

        Log.v(TAG, "Attempting to reconnect");
        attemptConnection();
    }

    private void closeConnection() {
        Log.v(TAG, "Closing connection to device");

        if (openConnectionThread != null && openConnectionThread.isAlive()) {
            openConnectionThread.interrupt();
        }
        openConnectionThread = null;

        if (readThread != null && readThread.isOpen()) {
            readThread.close();
        }

        readThread = null;
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

            connectionState = STATE_CONNECTING;
            notifyConnecting();

            attemptConnection();
        }
    }

    /**
     * Closes the connection  and closes device connection
     */
    public void close() {
        if (running) {
            running = false;
            Log.v(TAG, "Closing");

            context.unregisterReceiver(usbBroadcastReceiver);

            if (connectionState != STATE_DISCONNECTED) {
                connectionState = STATE_DISCONNECTED;
                closeConnection();
                notifyDisconnected();
            }

            cancelNotification();
        }
    }

    public boolean isRunning() {
        return running;
    }

    public int getConnectionState() {
        return connectionState;
    }

    public ShuttleXpressDevice getDevice() {
        return shuttleXpressDevice;
    }

    public void registerConnectionChangeListener(ConnectionStateChangeListener listener) {
        connectionStateChangeListeners.add(listener);
    }

    public void unregisterConnectionChangeListener(ConnectionStateChangeListener listener) {
        connectionStateChangeListeners.remove(listener);
    }

    public void setShowNotifications(boolean show) {
        showNotification = show;
    }

    private void cancelNotification() {
        notificationManager.cancel(NOTIFICATION_ID);
    }

    private void notifyConnected() {
        synchronized (connectionStateChangeListeners) {
            for (final ConnectionStateChangeListener changeListener: connectionStateChangeListeners) {
                mainThread.post(new Runnable() {
                    @Override
                    public void run() {
                        changeListener.onConnected();
                    }
                });
            }
        }

        if (showNotification) {
            Notification notification = notificationBuilder
                    .setContentText(context.getString(R.string.notification_connected_text))
                    .setOngoing(true)
                    .build();
            notificationManager.notify(NOTIFICATION_ID, notification);
        }
    }

    private void notifyDisconnected() {
        synchronized (connectionStateChangeListeners) {
            for (final ConnectionStateChangeListener changeListener: connectionStateChangeListeners) {
                mainThread.post(new Runnable() {
                    @Override
                    public void run() {
                        changeListener.onDisconnected();
                    }
                });
            }
        }
    }

    private void notifyConnecting() {
        synchronized (connectionStateChangeListeners) {
            for (final ConnectionStateChangeListener changeListener: connectionStateChangeListeners) {
                mainThread.post(new Runnable() {
                    @Override
                    public void run() {
                        changeListener.onConnecting();
                    }
                });
            }
        }
    }

    private void notifyReconnecting() {
        synchronized (connectionStateChangeListeners) {
            for (final ConnectionStateChangeListener changeListener: connectionStateChangeListeners) {
                mainThread.post(new Runnable() {
                    @Override
                    public void run() {
                        changeListener.onReconnecting();
                    }
                });
            }
        }

        if (showNotification) {
            Notification notification = notificationBuilder
                    .setContentText("Connection issues, attempting to reconnect")
                    .build();
            notificationManager.notify(NOTIFICATION_ID, notification);
        }
    }

    private class ShuttleXpressConnectThread extends Thread {
        private final String TAG = ShuttleXpressConnectThread.class.getSimpleName();
        @Override
        public void run() {
            Log.v(TAG, "Started");

            long startTime = System.currentTimeMillis();

            while ((System.currentTimeMillis() - startTime) < WAIT_FOR_ATTACH_TIMEOUT && !Thread.interrupted()) {
                if (isDeviceAttached()) {
                    // The device is already marked as connected, so this is probably a reconnect
                    if (connectionState == STATE_RECONNECTING) {
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

            Log.v(TAG, "Waited too long for device to attach");
            close();
        }

        private void requestConnection() {
            openConnectionThread = null;

            Log.v(TAG, "Requesting connection to device");

            UsbDevice device = getUsbDevice();
            if (device != null) {
                if (usbManager.hasPermission(device)) {
                    openConnection(device);
                } else {
                    Log.v(TAG, "Requesting permission for device");
                    usbManager.requestPermission(device, usbPermissionIntent);
                }
                return;
            }

            Log.v(TAG, "No devices found");
            close();
        }

        private void openConnection(UsbDevice usbDevice) {
            if (!running) {
                return;
            }

            Log.v(TAG, "Opening connection to device");
            if (usbDevice != null) {
                UsbInterface usbInterface = usbDevice.getInterface(0);
                UsbEndpoint usbEndpoint = usbInterface.getEndpoint(0);
                int inMaxPacketSize = usbEndpoint.getMaxPacketSize();

                UsbDeviceConnection connection = usbManager.openDevice(usbDevice);
                connection.claimInterface(usbInterface, true);

                UsbRequest inUsbRequest = new UsbRequest();
                inUsbRequest.initialize(connection, usbEndpoint);

                readThread = new ShuttleXpressReadThread(connection, inUsbRequest, inMaxPacketSize);
                if (readThread.open()) {
                    connectionState = STATE_CONNECTED;
                    notifyConnected();
                } else {
                    connection.close();
                    inUsbRequest.close();
                }
            }

            Log.d(TAG, "Error opening connection");
            close();
        }
    }

    private class ShuttleXpressReadThread extends Thread {
        private final String TAG = ShuttleXpressReadThread.class.getSimpleName();

        UsbDeviceConnection usbDeviceConnection;
        UsbRequest inUsbRequest;
        ByteBuffer readBuffer;

        int inMaxPacketSize;
        boolean firstInput;
        boolean open;

        private ByteBuffer dataReadBuffer;

        ShuttleXpressReadThread(UsbDeviceConnection connection, UsbRequest request, int maxPacketSize) {
            super();
            inMaxPacketSize = maxPacketSize;
            inUsbRequest = request;
            usbDeviceConnection = connection;
            readBuffer = ByteBuffer.allocate(inMaxPacketSize);
        }

        @Override
        public void run() {
            Log.v(TAG, "Started");

            while (open) {
                // Wait for data to be received
                UsbRequest usbResponse = usbDeviceConnection.requestWait();

                // If the type of data received is correct
                if (usbResponse != null &&
                        usbResponse == inUsbRequest &&
                        open &&
                        inUsbRequest.queue(readBuffer, inMaxPacketSize)) {

                    // Copy the received data buffer and set that as the current state of the device
                    final ByteBuffer receivedData = readBuffer.duplicate();
                    int numBytesReceived = receivedData.position();

                    // If the next request doesn't work then the device is probably dead and the
                    // last input was not valid so don't let the device know it was changed.
                    //
                    // Also make sure we actually read data before parsing it
                    if (numBytesReceived >= 5) {
                        // Let the device know it has new data
                        mainThread.post(new Runnable() {
                            @Override
                            /**
                             * Make sure any functions with callbacks are run inside the main
                             * thread
                             */
                            public void run() {
                                shuttleXpressDevice.parseNewData(receivedData, firstInput);
                                firstInput = false;
                            }
                        });


                    }
                } else if (open) { // device disconnected
                    Log.v(TAG, "Device connection error");
                    break;
                }
            }

            if (open) {
                close();
                try {
                    sleep(500);
                } catch (InterruptedException ignored){}
                attemptReopenConnection();
            }

            Log.v(TAG, "Stopped");

            usbDeviceConnection.close();
            inUsbRequest.close();
        }

        boolean open() {
            if (inUsbRequest.queue(readBuffer, inMaxPacketSize)) {
                firstInput = true;
                open = true;
                start();
                return true;
            }
            return false;
        }

        void close() {
            open = false;
        }

        boolean isOpen() {
            return open;
        }
    }
}
