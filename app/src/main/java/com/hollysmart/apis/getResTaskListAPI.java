package com.hollysmart.apis;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.hollysmart.beans.ProjectBean;
import com.hollysmart.beans.UserInfoBean;
import com.hollysmart.utils.Mlog;
import com.hollysmart.utils.taskpool.INetModel;
import com.hollysmart.value.UserToken;
import com.hollysmart.value.Values;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.MediaType;

/**
 *  采集任务数据list
 * Created by Lenovo on 2019/4/17.
 */

public class getResTaskListAPI implements INetModel {



    private ResTaskListIF sheBeiListIF;
    private int pageNo;
    private String  token;
    private String fdTaskId;

    public getResTaskListAPI(String token,String fdTaskId,int pageNo, ResTaskListIF sheBeiListIF) {
        this.pageNo = pageNo;
        this.token = token;
        this.fdTaskId = fdTaskId;
        this.sheBeiListIF = sheBeiListIF;
    }

    @Override
    public void request() {
        JSONObject object = new JSONObject();
        try {
            object.put("fdTaskId", fdTaskId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String urlStr = Values.SERVICE_URL_FORM + "/admin/api/restask/get";
        OkHttpUtils.postString().url(urlStr)
                .content(object.toString()).addHeader("Authorization", token)
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                e.printStackTrace();
                sheBeiListIF.onResTaskListResult(false, null,0);
            }

            @Override
            public void onResponse(String response, int id) {
                Mlog.d("项目管理列表:" + response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    int status = jsonObject.getInt("status");


                    if ( status == 200){
                        Gson mGson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm").create();
                        JSONObject dataOBJ = new JSONObject(jsonObject.getString("data"));
                        ProjectBean projectBean = mGson.fromJson(dataOBJ.toString(),
                                new TypeToken<ProjectBean>() {}.getType());
                        List<ProjectBean> projectBeanList = new ArrayList<>();
                        projectBeanList.add(projectBean);
                        for(ProjectBean projectBean1:projectBeanList){
                            projectBean1.setUserinfoid(UserToken.getUserToken().getToken());
                        }

                        sheBeiListIF.onResTaskListResult(true, projectBeanList,projectBeanList.size());
                    }else {
                        sheBeiListIF.onResTaskListResult(false, null,0);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });

    }

    public interface ResTaskListIF{
        void onResTaskListResult(boolean isOk, List<ProjectBean> projectBeanList, int count);
    }

}
