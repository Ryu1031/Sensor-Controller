package com.example.myapplication.websocket;

import android.os.Build;

import org.java_websocket.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;

import utils.Log;

/**
 * Created by Ryusuke on 2016/08/11.
 */
public class WebSocket {
    public enum ReadyState {
        CONNECTING(0),
        OPEN(1),
        CLOSING(2),
        CLOSED(3);
        private final int value;

        ReadyState(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    private WebScoketCallbackListener mListener;
    private URI mUri;

    public WebSocket(String iPAddress) {
        if ("sdk".equals(Build.PRODUCT)) {
            // エミュレータの場合はIPv6を無効
            java.lang.System.setProperty("java.net.preferIPv6Addresses", "false");
            java.lang.System.setProperty("java.net.preferIPv4Stack", "true");
        }
        try {
            mUri = new URI("ws://" + iPAddress + ":80");
        } catch (URISyntaxException e) {
            Log.e(e);
        }
    }

    public void setOnCallbackListener(WebScoketCallbackListener listener) {
        mListener = listener;
    }

    public WebSocketClient setup() {
        Log.d("setup:" + mUri);
        WebSocketClient scokect = new WebSocketClient(mUri) {
            @Override
            public void onOpen(ServerHandshake serverHandshake) {
                Log.d("onOpen:" + serverHandshake.getHttpStatus());
                if (mListener != null) {
                    mListener.onOpen(serverHandshake.getHttpStatus());
                }
            }

            @Override
            public void onMessage(final String message) {
                Log.d("onMessage:" + message);
                if (mListener != null) {
                    mListener.onMessage(message);
                }
            }

            @Override
            public void onClose(int code, String reason, boolean remote) {
                Log.d("onClose code:" + code + " reason:" + reason + " remote:" + remote);
                if (mListener != null) {
                    mListener.onClose(code, reason, remote);
                }
            }

            @Override
            public void onError(Exception e) {
                Log.e("onError:" + e);
                if (mListener != null) {
                    mListener.onError(e);
                }
            }
        };
        return scokect;
    }

    public interface WebScoketCallbackListener {
        void onOpen(Short httpState);

        void onMessage(String message);

        void onClose(int code, String reason, boolean remote);

        void onError(Exception e);
    }
}
