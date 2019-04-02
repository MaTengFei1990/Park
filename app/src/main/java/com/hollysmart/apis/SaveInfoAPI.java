package com.hollysmart.apis;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hollysmart.utils.Mlog;
import com.hollysmart.utils.Utils;
import com.hollysmart.utils.taskpool.INetModel;
import com.hollysmart.value.UserToken;
import com.hollysmart.value.Values;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.builder.PostFormBuilder;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;


import okhttp3.Call;

/**
 * 保存信息
 * <p>
 * Args: content,latitude,longitude,position,ispublic（是否公开）,state（1正常，-1草稿）,tagid（标签id）,imgs（图片以|分隔）
 * Return: resultid（1:成功）
 */


public class SaveInfoAPI implements INetModel {


    private String id;
    private String type_id;
    private String content;

    private String latitude;
    private String longitude;
    private String position;
    private String ispublic;
    private String state;
    private String tagid;
    private String imgs;
    private SaveInfoIF saveInfoIFIF;

    public SaveInfoAPI(String id,String type_id,String content, String latitude, String longitude, String position, String ispublic, String state, String tagid, String imgs, SaveInfoIF saveInfoIFIF) {
        this.id = id;
        this.type_id = type_id;
        this.content = content;
        this.latitude = latitude;
        this.longitude = longitude;
        this.position = position;
        this.ispublic = ispublic;
        this.state = state;
        this.tagid = tagid;
        this.imgs = imgs;
        this.saveInfoIFIF = saveInfoIFIF;
    }

    @Override
    public void request() {
        String url = Values.SERVICE_URL + "api/article/save";
        PostFormBuilder postFormBuilder = OkHttpUtils.post().url(url);
        String token = UserToken.getUserToken().getToken();
        postFormBuilder.addHeader("Authorization", token);
        postFormBuilder.addParams("type_id",type_id);

        if (!Utils.isEmpty(id)) {
            postFormBuilder.addParams("id", id);
        }
        if (!Utils.isEmpty(content)) {
            postFormBuilder.addParams("content", content);
        }
        if (!Utils.isEmpty(latitude)) {
            postFormBuilder.addParams("latitude", latitude);
        }
        if (!Utils.isEmpty(longitude)) {
            postFormBuilder.addParams("longitude", longitude);
        }
        if (!Utils.isEmpty(position)) {
            postFormBuilder.addParams("position", position);
        }
        if (!Utils.isEmpty(ispublic)) {
            postFormBuilder.addParams("ispublic", ispublic);
        }
        if (!Utils.isEmpty(state)) {
            postFormBuilder.addParams("state", state);
        }
        if (!Utils.isEmpty(tagid)) {
            postFormBuilder.addParams("tagid", tagid);
        }
        if (!Utils.isEmpty(imgs)) {
            postFormBuilder.addParams("imgs", imgs);
        }

        postFormBuilder.build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(String response, int id) {
                Mlog.d("保存信息 result = " + response);
                response = response.replace("\"\"", "null");
                try {
                    JSONObject object = new JSONObject(response);
                    int status = object.getInt("resultid");
                    if (status >0) {
                        saveInfoIFIF.saveResult(true, "msg");
                    } else {
                        saveInfoIFIF.saveResult(false, "msg");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public interface SaveInfoIF {

        void saveResult(boolean isOk, String msg);

    }
}
























