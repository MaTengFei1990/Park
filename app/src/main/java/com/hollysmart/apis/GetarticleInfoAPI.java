package com.hollysmart.apis;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.hollysmart.beans.CaoGaoBean;
import com.hollysmart.utils.Mlog;
import com.hollysmart.utils.taskpool.INetModel;
import com.hollysmart.value.Values;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import okhttp3.Call;

/**
 * Created by cai on 2017/7/17.加载信息详情
 */

public class GetarticleInfoAPI implements INetModel {

    private GetarticleInfoIF getarticleInfoIF;
    private String id;

    public GetarticleInfoAPI(String id, GetarticleInfoIF getarticleInfoIF) {
        this.id = id;
        this.getarticleInfoIF = getarticleInfoIF;
    }

    @Override
    public void request() {
        String url = Values.SERVICE_URL + "api/article/info";
        OkHttpUtils.get().url(url)
                .addParams("id", id)
                .build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                e.printStackTrace();
                getarticleInfoIF.getarticleInfoResult(false, null);
            }

            @Override
            public void onResponse(String response, int id) {
                Mlog.d("加载草稿   result = " + response);
                Gson mGson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm").create();
                CaoGaoBean result = mGson.fromJson(response, new TypeToken<CaoGaoBean>() {
                }.getType());
                getarticleInfoIF.getarticleInfoResult(true, result);


            }
        });
    }

    public interface GetarticleInfoIF {
        void getarticleInfoResult(boolean isOK, CaoGaoBean bean);
    }
}
























