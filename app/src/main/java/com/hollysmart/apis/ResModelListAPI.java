package com.hollysmart.apis;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.hollysmart.beans.ResModelBean;
import com.hollysmart.utils.Mlog;
import com.hollysmart.utils.taskpool.INetModel;
import com.hollysmart.value.Values;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import okhttp3.Call;
import okhttp3.MediaType;

/**
 * Created by Lenovo on 2019/4/17.
 */

public class ResModelListAPI implements INetModel {


    private String access_token;
    private ResModelListIF resModelListIF;

    public ResModelListAPI(String access_token, ResModelListIF resModelListIF) {
        this.access_token = access_token;
        this.resModelListIF = resModelListIF;
    }

    @Override
    public void request() {
        Gson gson = new Gson();
        String params = "{  }";
        String urlStr = Values.SERVICE_URL_FORM + "/admin/api/resmodel/list";
        OkHttpUtils.postString().url(urlStr)
                .content(params).addHeader("Authorization", access_token)
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                e.printStackTrace();
                resModelListIF.onResModelListResult(false, null);
            }

            @Override
            public void onResponse(String response, int id) {
                Mlog.d("DY分类表单列表:" + response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    int status = jsonObject.getInt("status");


                    if ( status == 200){
                        Gson mGson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm").create();
                        JSONObject dataOBJ = new JSONObject(jsonObject.getString("data"));
                        List<ResModelBean> resModelList = mGson.fromJson(dataOBJ.getString("list"),
                                new TypeToken<List<ResModelBean>>() {}.getType());
                        resModelListIF.onResModelListResult(true, resModelList);
                    }else {
                        resModelListIF.onResModelListResult(false, null);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });

    }

    public interface ResModelListIF{
        void onResModelListResult(boolean isOk, List<ResModelBean> resModelBeanList);
    }
}
