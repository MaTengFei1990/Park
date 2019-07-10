package com.hollysmart.formlib.apis;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.hollysmart.formlib.beans.DongTaiFormBean;
import com.hollysmart.formlib.beans.FormModelBean;
import com.hollysmart.beans.JDPicInfo;
import com.hollysmart.formlib.beans.ProjectBean;
import com.hollysmart.formlib.beans.ResDataBean;
import com.hollysmart.beans.cgformRuleBean;
import com.hollysmart.utils.Mlog;
import com.hollysmart.utils.Utils;
import com.hollysmart.utils.taskpool.INetModel;
import com.hollysmart.utils.taskpool.OnNetRequestListener;
import com.hollysmart.value.Values;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
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
    private HashMap<String, List<JDPicInfo>> formPicMap ;
    private OnNetRequestListener onNetRequestListener;

    public SaveResDataAPI(String access_token,ResDataBean resDataBean,HashMap<String, List<JDPicInfo>> formPicMap , OnNetRequestListener onNetRequestListener) {
        this.access_token = access_token;
        this.resDataBean = resDataBean;
        this.formPicMap = formPicMap;
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


//            JSONArray picArr = new JSONArray();

//            for (int i = 0; i < resDataBean.getPic().size(); i++) {
//                JSONObject picObj = new JSONObject();
//                JDPicInfo jdPicInfo = resDataBean.getPic().get(i);
//
//                picObj.put("filePath", jdPicInfo.getFilePath());
//                picObj.put("filename", jdPicInfo.getFilename());
//                picObj.put("imageUrl", jdPicInfo.getImageUrl());
//                picObj.put("createdAt", jdPicInfo.getCreatetime());
//
//                picArr.put(picObj);
//
//            }
//            resjson.put("pic", picArr);
//
//
//            JSONArray audioArr = new JSONArray();
//            if (resDataBean.getAudio() != null && resDataBean.getAudio().size() > 0) {
//
//                for (int i = 0; i < resDataBean.getAudio().size(); i++) {
//                    JSONObject audioObj = new JSONObject();
//                    SoundInfo soundInfo = resDataBean.getAudio().get(i);
//
//                    audioObj.put("filePath", soundInfo.getFilePath());
//                    audioObj.put("audioUrl", soundInfo.getAudioUrl());
//                    audioObj.put("filename", soundInfo.getFilename());
//                    audioObj.put("createdAt", soundInfo.getCreatetime());
//
//                    audioArr.put(audioObj);
//
//                }
//            }
//
//
//            resjson.put("audio", audioArr);


            JSONObject formJson = new JSONObject();
            JSONArray formArr = new JSONArray();

            Gson mGson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm").create();
            FormModelBean formModelBean = mGson.fromJson(resDataBean.getFormData(),
                    new TypeToken<FormModelBean>() {}.getType());

            resDataBean.setFormModel(formModelBean);

            List<DongTaiFormBean> cgformFieldList = resDataBean.getFormModel().getCgformFieldList();


            JSONArray formJsonArray = new JSONArray();

            for (int i = 0; i < cgformFieldList.size(); i++) {

                DongTaiFormBean dongTaiFormBean = cgformFieldList.get(i);


                List<JDPicInfo> jdPicInfos = formPicMap.get(dongTaiFormBean.getJavaField());

                if (jdPicInfos != null && jdPicInfos.size() > 0) {
                    String picUrl = "";
                    for (JDPicInfo jdPicInfo : jdPicInfos) {

                        if (!Utils.isEmpty(jdPicInfo.getImageUrl())) {

                            if (Utils.isEmpty(picUrl)) {
                                picUrl = jdPicInfo.getImageUrl();
                            } else {
                                picUrl = picUrl + "," + jdPicInfo.getImageUrl();
                            }

                        }
                    }

                    dongTaiFormBean.setPropertyLabel(picUrl);
                }

                if (dongTaiFormBean.getCgformFieldList() != null && dongTaiFormBean.getCgformFieldList().size() > 0) {

                    List<DongTaiFormBean> childFormList = dongTaiFormBean.getCgformFieldList();


                    for (DongTaiFormBean childForm : childFormList) {

                        List<JDPicInfo> childjdPicInfos = formPicMap.get(childForm.getJavaField());

                        if (childjdPicInfos != null && childjdPicInfos.size() > 0) {
                            String picUrl = "";
                            for (JDPicInfo jdPicInfo : childjdPicInfos) {

                                if (!Utils.isEmpty(jdPicInfo.getImageUrl())) {

                                    if (Utils.isEmpty(picUrl)) {
                                        picUrl = jdPicInfo.getImageUrl();
                                    } else {
                                        picUrl = picUrl + "," + jdPicInfo.getImageUrl();
                                    }

                                }
                            }

                            childForm.setPropertyLabel(picUrl);
                        }
                    }

                }


                JSONObject formobj = new JSONObject();
                try {
                    formobj.put("content", dongTaiFormBean.getContent());
                    formobj.put("fieldMustInput", dongTaiFormBean.getFieldMustInput());
                    formobj.put("fieldName", dongTaiFormBean.getFieldName());
                    formobj.put("isEdit", dongTaiFormBean.getIsEdit());
                    formobj.put("isQuery", dongTaiFormBean.getIsQuery());
                    formobj.put("isShow", dongTaiFormBean.getIsShow());
                    formobj.put("isShowList", dongTaiFormBean.getIsShowList());
                    formobj.put("javaField", dongTaiFormBean.getJavaField());
                    formobj.put("orderNum", dongTaiFormBean.getOrderNum());
                    formobj.put("propertyLabel", dongTaiFormBean.getPropertyLabel());
                    formobj.put("showTiShi", dongTaiFormBean.isShowTiShi());
                    formobj.put("showType", dongTaiFormBean.getShowType());
                    formobj.put("dictText", dongTaiFormBean.getDictText());
                } catch (JSONException e) {
                    e.printStackTrace();
                }


                if (dongTaiFormBean.getCgformFieldList() != null && dongTaiFormBean.getCgformFieldList().size() > 0) {

                    JSONArray childformJsonArray = new JSONArray();

                    for (int j = 0; j < dongTaiFormBean.getCgformFieldList().size(); j++) {

                        DongTaiFormBean ChilddongTaiFormBean = dongTaiFormBean.getCgformFieldList().get(j);

                        JSONObject childformobj = new JSONObject();
                        try {
                            childformobj.put("content", ChilddongTaiFormBean.getContent());
                            childformobj.put("fieldMustInput", ChilddongTaiFormBean.getFieldMustInput());
                            childformobj.put("fieldName", ChilddongTaiFormBean.getFieldName());
                            childformobj.put("isEdit", ChilddongTaiFormBean.getIsEdit());
                            childformobj.put("isQuery", ChilddongTaiFormBean.getIsQuery());
                            childformobj.put("isShow", ChilddongTaiFormBean.getIsShow());
                            childformobj.put("isShowList", ChilddongTaiFormBean.getIsShowList());
                            childformobj.put("javaField", ChilddongTaiFormBean.getJavaField());
                            childformobj.put("orderNum", ChilddongTaiFormBean.getOrderNum());
                            childformobj.put("propertyLabel", ChilddongTaiFormBean.getPropertyLabel());
                            childformobj.put("showTiShi", ChilddongTaiFormBean.isShowTiShi());
                            childformobj.put("showType", ChilddongTaiFormBean.getShowType());
                            childformobj.put("dictText", ChilddongTaiFormBean.getDictText());

                            childformJsonArray.put(childformobj);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }

                    formobj.put("cgformFieldList", childformJsonArray);



                }
                if (dongTaiFormBean.getCgformRuleList() != null && dongTaiFormBean.getCgformRuleList().size() > 0) {

                    JSONArray conformRuleList = new JSONArray();

                    for (int k = 0; k < dongTaiFormBean.getCgformRuleList().size(); k++) {

                        cgformRuleBean ChildchnformRule = dongTaiFormBean.getCgformRuleList().get(k);

                        JSONObject childRuleobj = new JSONObject();
                        try {
                            childRuleobj.put("createDate", ChildchnformRule.getCreateDate());
                            childRuleobj.put("error", ChildchnformRule.getError());
                            childRuleobj.put("modified", ChildchnformRule.getModified());
                            childRuleobj.put("mykey", ChildchnformRule.getMykey());
                            childRuleobj.put("pageNo", ChildchnformRule.getPageNo());
                            childRuleobj.put("pageSize", ChildchnformRule.getPageSize());
                            childRuleobj.put("pattern", ChildchnformRule.getPattern());
                            childRuleobj.put("remarks", ChildchnformRule.getRemarks());
                            childRuleobj.put("type", ChildchnformRule.getType());
                            childRuleobj.put("updateDate", ChildchnformRule.getUpdateDate());

                            conformRuleList.put(childRuleobj);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }

                    formobj.put("cgformRuleList", conformRuleList);



                }


                formJsonArray.put(formobj);


            }


            JSONArray array = formJsonArray;












//            if (array.length() == 0) {
//
//            } else {
            formJson.put("cgformFieldList", array);
//
//            }




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
