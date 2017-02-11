package com.freshollie.shuttlexpressdriver;

import android.inputmethodservice.Keyboard;
import android.util.Log;

import java.nio.ByteBuffer;
import java.security.Key;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by freshollie on 12/26/16.
 */

/**
 * Stores the current device data and performs callbacks
 * when data changes
 */

public class ShuttleXpressDevice {
    public static String TAG = ShuttleXpressDevice.class.getSimpleName();
    public static final int PRODUCT_ID = 32;
    public static final int VENDOR_ID = 2867;

    public static final class KeyCodes {
        public static final int BUTTON_0 = 129379817;
        public static final int BUTTON_1 = 129379818;
        public static final int BUTTON_2 = 129379819;
        public static final int BUTTON_3 = 129379820;
        public static final int BUTTON_4 = 129379821;

        public static final int RING_RIGHT = 129379822;
        public static final int RING_LEFT = 129379823;
        public static final int RING_MIDDLE = 129379824;

        public static final int WHEEL_LEFT = 129379825;
        public static final int WHEEL_RIGHT = 129379826;

        public static final int ACTION_DOWN = 22394082;
        public static final int ACTION_UP = 30923849;

        public static final int STATE_UP = 0;
        public static final int STATE_DOWN = 1;

        public static int NUM_KEYS = 10;
    }

    public static final int STATUS_CONNECTED = 23947234;
    public static final int STATUS_DISCONNECTED = 3289489;

    public static boolean DEBUG_OUT = false;

    private static ShuttleXpressDevice INSTANCE = new ShuttleXpressDevice();

    private ArrayList<KeyListener> keyListeners = new ArrayList<>();
    private ArrayList<ConnectedListener> connectedCallbacks = new ArrayList<>();

    private ByteBuffer state;

    private int ring;
    private int wheel;
    private Integer buttons[];

    private boolean connected = false;

    public static ShuttleXpressDevice getInstance() {
        return INSTANCE;
    }

    public interface KeyListener {
        void onDown(int key);
        void onUp(int key);
    }

    public interface ConnectedListener {
        void onConnected();
        void onDisconnected();
    }

    private ShuttleXpressDevice() {

    }

    public void setConnected() {
        connected = true;
        onConnected();
    }

    public void setDisconnected() {
        connected = false;
        onDisconnected();
    }

    public boolean isConnected() {
        return connected;
    }

    public ByteBuffer getStateBuffer() {
        return state;
    }

    public int getButtonState(int id) {
        return buttons[id];
    }

    public int getRingState() {
        if (ring == 7) {
            return KeyCodes.RING_RIGHT;
        } else if (ring == -7) {
            return KeyCodes.RING_LEFT;
        } else {
            return KeyCodes.RING_MIDDLE;
        }
    }

    public int getRingPosition() {
        return ring;
    }

    public int getWheelPosition() {
        return wheel;
    }

    public void resetDevice(int maxPacketSize) {
        state = ByteBuffer.allocate(maxPacketSize);
        ring = 0;
        wheel = 0;
        buttons = new Integer[5];
        connected = false;
    }

    public void setStateBuffer(ByteBuffer data) {
        state = data;
    }

    /**
     * Parses the new data store the new values
     * and makes the relevant callbacks for changed data
     */
    public void onNewData() {
        if (state != null) {
            int newRing, newWheel;

            newRing = state.get(0); // Byte 1 contains the value of the ring position
            newWheel = state.get(1); // Byte 2 contains the value of the wheel position

            Integer newButtons[] = new Integer[5];

            int newButtonsBytes = ((int) state.get(3)) + 128;
            /*
             * Byte 4 contains a binary value with each bit representing a button
             * Button 1: Bit 4
             * Button 2: Bit 5
             * Button 3: Bit 6
             * Button 4: Bit -7
             * We add 128 to negate the negative bit
             */

            newButtons[4] = ((Byte) state.get(4)).intValue(); // Byte 4 contains the value of button 5

            for (int i = 7; i >= 4; i--) { // Gets the bits from the integer value of Byte 5
                newButtons[i-4] = (newButtonsBytes & (1 << i)) != 0 ? 1 : 0;
            }

            newButtons[3] = newButtons[3] == 1 ? 0: 1;

            if (buttons[0] != null
                    && (newRing != ring || newButtons != buttons || newWheel != wheel)) {

                if (DEBUG_OUT) {
                    Log.v(TAG, "Ring: " + String.valueOf(newRing) + ", Wheel: " + String.valueOf(newWheel) +
                            ", Buttons["
                            + newButtons[0]
                            + newButtons[1]
                            + newButtons[2]
                            + newButtons[3]
                            + newButtons[4]
                            + "]");
                }
                doCallbacks(newButtons, newRing, newWheel);
            }

            buttons = newButtons;
            ring = newRing;
            wheel = newWheel;
        }
    }

    private void onConnected() {
        Log.v(TAG, "Connected");
        for (ConnectedListener listener: connectedCallbacks) {
            listener.onConnected();
        }
    }

    private void onDisconnected() {
        Log.v(TAG, "Disconnected");
        for (ConnectedListener listener: connectedCallbacks) {
            listener.onDisconnected();
        }
    }

    private void onKeyDown(int key) {
        if (DEBUG_OUT) {
            Log.v(TAG, "Key Down:" + String.valueOf(key));
        }
        for (KeyListener keyListener: keyListeners) {
            keyListener.onDown(key);
        }
    }

    private void onKeyUp(int key) {
        if (DEBUG_OUT) {
            Log.v(TAG, "Key Up:" + String.valueOf(key));
        }
        for (KeyListener keyListener: keyListeners) {
            keyListener.onUp(key);
        }
    }

    /**
     * Checks what has changed from the last data to the new data given
     * and runs the correct callbacks for the new data changes
     * @param newButtons
     * @param newRing
     * @param newWheel
     */
    private void doCallbacks(Integer[] newButtons, int newRing, int newWheel) {

        // Checks if the ring position has changed
        if (newRing == 7 && ring != 7) {
            onKeyUp(KeyCodes.RING_MIDDLE);
            onKeyDown(KeyCodes.RING_RIGHT);

        } else if (newRing != 7 && ring == 7) {
            onKeyUp(KeyCodes.RING_RIGHT);
            onKeyDown(KeyCodes.RING_MIDDLE);

        } else if (newRing != -7 && ring == -7) {
            onKeyUp(KeyCodes.RING_LEFT);
            onKeyDown(KeyCodes.RING_MIDDLE);

        } else if (newRing == -7 && ring != -7) {
            onKeyUp(KeyCodes.RING_MIDDLE);
            onKeyDown(KeyCodes.RING_LEFT);
        }

        // Checks if wheel has moved
        if (newWheel != wheel) {
            if (newWheel < 0 && wheel > 0) {
                onKeyDown(KeyCodes.WHEEL_RIGHT);
                onKeyUp(KeyCodes.WHEEL_RIGHT);
            } else if (newWheel > 0 && wheel < 0) {
                onKeyDown(KeyCodes.WHEEL_LEFT);
                onKeyUp(KeyCodes.WHEEL_LEFT);
            } else if (newWheel > wheel) {
                onKeyDown(KeyCodes.WHEEL_RIGHT);
                onKeyUp(KeyCodes.WHEEL_RIGHT);
            } else {
                onKeyDown(KeyCodes.WHEEL_LEFT);
                onKeyUp(KeyCodes.WHEEL_LEFT);
            }
        }

        // Checks if buttons have changed
        for (int i = 0; i < 5; i++) {
            if (!newButtons[i].equals(buttons[i])) {
                if (newButtons[i] == 1) {
                    onKeyDown(KeyCodes.BUTTON_0 + i); // Because buttons are in chronological order
                } else {
                    onKeyUp(KeyCodes.BUTTON_0 + i);
                }
            }
        }
    }

    public void registerKeyListener(KeyListener listener) {
        keyListeners.add(listener);
    }

    public void unregisterKeyListener(KeyListener listener) {
        keyListeners.remove(listener);
    }

    public void registerConnectedListener(ConnectedListener listener) {
        connectedCallbacks.add(listener);
    }

    public void unregisterConnectedListener(ConnectedListener listener) {
        connectedCallbacks.remove(listener);
    }

}
