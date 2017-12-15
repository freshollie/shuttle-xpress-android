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
 * Created by Oliver Bell on 30.11.17.
 */

public class ShuttleXpressConnection {
    public static final String TAG = ShuttleXpressConnection.class.getSimpleName();

    private static final int WAIT_FOR_ATTACH_TIMEOUT = 5000;

    public static final int STATE_CONNECTED = 23947234;
    public static final int STATE_CONNECTING = 34532455;
    public static final int STATE_RECONNECTING = 4657465;
    public static final int STATE_DISCONNECTED = 3289489;

    private static final int NOTIFICATION_ID = 34543532;
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
        void onChange(int newState);
    }

    /**
     * This receiver is called when a usb device is detached and when a usb
     * permission is granted or denied.
     */
    private BroadcastReceiver usbBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            log("Intent Received: " + intent.getAction());

            if (ACTION_USB_PERMISSION.equals(intent.getAction())) {
                synchronized (this) {
                    UsbDevice device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        if (device != null) {
                            log("Permission for device granted");
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
            CharSequence name = context.getString(R.string.notification_channel_name);
            String description = context.getString(R.string.notification_channel_description);

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
                .setSmallIcon(R.drawable.ic_notification_shuttle_xpress)
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

    private void attemptReopenConnection() {
        setConnectionState(STATE_RECONNECTING);

        showReconnectingNotification();
        closeConnection();

        log("Attempting to reconnect");
        attemptConnection();
    }

    private void closeConnection() {
        log("Closing connection to device");

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
            log("Opening");
            context.registerReceiver(usbBroadcastReceiver, intentFilter);
            running = true;

            setConnectionState(STATE_CONNECTING);
            showConnectingNotification();

            attemptConnection();
        }
    }

    /**
     * Closes the connection  and closes device connection
     */
    public void close() {
        if (running) {
            running = false;
            log("Closing");

            context.unregisterReceiver(usbBroadcastReceiver);

            if (connectionState != STATE_DISCONNECTED) {
                setConnectionState(STATE_DISCONNECTED);
                closeConnection();
                showDisconnectedNotification();
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

    private void setConnectionState(int state) {
        connectionState = state;
        notifyConnectionStateChange();
    }

    private void notifyConnectionStateChange() {
        synchronized (connectionStateChangeListeners) {
            final int finalConnectionState = connectionState;
            for (final ConnectionStateChangeListener changeListener: connectionStateChangeListeners) {
                mainThread.post(new Runnable() {
                    @Override
                    public void run() {
                        changeListener.onChange(finalConnectionState);
                    }
                });
            }
        }
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

    private void showConnectedNotification() {
        if (showNotification) {
            Notification notification = notificationBuilder
                    .setContentText(context.getString(R.string.notification_connected_text))
                    .setOngoing(true)
                    .build();
            notificationManager.notify(NOTIFICATION_ID, notification);
        }
    }

    private void showDisconnectedNotification() {
        // Stub
    }

    private void showConnectingNotification() {
        if (showNotification) {
            Notification notification = notificationBuilder
                    .setContentText(context.getString(R.string.notification_text_connecting))
                    .build();
            notificationManager.notify(NOTIFICATION_ID, notification);
        }
    }

    private void showReconnectingNotification() {
        if (showNotification) {
            Notification notification = notificationBuilder
                    .setContentText(context.getString(R.string.notification_text_connection_issues))
                    .build();
            notificationManager.notify(NOTIFICATION_ID, notification);
        }
    }

    private class ShuttleXpressConnectThread extends Thread {
        private final String TAG = ShuttleXpressConnectThread.class.getSimpleName();
        @Override
        public void run() {
            log("Started");

            long startTime = System.currentTimeMillis();

            while ((System.currentTimeMillis() - startTime) < WAIT_FOR_ATTACH_TIMEOUT && !interrupted()) {
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

            if ((System.currentTimeMillis() - startTime) >= WAIT_FOR_ATTACH_TIMEOUT) {
                log("Waited too long for device to attach");
                close();
            }
        }

        private void requestConnection() {
            openConnectionThread = null;

            log("Requesting connection to device");

            UsbDevice device = getUsbDevice();
            if (device != null) {
                if (usbManager.hasPermission(device)) {
                    openConnection(device);
                } else {
                    log("Requesting permission for device");
                    usbManager.requestPermission(device, usbPermissionIntent);
                }
                return;
            }

            log("No devices found");
            close();
        }

        private void openConnection(UsbDevice usbDevice) {
            if (!running) {
                return;
            }

            log("Opening connection to device");
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
                    setConnectionState(STATE_CONNECTED);
                    showConnectedNotification();
                    return;
                } else {
                    connection.close();
                    inUsbRequest.close();
                }
            }

            log("Error opening connection");
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
            log("Started");

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
                    log("Device connection error");
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

            log("Stopped");

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

    private void log(String message) {
        if (BuildConfig.DEBUG) Log.d(TAG, message);
    }
}
