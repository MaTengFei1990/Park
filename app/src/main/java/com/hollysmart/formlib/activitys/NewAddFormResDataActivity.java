package com.hollysmart.formlib.activitys;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.hollysmart.apis.SaveInfoAPI;
import com.hollysmart.formlib.adapters.BiaoGeRecyclerAdapter2;
import com.hollysmart.apis.GetDictListDataAPI;
import com.hollysmart.apis.GetResModelAPI;
import com.hollysmart.apis.GetResModelVersionAPI;
import com.hollysmart.formlib.apis.SaveResDataAPI;
import com.hollysmart.apis.UpLoadFormPicAPI;
import com.hollysmart.beans.DictionaryBean;
import com.hollysmart.formlib.beans.DongTaiFormBean;
import com.hollysmart.formlib.beans.FormModelBean;
import com.hollysmart.beans.GPS;
import com.hollysmart.beans.JDPicInfo;
import com.hollysmart.formlib.beans.ResDataBean;
import com.hollysmart.beans.ResModelBean;
import com.hollysmart.beans.cgformRuleBean;
import com.hollysmart.db.DictionaryDao;
import com.hollysmart.db.ResDataDao;
import com.hollysmart.db.ResModelDao;
import com.hollysmart.db.UserInfo;
import com.hollysmart.dialog.LoadingProgressDialog;
import com.hollysmart.park.BuildConfig;
import com.hollysmart.park.R;
import com.hollysmart.style.StyleAnimActivity;
import com.hollysmart.utils.ACache;
import com.hollysmart.utils.CCM_Bitmap;
import com.hollysmart.utils.GPSConverterUtils;
import com.hollysmart.utils.PicYasuo;
import com.hollysmart.utils.Utils;
import com.hollysmart.utils.loctionpic.ImageItem;
import com.hollysmart.utils.taskpool.OnNetRequestListener;
import com.hollysmart.utils.taskpool.TaskPool;
import com.hollysmart.value.Values;

import org.json.JSONArray;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class  NewAddFormResDataActivity extends StyleAnimActivity  {


    @Override
    public int layoutResID() {
        return R.layout.activity_dynamic_form;
    }

    private RecyclerView recy_view;

    private List<DongTaiFormBean> resFromBeanLsit;
    private List<DongTaiFormBean> formBeanList;


    private BiaoGeRecyclerAdapter2 biaoGeRecyclerAdapter;

    private TextView tv_title;

    //目标项是否在最后一个可见项之后
    private boolean mShouldScroll;
    //记录目标项位置
    private int mToPosition;

    private ResModelBean showRes;

    private ResModelDao resModelDao;
    private  DictionaryDao dictionaryDao;


    private HashMap<String, List<JDPicInfo>> formPicMap = new HashMap<String, List<JDPicInfo>>();

    private LoadingProgressDialog lpd;

    private boolean sportEditFlag = false; // ture 新添加 false 修改


    private ResDataBean resDataBean;

    @Override
    public void findView() {
        recy_view = findViewById(R.id.recy_view);
        tv_title = findViewById(R.id.tv_title);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        //设置布局管理器
        recy_view.setLayoutManager(layoutManager);

        findViewById(R.id.iv_shure).setOnClickListener(this);
        findViewById(R.id.ib_back).setOnClickListener(this);


    }

    @Override
    public void init() {
        isLogin();
        setLpd();

        resDataBean = (ResDataBean) getIntent().getSerializableExtra("resDataBean");
        resFromBeanLsit = (List<DongTaiFormBean>) getIntent().getSerializableExtra("formBeanList");
        formPicMap = (HashMap<String, List<JDPicInfo>>) getIntent().getSerializableExtra("formPicMap");
        sportEditFlag = getIntent().getBooleanExtra("sportEditFlag", false);

        picAdd2From(formPicMap, resFromBeanLsit);

        tv_title.setText("信息录入-" + resDataBean.getFd_resmodelname());

        resModelDao = new ResModelDao(mContext);
        dictionaryDao = new DictionaryDao(mContext);

        showRes = resModelDao.getDatById(resDataBean.getFd_resmodelid());

        initDatas(resDataBean.getFd_resmodelid());

    }

    private void setLpd() {
        lpd = new LoadingProgressDialog();
        lpd.setMessage("正在保存，请稍等...");
        lpd.create(mContext, lpd.STYLE_SPINNER);
    }


    private void picAdd2From(HashMap<String, List<JDPicInfo>> formPicMap, List<DongTaiFormBean> formBeans) {

        for (int i = 0; i < formBeans.size(); i++) {
            DongTaiFormBean formBean = formBeans.get(i);

            if (formBean.getShowType().equals("image")) {
                if (formPicMap != null && formPicMap.size() > 0) {
                    List<JDPicInfo> picInfos = formPicMap.get(formBean.getJavaField());
                    formBean.setPic(picInfos);

                }


            }

            if (formBean.getCgformFieldList() != null && formBean.getCgformFieldList().size() > 0) {

                List<DongTaiFormBean> childList = formBean.getCgformFieldList();

                for (int j = 0; j < childList.size(); j++) {

                    DongTaiFormBean childbean = childList.get(j);

                    if (childbean.getShowType().equals("image")) {

                        if (formPicMap != null && formPicMap.size() > 0) {
                            List<JDPicInfo> picInfos = formPicMap.get(childbean.getJavaField());
                            childbean.setPic(picInfos);

                        }


                    }


                }



            }

        }


    }

    private void initDatas(final String  resmodelid) {

        //先判断有无网络
        int netWorkStart = Utils.getNetWorkStart(mContext);


        if (Utils.NETWORK_NONE == netWorkStart) {
            //无网络
            showLocalData( resmodelid);

        } else {
            //有网络
            new GetResModelVersionAPI(userInfo.getAccess_token(), resmodelid, new GetResModelVersionAPI.GetResModelVersionIF() {
                @Override
                public void onGetResModelVersionResult(boolean isOk, int version) {

                    HashMap<String, List<DictionaryBean>> stringListHashMap = getlocalDicItems(resFromBeanLsit);

                    if ((isOk && (version > showRes.getfVersion()))||(stringListHashMap==null||stringListHashMap.size()==0)) {//有更新获取网络数据
                        // 获取表单数据

                        new GetResModelAPI(userInfo.getAccess_token(), showRes.getId(), new GetResModelAPI.GetResModelIF() {
                            @Override
                            public void ongetResModelIFResult(boolean isOk, ResModelBean resModelBean) {

                                if (isOk) {//获取到网络数据

                                    resModelDao.addOrUpdate(resModelBean);

                                    String getfJsonData = resModelBean.getfJsonData();
                                    Gson mGson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm").create();
                                    formBeanList = mGson.fromJson(getfJsonData, new TypeToken<List<DongTaiFormBean>>() {
                                    }.getType());


                                    String showType = new String();

                                    List<String> showTypelist = new ArrayList<>();


                                    for (int i = 0; i < formBeanList.size(); i++) {

                                        DongTaiFormBean dongTaiFormBean = formBeanList.get(i);

                                        if (dongTaiFormBean.getShowType().equals("list")) {

                                            if (!Utils.isEmpty(dongTaiFormBean.getDictText())) {
                                                if (!showTypelist.contains(dongTaiFormBean.getDictText())) {
                                                    showTypelist.add(dongTaiFormBean.getDictText());
                                                    if (showTypelist.size()==1) {
                                                        showType = dongTaiFormBean.getDictText();
                                                    } else {
                                                        showType = showType + "," + dongTaiFormBean.getDictText();
                                                    }
                                                }


                                            }


                                        }

                                    }
                                    cocalmap.clear();

                                    //获取字典数据
                                    new GetDictListDataAPI(userInfo.getAccess_token(), showType, showTypelist, new GetDictListDataAPI.GetDictListDataIF() {
                                        @Override
                                        public void getResDataList(boolean isOk, HashMap<String, List<DictionaryBean>> map) {

                                            if (isOk) {
                                                cocalmap.putAll(map);

                                                if (resFromBeanLsit != null && resFromBeanLsit.size() > 0) {
                                                    formBeanList.clear();
                                                    formBeanList.addAll(resFromBeanLsit);


                                                }

                                                biaoGeRecyclerAdapter = new BiaoGeRecyclerAdapter2(mContext, formBeanList,false);

                                                recy_view.setAdapter(biaoGeRecyclerAdapter);

                                                recy_view.addOnScrollListener(new RecyclerView.OnScrollListener() {
                                                    @Override
                                                    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                                                        super.onScrollStateChanged(recyclerView, newState);
                                                        if (mShouldScroll && RecyclerView.SCROLL_STATE_IDLE == newState) {
                                                            mShouldScroll = false;
                                                            smoothMoveToPosition(recy_view, mToPosition);
                                                        }
                                                    }
                                                });

                                                UpdateData();
                                                biaoGeRecyclerAdapter.setMap(map);

                                            }

                                        }
                                    }).request();


                                } else {
                                    showLocalData(resmodelid);
                                }


                            }
                        }).request();


                    } else {
                        showLocalData(resmodelid);
                    }






                }
            }).request();


        }


    }

    /***
     * 显示本地的数据
     */

    private void showLocalData(String  resmodelid) {

        //获取本地表单

        showRes = resModelDao.getDatById(resmodelid);
        String getfJsonData = showRes.getfJsonData();
        Gson mGson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm").create();
        formBeanList = mGson.fromJson(getfJsonData, new TypeToken<List<DongTaiFormBean>>() {
        }.getType());

        if (resFromBeanLsit != null && resFromBeanLsit.size() != 0) {
            formBeanList.clear();
            formBeanList.addAll(resFromBeanLsit);
        }

        //获取本地字典
        for (DongTaiFormBean dongTaiFormBean : formBeanList) {

            if (dongTaiFormBean.getShowType().equals("list")) {

                if (!Utils.isEmpty(dongTaiFormBean.getDictText())) {

                    List<DictionaryBean> dictionaryBeanList = dictionaryDao.getDataType(dongTaiFormBean.getDictText());

                    cocalmap.put(dongTaiFormBean.getDictText(), dictionaryBeanList);

                }


            }

        }


        biaoGeRecyclerAdapter = new BiaoGeRecyclerAdapter2(mContext, formBeanList,false);
        recy_view.setAdapter(biaoGeRecyclerAdapter);
        recy_view.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (mShouldScroll && RecyclerView.SCROLL_STATE_IDLE == newState) {
                    mShouldScroll = false;
                    smoothMoveToPosition(recy_view, mToPosition);
                }
            }
        });

        biaoGeRecyclerAdapter.setMap(cocalmap);



    }





    private List<DongTaiFormBean>  comparis(List<DongTaiFormBean> oldFormList,List<DongTaiFormBean> newFormList, ResDataBean resDataBean) {

        if (oldFormList == null || oldFormList.size() == 0) {

            return null;
        }
        if (newFormList == null || newFormList.size() == 0) {

            return null;
        }

        boolean isNewForm = false;

        for (int s = 0; s < oldFormList.size(); s++) {

            DongTaiFormBean formBean = oldFormList.get(s);

            if (formBean.getJavaField().equals("name")) {
                isNewForm=true;
            }
            if (formBean.getJavaField().equals("number")) {
                isNewForm=true;
            }
            if (formBean.getJavaField().equals("location")) {
                isNewForm=true;
            }
        }


        if (isNewForm) {
            return oldFormList;

        } else {

            for (int i = 0; i < oldFormList.size(); i++) {

                DongTaiFormBean oldBean = oldFormList.get(i);

                if (oldBean.getShowType().equals("list") ) {

                    List<DongTaiFormBean> oldChildList = oldBean.getCgformFieldList();

                    if (oldChildList == null || oldChildList.size() == 0) {
                        break;
                    }

                    if ("是".equals(oldBean.getPropertyLabel())) {

                        oldBean.setPropertyLabel("1");
                    }

                    if ("否".equals(oldBean.getPropertyLabel())) {

                        oldBean.setPropertyLabel("0");
                    }

                }

                for (int j = 0; j < newFormList.size(); j++) {

                    DongTaiFormBean newBean = newFormList.get(j);


                    if (oldBean.getJavaField().equals(newBean.getJavaField())) {

                        newBean.setPropertyLabel(oldBean.getPropertyLabel());


                        if (oldBean.getShowType().equals("list") && newBean.getShowType().equals("switch")) {

                            List<DongTaiFormBean> oldChildList = oldBean.getCgformFieldList();
                            List<DongTaiFormBean> newchildList = newBean.getCgformFieldList();

                            if (oldChildList == null || oldChildList.size() == 0) {
                                break;
                            }
                            if (newchildList == null || newchildList.size() == 0) {
                                break;
                            }

                            for (int k = 0; k < oldChildList.size(); k++) {

                                DongTaiFormBean oldchildBean = oldChildList.get(k);


                                for (int m = 0; m < newchildList.size(); m++) {

                                    DongTaiFormBean newchildBean = newFormList.get(m);


                                    if (oldchildBean.getJavaField().equals(newchildBean.getJavaField())) {

                                        newchildBean.setPropertyLabel(oldchildBean.getPropertyLabel());
                                    }

                                }


                            }

                        }



                        if (oldBean.getShowType().equals("switch") && newBean.getShowType().equals("switch")) {

                            List<DongTaiFormBean> oldChildList = oldBean.getCgformFieldList();
                            List<DongTaiFormBean> newchildList = newBean.getCgformFieldList();

                            if (oldChildList == null || oldChildList.size() == 0) {
                                break;
                            }
                            if (newchildList == null || newchildList.size() == 0) {
                                break;
                            }

                            for (int k = 0; k < oldChildList.size(); k++) {

                                DongTaiFormBean oldchildBean = oldChildList.get(k);


                                for (int m = 0; m < newchildList.size(); m++) {

                                    DongTaiFormBean newchildBean = newchildList.get(m);


                                    if (oldchildBean.getJavaField().equals(newchildBean.getJavaField())) {

                                        newchildBean.setPropertyLabel(oldchildBean.getPropertyLabel());
                                    }

                                }


                            }

                        }


                        if (resDataBean != null) {

                            if (newBean.getJavaField().equals("number")) {

                                newBean.setPropertyLabel(resDataBean.getRescode());

                            }
                            if (newBean.getJavaField().equals("name")) {
                                newBean.setPropertyLabel(resDataBean.getFd_resname());
                            }
                            if (newBean.getJavaField().equals("location")) {
                                newBean.setPropertyLabel(resDataBean.getLatitude() +","+ resDataBean.getLongitude());

                            }
                        }



                    }




                }


            }

            oldFormList.clear();
            oldFormList.addAll(newFormList);
            return newFormList;
        }







    }



    /***
     * 获取本地字典数据
     *
     */


    private HashMap<String, List<DictionaryBean>> getlocalDicItems(List<DongTaiFormBean> formBeans) {

        HashMap<String, List<DictionaryBean>> map = new HashMap<>();

        //获取本地字典
        for (DongTaiFormBean dongTaiFormBean : formBeans) {

            if (dongTaiFormBean.getShowType().equals("list")) {

                if (!Utils.isEmpty(dongTaiFormBean.getDictText())) {

                    List<DictionaryBean> dictionaryBeanList = dictionaryDao.getDataType(dongTaiFormBean.getDictText());

                    if (dictionaryBeanList != null && dictionaryBeanList.size() > 0) {

                        map.put(dongTaiFormBean.getDictText(), dictionaryBeanList);
                    }


                }


            }

        }


        return map;
    }


    /***
     * 获取网络的字典数据
     */

    private void getNetDicItems() {

        String showType = new String();

        List<String> showTypelist = new ArrayList<>();


        for (int i = 0; i < formBeanList.size(); i++) {

            DongTaiFormBean dongTaiFormBean = formBeanList.get(i);

            if (dongTaiFormBean.getShowType().equals("list")) {

                if (!Utils.isEmpty(dongTaiFormBean.getDictText())) {
                    if (!showTypelist.contains(dongTaiFormBean.getDictText())) {
                        showTypelist.add(dongTaiFormBean.getDictText());
                        if (showTypelist.size()==1) {
                            showType = dongTaiFormBean.getDictText();
                        } else {
                            showType = showType + "," + dongTaiFormBean.getDictText();
                        }
                    }


                }


            }

        }
        cocalmap.clear();


        //获取字典数据
        new GetDictListDataAPI(userInfo.getAccess_token(), showType, showTypelist, new GetDictListDataAPI.GetDictListDataIF() {
            @Override
            public void getResDataList(boolean isOk, HashMap<String, List<DictionaryBean>> map) {

                if (isOk) {
                    map.putAll(map);

                    DictionaryDao dictionaryDao = new DictionaryDao(mContext);

                    for (List<DictionaryBean> value : map.values()) {
                        dictionaryDao.addOrUpdate(value);
                    }


                    if (resFromBeanLsit != null && resFromBeanLsit.size() > 0) {
                        formBeanList.clear();
                        formBeanList.addAll(resFromBeanLsit);


                    }

                    biaoGeRecyclerAdapter = new BiaoGeRecyclerAdapter2(mContext, formBeanList,false);

                    recy_view.setAdapter(biaoGeRecyclerAdapter);

                    recy_view.addOnScrollListener(new RecyclerView.OnScrollListener() {
                        @Override
                        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                            super.onScrollStateChanged(recyclerView, newState);
                            if (mShouldScroll && RecyclerView.SCROLL_STATE_IDLE == newState) {
                                mShouldScroll = false;
                                smoothMoveToPosition(recy_view, mToPosition);
                            }
                        }
                    });

                    UpdateData();
                    biaoGeRecyclerAdapter.setMap(map);

                }

            }
        }).request();

    }



    /****
     * 更新本地的数据，然后在展示数据；
     */

    private void UpdateData() {
        Set<String>   keys = cocalmap.keySet();
        for(String key :keys){

            List<DictionaryBean> dataType = dictionaryDao.getDataType(key);

            if (dataType != null && dataType.size() > 0) {

                dictionaryDao.deletByType(key);
            }

        }

        for (List<DictionaryBean> value : cocalmap.values()) {

            dictionaryDao.addOrUpdate(value);

        }











    }


    /**
     * 滑动到指定位置
     */
    private void smoothMoveToPosition(RecyclerView mRecyclerView, final int position) {
        // 第一个可见位置
        int firstItem = mRecyclerView.getChildLayoutPosition(mRecyclerView.getChildAt(0));
        // 最后一个可见位置
        int lastItem = mRecyclerView.getChildLayoutPosition(mRecyclerView.getChildAt(mRecyclerView.getChildCount() - 1));
        if (position < firstItem) {
            // 第一种可能:跳转位置在第一个可见位置之前
            mRecyclerView.smoothScrollToPosition(position);
        } else if (position <= lastItem) {
            // 第二种可能:跳转位置在第一个可见位置之后
            int movePosition = position - firstItem;
            if (movePosition >= 0 && movePosition < mRecyclerView.getChildCount()) {
                int top = mRecyclerView.getChildAt(movePosition).getTop();
                mRecyclerView.smoothScrollBy(0, top);
            }
        } else {
            // 第三种可能:跳转位置在最后可见项之后
            mRecyclerView.smoothScrollToPosition(position);
            mToPosition = position;
            mShouldScroll = true;
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.iv_shure:

                labe: for (int i = 0; i < formBeanList.size(); i++) {
                    DongTaiFormBean dongTaiFormBean = formBeanList.get(i);


                    List<DongTaiFormBean> propertys = dongTaiFormBean.getCgformFieldList();


                    List<cgformRuleBean> cgformRuleList = dongTaiFormBean.getCgformRuleList();

                    if (cgformRuleList != null && cgformRuleList.size() > 0) {
                        cgformRuleBean cgformRuleBean = cgformRuleList.get(0);
                        String par = cgformRuleBean.getPattern();
                        Pattern p = Pattern.compile(par);
                        if (!Utils.isEmpty(dongTaiFormBean.getPropertyLabel())) {
                            Matcher m = p.matcher(dongTaiFormBean.getPropertyLabel());
                            if (!m.matches()) {
                                smoothMoveToPosition(recy_view, dongTaiFormBean.getPosition());
                                dongTaiFormBean.setPropertyLabel("");
                                break;
                            }

                        }
                    }

                    if (dongTaiFormBean.getFieldMustInput()) {

                        if (Utils.isEmpty(dongTaiFormBean.getPropertyLabel())) {

                            if ((dongTaiFormBean.getShowType().equals("image")) && (dongTaiFormBean.getPic() != null && dongTaiFormBean.getPic().size() >0)) {
                                dongTaiFormBean.setShowTiShi(false);
                            } else {

                                dongTaiFormBean.setShowTiShi(true);
                                smoothMoveToPosition(recy_view, dongTaiFormBean.getPosition());
                                break;
                            }


                        } else {
                            dongTaiFormBean.setShowTiShi(false);
                        }

                    }


                    if (propertys != null && propertys.size() > 0) {


                        if (propertys != null && (!Utils.isEmpty(dongTaiFormBean.getPropertyLabel()) && dongTaiFormBean.getPropertyLabel().equals("1"))) {

                            for (int j = 0; j < propertys.size(); j++) {


                                DongTaiFormBean dongTaiFormBean1 = propertys.get(j);

                                List<cgformRuleBean> cgformRuleList2 = dongTaiFormBean1.getCgformRuleList();

                                if (cgformRuleList2 != null && cgformRuleList2.size() > 0) {
                                    cgformRuleBean cgformRuleBean = cgformRuleList2.get(0);
                                    String par = cgformRuleBean.getPattern();
                                    Pattern p = Pattern.compile(par);
                                    if (!Utils.isEmpty(dongTaiFormBean1.getPropertyLabel())) {
                                        Matcher m = p.matcher(dongTaiFormBean1.getPropertyLabel());
                                        if (!m.matches()) {
                                            smoothMoveToPosition(recy_view, dongTaiFormBean1.getPosition());
                                            dongTaiFormBean1.setPropertyLabel("");
                                            break labe;
                                        }

                                    }
                                }

                                if (dongTaiFormBean1.getFieldMustInput()) {

                                    if (Utils.isEmpty(dongTaiFormBean1.getPropertyLabel())) {


                                        if ((dongTaiFormBean1.getShowType().equals("image")) && (dongTaiFormBean1.getPic() != null && dongTaiFormBean1.getPic().size() >0)) {
                                            dongTaiFormBean1.setShowTiShi(false);
                                            if (dongTaiFormBean1.getPic().size() == 1) {
                                                if (dongTaiFormBean1.getPic().get(0).getIsAddFlag() == 1) {
                                                    dongTaiFormBean1.setShowTiShi(true);
                                                    smoothMoveToPosition(recy_view, dongTaiFormBean1.getPosition());
                                                    break labe;
                                                } else {
                                                    dongTaiFormBean1.setShowTiShi(false);
                                                }
                                            }

                                        } else {

                                            dongTaiFormBean1.setShowTiShi(true);
                                            smoothMoveToPosition(recy_view, dongTaiFormBean1.getPosition());
                                            break labe;
                                        }


                                    } else {
                                        dongTaiFormBean1.setShowTiShi(false);
                                    }

                                }


                            }
                        }




                    } else {


                        if (cgformRuleList != null && cgformRuleList.size() > 0) {
                            cgformRuleBean cgformRuleBean = cgformRuleList.get(0);
                            String par = cgformRuleBean.getPattern();
                            Pattern p = Pattern.compile(par);
                            if (!Utils.isEmpty(dongTaiFormBean.getPropertyLabel())) {
                                Matcher m = p.matcher(dongTaiFormBean.getPropertyLabel());
                                if (!m.matches()) {
                                    smoothMoveToPosition(recy_view,dongTaiFormBean.getPosition());
                                    dongTaiFormBean.setPropertyLabel("");
                                    break;
                                }

                            }
                        }

                        if (dongTaiFormBean.getFieldMustInput()) {

                            if (Utils.isEmpty(dongTaiFormBean.getPropertyLabel())) {

                                if ((dongTaiFormBean.getShowType().equals("image")) && (dongTaiFormBean.getPic() != null && dongTaiFormBean.getPic().size() >0)) {
                                    dongTaiFormBean.setShowTiShi(false);
                                } else {

                                    dongTaiFormBean.setShowTiShi(true);
                                    smoothMoveToPosition(recy_view, dongTaiFormBean.getPosition());
                                    break;
                                }


                            } else {
                                dongTaiFormBean.setShowTiShi(false);
                            }

                        }


                    }


                }
                biaoGeRecyclerAdapter.notifyDataSetChanged();

                boolean allEdit = false;

                labe: for (DongTaiFormBean dongTaiFormBean : formBeanList) {

                    if (dongTaiFormBean.getFieldMustInput()) {

                        if (Utils.isEmpty(dongTaiFormBean.getPropertyLabel())) {


                            if (dongTaiFormBean.getShowType().equals("image")) {

                                if (dongTaiFormBean.getPic() == null || dongTaiFormBean.getPic().size() == 0) {
                                    allEdit = false;
                                    break;
                                }


                            }else {

                                allEdit = false;
                                break;

                            }


                        }
                    }


                    List<DongTaiFormBean> propertys = dongTaiFormBean.getCgformFieldList();

                    if ((propertys != null && propertys.size() > 0)&&(!Utils.isEmpty(dongTaiFormBean.getPropertyLabel()) && dongTaiFormBean.getPropertyLabel().equals("1"))) {

                        for (DongTaiFormBean dongTaiFormBean1 : propertys) {

                            if (dongTaiFormBean1.getFieldMustInput()) {

                                if (Utils.isEmpty(dongTaiFormBean1.getPropertyLabel())) {

                                    if (dongTaiFormBean1.getShowType().equals("image")) {

                                        if (dongTaiFormBean1.getPic() == null || dongTaiFormBean1.getPic().size() == 0) {
                                            allEdit = false;
                                            break labe;
                                        }


                                    }else {

                                        allEdit = false;
                                        break labe;

                                    }

                                }


                            }

                        }


                    }

                    allEdit = true;
                }
                if (allEdit) {

                    submitForm();


                }

                break;


            case R.id.ib_back:

              showdialogs();
                break;

        }

    }


    /**
     * 弹出对话框。
     */
    private void showdialogs() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("未保存数据将丢失！");
        builder.setNegativeButton("继续退出", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                finish();


            }
        });
        builder.create().show();
    }



    /**
     * 上传form表单；
     */
    private void submitForm(){

        for (int i = 0; i < formBeanList.size(); i++) {

            DongTaiFormBean formBean = formBeanList.get(i);

            if (formBean.getJavaField().equals("name")) {

                resDataBean.setFd_resname(formBean.getPropertyLabel());
            }
            if (formBean.getJavaField().equals("number")) {

                resDataBean.setRescode(formBean.getPropertyLabel());
            }
            if (formBean.getJavaField().equals("location")) {

                String propertyLabel = formBean.getPropertyLabel();

                if (!Utils.isEmpty(propertyLabel)) {
                    resDataBean.setFd_resposition(propertyLabel);
                    String[] split = propertyLabel.split(",");

                    GPS gps = baiDu2GaoDe(new Double(split[0]),new Double(split[1]));

                    resDataBean.setLatitude(gps.getLat() + "");
                    resDataBean.setLongitude(gps.getLon() + "");
                    resDataBean.setFd_resposition(gps.getLat() + "," + gps.getLon());
                    formBean.setPropertyLabel(gps.getLat() + "," + gps.getLon());
                }


            }


        }

        addDB(resDataBean);

        uploadResData(resDataBean.getId());




    }

    private GPS baiDu2GaoDe(double bd_lat, double bd_lon) {
        GPS Gcj02_gps = GPSConverterUtils.bd09_To_Gps84(bd_lat, bd_lon);

        return Gcj02_gps;

    }

    // 数据库操作
    private void addDB(ResDataBean resDataBean) {


        for (int i = 0; i < formBeanList.size(); i++) {

            DongTaiFormBean dongTaiFormBean = formBeanList.get(i);


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


        }


        FormModelBean formModelBean = new FormModelBean();

        formModelBean.setCgformFieldList(formBeanList);

        resDataBean.setFormModel(formModelBean);

        Gson gson = new Gson();
        String formBeanStr = gson.toJson(formModelBean);

        resDataBean.setFormData(formBeanStr);




        ResDataDao resDataDao = new ResDataDao(mContext);

        resDataDao.addOrUpdate(resDataBean);



    }


    /**
     * 将资源上传到后台
     */
    private void uploadResData(String uuid) {
        final ResDataDao formDao = new ResDataDao(mContext);

        final TaskPool taskPool = new TaskPool();

        final OnNetRequestListener listener= new OnNetRequestListener() {
            @Override
            public void onFinish() {
                lpd.cancel();
            }

            @Override
            public void OnNext() {
                taskPool.execute(this);
            }

            @Override
            public void OnResult(boolean isOk, String msg, Object object) {
                if (isOk) {
                    ResDataBean bean = (ResDataBean) object;
                    if (bean != null) {
                        formDao.addOrUpdate(bean);
                        taskPool.execute(this);

                        if (sportEditFlag) {
                            lpd.setMessage("新增成功");

                        } else {
                            lpd.setMessage("修改成功");
                        }


                    }

                    if (taskPool.getTotal() == 0) {
                        if (sportEditFlag) {
                            Utils.showDialog(mContext, "新增成功");

                            final Timer t =new Timer();
                            t.schedule(new TimerTask() {
                                @Override
                                public void run() {
                                    setResult(1);
                                    addDB(resDataBean);
                                    NewAddFormResDataActivity.this.finish();

                                }
                                },2000);


                        } else {
                            Utils.showDialog(mContext, "修改成功");

                            final Timer t =new Timer();
                            t.schedule(new TimerTask() {
                                @Override
                                public void run() {
                                    setResult(1);
                                    addDB(resDataBean);
                                    NewAddFormResDataActivity.this.finish();

                                }
                            },2000);
                        }
                    }
                } else {
                    if (sportEditFlag) {
                        Utils.showDialog(mContext, "新增失败");

                    } else {
                        Utils.showDialog(mContext, "修改失败");
                    }
                }
            }
        };



            getFormPicMap(formBeanList);
            for (Map.Entry<String, List<JDPicInfo>> entry : formPicMap.entrySet()) {
                List<JDPicInfo> picInfoList = entry.getValue();

                for (JDPicInfo jdPicInfo : picInfoList) {

                    if (!Utils.isEmpty(jdPicInfo.getFilePath()) && jdPicInfo.getIsAddFlag() != 1&&(Utils.isEmpty(jdPicInfo.getImageUrl()))) {

                        taskPool.addTask(new PicYasuo(jdPicInfo,mContext,listener));
                        taskPool.addTask(new UpLoadFormPicAPI(userInfo.getAccess_token(), jdPicInfo, listener));
                    }
                }


            }


            taskPool.addTask(new SaveResDataAPI(userInfo.getAccess_token(),resDataBean,formPicMap,listener));



        if (sportEditFlag) {
            lpd.setMessage("正在同步新增的资源,请稍等...");
            lpd.show();

        } else {
            lpd.setMessage("正在同步修改的资源,请稍等...");
            lpd.show();
        }
        taskPool.execute(listener);

    }

    private void getFormPicMap(List<DongTaiFormBean> formBeans) {

        for (int i = 0; i < formBeans.size(); i++) {
            DongTaiFormBean formBean = formBeans.get(i);

            if (formBean.getPic() != null && formBean.getPic().size() > 0) {
                formPicMap.put(formBean.getJavaField(), formBean.getPic());

            }else {

//                if (formBean.getShowType().equals("image")) {
//
//                    if (!Utils.isEmpty(formBean.getPropertyLabel())) {
//                        String[] split = formBean.getPropertyLabel().split(",");
//                        List<JDPicInfo> picInfos = new ArrayList<>();
//
//                        for (int k = 0; k < split.length; k++) {
//
//                            JDPicInfo jdPicInfo = new JDPicInfo();
//
//                            jdPicInfo.setImageUrl(split[k]);
//                            jdPicInfo.setIsDownLoad("true");
//                            jdPicInfo.setIsAddFlag(0);
//
//                            picInfos.add(jdPicInfo);
//                        }
//                        if (picInfos != null && picInfos.size() > 0) {
//
//                            formPicMap.put(formBean.getJavaField(), picInfos);
//                        }
//
//
//                    }
//
//
//                }

            }

            if (formBean.getCgformFieldList() != null && formBean.getCgformFieldList().size() > 0) {

                List<DongTaiFormBean> childList = formBean.getCgformFieldList();

                for (int j = 0; j < childList.size(); j++) {

                    DongTaiFormBean childbean = childList.get(j);

                    if (childbean.getPic() != null && childbean.getPic().size() > 0) {
                        formPicMap.put(childbean.getJavaField(), childbean.getPic());

                    }else {

                        if (childbean.getShowType().equals("image")) {

                            if (!Utils.isEmpty(childbean.getPropertyLabel())) {
                                String[] split = childbean.getPropertyLabel().split(",");
                                List<JDPicInfo> picInfos = new ArrayList<>();

                                for (int k = 0; k < split.length; k++) {

                                    JDPicInfo jdPicInfo = new JDPicInfo();

                                    jdPicInfo.setImageUrl(split[k]);
                                    jdPicInfo.setIsDownLoad("true");
                                    jdPicInfo.setIsAddFlag(0);

                                    picInfos.add(jdPicInfo);
                                }
                                if (picInfos != null && picInfos.size() > 0) {

                                    formPicMap.put(childbean.getJavaField(), picInfos);
                                }


                            }


                        }


                    }



                }

            }

        }

    }


    /**
     * 判断用户登录状态，登录获取用户信息
     */
    private UserInfo userInfo;

    public boolean isLogin() {
        if (userInfo != null)
            return true;
        try {
            String userPath = Values.SDCARD_FILE(Values.SDCARD_CACHE) + Values.CACHE_USER;
            Object obj = ACache.get(new File(userPath)).getAsObject(Values.CACHE_USERINFO);
            if (obj != null) {
                userInfo = (UserInfo) obj;
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    private HashMap<String, List<DictionaryBean>> cocalmap = new HashMap<>();

    private DongTaiFormBean takePhotoFormBean;


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            case 6:
                //地图
                if (resultCode == 1) {

                    DongTaiFormBean dongTaiFormBean = (DongTaiFormBean) data.getSerializableExtra("data");

                    if (dongTaiFormBean != null) {

                        for (DongTaiFormBean bean : formBeanList) {

                            if (bean.getJavaField().equals(dongTaiFormBean.getJavaField())) {
                                bean.setPropertyLabel(dongTaiFormBean.getPropertyLabel());

                            }

                            List<DongTaiFormBean> childList = bean.getCgformFieldList();

                            if (childList != null && childList.size() > 0) {

                                for (DongTaiFormBean child : childList) {

                                    if (child.getJavaField().equals(dongTaiFormBean.getJavaField())) {
                                        child.setPropertyLabel(dongTaiFormBean.getPropertyLabel());

                                    }
                                }
                            }
                        }

                        biaoGeRecyclerAdapter.notifyDataSetChanged();

                    }

                }
                break;

            //图片；
            case 1:
                if (resultCode == 1) {
                    String picPath = data.getStringExtra("picPath");

                    takePhotoFormBean= (DongTaiFormBean) data.getSerializableExtra("bean");
                    Uri mUri;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        mUri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".fileprovider", new File(picPath));
                    } else {
                        mUri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".fileprovider", new File(picPath));
                    }
                    startPhotoZoom(mUri,takePhotoFormBean);
                } else if (resultCode == 2) {
                    List<ImageItem> picPaths = (List<ImageItem>) data.getSerializableExtra("picPath");
                    DongTaiFormBean formBean= (DongTaiFormBean) data.getSerializableExtra("bean");

                    List<JDPicInfo> picList = new ArrayList<>();
                    for (ImageItem item : picPaths) {
                        String cameraPath = item.imagePath;

                        String cameraName =item.imageId+ System.currentTimeMillis() + ".jpg";

                        JDPicInfo picBean = new JDPicInfo(0, cameraName, cameraPath, null, 0, "false");
                        picList.add(picBean);

                    }

                    List<JDPicInfo> localpiclist = formBean.getPic();

                    if (localpiclist != null && localpiclist.size()> 0) {

                        picList.addAll(localpiclist);

                    }


                    if (formBean != null) {

                        for (DongTaiFormBean bean : formBeanList) {

                            if (bean.getJavaField().equals(formBean.getJavaField())) {
                                bean.setPic(picList);
                                formPicMap.put(bean.getJavaField(), picList);
                            }


                            List<DongTaiFormBean> childList = bean.getCgformFieldList();

                            if (childList != null && childList.size() > 0) {

                                for (DongTaiFormBean child : childList) {

                                    if (child.getJavaField().equals(formBean.getJavaField())) {
                                        child.setPic(picList);
                                        formPicMap.put(child.getJavaField(), picList);

                                    }
                                }
                            }
                        }

                    }

                    biaoGeRecyclerAdapter.notifyDataSetChanged();
                }
                break;

            case 3:
                setPicToView(data,takePhotoFormBean);

                break;

        }
    }

    Uri uritempFile;
    private String tempPicFilePath= Values.SDCARD_FILE(Values.SDCARD_PIC) ;

    /**
     * 保存裁剪之后的图片数据
     *
     * @param picdata
     */
    private void setPicToView(Intent picdata,DongTaiFormBean formBean) {
        if (!Utils.isEmpty(uritempFile.toString())) {

            try {
                String picName = System.currentTimeMillis() + ".jpg";
                Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(uritempFile));

                String path = tempPicFilePath ;

                CCM_Bitmap.getBitmapToFile(bitmap, path + picName);
                JDPicInfo bean = new JDPicInfo(0, picName, path + picName, null, 0, "false");
//                resPicList.add(0, bean);
//                picAdapter.notifyDataSetChanged();

                List<JDPicInfo> picList = new ArrayList<>();

                picList.add(bean);

                List<JDPicInfo> localpiclist = formBean.getPic();

                if (localpiclist != null && localpiclist.size()> 0) {

                    picList.addAll(localpiclist);

                }


                if (formBean != null) {

                    for (DongTaiFormBean bean1 : formBeanList) {

                        if (bean1.getJavaField().equals(formBean.getJavaField())) {
                            bean1.setPic(picList);
                            formPicMap.put(bean1.getJavaField(), picList);

                        }

                        List<DongTaiFormBean> childList = bean1.getCgformFieldList();

                        if (childList != null && childList.size() > 0) {

                            for (DongTaiFormBean child : childList) {

                                if (child.getJavaField().equals(formBean.getJavaField())) {
                                    child.setPic(picList);
                                    formPicMap.put(child.getJavaField(), picList);

                                }
                            }
                        }
                    }

                }

                biaoGeRecyclerAdapter.notifyDataSetChanged();



            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 裁剪图片方法实现
     *
     * @param uri
     */
    public void startPhotoZoom(Uri uri,DongTaiFormBean formBean) {
        /*
         * 至于下面这个Intent的ACTION是怎么知道的，大家可以看下自己路径下的如下网页
         * yourself_sdk_path/docs/reference/android/content/Intent.html
         * 直接在里面Ctrl+F搜：CROP ，之前小马没仔细看过，其实安卓系统早已经有自带图片裁剪功能,
         * 是直接调本地库的，小马不懂C C++  这个不做详细了解去了，有轮子就用轮子，不再研究轮子是怎么
         * 制做的了...吼吼
         */
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.setDataAndType(uri, "image/*");
        //下面这个crop=true是设置在开启的Intent中设置显示的VIEW可裁剪
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 500);
        intent.putExtra("outputY", 500);
        intent.putExtra("return-data", true);
        //裁剪后的图片Uri路径，uritempFile为Uri类变量
        uritempFile = Uri.parse("file://" + "/" + Environment.getExternalStorageDirectory().getPath() + "/" + "wodeIcon.jpg");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uritempFile);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        startActivityForResult(intent, 3);
    }

}
