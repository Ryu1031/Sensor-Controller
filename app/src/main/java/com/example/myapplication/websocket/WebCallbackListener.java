package com.example.myapplication.websocket;

/**
 * Created by Ryusuke on 2016/08/12.
 */
public interface WebCallbackListener {
    void onOpen(Short httpState);

    void onMessage(Short httpState);

    void onClose(Short httpState);

    void onError(Short httpState);
}
