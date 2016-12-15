package com.freshollie.shuttleexpressdriver;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import net.pocketmagic.keyinjector.NativeInput;

public class DeviceService extends Service {
    NativeInput m_ni = new NativeInput();
    public DeviceService() {
        m_ni.SendKey(46, true);
        m_ni.SendKey(46, false);
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
