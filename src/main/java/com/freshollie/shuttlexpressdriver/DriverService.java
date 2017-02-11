package com.freshollie.shuttlexpressdriver;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
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
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.nio.ByteBuffer;

public class DriverService extends Service {
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

    private PendingIntent usbPermissionIntent;

    private UsbManager usbManager;
    private UsbDevice usbDevice;
    private UsbDeviceConnection usbDeviceConnection;
    private UsbRequest inUsbRequest;
    private int inMaxPacketSize;

    private boolean running = false;

    private boolean runBackground = true;

    private Handler mainHandler;

    private NotificationCompat.Builder notificationBuilder;

    private ByteBuffer receivedDataBuffer;

    private ShuttleXpressDevice shuttleXpressDevice = ShuttleXpressDevice.getInstance();

    private Thread inputListeningThread = new Thread(new Runnable() {
        @Override
        public void run() {
            Log.v(TAG, "Started input listener");

            while (shuttleXpressDevice.isConnected()) {
                // Wait for data to be received

                UsbRequest usbRequest = usbDeviceConnection.requestWait();

                // If the type of data received is correct
                if (usbRequest == inUsbRequest &&
                        shuttleXpressDevice.isConnected()) {

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
                    Log.v(TAG, "Input thread interrupted");
                    stop();
                    break;
                }

            }
        }
    });

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
                                requestConnection();
                            }
                        }
                    }
                    return;

                case (UsbManager.ACTION_USB_DEVICE_DETACHED):
                    UsbDevice device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                    if (device != null && isRunning()) {
                        if (device.getVendorId() == ShuttleXpressDevice.VENDOR_ID &&
                                device.getProductId() == ShuttleXpressDevice.PRODUCT_ID) {
                            stop();
                        }
                    }

            }
        }
    };

    @Override
    public void onCreate() {
        Log.v(TAG, "Created");

        usbManager = (UsbManager) getSystemService(USB_SERVICE);
        usbPermissionIntent =
                PendingIntent.getBroadcast(this, 0, new Intent(ACTION_USB_PERMISSION), 0);

        mainHandler = new Handler(getMainLooper());

        IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
        filter.addAction(ACTION_USB_PERMISSION);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        registerReceiver(usbBroadcastReceiver, filter);

        notificationBuilder = new NotificationCompat.Builder(this)
                .setContentTitle(getString(R.string.driver_title))
                .setContentText(getString(R.string.driver_running_text))
                .setSmallIcon(R.drawable.ic_gamepad_black_24dp)
                .setOngoing(true);
    }

    @Override
    public int onStartCommand(Intent intent, int flag, int startId) {
        switch (intent.getAction()) {
            case ACTION_CONNECT:
                runBackground = true;
                start();

                return START_NOT_STICKY;

            case ACTION_CONNECT_SILENT:
                runBackground = false;
                start();
                return START_NOT_STICKY;

            case ACTION_DISCONNECT:
                stop();
                return START_NOT_STICKY;
        }
        return START_NOT_STICKY;
    }

    public void requestConnection() {
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

    public void openConnection() {
        Log.v(TAG, "Opening connection to device");
        if (usbDevice != null) {
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

                if (runBackground) {
                    Notification notification = notificationBuilder.build();
                    startForeground(NOTIFICATION_ID, notification);
                }

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

            if (runBackground) {
                stopForeground(true);
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
            Log.v(TAG, "Starting");
            running = true;
            requestConnection();
        }
    }

    /**
     * stops the service and closes device connection
     */
    public void stop() {
        if (running) {
            Log.v(TAG, "Stopping");
            running = false;
            closeConnection();
        }
        stopSelf();
    }

    public boolean isRunning() {
        return running;
    }

    @Override
    public void onDestroy() {
        stopForeground(true);
        unregisterReceiver(usbBroadcastReceiver);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}