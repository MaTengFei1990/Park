package com.hollysmart.apis;

import com.hollysmart.beans.SoundInfo;
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
 * Created by sunpengfei on 2017/9/18. 修改用户信息
 */

public class UpLoadSoundAPI implements INetModel {

    private String picPath;
    private String access_token;
    private String picName;

    private SoundInfo bean;
    private OnNetRequestListener onNetRequestListener;

    public UpLoadSoundAPI(String access_token, SoundInfo bean, OnNetRequestListener onNetRequestListener) {
        this.access_token = access_token;
        this.bean = bean;
        picPath = bean.getFilePath();
        this.onNetRequestListener = onNetRequestListener;
        String[] str = picPath.split("/");
        picName = str[str.length - 1];
    }

    @Override
    public void request() {
        String url = Values.SERVICE_URL_FORM + "/admin/api/busMedia/upload";
        Mlog.d("音频上传地址 = " + picPath);
        OkHttpUtils.post().url(url)
                .addHeader("Authorization",access_token)
                .addFile("files", picName, new File(picPath))
                .build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                e.fillInStackTrace();
                onNetRequestListener.OnNext();
            }
            @Override
            public void onResponse(String response, int id) {

                try {
                    Mlog.d("音频上传 = " + response);
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getInt("status") == 200) {
                        JSONObject data = jsonObject.getJSONObject("data");
                        String showUrl = data.getString("showUrl");
                        bean.setAudioUrl(showUrl);
                        onNetRequestListener.OnNext();

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    onNetRequestListener.OnNext();
                }
            }
        });
    }
}
