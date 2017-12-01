package com.freshollie.shuttlexpress;

import android.util.Log;

import java.nio.ByteBuffer;
import java.util.ArrayList;

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

        public static ArrayList<Integer> ALL_KEYS = getAllKeys();

        private static ArrayList<Integer> getAllKeys() {
            ArrayList<Integer> allKeys = new ArrayList<>();

            for (int i = 0; i < NUM_KEYS; i++) {
                allKeys.add(BUTTON_0 + i);
            }

            return allKeys;
        }

        public static ArrayList<Integer> ALL_BUTTONS = getButtons();

        private static ArrayList<Integer> getButtons() {
            return new ArrayList<>(ALL_KEYS.subList(0, 5));
        }
    }

    public static final int STATUS_CONNECTED = 23947234;
    public static final int STATUS_DISCONNECTED = 3289489;

    public static boolean DEBUG_OUT = false;

    private ShuttleXpressConnection shuttleXpressConnection;

    private ArrayList<KeyListener> keyListeners = new ArrayList<>();

    private int ring;
    private int wheel;
    private Integer buttons[];

    public interface KeyListener {
        void onDown(int key);
        void onUp(int key);
    }



    ShuttleXpressDevice() {
        ring = 0;
        wheel = 0;
        buttons = new Integer[5];
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

    /**
     * Parses the new data store the new values
     * and makes the relevant callbacks for changed data
     */
    void parseNewData(ByteBuffer newData) {
        if (newData != null) {
            int newRing, newWheel;

            newRing = newData.get(0); // Byte 1 contains the value of the ring position
            newWheel = newData.get(1); // Byte 2 contains the value of the wheel position

            Integer newButtons[] = new Integer[5];

            int newButtonsBytes = ((int) newData.get(3)) + 128;
            /*
             * Byte 4 contains a binary value with each bit representing a button
             * Button 1: Bit 4
             * Button 2: Bit 5
             * Button 3: Bit 6
             * Button 4: Bit -7
             * We add 128 to negate the negative bit
             */

            newButtons[4] = ((Byte) newData.get(4)).intValue(); // Byte 4 contains the value of button 5

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

    private void notifyKeyDown(int key) {
        if (DEBUG_OUT) {
            Log.v(TAG, "Key Down:" + String.valueOf(key));
        }
        synchronized (keyListeners) {
            for (KeyListener keyListener : new ArrayList<>(keyListeners)) {
                keyListener.onDown(key);
            }
        }
    }

    private void notifyKeyUp(int key) {
        if (DEBUG_OUT) {
            Log.v(TAG, "Key Up:" + String.valueOf(key));
        }
        synchronized (keyListeners) {
            for (KeyListener keyListener : new ArrayList<>(keyListeners)) {
                keyListener.onUp(key);
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
    private void doCallbacks(Integer[] newButtons, int newRing, int newWheel) {

        // Checks if the ring position has changed
        if (newRing == 7 && ring != 7) {
            notifyKeyUp(KeyCodes.RING_MIDDLE);
            notifyKeyDown(KeyCodes.RING_RIGHT);

        } else if (newRing != 7 && ring == 7) {
            notifyKeyUp(KeyCodes.RING_RIGHT);
            notifyKeyDown(KeyCodes.RING_MIDDLE);

        } else if (newRing != -7 && ring == -7) {
            notifyKeyUp(KeyCodes.RING_LEFT);
            notifyKeyDown(KeyCodes.RING_MIDDLE);

        } else if (newRing == -7 && ring != -7) {
            notifyKeyUp(KeyCodes.RING_MIDDLE);
            notifyKeyDown(KeyCodes.RING_LEFT);
        }

        // Checks if wheel has moved
        if (newWheel != wheel) {
            if (newWheel < 0 && wheel > 0) {
                notifyKeyDown(KeyCodes.WHEEL_RIGHT);
                notifyKeyUp(KeyCodes.WHEEL_RIGHT);
            } else if (newWheel > 0 && wheel < 0) {
                notifyKeyDown(KeyCodes.WHEEL_LEFT);
                notifyKeyUp(KeyCodes.WHEEL_LEFT);
            } else if (newWheel > wheel) {
                notifyKeyDown(KeyCodes.WHEEL_RIGHT);
                notifyKeyUp(KeyCodes.WHEEL_RIGHT);
            } else {
                notifyKeyDown(KeyCodes.WHEEL_LEFT);
                notifyKeyUp(KeyCodes.WHEEL_LEFT);
            }
        }

        // Checks if buttons have changed
        for (int i = 0; i < 5; i++) {
            if (!newButtons[i].equals(buttons[i])) {
                if (newButtons[i] == 1) {
                    notifyKeyDown(KeyCodes.BUTTON_0 + i); // Because buttons are in chronological order
                } else {
                    notifyKeyUp(KeyCodes.BUTTON_0 + i);
                }
            }
        }
    }

    public void registerKeyListener(ShuttleXpressDevice.KeyListener listener) {
        keyListeners.add(listener);
    }

    public void unregisterKeyListener(ShuttleXpressDevice.KeyListener listener) {
        keyListeners.remove(listener);
    }
}
