package com.hollysmart.apis;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.hollysmart.beans.ResModelBean;
import com.hollysmart.utils.Mlog;
import com.hollysmart.utils.taskpool.INetModel;
import com.hollysmart.value.Values;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Call;
import okhttp3.MediaType;

/**
 * Created by Lenovo on 2019/4/18.
 */

public class GetResModelAPI implements INetModel {


    private String access_token;
    private String id;
    private GetResModelIF getResModelIF;

    public GetResModelAPI(String access_token, String id, GetResModelIF getResModelIF) {
        this.access_token = access_token;
        this.id = id;
        this.getResModelIF = getResModelIF;
    }

    @Override
    public void request() {
        JsonObject job=new JsonObject();
        job.addProperty("id", id);
        String urlStr = Values.SERVICE_URL_FORM + "/admin/api/resmodel/get";
        OkHttpUtils.postString().url(urlStr)
                .content(job.toString()).addHeader("Authorization", access_token)
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                e.printStackTrace();
                getResModelIF.ongetResModelIFResult(false, null);
            }

            @Override
            public void onResponse(String response, int id) {
                Mlog.d("分类表单get:" + response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    int status = jsonObject.getInt("status");


                    if ( status == 200){
                        Gson mGson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm").create();
                        JSONObject dataOBJ = new JSONObject(jsonObject.getString("data"));
                       ResModelBean sheBeiBean = mGson.fromJson(dataOBJ.toString(),
                                new TypeToken<ResModelBean>() {}.getType());
                        getResModelIF.ongetResModelIFResult(true, sheBeiBean);
                    }else {
                        getResModelIF.ongetResModelIFResult(false, null);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });

    }

    public interface GetResModelIF{
        void ongetResModelIFResult(boolean isOk, ResModelBean projectBeanList);
    }
}
