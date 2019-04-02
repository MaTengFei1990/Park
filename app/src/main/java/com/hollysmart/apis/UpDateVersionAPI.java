package com.hollysmart.apis;

import com.hollysmart.utils.Mlog;
import com.hollysmart.utils.Utils;
import com.hollysmart.utils.taskpool.INetModel;
import com.hollysmart.value.Values;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONObject;

import okhttp3.Call;

/**
 * Created by sunpengfei on 2017/11/27.
 */

public class UpDateVersionAPI implements INetModel {
    private int version;
    private UpdateVersionIF updateVersionIF;

    public UpDateVersionAPI(int version, UpdateVersionIF updateVersionIF) {
        this.version = version;
        this.updateVersionIF = updateVersionIF;
    }

    @Override
    public void request() {
//        String url = Values.SERVICE_URL + "hbj/api/app/version";
        String url = Values.SERVICE_URL + "api/app/checkupdate";
        OkHttpUtils.get()
                .url(url)
                .addParams("version", version + "")
                .build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                e.fillInStackTrace();
            }

            @Override
            public void onResponse(String response, int id) {
                Mlog.d("更新response = " + response);
                try {
                    JSONObject object = new JSONObject(response);
                    int needupdate = object.getInt("needupdate");
                    if (needupdate == 1) {
                        String downLoadURL = object.getString("url");
                        String remark;
                        if (object.has("description")) {
                            remark = object.getString("description");
                            remark = remark.replace("\\n", "\n");
                            if (Utils.isEmpty(remark)) {
                                remark = "发现新版本，是否进行更新";
                            }
                        } else {
                            remark = "发现新版本，是否进行更新";
                        }
                        updateVersionIF.getUpdateVersion(true,downLoadURL, remark);
                    } else {
                        String msg = object.getString("msg");
                        updateVersionIF.getUpdateVersion(false,null, msg);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }


    public interface UpdateVersionIF {
        void getUpdateVersion(boolean result, String downLoadURL, String remark);
    }
}
