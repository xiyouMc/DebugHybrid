package com.vivavideo.mobile.debugjs.server;

import com.dynamicload.framework.util.FrameworkUtil;
import com.vivavideo.mobile.debugjs.model.Response;
import com.vivavideo.mobile.debugjs.util.DebugUtil;
import com.vivavideo.mobile.h5api.api.H5Bundle;
import com.vivavideo.mobile.h5api.api.H5Context;
import com.vivavideo.mobile.h5api.api.H5Param;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.URLDecoder;

/**
 * Description:
 * Data：11/18/16-4:49 PM
 * Author: Mark
 */

public class DebugServer implements Runnable {
    private static final String TAG = "DebugServer";

    private final int mPort;

    private Context mContext;
    private boolean mIsRunning;
    private ServerSocket mServerSocket;
    private final AssetManager mAssets;

    public DebugServer(Context context, int port) {
        mAssets = context.getResources().getAssets();
        this.mContext = context;
        this.mPort = port;
    }

    public void start() {
        mIsRunning = true;
        new Thread(this).start();
    }

    public void stop() {
        try {
            mIsRunning = false;
            if (null != mServerSocket) {
                mServerSocket.close();
                mServerSocket = null;
            }
        } catch (IOException e) {
            Log.e(TAG, "Error closing the server socket.", e);
        }
    }

    @Override
    public void run() {
        try {
            mServerSocket = new ServerSocket(mPort);
            while (mIsRunning) {
                Socket socket = mServerSocket.accept();
                handle(socket);
                socket.close();
            }
        } catch (SocketException e) {
            // The server was stopped; ignore.
        } catch (IOException e) {
            Log.e(TAG, "Web server error.", e);
        }
    }

    private void handle(Socket socket) throws IOException {
        BufferedReader reader = null;
        PrintStream output = null;
        try {
            String route = null;

            // Read HTTP headers and parse out the route.
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String line;
            while (!TextUtils.isEmpty(line = reader.readLine())) {
                if (line.startsWith("GET /")) {
                    int start = line.indexOf('/') + 1;
                    int end = line.indexOf(' ', start);
                    route = line.substring(start, end);
                    break;
                }
            }

            // Output stream that we send the response to
            output = new PrintStream(socket.getOutputStream());

            if (route == null || route.isEmpty()) {
                route = "index.html";
            }
            byte[] bytes = null;
            if (route.startsWith("open")) {
                String openUrl = null;
                if (route.contains("?url=")) {
                    openUrl = route.substring(route.indexOf("=") + 1, route.length());
                }
                Response response;
                try {
                    openUrl = URLDecoder.decode(openUrl, "UTF-8");
                } catch (Exception e) {
                    e.printStackTrace();
                }

                H5Context h5Context = new H5Context(FrameworkUtil.getContext());
                H5Bundle h5Bundle = new H5Bundle();
                Bundle bundle = new Bundle();
                bundle.putString(H5Param.URL, openUrl);
                h5Bundle.setParams(bundle);
                //start Hybrid
                try {
                    DebugUtil.getH5Service().startPage(h5Context, h5Bundle);
                } catch (Throwable e) {
                    writeServerError(output, e.toString());
                    return;
                }
            } else if (route.startsWith("jsBridge")) {
                IntentFilter filter = new IntentFilter();
                filter.addAction("hybrid.action.console.log");
                FrameworkUtil.getContext().registerReceiver(new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        FrameworkUtil.getContext().unregisterReceiver(this);
                        String message = intent.getStringExtra("consoleLog");
                        Log.d(TAG, "message:" + message);
                    }
                }, filter);
                String jsApi = null;
                if (route.contains("?jsApi=")) {
                    jsApi = route.substring(route.indexOf("=") + 1,
                            route.contains("&") ? route.indexOf("&") : route.length());
                }
                JSONObject params = null;
                if (route.contains("params=")) {
                    try {
                        params = new JSONObject(URLDecoder.decode(route.substring(route.indexOf("params=") + 7, route.length()), "utf-8"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                Log.d(TAG, "jsApi:" + jsApi + " params:" + (params != null ? params.toString() : ""));
                rountJSBridge(jsApi, params);
            } else {
                bytes = loadContent(route);
            }
            if (null == bytes) {
                writeServerError(output, "");
                return;
            }

            // Send out the content.
            output.println("HTTP/1.0 200 OK");
            output.println("Content-Type: " + detectMimeType(route));
            output.println("Content-Length: " + bytes.length);
            output.println();
            output.write(bytes);
            output.flush();
        } finally {
            if (null != output) {
                output.close();
            }
            if (null != reader) {
                reader.close();
            }
        }
    }

    private void rountJSBridge(String event, JSONObject params) {
        DebugUtil.getH5Service().sendIntent(event, params);
    }

    private String detectMimeType(String fileName) {
        if (TextUtils.isEmpty(fileName)) {
            return null;
        } else if (fileName.endsWith(".html")) {
            return "text/html";
        } else if (fileName.endsWith(".js")) {
            return "application/javascript";
        } else if (fileName.endsWith(".css")) {
            return "text/css";
        } else {
            return "application/octet-stream";
        }
    }

    private void writeServerError(PrintStream output, String exception) {
        output.println("HTTP/1.0 500 Internal Server Error :" + exception);
        output.flush();
    }


    private byte[] loadContent(String fileName) throws IOException {
        InputStream input = null;
        try {
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            input = mAssets.open(fileName);
            byte[] buffer = new byte[1024];
            int size;
            while (-1 != (size = input.read(buffer))) {
                output.write(buffer, 0, size);
            }
            output.flush();
            return output.toByteArray();
        } catch (FileNotFoundException e) {
            return null;
        } finally {
            if (null != input) {
                input.close();
            }
        }
    }
}
