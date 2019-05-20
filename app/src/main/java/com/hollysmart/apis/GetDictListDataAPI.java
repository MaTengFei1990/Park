package com.hollysmart.apis;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.hollysmart.beans.DictionaryBean;
import com.hollysmart.utils.Mlog;
import com.hollysmart.utils.taskpool.INetModel;
import com.hollysmart.value.Values;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

import okhttp3.Call;

/**
 * Created by Lenovo on 2019/4/24.
 */

public class GetDictListDataAPI implements INetModel {

    private String access_token;
    private String type;
    private List<String> tyepList;
    private GetDictListDataIF getDictListDataIF;

    public GetDictListDataAPI(String access_token, String type, List<String> tyepList, GetDictListDataIF getDictListDataIF) {
        this.access_token = access_token;
        this.type = type;
        this.tyepList = tyepList;
        this.getDictListDataIF = getDictListDataIF;
    }

    @Override
    public void request() {
        String urlStr = Values.SERVICE_URL_FORM + "/admin/sys/dict/listData";
        OkHttpUtils.get().url(urlStr)
                .addHeader("Authorization", access_token)
                .addParams("type",type)
                .build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                e.printStackTrace();
                getDictListDataIF.getResDataList(false, null);
            }

            @Override
            public void onResponse(String response, int id) {
                Mlog.d("list下的字典数据:" + response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    int status = jsonObject.getInt("status");
                    if ( status == 200){
                        Gson mGson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm").create();

                        JSONObject datalist = jsonObject.getJSONObject("data");

                        HashMap<String, List<DictionaryBean>> map = new HashMap<>();

                        for(int i=0;i<tyepList.size();i++) {
                            List<DictionaryBean> dictList = mGson.fromJson(datalist.getString(tyepList.get(i)),
                                    new TypeToken<List<DictionaryBean>>() {
                                    }.getType());
                            map.put(tyepList.get(i), dictList);
                        }


                        getDictListDataIF.getResDataList(true, map);
                    }else {
                        getDictListDataIF.getResDataList(false, null);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    getDictListDataIF.getResDataList(false, null);
                }

            }
        });

    }


    public interface GetDictListDataIF{

       void  getResDataList(boolean isOk, HashMap<String, List<DictionaryBean>> map);

    }

}
