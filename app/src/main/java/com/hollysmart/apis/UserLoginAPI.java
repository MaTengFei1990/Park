package com.hollysmart.apis;

import com.hollysmart.utils.Mlog;
import com.hollysmart.utils.taskpool.INetModel;
import com.hollysmart.value.Values;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Call;

/**
 * Created by cai on 2017/7/17. 登录接口
 */

public class UserLoginAPI implements INetModel {

    private String username;
    private String password;
    private LoginInfoIF loginInfoIF;

    public UserLoginAPI(String username, String password, LoginInfoIF loginInfoIF) {
        this.username = username;
        this.password =  password;
        Mlog.d("login password = " + password);
        this.loginInfoIF = loginInfoIF;
    }
    @Override
    public void request() {
        String url = Values.SERVICE_URL + "/authz/oauth/token";
         OkHttpUtils.post().url(url)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .addHeader("Authorization", "Basic OTgxNjhhMGRiMDEzM2I4ZWUwYzU6ZGMyZjk0Mzk4NTEwZTc0YTZkZmM2MjRlZDA4NTY0OTFiNzJkZWI5Ng==")
                .addParams("grant_type", "password")
                .addParams("scope", "read write")
                .addParams("username", username)
                .addParams("password", password)
                .build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                e.printStackTrace();
                loginInfoIF.loginResult(false, "网络错误，请检查网络", null, null);
            }

            @Override
            public void onResponse(String response, int id) {
                Mlog.d("登录result = " + response);
                response = response.replace("\"\"", "null");
                try {
                    JSONObject object = new JSONObject(response);
                    if (!object.isNull("status") && object.getInt("status") == 200) {
                        JSONObject data = object.getJSONObject("data");
                        loginInfoIF.loginResult(true, object.getString("msg"), data.getString("access_token"),
                                data.getString("token_type"));
                    } else {
                        loginInfoIF.loginResult(false, object.getString("msg"), null, null);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    public interface LoginInfoIF{
        void loginResult(boolean isOk, String msg, String access_token, String token_type);
    }

}
