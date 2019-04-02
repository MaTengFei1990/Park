package com.hollysmart.imgdownLoad;

import android.graphics.Bitmap;

import java.io.File;

/**
 * Created by Lenovo on 2019/1/9.
 */

public interface ImageDownLoadCallBack {


    void onDownLoadSuccess(File file);
    void onDownLoadSuccess(Bitmap bitmap);

    void onDownLoadFailed();

}
