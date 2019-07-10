package com.hollysmart.formlib.apis;

import android.content.Context;

import com.hollysmart.beans.LuXianInfo;
import com.hollysmart.formlib.beans.ProjectBean;
import com.hollysmart.db.LuXianDao;
import com.hollysmart.utils.Mlog;
import com.hollysmart.utils.taskpool.INetModel;
import com.hollysmart.utils.taskpool.OnNetRequestListener;
import com.hollysmart.value.Values;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Call;
import okhttp3.MediaType;

/**
 *  插入路线save
 * Created by Lenovo on 2019/4/17.
 */

public class SaveResRouateAPI implements INetModel {


    private Context mContent;
    private String access_token;
    private LuXianInfo luXianInfo;
    private OnNetRequestListener onNetRequestListener;

    public SaveResRouateAPI(Context mContent, String access_token, LuXianInfo luXianInfo, OnNetRequestListener onNetRequestListener) {
        this.mContent = mContent;
        this.access_token = access_token;
        this.luXianInfo = luXianInfo;
        this.onNetRequestListener = onNetRequestListener;

    }

    @Override
    public void request() {

        JSONObject outJs = new JSONObject();

        try {
            outJs.put("fd_resmodelid", luXianInfo.getFd_resmodelid());
            outJs.put("fd_resdate",luXianInfo.getCreatetime()) ;
            outJs.put("id",luXianInfo.getId());
            outJs.put("fd_rescode", luXianInfo.getId());
            outJs.put("fd_restaskid",luXianInfo.getFd_restaskid()) ;
            outJs.put("fd_resname",luXianInfo.getName()) ;
            outJs.put("fd_restaskname", luXianInfo.getFd_restaskname());
            outJs.put("fd_resmodelname", luXianInfo.getFd_resmodelname());
            outJs.put("fd_resposition",luXianInfo.getStartCoordinate());

            JSONObject resjson = new JSONObject();

            resjson.put("fd_resmodelid", luXianInfo.getFd_resmodelid());
            resjson.put("startCoordinate", luXianInfo.getStartCoordinate());
            resjson.put("isUpload", "true");
            resjson.put("id", luXianInfo.getId());
            resjson.put("fd_restaskid", luXianInfo.getFd_restaskid());
            resjson.put("fd_resmodelname", luXianInfo.getFd_resmodelname());
            resjson.put("fd_restaskname", luXianInfo.getFd_restaskname());
            resjson.put("endCoordinate", luXianInfo.getEndCoordinate());
            resjson.put("name", luXianInfo.getName());
            resjson.put("lineCoordinates", luXianInfo.getLineCoordinates());

            outJs.put("fdResData", resjson);






        } catch (JSONException e) {
            e.printStackTrace();
        }


        String urlStr = Values.SERVICE_URL_FORM + "/admin/api/resdata/save";
        OkHttpUtils.postString().url(urlStr)
                .content(outJs.toString()).addHeader("Authorization", access_token)
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                e.printStackTrace();

                onNetRequestListener.OnResult(false,null,null);

            }

            @Override
            public void onResponse(String response, int id) {
                Mlog.d("插入单个资源采集数据save:" + response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    int status = jsonObject.getInt("status");


                    if ( status == 200){
                        luXianInfo.setIsUpload(true + "");
                        LuXianDao luXianDao = new LuXianDao(mContent);
                        luXianDao.addOrUpdate(luXianInfo);
                        onNetRequestListener.OnResult(true,null,luXianInfo);
                    }else {
                        onNetRequestListener.OnResult(false,null,null);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    onNetRequestListener.OnResult(false,null,null);
                }

            }
        });

    }

    public interface SaveResTaskIF{
        void onSaveResTaskResult(boolean isOk, ProjectBean projectBean);
    }

}
