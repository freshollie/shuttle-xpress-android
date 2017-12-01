package com.freshollie.example;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.freshollie.shuttlexpress.DriverService;
import com.freshollie.shuttlexpress.ShuttleXpressDevice;

public class DeviceStatusActivity extends AppCompatActivity {
    public static String TAG = DeviceStatusActivity.class.getSimpleName();

    private ShuttleXpressDevice shuttleXpressDevice = ShuttleXpressDevice.getInstance();
    private DeviceRepresentation deviceRepresentation = new DeviceRepresentation();

    private ShuttleXpressDevice.ConnectedListener connectedListener =
            new ShuttleXpressDevice.ConnectedListener() {
        @Override
        public void onConnected() {
            setConnectionText(ShuttleXpressDevice.STATUS_CONNECTED);
        }

        @Override
        public void onDisconnected() {
            setConnectionText(ShuttleXpressDevice.STATUS_DISCONNECTED);
        }
    };

    private ShuttleXpressDevice.ButtonListener deviceButtonListener = new ShuttleXpressDevice.ButtonListener() {
        @Override
        public void onDown(int id) {

        }

        @Override
        public void onUp(int id) {

        }
    };

    private ShuttleXpressDevice.ClickWheelListener clickWheelListener = new ShuttleXpressDevice.ClickWheelListener() {
        @Override
        public void onRight() {
            deviceRepresentation.rotateClickWheel(ShuttleXpressDevice.ACTION_RIGHT);
        }

        @Override
        public void onLeft() {
            deviceRepresentation.rotateClickWheel(ShuttleXpressDevice.ACTION_RIGHT);
        }
    };

    private ShuttleXpressDevice.RingListener ringListener = new ShuttleXpressDevice.RingListener() {
        @Override
        public void onRight() {

        }

        @Override
        public void onLeft() {

        }

        @Override
        public void onMiddle() {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        deviceRepresentation.loadFromView(findViewById(R.id.shuttle_xpress_representation));
        registerDeviceCallbacks();
        refreshConnectionText();
    }

    public void registerDeviceCallbacks() {
        shuttleXpressDevice.registerButtonListener(deviceButtonListener);
        shuttleXpressDevice.registerClickWheelListener(clickWheelListener);
        shuttleXpressDevice.registerConnectedListener(connectedListener);
        shuttleXpressDevice.registerRingListener(ringListener);
    }

    public void unregisterDeviceCallbacks() {
        shuttleXpressDevice.unregisterButtonListener(deviceButtonListener);
        shuttleXpressDevice.unregisterClickWheelListener(clickWheelListener);
        shuttleXpressDevice.unregisterConnectedListener(connectedListener);
        shuttleXpressDevice.unregisterRingListener(ringListener);
    }

    public void refreshConnectionText() {
        if (shuttleXpressDevice.isConnected()) {
            Log.v(TAG, "Device is currently connected");
            setConnectionText(ShuttleXpressDevice.STATUS_CONNECTED);
        } else {
            Log.v(TAG, "Device is currently disconnected");
            setConnectionText(ShuttleXpressDevice.STATUS_DISCONNECTED);
        }
    }

    public void setConnectionText(int connectionStatus) {
        TextView connectionText = (TextView) findViewById(R.id.connection_status_text);
        Button connectButton = (Button) findViewById(R.id.connect_button);

        if (connectionStatus == ShuttleXpressDevice.STATUS_CONNECTED) {
            connectionText.setText(getString(R.string.connected));
            connectButton.setText(getString(R.string.disconnect));
            connectButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    closeConnection();
                }
            });
        } else {
            connectionText.setText(getString(R.string.disconnected));
            connectButton.setText(getString(R.string.connect));
            connectButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openConnection();
                }
            });
        }

    }

    public void openConnection() {
        startService(
                new Intent(this, DriverService.class)
                        .setAction(DriverService.ACTION_CONNECT)
        );
    }

    public void closeConnection() {
        startService(
                new Intent(this, DriverService.class)
                        .setAction(DriverService.ACTION_DISCONNECT)
        );
    }

    public void onDestroy() {
        super.onDestroy();
        unregisterDeviceCallbacks();
    }
}
