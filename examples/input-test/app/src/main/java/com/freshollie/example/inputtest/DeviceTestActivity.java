package com.freshollie.example.inputtest;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.freshollie.example.R;
import com.freshollie.shuttlexpress.ShuttleXpressConnection;
import com.freshollie.shuttlexpress.ShuttleXpressDevice;

public class DeviceTestActivity extends AppCompatActivity {
    public static String TAG = DeviceTestActivity.class.getSimpleName();

    private ShuttleXpressDevice shuttleXpressDevice;
    private ShuttleXpressConnection deviceConnection;

    private DeviceRepresentation deviceRepresentation = new DeviceRepresentation();

    private ShuttleXpressConnection.ConnectionChangeListener connectionChangeListener =
            new ShuttleXpressConnection.ConnectionChangeListener() {
        @Override
        public void onConnected() {
            setConnectionStatus(ShuttleXpressDevice.STATUS_CONNECTED);
        }

        @Override
        public void onDisconnected() {
            setConnectionStatus(ShuttleXpressDevice.STATUS_DISCONNECTED);
        }
    };

    private ShuttleXpressDevice.KeyListener deviceKeyListener = new ShuttleXpressDevice.KeyListener() {
        @Override
        public void onDown(int keycode) {
            if (keycode == ShuttleXpressDevice.KeyCodes.WHEEL_RIGHT ||
                    keycode == ShuttleXpressDevice.KeyCodes.WHEEL_LEFT) {
                deviceRepresentation.rotateClickWheel(keycode);
            }
        }

        @Override
        public void onUp(int id) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        deviceRepresentation.loadFromView(findViewById(R.id.shuttle_xpress_representation));

        deviceConnection = new ShuttleXpressConnection(this);
        shuttleXpressDevice = deviceConnection.getDevice();

        registerCallbacks();
        refreshConnectionStatus();
    }

    public void registerCallbacks() {
        deviceConnection.registerConnectionChangeListener(connectionChangeListener);
        shuttleXpressDevice.registerKeyListener(deviceKeyListener);
    }

    public void unregisterDeviceCallbacks() {
        deviceConnection.unregisterConnectionChangeListener(connectionChangeListener);
        shuttleXpressDevice.unregisterKeyListener(deviceKeyListener);
    }

    public void refreshConnectionStatus() {
        if (deviceConnection.isConnected()) {
            Log.v(TAG, "Device is currently connected");
            setConnectionStatus(ShuttleXpressDevice.STATUS_CONNECTED);
        } else {
            Log.v(TAG, "Device is currently disconnected");
            setConnectionStatus(ShuttleXpressDevice.STATUS_DISCONNECTED);
        }
    }

    public void setConnectionStatus(int connectionStatus) {
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
        if (deviceConnection.isRunning()) {
            deviceConnection.close();
        }

        deviceConnection.open();
    }

    public void closeConnection() {
        deviceConnection.close();
    }

    public void onDestroy() {
        super.onDestroy();
        unregisterDeviceCallbacks();
        closeConnection();
    }
}
