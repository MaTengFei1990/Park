package com.hollysmart.apis;

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
 * 删除单个路线
 * Created by Lenovo on 2019/4/18.
 */

public class ResRouteDeleteAPI implements INetModel {


    private String access_token;

    private LuXianInfo resDataBean;
    private ResDataDeleteIF resDataDeleteIF;

    public ResRouteDeleteAPI(String access_token, LuXianInfo resDataBean, ResDataDeleteIF resDataDeleteIF) {
        this.resDataBean = resDataBean;
        this.access_token = access_token;
        this.resDataDeleteIF = resDataDeleteIF;
    }

    @Override
    public void request() {


        JSONObject object = new JSONObject();

        try {
            object.put("id", resDataBean.getId());
            object.put("fd_restaskid", resDataBean.getFd_restaskid());
            object.put("fd_resmodelid", resDataBean.getFd_resmodelid());
        } catch (JSONException e) {
            e.printStackTrace();
        }


        Mlog.d("删除单个资源---object===" + object.toString());

        String urlStr = Values.SERVICE_URL + "/admin/api/resdata/delete";
        OkHttpUtils.postString().url(urlStr)
                .content(object.toString()).addHeader("Authorization", access_token)
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                e.printStackTrace();
                resDataDeleteIF.onResDataDeleteResult(false, null);
            }

            @Override
            public void onResponse(String response, int id) {
                Mlog.d("项目删除:" + response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    int status = jsonObject.getInt("status");


                    if ( status == 200){
                        String msg = jsonObject.getString("msg");
                        resDataDeleteIF.onResDataDeleteResult(true, msg);
                    }else {
                        resDataDeleteIF.onResDataDeleteResult(false, null);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });

    }

    public interface ResDataDeleteIF{
        void onResDataDeleteResult(boolean isOk, String msg);
    }
}
