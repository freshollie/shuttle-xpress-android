package com.freshollie.shuttlexpressdriver;

import android.app.Notification;
import android.app.NotificationManager;
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

public class DeviceService extends Service {
    public static String TAG = DeviceService.class.getSimpleName();
    private static int NOTIFICATION_ID = 0;

    private static final String ACTION_USB_PERMISSION =
            "com.freshollie.shuttlexpressdriver.DeviceService.action.USB_PERMISSION";

    public static final String ACTION_CONNECT =
            "com.freshollie.shuttlexpressdriver.DeviceService.action.CONNECT";

    public static final String ACTION_DISCONNECT =
            "com.freshollie.shuttlexpressdriver.DeviceService.action.DISCONNECT";

    private PendingIntent usbPermissionIntent;

    private UsbManager usbManager;
    private UsbDevice usbDevice;
    private UsbDeviceConnection usbDeviceConnection;
    private UsbRequest inUsbRequest;
    private int inMaxPacketSize;

    private boolean running = false;

    private NotificationCompat.Builder notificationBuilder;
    private NotificationManager notificationManager;

    private ShuttleXpressDevice shuttleXpressDevice = ShuttleXpressDevice.getInstance();


    //NativeInput m_ni = new NativeInput();

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
                    if (device != null) {
                        if (device.getVendorId() == ShuttleXpressDevice.VENDOR_ID &&
                                device.getDeviceId() == ShuttleXpressDevice.PRODUCT_ID) {
                            stop();
                        }
                    }

            }
        }
    };

    @Override
    public void onCreate() {
        Log.d(TAG, "Created");

        usbManager = (UsbManager) getSystemService(USB_SERVICE);
        usbPermissionIntent =
                PendingIntent.getBroadcast(this, 0, new Intent(ACTION_USB_PERMISSION), 0);

        IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
        filter.addAction(ACTION_USB_PERMISSION);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        registerReceiver(usbBroadcastReceiver, filter);

        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        notificationBuilder = new NotificationCompat.Builder(this)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(getString(R.string.running_text))
                .setContentIntent(
                        PendingIntent.getActivity(
                                this,
                                0,
                                new Intent(this, DeviceStatusActivity.class),
                                PendingIntent.FLAG_UPDATE_CURRENT
                        )
                )
                .setSmallIcon(R.drawable.ic_gamepad_black_24dp)
                .setOngoing(true);

    }

    @Override
    public int onStartCommand(Intent intent, int flag, int startId) {
        switch (intent.getAction()) {
            case ACTION_CONNECT:
                // Start device service if the device service is currently not running
                if (!running) {
                    start();
                }
                return START_NOT_STICKY;

            case ACTION_DISCONNECT:
                stop();
                return START_NOT_STICKY;
        }
        return START_NOT_STICKY;
    }

    public void requestConnection() {
        Log.v(TAG, "Requesting connection to device");
        for (UsbDevice device: usbManager.getDeviceList().values()) {
            Log.d(TAG, "ProductId: " + String.valueOf(device.getDeviceId()));
            Log.d(TAG, "VendorId: "+ String.valueOf(device.getVendorId()));

            if (device.getDeviceId() == ShuttleXpressDevice.PRODUCT_ID &&
                    device.getVendorId() == ShuttleXpressDevice.VENDOR_ID) {
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
            if (inUsbRequest.queue(shuttleXpressDevice.getStateBuffer(), inMaxPacketSize)) {
                shuttleXpressDevice.setConnected();
                Notification notification = notificationBuilder.build();
                notificationManager.notify(NOTIFICATION_ID, notification);
                startForeground(NOTIFICATION_ID, notification);

                startListeningThread();
            } else {
                Log.d(TAG, "Error requesting data, stopping");
                stop();
            }
        }
    }

    public void startListeningThread() {
        Log.v(TAG, "Started connection listener");
        new Thread(new Runnable() {
            @Override
            public void run() {
                while(shuttleXpressDevice.isConnected()) {
                    UsbRequest usbRequest = usbDeviceConnection.requestWait();

                    if (usbRequest == inUsbRequest && shuttleXpressDevice.isConnected()) {

                        new Handler(getMainLooper()).post(new Runnable() {
                            @Override
                            /**
                             * Make sure any functions with callbacks are run inside the main
                             * thread
                             */
                            public void run() {
                                shuttleXpressDevice.newData();
                            }
                        });

                        usbRequest.queue(shuttleXpressDevice.getStateBuffer(), inMaxPacketSize);

                    } else if (usbRequest == null) { // device disconnected
                        stop();
                    }

                }
            }
        }).start();
    }

    public void closeConnection() {
        if (shuttleXpressDevice.isConnected()) {
            Log.v(TAG, "Closing connection");
            notificationManager.cancel(NOTIFICATION_ID);
            usbDeviceConnection.close();
            usbDevice = null;
        }
        shuttleXpressDevice.setDisconnected();

    }

    public void start() {
        Log.v(TAG, "Starting");
        running = true;
        requestConnection();

    }

    /**
     * stops the service and closes device connection
     */
    public void stop() {
        Log.v(TAG, "Stopping");
        if (running) {
            running = false;
            closeConnection();
        }
        stopSelf();
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(usbBroadcastReceiver);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /**
     * We need to perform this on the input before its done
     * adb shell
     * su
     * chmod 666 /dev/input/event3
     */
}
