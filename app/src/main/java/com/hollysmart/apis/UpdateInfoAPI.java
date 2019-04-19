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

public class UpdateInfoAPI implements INetModel {

    private PicBean bean;
    private UpdateInfoIF updateInfoIF;

    public UpdateInfoAPI(PicBean bean, UpdateInfoIF updateInfoIF) {
        this.bean = bean;
        this.updateInfoIF = updateInfoIF;
    }

    @Override
    public void request() {
        String url = Values.SERVICE_URL + "api/user/save";
        String token = UserToken.getUserToken().getToken();
        OkHttpUtils.post().url(url).addHeader("Authorization", token)
                .addParams("avatar", Values.SERVICE_URL+bean.getUrlpath())
                .build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                e.fillInStackTrace();
                updateInfoIF.UpdateInfoResult(false);
            }

            @Override
            public void onResponse(String response, int id) {
                Mlog.d("图片上传 = " + response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getInt("state") == 1) {
                        String imageUrl = jsonObject.getString("path");
                        bean.setUrlpath(imageUrl);

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                updateInfoIF.UpdateInfoResult(true);
            }
        });
    }

    public interface UpdateInfoIF {
        void UpdateInfoResult(boolean isOk);
    }
}

