package com.hollysmart.apis;

import com.hollysmart.beans.PicBean;
import com.hollysmart.utils.Mlog;
import com.hollysmart.utils.taskpool.INetModel;
import com.hollysmart.value.UserToken;
import com.hollysmart.value.Values;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Call;

/**
 * Created by sunpengfei on 2017/11/29.更新个人信息。
 */

public class UpDateImgInfoAPI implements INetModel {

    private String id;
    private PicBean bean;
    private UpDateImgInfoIF updateInfoIF;

    public UpDateImgInfoAPI(String id,PicBean bean, UpDateImgInfoIF updateInfoIF) {
        this.id = id;
        this.bean = bean;
        this.updateInfoIF = updateInfoIF;
    }

    @Override
    public void request() {
        String url = Values.SERVICE_URL + "api/regpark/save";
        String token = UserToken.getUserToken().getToken();
        OkHttpUtils.post().url(url).addHeader("Authorization", token)
                .addParams("iid", id)
                .addParams("cover", bean.getUrlpath())
                .build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                e.fillInStackTrace();
                updateInfoIF.UpDateImgInfoResult(false);
            }

            @Override
            public void onResponse(String response, int id) {
                Mlog.d("图片上传 = " + response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getInt("resultid") != 0) {
                        updateInfoIF.UpDateImgInfoResult(true);

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                updateInfoIF.UpDateImgInfoResult(true);
            }
        });
    }

    public interface UpDateImgInfoIF {
        void UpDateImgInfoResult(boolean isOk);
    }
}

