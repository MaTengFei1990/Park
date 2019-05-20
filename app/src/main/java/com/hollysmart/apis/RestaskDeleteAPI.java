package com.hollysmart.apis;

import com.hollysmart.beans.ProjectBean;
import com.hollysmart.utils.Mlog;
import com.hollysmart.utils.taskpool.INetModel;
import com.hollysmart.value.Values;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import okhttp3.Call;
import okhttp3.MediaType;

/**
 * Created by Lenovo on 2019/4/18.
 */

public class RestaskDeleteAPI implements INetModel {


    private String access_token;

    private List<ProjectBean> projectBeanList;
    private RestaskDeleteIF restaskDeleteIF;

    public RestaskDeleteAPI(String access_token, List<ProjectBean> projectBeanList, RestaskDeleteIF restaskDeleteIF) {
        this.projectBeanList = projectBeanList;
        this.access_token = access_token;
        this.restaskDeleteIF = restaskDeleteIF;
    }

    @Override
    public void request() {

        JSONArray arr = new JSONArray();

        for(int i=0;i<projectBeanList.size();i++) {

            ProjectBean projectBean = projectBeanList.get(i);
            JSONObject object = new JSONObject();
            try {
                object.put("id", projectBean.getId());
                arr.put(object);
            } catch (JSONException e) {
                e.printStackTrace();
            }


        }

        Mlog.d("项目删除---arr===" + arr.toString());

        String urlStr = Values.SERVICE_URL_FORM + "/admin/api/restask/delete";
        OkHttpUtils.postString().url(urlStr)
                .content(arr.toString()).addHeader("Authorization", access_token)
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                e.printStackTrace();
                restaskDeleteIF.onRestaskDeleteResult(false, null);
            }

            @Override
            public void onResponse(String response, int id) {
                Mlog.d("项目删除:" + response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    int status = jsonObject.getInt("status");


                    if ( status == 200){
                        String msg = jsonObject.getString("msg");
                        restaskDeleteIF.onRestaskDeleteResult(true, msg);
                    }else {
                        restaskDeleteIF.onRestaskDeleteResult(false, null);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });

    }

    public interface RestaskDeleteIF{
        void onRestaskDeleteResult(boolean isOk, String msg);
    }
}
