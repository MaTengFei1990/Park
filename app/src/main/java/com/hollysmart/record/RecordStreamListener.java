package com.hollysmart.record;

/**
 * Created by Lenovo on 2019/3/26.
 */

public interface RecordStreamListener {


    void onRecording(byte[] audiodata, int i, int length);

    void finishRecord();
}
