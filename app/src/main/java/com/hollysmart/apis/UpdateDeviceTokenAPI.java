package com.hollysmart.apis;

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
 * Created by Lenovo on 2018/10/22.
 */

public class UpdateDeviceTokenAPI implements INetModel {

    private String deviceToken;
    private UpdateDeviceTokenIF updateDeviceTokenIF;

    public UpdateDeviceTokenAPI(String deviceToken, UpdateDeviceTokenIF updateDeviceTokenIF) {
        this.deviceToken = deviceToken;
        this.updateDeviceTokenIF = updateDeviceTokenIF;
    }

    @Override
    public void request() {
        String url = Values.SERVICE_URL + "api/User/UpdateDevice";
        String token = UserToken.getUserToken().getToken();
        OkHttpUtils.post().url(url).addHeader("Authorization", token)
                .addParams("deviceToken", deviceToken)
                .addParams("deviceType", "2")
                .build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                e.fillInStackTrace();
                updateDeviceTokenIF.updateDeviceTokenResult(false);
            }

            @Override
            public void onResponse(String response, int id) {
                Mlog.d("设备token = " + response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getInt("resultid") == 1) {

                        updateDeviceTokenIF.updateDeviceTokenResult(true);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public interface UpdateDeviceTokenIF {
        void updateDeviceTokenResult(boolean isOk);
    }

}
