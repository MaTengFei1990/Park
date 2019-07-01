package com.hollysmart.utils;

import android.content.Context;

import com.hollysmart.beans.JDPicInfo;
import com.hollysmart.utils.taskpool.INetModel;
import com.hollysmart.utils.taskpool.OnNetRequestListener;
import com.hollysmart.value.Values;

import java.io.File;

import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;

/**
 * Created by sunpengfei on 2017/11/6.
 */

public class PicYasuo implements INetModel {

    private JDPicInfo info;
    private String filePath;
    private Context context;
    private OnNetRequestListener onNetRequestListener;

    public PicYasuo(JDPicInfo info, Context context, OnNetRequestListener onNetRequestListener ) {
        this.info = info;
        this.context = context;
        this.onNetRequestListener = onNetRequestListener;
    }


    @Override
    public void request() {
        String str[] = info.getFilePath().split("/");
        String picName = str[str.length-1];


        Luban.with(context)
                .load(info.getFilePath())
                .ignoreBy(100)
                .setTargetDir(Values.SDCARD_FILE(Values.SDCARD_PIC))
                .setCompressListener(new OnCompressListener() {
                    @Override
                    public void onStart() {
                        // TODO 压缩开始前调用，可以在方法内启动 loading UI
                        Utils.showToast(context,"start zip");
                    }

                    @Override
                    public void onSuccess(File file) {
                        // TODO 压缩成功后调用，返回压缩后的图片文件
                        info.setFilePath(file.getPath());
                        onNetRequestListener.OnNext();
                    }

                    @Override
                    public void onError(Throwable e) {
                        // TODO 当压缩过程出现问题时调用
                        Utils.showToast(context,"eeeee==="+e.getMessage());
                        onNetRequestListener.OnNext();
                    }
                }).launch();

    }


}
