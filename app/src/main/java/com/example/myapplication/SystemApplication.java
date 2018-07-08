package com.example.myapplication;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import org.java_websocket.WebSocketClient;

import java.util.List;
import java.util.Observer;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by Ryusuke on 2016/08/12.
 */
public class SystemApplication extends Application {

    public WebSocketClient mWebSocketClient;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    public void setWebSocketClient(WebSocketClient webSocketClient) {
        mWebSocketClient = webSocketClient;
    }

    public WebSocketClient getWebSocketClient() {
        return mWebSocketClient;
    }
}
