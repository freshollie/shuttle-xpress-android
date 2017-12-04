package com.freshollie.example.shuttlexpress.test;

import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import com.freshollie.shuttlexpress.ShuttleXpressDevice;

/**
 * Created by freshollie on 12/29/16.
 */

public class DeviceRepresentation {
    public static String TAG = DeviceRepresentation.class.getSimpleName();

    private View ring;
    private View base;
    private View clickWheel;
    private FrameLayout holder;

    private static float CLICK_WHEEL_STEP = 360/10;

    private int clickWheelAngle = 0;


    public DeviceRepresentation() {

    };

    public float getCenterX() {
        return holder.getX() + holder.getWidth() / 2;
    }

    public float getCenterY() {
        return holder.getY() + holder.getHeight() / 2;
    }

    public void loadFromView(View view) {
        ring = view.findViewById(R.id.shuttle_xpress_ring);
        clickWheel = view.findViewById(R.id.shuttle_xpress_clickwheel);
        holder = (FrameLayout) view;
    }

    public void rotateClickWheel(int direction) {
        Log.v(TAG, "");
        int modifier;

        if (direction == ShuttleXpressDevice.KeyCodes.WHEEL_RIGHT) {
            modifier = 1;
        } else {
            modifier = -1;
        }

        clickWheelAngle += (modifier * CLICK_WHEEL_STEP);
        clickWheelAngle %= 360;

        clickWheel.setRotation(clickWheelAngle);
    }
}
