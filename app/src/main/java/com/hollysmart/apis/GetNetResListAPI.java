package com.hollysmart.apis;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.hollysmart.beans.ProjectBean;
import com.hollysmart.beans.ResDataBean;
import com.hollysmart.db.UserInfo;
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
 * Created by Lenovo on 2019/4/18.
 */

public class GetNetResListAPI implements INetModel {


    private UserInfo userInfo;
    private ProjectBean projectBean;
    private DatadicListIF datadicListIF;

    public GetNetResListAPI(UserInfo userInfo, ProjectBean projectBean, DatadicListIF datadicListIF) {
        this.userInfo = userInfo;
        this.projectBean = projectBean;
        this.datadicListIF = datadicListIF;
    }

    @Override
    public void request() {
        JSONObject object = new JSONObject();
        try {
            object.put("pageNo", "1");
            object.put("pageSize", "10000");
            object.put("fd_restaskid",projectBean.getId()) ;
            object.put("fd_resmodelid", projectBean.getfTaskmodel());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String urlStr = Values.SERVICE_URL_FORM + "/admin/api/resdata/datadicList";
        OkHttpUtils.postString().url(urlStr)
                .content(object.toString()).addHeader("Authorization", userInfo.getAccess_token())
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                e.printStackTrace();
                datadicListIF.datadicListResult(false, null);
            }

            @Override
            public void onResponse(String response, int id) {
                Mlog.d("datadicList......... = " + response);
                response = response.replace("\"\"", "null");
                try {
                    JSONObject object = new JSONObject(response);
                    int status = object.getInt("status");
                    if (status == 200) {

                        JSONObject jsData = object.getJSONObject("data");
//                        Gson mGson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm").create();
//                        List<ResDataBean> menuBeanList = mGson.fromJson(jsData.getString("list"),
//                                new TypeToken<List<ResDataBean>>() {}.getType());


                        JSONArray list = jsData.getJSONArray("list");
                        List<ResDataBean> menuBeanList = new ArrayList<>();

                        for (int i = 0; i < list.length(); i++) {
                            JSONObject objDataBen = (JSONObject) list.get(i);
                            ResDataBean resDataBean = new ResDataBean();
                            resDataBean.setId(objDataBen.getString("fd_resid"));
                            resDataBean.setFd_resmodelid(objDataBen.getString("fd_resmodelid"));
                            resDataBean.setFd_resmodelname(objDataBen.getString("fd_resmodelname"));
                            resDataBean.setFdTaskId(objDataBen.getString("fd_restaskid"));
                            resDataBean.setFd_restaskname(objDataBen.getString("fd_restaskname"));
                            resDataBean.setFd_resdate(objDataBen.getString("fd_resdate"));
                            resDataBean.setRescode(objDataBen.getString("fd_rescode"));
                            resDataBean.setFd_resposition(objDataBen.getString("fd_resposition"));
                            resDataBean.setFd_resname(objDataBen.getString("fd_resname"));

                            menuBeanList.add(resDataBean);
                        }

                        datadicListIF.datadicListResult(true, menuBeanList);
                    } else {
                        datadicListIF.datadicListResult(false, null);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    datadicListIF.datadicListResult(false, null);
                }
            }
        });
    }

    public interface DatadicListIF {
        void datadicListResult(boolean isOk, List<ResDataBean> menuBeanList);
    }
}
