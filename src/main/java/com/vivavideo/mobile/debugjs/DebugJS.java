package com.vivavideo.mobile.debugjs;

import com.vivavideo.mobile.debugjs.server.DebugServer;
import com.vivavideo.mobile.debugjs.util.NetworkUtils;

import android.content.Context;
import android.util.Log;

/**
 * Description:
 * Data：11/18/16-5:31 PM
 * Author: Mark
 */

public class DebugJS {
    private static final String TAG = DebugJS.class.getSimpleName();
    private static final int DEFAULT_PORT = 9090;
    private static DebugServer mDebugServer;
    private static String addressLog = "not available";

    private DebugJS() {
        // This class in not publicly instantiable
    }

    public static void initialize(Context context) {
        mDebugServer = new DebugServer(context, DEFAULT_PORT);
        mDebugServer.start();
        addressLog = NetworkUtils.getAddressLog(context, DEFAULT_PORT);
        Log.d(TAG, addressLog);
    }
}
