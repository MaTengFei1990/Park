package com.hollysmart.apis;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
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

public class GetResModelVersionAPI implements INetModel {


    private String access_token;
    private String id;
    private GetResModelVersionIF resModelVersionIF;

    public GetResModelVersionAPI(String access_token, String id, GetResModelVersionIF resModelVersionIF) {
        this.access_token = access_token;
        this.id = id;
        this.resModelVersionIF = resModelVersionIF;
    }

    @Override
    public void request() {
        JsonObject job=new JsonObject();
        job.addProperty("id", id);

        String urlStr = Values.SERVICE_URL_FORM + "/admin/api/resmodel/getVersion";
        OkHttpUtils.postString().url(urlStr)
                .content(job.toString()).addHeader("Authorization", access_token)
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                e.printStackTrace();
                resModelVersionIF.onGetResModelVersionResult(false, 0);
            }

            @Override
            public void onResponse(String response, int id) {
                Mlog.d("项目管理列表:" + response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    int status = jsonObject.getInt("status");


                    if (status == 200) {
                        Gson mGson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm").create();
                        JSONObject dataOBJ = new JSONObject(jsonObject.getString("data"));

                        int version = dataOBJ.getInt("version");

                        resModelVersionIF.onGetResModelVersionResult(true, version);
                    } else {
                        resModelVersionIF.onGetResModelVersionResult(false, 0);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });

    }

    public interface GetResModelVersionIF{
        void onGetResModelVersionResult(boolean isOk, int version);
    }
}
