package com.hollysmart.apis;

import com.hollysmart.utils.Mlog;
import com.hollysmart.utils.taskpool.INetModel;
import com.hollysmart.value.UserToken;
import com.hollysmart.value.Values;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Call;

/**
 * 删除信息
 *
 */


public class DeleteInfoAPI implements INetModel {


    private String delId;
    private DeleteInfoIF deleteInfoIF;

    public DeleteInfoAPI(String delId, DeleteInfoIF deleteInfoIF) {
        this.delId = delId;
        this.deleteInfoIF = deleteInfoIF;
    }

    @Override
    public void request() {
        String token = UserToken.getUserToken().getToken();
        String url = Values.SERVICE_URL + "api/article/delarticle";
        OkHttpUtils.get().url(url)
                .addHeader("Authorization", token)
                .addParams("id", delId)
                .build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(String response, int id) {
                Mlog.d("删除信息 result = " + response);
                response = response.replace("\"\"", "null");
                try {
                    JSONObject object = new JSONObject(response);
                    int status = object.getInt("resultid");
                    if (status >0) {
                        deleteInfoIF.deleteResult(true, "该草稿删除成功");
                    } else {
                        deleteInfoIF.deleteResult(false, "该草稿删除失败！");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public interface DeleteInfoIF {

        void deleteResult(boolean isOk, String msg);

    }
}
























