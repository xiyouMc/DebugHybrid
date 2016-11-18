package com.vivavideo.mobile.debugjs.util;

import com.dynamicload.framework.framework.VivaApplication;
import com.vivavideo.mobile.h5api.api.H5Service;

/**
 * Description:
 * Data：11/18/16-5:08 PM
 * Author: Mark
 */

public class DebugUtil {
    public static final String OPEN_URL_BROAD = "hybrid.action.openurl";
    private static H5Service h5Service;

    public static H5Service getH5Service() {
        if (h5Service == null) {
            h5Service = VivaApplication.getInstance().getMicroApplicationContext()
                    .findServiceByInterface(H5Service.class.getName());
        }
        return h5Service;
    }
}
