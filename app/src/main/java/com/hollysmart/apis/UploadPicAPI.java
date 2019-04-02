package com.hollysmart.apis;

import com.hollysmart.beans.PicBean;
import com.hollysmart.utils.Mlog;
import com.hollysmart.utils.taskpool.INetModel;
import com.hollysmart.utils.taskpool.OnNetRequestListener;
import com.hollysmart.value.Values;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

import okhttp3.Call;

/**
 * Created by sunpengfei on 2017/11/29.
 */

public class UploadPicAPI implements INetModel {
    private String picPath;
    private String picName;

    private PicBean bean;
    private OnNetRequestListener onNetRequestListener;

    public UploadPicAPI( PicBean bean, OnNetRequestListener onNetRequestListener) {
        this.bean = bean;
        picPath = bean.getPath();
        this.onNetRequestListener = onNetRequestListener;
        String[] str = picPath.split("/");
        picName = str[str.length - 1];
    }

    @Override
    public void request() {
        String url = Values.SERVICE_URL + "up/fileup.ashx?json=1";
        Mlog.d("图片上传地址 = " + picPath);
        OkHttpUtils.post().url(url)
                .addFile(picName, picName, new File(picPath))
                .build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                e.fillInStackTrace();
                onNetRequestListener.OnNext();
            }
            @Override
            public void onResponse(String response, int id) {
                Mlog.d("图片上传 = " + response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getInt("state") == 1) {
                        String imageUrl = jsonObject.getString("path");
                        bean.setUrlpath(imageUrl);
                        onNetRequestListener.OnResult(true, null, null);

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    onNetRequestListener.OnResult(true, null, null);
                }
            }
        });
    }
}

