package com.hollysmart.apis;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.hollysmart.beans.ProjectBean;
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
 *  采集任务数据save
 * Created by Lenovo on 2019/4/17.
 */

public class SaveResTaskAPI implements INetModel {



    private String access_token;
    private ProjectBean projectBean;
    private SaveResTaskIF saveResTaskIF;

    public SaveResTaskAPI(String access_token, ProjectBean projectBean, SaveResTaskIF saveResTaskIF) {
        this.access_token = access_token;
        this.projectBean = projectBean;
        this.saveResTaskIF = saveResTaskIF;
    }

    @Override
    public void request() {
        JSONObject object = new JSONObject();
        try {
//            object.put("remarks", projectBean.getRemarks());
            object.put("fTaskname", projectBean.getfTaskname());
            object.put("fTaskmodel", projectBean.getfTaskmodel());
            object.put("fBegindate", projectBean.getfBegindate());
            object.put("fEnddate", projectBean.getfEnddate());
            object.put("fdTasktype ", projectBean.getfTaskmodel());
            object.put("fState", projectBean.getfState());
            object.put("fRange", projectBean.getfRange());
            object.put("fdTaskId", projectBean.getId());
            object.put("fOfficeId", projectBean.getfOfficeId());
            object.put("fDescription", "");
        } catch (JSONException e) {
            e.printStackTrace();
        }



        String urlStr = Values.SERVICE_URL_FORM + "/admin/api/restask/save";
        OkHttpUtils.postString().url(urlStr)
                .content(object.toString()).addHeader("Authorization", access_token)
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                e.printStackTrace();
                saveResTaskIF.onSaveResTaskResult(false, null);
            }

            @Override
            public void onResponse(String response, int id) {
                Mlog.d("采集任务数据save:" + response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    int status = jsonObject.getInt("status");


                    if ( status == 200){
                        Gson mGson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm").create();
                        ProjectBean bean = mGson.fromJson(jsonObject.getString("data"),
                                new TypeToken<ProjectBean>() {}.getType());
                        projectBean = bean;
                        saveResTaskIF.onSaveResTaskResult(true, projectBean);
                    }else {
                        saveResTaskIF.onSaveResTaskResult(false, null);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });

    }

    public interface SaveResTaskIF{
        void onSaveResTaskResult(boolean isOk, ProjectBean projectBean);
    }

}
