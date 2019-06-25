package com.hollysmart.apis;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.hollysmart.beans.LuXianInfo;
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
 * 查询线路详情
 * Created by Lenovo on 2019/4/18.
 */

public class ResRouteDataGetAPI implements INetModel {


    private String access_token;

    private LuXianInfo luXianInfo;
    private LuXianDetailsIF luXianDetailsIF;

    public ResRouteDataGetAPI(String access_token, LuXianInfo luXianInfo, LuXianDetailsIF luXianDetailsIF) {
        this.luXianInfo = luXianInfo;
        this.access_token = access_token;
        this.luXianDetailsIF = luXianDetailsIF;
    }

    @Override
    public void request() {


        JSONObject object = new JSONObject();

        try {
            object.put("id", luXianInfo.getId());
            object.put("fd_restaskid", luXianInfo.getFd_restaskid());
            object.put("fd_resmodelid", luXianInfo.getFd_resmodelid());
            object.put("fd_resmodelname", luXianInfo.getFd_resmodelname());
        } catch (JSONException e) {
            e.printStackTrace();
        }


        Mlog.d("查询线路详情---object===" + object.toString());

        String urlStr = Values.SERVICE_URL + "/admin/api/resdata/get";
        OkHttpUtils.postString().url(urlStr)
                .content(object.toString()).addHeader("Authorization", access_token)
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                e.printStackTrace();
                luXianDetailsIF.onLuXianDetailsResult(false, null);
            }

            @Override
            public void onResponse(String response, int id) {
                Mlog.d("查询线路详情返回的数据:" + response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    int status = jsonObject.getInt("status");


                    if ( status == 200){

                        Gson mGson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm").create();
                        LuXianInfo redata = mGson.fromJson(jsonObject.getString("data"),
                                new TypeToken<LuXianInfo>() {}.getType());

                        luXianInfo=redata;



                        luXianDetailsIF.onLuXianDetailsResult(true, luXianInfo);
                    }else {
                        luXianDetailsIF.onLuXianDetailsResult(false, null);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });

    }

    public interface LuXianDetailsIF{
        void onLuXianDetailsResult(boolean isOk, LuXianInfo resDataBean);
    }
}
