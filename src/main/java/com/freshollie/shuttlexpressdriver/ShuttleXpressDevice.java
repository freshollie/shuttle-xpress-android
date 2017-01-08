package com.freshollie.shuttlexpressdriver;

import android.util.Log;

import java.nio.ByteBuffer;
import java.util.ArrayList;

/**
 * Created by freshollie on 12/26/16.
 */

public class ShuttleXpressDevice {
    /**
     * Stores the current device data and performs callbacks
     * when data changes
     */
    public static String TAG = ShuttleXpressDevice.class.getSimpleName();
    public static final int PRODUCT_ID = 1002;
    public static final int VENDOR_ID = 2867;

    public static final class KeyCodes {
        public static final int BUTTON_0 = 129379817;
        public static final int BUTTON_1 = 129379818;
        public static final int BUTTON_2 = 129379819;
        public static final int BUTTON_3 = 129379820;
        public static final int BUTTON_4 = 129379821;

        public static final int RING_RIGHT = 129379822;
        public static final int RING_LEFT = 129379823;
        public static final int RING_MIDDLE = 129379823;

        public static final int WHEEL_LEFT = 129379824;
        public static final int WHEEL_RIGHT = 129379825;

        public static final int ACTION_DOWN = 22394082;
        public static final int ACTION_UP = 30923849;
    }

    public static final int STATUS_CONNECTED = 23947234;
    public static final int STATUS_DISCONNECTED = 3289489;

    public static boolean DEBUG_OUT = false;

    private static ShuttleXpressDevice INSTANCE = new ShuttleXpressDevice();

    private ArrayList<ClickWheelListener> clickWheelCallbacks = new ArrayList<>();
    private ArrayList<RingListener> ringCallbacks = new ArrayList<>();
    private ArrayList<ButtonListener> buttonCallbacks = new ArrayList<>();
    private ArrayList<ConnectedListener> connectedCallbacks = new ArrayList<>();

    private ByteBuffer state;

    private int ring;
    private int wheel;
    private Integer buttons[];

    private boolean connected = false;

    public static ShuttleXpressDevice getInstance() {
        return INSTANCE;
    }

    public interface ClickWheelListener {
        void onRight();
        void onLeft();
    }

    public interface ButtonListener {
        void onDown(int id);
        void onUp(int id);
    }

    public interface RingListener {
        void onRight();
        void onLeft();
        void onMiddle();
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

    public void resetDevice(int maxPacketSize) {
        state = ByteBuffer.allocate(maxPacketSize);
        ring = 0;
        wheel = 0;
        buttons = new Integer[5];
        connected = false;
    }

    /**
     * Parses the new data store the new values
     * and makes the relevant callbacks for changed data
     */
    public void newData() {
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

    public void onConnected() {
        Log.v(TAG, "Connected");
        for (ConnectedListener listener: connectedCallbacks) {
            listener.onConnected();
        }
    }

    public void onDisconnected() {
        Log.v(TAG, "Disconnected");
        for (ConnectedListener listener: connectedCallbacks) {
            listener.onDisconnected();
        }
    }

    public void onWheel(int direction) {
        Log.v(TAG, "Wheel rotated " + (direction == ACTION_RIGHT ? "Right" : "Left"));

        for (ClickWheelListener listener: clickWheelCallbacks) {
            if (direction == ACTION_LEFT) {
                listener.onLeft();
            } else {
                listener.onRight();
            }
        }
    }

    public void onRing(int position) {
        Log.v(TAG, "Ring rotated " +
                (position == POSITION_LEFT ? "Right":
                        position == POSITION_MIDDLE ? "Middle":
                                "Left"));

        for (RingListener listener: ringCallbacks) {
            if (position == POSITION_LEFT) {
                listener.onLeft();
            } else if (position == POSITION_RIGHT) {
                listener.onRight();
            } else {
                listener.onMiddle();
            }
        }
    }

    public void onButton(int buttonId, int action) {
        Log.v(TAG, "Button " + String.valueOf(buttonId) + " " + (action == ACTION_DOWN ? "Down" : "Up"));

        for (ButtonListener listener: buttonCallbacks) {
            if (action == ACTION_DOWN) {
                listener.onDown(buttonId);
            } else {
                listener.onUp(buttonId);
            }
        }
    }

    /**
     * Checks what has changed from the last data to the new data given
     * and runs the correct callbacks for the new data changes
     * @param newButtons
     * @param newRing
     * @param newWheel
     */
    public void doCallbacks(Integer[] newButtons, int newRing, int newWheel) {
        if (newRing == 7 && ring != 7) {
            onRing(POSITION_LEFT);
        } else if ((newRing != 7 && ring == 7) || (newRing != -7 && ring == -7)) {
            onRing(POSITION_MIDDLE);
        } else if (newRing == -7 && ring != -7) {
            onRing(POSITION_RIGHT);
        }

        if (newWheel != wheel) {
            if (newWheel < 0 && wheel > 0) {
                onWheel(ACTION_RIGHT);
            } else if (newWheel > 0 && wheel < 0) {
                onWheel(ACTION_LEFT);
            } else if (newWheel > wheel) {
                onWheel(ACTION_RIGHT);
            } else {
                onWheel(ACTION_LEFT);
            }
        }

        for (int i = 0; i < 5; i++) {
            if (!newButtons[i].equals(buttons[i])) {
                if (newButtons[i] == 1) {
                    onButton(i, ACTION_DOWN);
                } else {
                    onButton(i, ACTION_UP);
                }
            }
        }
    }

    public void registerClickWheelListener(ClickWheelListener listener) {
        clickWheelCallbacks.add(listener);
    }

    public void unregisterClickWheelListener(ClickWheelListener listener) {
        clickWheelCallbacks.remove(listener);
    }

    public void registerRingListener(RingListener listener) {
        ringCallbacks.add(listener);
    }

    public void unregisterRingListener(RingListener listener) {
        ringCallbacks.remove(listener);
    }

    public void registerButtonListener(ButtonListener listener) {
        buttonCallbacks.add(listener);
    }

    public void unregisterButtonListener(ButtonListener listener) {
        buttonCallbacks.remove(listener);
    }

    public void registerConnectedListener(ConnectedListener listener) {
        connectedCallbacks.add(listener);
    }

    public void unregisterConnectedListener(ConnectedListener listener) {
        connectedCallbacks.remove(listener);
    }

}
