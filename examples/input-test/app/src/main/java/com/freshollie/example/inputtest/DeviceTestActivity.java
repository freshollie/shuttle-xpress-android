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

    private Button connectButton;
    private TextView connectionText;

    private DeviceRepresentation deviceRepresentation = new DeviceRepresentation();

    private ShuttleXpressConnection.ConnectionStateChangeListener connectionChangeListener =
            new ShuttleXpressConnection.ConnectionStateChangeListener() {
                @Override
                public void onChange(int newState) {
                    refreshConnectionStatus();
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
        deviceConnection.setShowNotifications(true);

        shuttleXpressDevice = deviceConnection.getDevice();

        connectionText = (TextView) findViewById(R.id.connection_status_text);
        connectButton = (Button) findViewById(R.id.connect_button);
        connectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleConnection();
            }
        });

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
        int connectionTextResource = R.string.disconnected;
        int connectButtonTextResource = R.string.disconnect;

        switch(deviceConnection.getConnectionState()) {
            case ShuttleXpressConnection.STATE_CONNECTED:
                connectionTextResource = R.string.connected;
                break;

            case ShuttleXpressConnection.STATE_CONNECTING:
                connectionTextResource = R.string.connecting;
                break;

            case ShuttleXpressConnection.STATE_RECONNECTING:
                connectionTextResource = R.string.reconnecting;
                break;

            case ShuttleXpressConnection.STATE_DISCONNECTED:
                connectButtonTextResource = R.string.connect;
                break;
        }

        connectButton.setText(getString(connectButtonTextResource));
        connectionText.setText(getString(connectionTextResource));
    }

    public void toggleConnection() {
        if (deviceConnection.isRunning()) {
            deviceConnection.close();
        } else {
            deviceConnection.open();
        }
    }

    public void onDestroy() {
        super.onDestroy();
        unregisterDeviceCallbacks();
        deviceConnection.close();
    }
}
