package com.freshollie.example;

import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
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

    private int clickWheelAngle = 0;
    private float clickWheelAngleInterval = 360/10;

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

        if (direction == ShuttleXpressDevice.ACTION_RIGHT) {
            modifier = 1;
        } else {
            modifier = -1;
        }

        RotateAnimation anim = new RotateAnimation(clickWheelAngle, clickWheelAngle + modifier * clickWheelAngleInterval, getCenterX(), getCenterY());
        anim.setInterpolator(new LinearInterpolator());
        anim.setRepeatCount(0);
        anim.setDuration(0);
        clickWheel.startAnimation(anim);

        clickWheelAngle += modifier * clickWheelAngleInterval;
        clickWheelAngle = clickWheelAngle % 360;
    }
}
