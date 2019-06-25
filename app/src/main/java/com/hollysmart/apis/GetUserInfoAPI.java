package com.hollysmart.apis;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.hollysmart.db.UserInfo;
import com.hollysmart.utils.Mlog;
import com.hollysmart.utils.taskpool.INetModel;
import com.hollysmart.value.Values;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Call;

/**
 * Created by Lenovo on 2019/4/18.
 */

public class GetUserInfoAPI implements INetModel {


    private UserInfo userInfo;
    private UserInfoIF userInfoIF;

    public GetUserInfoAPI(UserInfo userInfo, UserInfoIF userInfoIF) {
        this.userInfo = userInfo;
        this.userInfoIF = userInfoIF;
    }

    @Override
    public void request() {
        String urlStr = Values.SERVICE_URL_FORM + "/admin/sys/user/info";
        OkHttpUtils.get().url(urlStr)
                .addHeader("Authorization", userInfo.getAccess_token())
                .build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                e.printStackTrace();
                userInfoIF.userResult(false, null);
            }

            @Override
            public void onResponse(String response, int id) {
                Mlog.d("用户result = " + response);
                response = response.replace("\"\"", "null");
                try {
                    JSONObject object = new JSONObject(response);
                    int status = object.getInt("status");
                    if (status == 200) {
                        Gson mGson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm").create();
                        UserInfo userInfo1 = mGson.fromJson(object.getString("data"),
                                new TypeToken<UserInfo>() {}.getType());
                        userInfo.setId(userInfo1.getId());
                        userInfo.setRemarks(userInfo1.getRemarks());
                        userInfo.setCreateDate(userInfo1.getCreateDate());
                        userInfo.setUpdateDate(userInfo1.getUpdateDate());
                        userInfo.setOffice(userInfo1.getOffice());
                        userInfo.setLoginName(userInfo1.getLoginName());
                        userInfo.setNo(userInfo1.getNo());
                        userInfo.setName(userInfo1.getName());
                        userInfo.setEmail(userInfo1.getEmail());
                        userInfo.setPhone(userInfo1.getPhone());
                        userInfo.setMobile(userInfo1.getMobile());
                        userInfo.setUserType(userInfo1.getUserType());

                        userInfo.setLoginIp(userInfo1.getLoginIp());
                        userInfo.setLoginDate(userInfo1.getLoginDate());
                        userInfo.setPhoto(userInfo1.getPhoto());

                        userInfo.setOldLoginName(userInfo1.getOldLoginName());
                        userInfo.setOldLoginIp(userInfo1.getOldLoginIp());

                        userInfo.setOldLoginDate(userInfo1.getOldLoginDate());
                        userInfo.setOauthId(userInfo1.getOauthId());
                        userInfo.setUserLevel(userInfo1.getUserLevel());

                        userInfo.setOrderUnitId(userInfo1.getOrderUnitId());
                        userInfo.setRoleIds(userInfo1.getRoleIds());
                        userInfo.setRoleNames(userInfo1.getRoleNames());


                        userInfoIF.userResult(true, userInfo);
                    } else {
                        userInfoIF.userResult(false, null);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public interface UserInfoIF {
        void userResult(boolean isOk, UserInfo userInfo);
    }
}
