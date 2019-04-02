package com.hollysmart.apis;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.hollysmart.beans.BiaoQianBean;
import com.hollysmart.utils.Mlog;
import com.hollysmart.utils.taskpool.INetModel;
import com.hollysmart.value.Values;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;


import java.util.List;

import okhttp3.Call;

/**
 * Created by cai on 2017/7/17. 加载标签列表
 */

public class LoadTagListAPI implements INetModel {

    private LoadTagListIF loadTagListResult;

    public LoadTagListAPI(LoadTagListIF loadTagListResult) {
        this.loadTagListResult = loadTagListResult;
    }

    @Override
    public void request() {
        String url = Values.SERVICE_URL + "api/tag?type=1&title";
        OkHttpUtils.get().url(url)
                .build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                e.printStackTrace();
                loadTagListResult.loadTagListResult(false, null);
            }

            @Override
            public void onResponse(String response, int id) {
                Mlog.d("加载标签列表   result = " + response);
                Gson mGson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm").create();
                List<BiaoQianBean> result = mGson.fromJson(response, new TypeToken<List<BiaoQianBean>>() {
                }.getType());
                if (result != null && result.size() > 0) {
                    loadTagListResult.loadTagListResult(true, result);
                } else {
                    loadTagListResult.loadTagListResult(false, null);

                }
            }
        });
    }

    public interface LoadTagListIF {
        void loadTagListResult(boolean isOK, List<BiaoQianBean> data);
    }
}
























