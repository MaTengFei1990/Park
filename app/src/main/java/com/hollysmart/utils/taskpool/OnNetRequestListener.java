package com.hollysmart.utils.taskpool;

/**
 * Created by cai on 16/6/6.
 */
public interface OnNetRequestListener {
    void onFinish();
    void OnNext();
    void OnResult(boolean isOk, String msg, Object object);
}
