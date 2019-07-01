package com.hollysmart.apis;

import com.hollysmart.beans.JDPicInfo;
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

public class UpLoadFormPicAPI implements INetModel {

    private String picPath;
    private String access_token;
    private String picName;

    private JDPicInfo bean;
    private OnNetRequestListener onNetRequestListener;

    public UpLoadFormPicAPI(String access_token, JDPicInfo bean, OnNetRequestListener onNetRequestListener) {
        this.access_token = access_token;
        this.bean = bean;
        this.onNetRequestListener = onNetRequestListener;
    }

    @Override
    public void request() {
        picPath = bean.getFilePath();
        String[] str = picPath.split("/");
        picName = str[str.length - 1];

        String url = Values.SERVICE_URL_FORM + "/admin/api/busMedia/upload";
        Mlog.d("图片上传地址 = " + picPath);
        OkHttpUtils.post().url(url)
                .addHeader("Authorization", access_token)
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
                    Mlog.d("图片上传 = " + response);
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getInt("status") == 200) {
                        JSONObject data = jsonObject.getJSONObject("data");
                        String showUrl = data.getString("showUrl");
                        bean.setImageUrl(showUrl);
                        onNetRequestListener.OnNext();

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    onNetRequestListener.OnNext();
                }
            }
        });
//            }
//        }).request();

    }
}
