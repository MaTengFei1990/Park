package com.hollysmart.utils;

import com.hollysmart.beans.JDPicInfo;
import com.hollysmart.utils.taskpool.INetModel;
import com.hollysmart.value.Values;

/**
 * Created by sunpengfei on 2017/11/6.
 */

public class PicYasuo implements INetModel {

    private JDPicInfo info;
    private String filePath;
    private PicYansuoIF picYansuoIF;

    public PicYasuo(JDPicInfo info, PicYansuoIF picYansuoIF ) {
        this.info = info;
        this.picYansuoIF = picYansuoIF;
    }


    @Override
    public void request() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String str[] = info.getFilePath().split("/");
                String picName = str[str.length-1];

                CCM_Bitmap.getBitmapToFile(CCM_Bitmap.ratio(info.getFilePath(), 600f, 600f), Values.SDCARD_FILE(Values.SDCARD_PIC) + picName);
                filePath = Values.SDCARD_FILE(Values.SDCARD_PIC) + picName;
                info.setFilePath(filePath);

                picYansuoIF.yaSouResult(true, info);

            }
        }).start();

    }


    public interface PicYansuoIF{
        void yaSouResult(boolean isOk, JDPicInfo info);
    }


}
