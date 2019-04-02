package com.hollysmart.apis;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.hollysmart.beans.CaoGaoBean;
import com.hollysmart.beans.ZhuanFaInfoBean;
import com.hollysmart.utils.Mlog;
import com.hollysmart.utils.taskpool.INetModel;
import com.hollysmart.value.Values;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import okhttp3.Call;

/**
 * Created by Lenovo on 2018/8/7.
 */

public class GetZhuanFaInfoAPI implements INetModel {
    private getZhuanFaInfoIF getZhuanFaInfoIF;
    private String id;

    public GetZhuanFaInfoAPI(String id, getZhuanFaInfoIF getZhuanFaInfoIF) {
        this.id = id;
        this.getZhuanFaInfoIF = getZhuanFaInfoIF;
    }

    @Override
    public void request() {
        String url = Values.SERVICE_URL + "api/article/InfoRef";
        OkHttpUtils.get().url(url)
                .addParams("id", id)
                .build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                e.printStackTrace();
                getZhuanFaInfoIF.getZhuanFaInfoResult(false, null);
            }

            @Override
            public void onResponse(String response, int id) {
                Mlog.d("加载草稿   result = " + response);
                Gson mGson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm").create();
                ZhuanFaInfoBean result = mGson.fromJson(response, new TypeToken<ZhuanFaInfoBean>() {
                }.getType());
                getZhuanFaInfoIF.getZhuanFaInfoResult(true, result);


            }
        });
    }

    public interface getZhuanFaInfoIF {
        void getZhuanFaInfoResult(boolean isOK, ZhuanFaInfoBean bean);
    }
}
