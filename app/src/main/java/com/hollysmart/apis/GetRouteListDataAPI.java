package com.hollysmart.apis;

import com.google.gson.JsonObject;
import com.hollysmart.beans.LuXianInfo;
import com.hollysmart.beans.ResDataBean;
import com.hollysmart.utils.Mlog;
import com.hollysmart.utils.taskpool.INetModel;
import com.hollysmart.value.Values;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.MediaType;

/**
 * Created by Lenovo on 2019/4/24.
 */

public class GetRouteListDataAPI implements INetModel {

    private String access_token;
    private ResDataBean resDataBean;
    private GetRouteListDataAPIF getRouteListDataAPIF;

    public GetRouteListDataAPI(String access_token, ResDataBean resDataBean, GetRouteListDataAPIF getRouteListDataAPIF) {
        this.access_token = access_token;
        this.resDataBean = resDataBean;
        this.getRouteListDataAPIF = getRouteListDataAPIF;
    }

    @Override
    public void request() {
        String urlStr = Values.SERVICE_URL + "/admin/api/resdata/datadicList";

        JsonObject job=new JsonObject();

        job.addProperty("fd_restaskid", resDataBean.getFdTaskId());
        job.addProperty("fd_resmodelid", resDataBean.getFd_resmodelid());

        OkHttpUtils.postString().url(urlStr)
                .content(job.toString()).addHeader("Authorization", access_token)
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                e.printStackTrace();
                getRouteListDataAPIF.getRouteListResult(false, null);
            }

            @Override
            public void onResponse(String response, int id) {
                Mlog.d("获取线路列表:" + response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    int status = jsonObject.getInt("status");
                    if ( status == 200){
                        JSONObject jsonData = new JSONObject(jsonObject.getString("data"));

                        List<LuXianInfo> dictList = new ArrayList<>();
                        JSONArray jsonArray = new JSONArray(jsonData.getString("list"));


                        for(int i=0;i<jsonArray.length();i++) {
                            JSONObject obj = (JSONObject) jsonArray.get(i);

                            LuXianInfo info = new LuXianInfo();

                            info.setId(obj.getString("fd_resid"));
                            info.setName(obj.getString("fd_resname"));

                            info.setIsUpload("true");

                            info.setFd_resmodelid(obj.getString("fd_resmodelid"));
                            info.setFd_resmodelname(obj.getString("fd_resmodelname"));

                            info.setFd_restaskid(obj.getString("fd_restaskid"));
                            info.setFd_restaskname(obj.getString("fd_restaskname"));

                            dictList.add(info);

                        }


                        getRouteListDataAPIF.getRouteListResult(true, dictList);
                    }else {
                        getRouteListDataAPIF.getRouteListResult(false, null);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    getRouteListDataAPIF.getRouteListResult(false, null);
                }

            }
        });

    }


    public interface GetRouteListDataAPIF{
        void  getRouteListResult(boolean isOK, List<LuXianInfo> luXianInfoList);
    }

}
