package com.hollysmart.apis;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;
import com.hollysmart.beans.DongTaiFormBean;
import com.hollysmart.beans.FormModelBean;
import com.hollysmart.beans.JDPicInfo;
import com.hollysmart.beans.ProjectBean;
import com.hollysmart.beans.ResDataBean;
import com.hollysmart.beans.SoundInfo;
import com.hollysmart.utils.Mlog;
import com.hollysmart.utils.taskpool.INetModel;
import com.hollysmart.utils.taskpool.OnNetRequestListener;
import com.hollysmart.value.Values;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.List;

import okhttp3.Call;
import okhttp3.MediaType;

/**
 *  插入单个资源采集数据save
 * Created by Lenovo on 2019/4/17.
 */

public class SaveResDataAPI implements INetModel {



    private String access_token;
    private ResDataBean resDataBean;
    private OnNetRequestListener onNetRequestListener;

    public SaveResDataAPI(String access_token,ResDataBean resDataBean, OnNetRequestListener onNetRequestListener) {
        this.access_token = access_token;
        this.resDataBean = resDataBean;
        this.onNetRequestListener = onNetRequestListener;

    }

    @Override
    public void request() {

        JSONObject outJs = new JSONObject();

        try {
            outJs.put("id", resDataBean.getId());
            outJs.put("fd_rescode", resDataBean.getRescode());
            outJs.put("fd_resname", resDataBean.getFd_resname());
            outJs.put("fd_restaskid", resDataBean.getFdTaskId());
            outJs.put("fd_resdate", resDataBean.getFd_resdate());
            outJs.put("fd_restaskname", resDataBean.getFd_restaskname());
            outJs.put("fd_resmodelid", resDataBean.getFd_resmodelid());
            outJs.put("fd_resmodelname", resDataBean.getFd_resmodelname());
            outJs.put("fd_resposition", resDataBean.getFd_resposition());


            JSONObject resjson = new JSONObject();

            resjson.put("id", resDataBean.getId());
            resjson.put("note", resDataBean.getNote());
            resjson.put("isUpload", resDataBean.isUpload());
            resjson.put("longitude", resDataBean.getLongitude());
            resjson.put("scope", resDataBean.getScope());
            resjson.put("latitude", resDataBean.getLatitude());
            resjson.put("type", resDataBean.getType());
            resjson.put("unitName", resDataBean.getFd_resname());
            resjson.put("isNeedManage", resDataBean.getId());
            resjson.put("number", resDataBean.getRescode());
            resjson.put("createdAt", resDataBean.getCreatedAt());
            resjson.put("categoryId", resDataBean.getFd_resmodelid());


            JSONArray picArr = new JSONArray();

            for (int i = 0; i < resDataBean.getPic().size(); i++) {
                JSONObject picObj = new JSONObject();
                JDPicInfo jdPicInfo = resDataBean.getPic().get(i);

                picObj.put("filePath", jdPicInfo.getFilePath());
                picObj.put("filename", jdPicInfo.getFilename());
                picObj.put("imageUrl", jdPicInfo.getImageUrl());
                picObj.put("createdAt", jdPicInfo.getCreatetime());

                picArr.put(picObj);

            }
            resjson.put("pic", picArr);


            JSONArray audioArr = new JSONArray();
            if (resDataBean.getAudio() != null && resDataBean.getAudio().size() > 0) {

                for (int i = 0; i < resDataBean.getAudio().size(); i++) {
                    JSONObject audioObj = new JSONObject();
                    SoundInfo soundInfo = resDataBean.getAudio().get(i);

                    audioObj.put("filePath", soundInfo.getFilePath());
                    audioObj.put("audioUrl", soundInfo.getAudioUrl());
                    audioObj.put("filename", soundInfo.getFilename());
                    audioObj.put("createdAt", soundInfo.getCreatetime());

                    audioArr.put(audioObj);

                }
            }


            resjson.put("audio", audioArr);


            JSONObject formJson = new JSONObject();
            JSONArray formArr = new JSONArray();

            Gson mGson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm").create();
            FormModelBean formModelBean = mGson.fromJson(resDataBean.getFormData(),
                    new TypeToken<FormModelBean>() {}.getType());

            resDataBean.setFormModel(formModelBean);

            List<DongTaiFormBean> cgformFieldList = resDataBean.getFormModel().getCgformFieldList();



            Type type =new TypeToken<List<DongTaiFormBean>>() {}.getType();

            JsonArray jsonArray = new Gson().toJsonTree(cgformFieldList, type).getAsJsonArray();


            JSONArray array = new JSONArray(jsonArray.toString());

            if (array.length() == 0) {

            } else {
                formJson.put("cgformFieldList", array);

            }




            resjson.put("formModel", formJson);



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
                        resDataBean.setUpload(true);
                        onNetRequestListener.OnResult(true,null,resDataBean);
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
