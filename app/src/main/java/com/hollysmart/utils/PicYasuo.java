package com.hollysmart.utils;

import com.hollysmart.beans.JDPicInfo;
import com.hollysmart.utils.taskpool.INetModel;
import com.hollysmart.utils.taskpool.OnNetRequestListener;
import com.hollysmart.value.Values;

/**
 * Created by sunpengfei on 2017/11/6.
 */

public class PicYasuo implements INetModel {

    private JDPicInfo info;
    private String filePath;
    private OnNetRequestListener onNetRequestListener;

    public PicYasuo(JDPicInfo info, OnNetRequestListener onNetRequestListener ) {
        this.info = info;
        this.onNetRequestListener = onNetRequestListener;
    }


    @Override
    public void request() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String str[] = info.getFilePath().split("/");
                String picName = str[str.length-1];

                CCM_Bitmap.getBitmapToFile(CCM_Bitmap.ratio(info.getFilePath(), 300f, 300f), Values.SDCARD_FILE(Values.SDCARD_PIC) + picName);
                filePath = Values.SDCARD_FILE(Values.SDCARD_PIC) + picName;
                info.setFilePath(filePath);

                onNetRequestListener.OnNext();

            }
        }).start();

    }


}
