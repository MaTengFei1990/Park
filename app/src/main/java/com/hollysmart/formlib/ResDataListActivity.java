package com.hollysmart.formlib;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.reflect.TypeToken;
import com.hollysmart.apis.GetResModelAPI;
import com.hollysmart.apis.ResModelListAPI;
import com.hollysmart.beans.JDPicInfo;
import com.hollysmart.beans.ResModelBean;
import com.hollysmart.db.JDPicDao;
import com.hollysmart.db.ProjectDao;
import com.hollysmart.db.ResDataDao;
import com.hollysmart.db.ResModelDao;
import com.hollysmart.db.UserInfo;
import com.hollysmart.dialog.LoadingProgressDialog;
import com.hollysmart.formlib.activitys.NewAddFormResDataActivity;
import com.hollysmart.formlib.adapters.ResDataManageAdapter;
import com.hollysmart.formlib.apis.GetNetResListAPI;
import com.hollysmart.formlib.apis.SaveResTaskAPI;
import com.hollysmart.formlib.apis.getResTaskListAPI;
import com.hollysmart.formlib.beans.DongTaiFormBean;
import com.hollysmart.formlib.beans.ProjectBean;
import com.hollysmart.formlib.beans.ResDataBean;
import com.hollysmart.park.R;
import com.hollysmart.style.StyleAnimActivity;
import com.hollysmart.utils.ACache;
import com.hollysmart.utils.CCM_DateTime;
import com.hollysmart.utils.Mlog;
import com.hollysmart.utils.Utils;
import com.hollysmart.value.Values;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ResDataListActivity extends StyleAnimActivity {

    @Override
    public int layoutResID() {
        return R.layout.activity_res_data_list;
    }

    @BindView(R.id.ib_back)
    ImageView ib_back;

    @BindView(R.id.iv_maplsit)
    ImageView iv_maplsit;

    @BindView(R.id.lv_jingdian)
    ListView lv_jingdian;

    @BindView(R.id.bn_add)
    LinearLayout bn_add;


    private List<JDPicInfo> picList; // 当前景点图片集
    private List<String> soundList; // 当前景点录音集

    private ProjectBean projectBean;

    @Override
    public void findView() {
        ButterKnife.bind(this);
        ib_back.setOnClickListener(this);
        iv_maplsit.setOnClickListener(this);
        bn_add.setOnClickListener(this);
    }

    private List<ResDataBean> mJingDians;
    private ResDataManageAdapter resDataManageAdapter;
    Map<String, String> map = new HashMap<String, String>();
    private HashMap<String, List<JDPicInfo>> formPicMap = new HashMap<String, List<JDPicInfo>>();

    boolean ischeck = false; //是否只能查看 true  只能查看不能编辑；

    private List<ResModelBean> resModelList = new ArrayList<ResModelBean>();
    private List<DongTaiFormBean> formBeanList = new ArrayList<>();// 当前资源的动态表单

    private LoadingProgressDialog lpd;

    @Override
    public void init() {
        isLogin();
        picList = new ArrayList<>();
        soundList = new ArrayList<>();
        mJingDians = new ArrayList<>();


        map = (Map<String, String>) getIntent().getSerializableExtra("exter");

        ischeck = getIntent().getBooleanExtra("ischeck", false);
        setLpd();
        selectDB();
    }



    private void setLpd() {
        lpd = new LoadingProgressDialog();
        lpd.setMessage("正在保存，请稍等...");
        lpd.create(this, lpd.STYLE_SPINNER);
        lpd.setCancelable(false);
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ib_back:
                finish();
                break;
            case R.id.iv_maplsit:
                Intent mapintent = new Intent(mContext, ResListShowOnMapActivity.class);
                mapintent.putExtra("projectBean", projectBean);
                mapintent.putExtra("exter", (Serializable) map);
                mapintent.putExtra("ischeck", ischeck);
                startActivityForResult(mapintent, 6);
                break;

            case R.id.bn_add:
                Intent intent = new Intent(mContext, NewAddFormResDataActivity.class);

                ResDataBean resDataBean = new ResDataBean();
                String createTime = new CCM_DateTime().Datetime2();
                resDataBean.setId(System.currentTimeMillis() + "");
                resDataBean.setFdTaskId(projectBean.getId());
                resDataBean.setFd_restaskname(projectBean.getfTaskname());
                resDataBean.setFd_resmodelid(resModelList.get(0).getId());
                resDataBean.setCreatedAt(createTime);
                resDataBean.setFd_resdate(createTime);
                resDataBean.setFd_resmodelname(resModelList.get(0).getfModelName());

                intent.putExtra("formBeanList", (Serializable) formBeanList);
                intent.putExtra("resDataBean", resDataBean);
                intent.putExtra("sportEditFlag", true);
                formPicMap.clear();
                intent.putExtra("formPicMap", (Serializable) formPicMap);

                startActivityForResult(intent, 4);
                break;
        }
    }


    // 查询
    private void selectDB() {
        mJingDians.clear();
        if (map != null && map.size() > 0) {
            lpd.setMessage("请求数据中...请稍后");
            lpd.show();
            if (ischeck) {
                bn_add.setVisibility(View.GONE);

                getResTaskById();

            } else {

                createResTask();

            }


        }


    }

    private void createResTask() {
        map.toString();

        ProjectBean newprojectBean = new ProjectBean();

        newprojectBean.setRemarks("");
        newprojectBean.setfTaskname(map.get("name"));
        newprojectBean.setfTaskmodel(map.get("type"));
        newprojectBean.setfBegindate(map.get("btime"));
        newprojectBean.setfEnddate(map.get("etime"));
        newprojectBean.setfState("2");
        newprojectBean.setfRange("");
        newprojectBean.setId(map.get("id"));
        newprojectBean.setfOfficeId(userInfo.getOffice().getId());
        newprojectBean.setfTaskmodelnames(map.get("typename"));
        newprojectBean.setfDescription("");


        new SaveResTaskAPI(userInfo.getAccess_token(), newprojectBean, new SaveResTaskAPI.SaveResTaskIF() {
            @Override
            public void onSaveResTaskResult(boolean isOk, ProjectBean projectBean1) {

                if (isOk) {

                    getResTaskById();

                }

            }
        }).request();
    }

    private void getResTaskById() {
        new getResTaskListAPI(userInfo.getAccess_token(), map.get("id"),map.get("unitid"), 10000, new getResTaskListAPI.ResTaskListIF() {
            @Override
            public void onResTaskListResult(boolean isOk, List<ProjectBean> projectBeanList, int count,String msg) {

                if (isOk) {
                    if (projectBeanList != null && projectBeanList.size() > 0) {

                        projectBean = projectBeanList.get(0);

                        new GetResModelAPI(userInfo.getAccess_token(), projectBean.getfTaskmodel(), new GetResModelAPI.GetResModelIF() {
                            @Override
                            public void ongetResModelIFResult(boolean isOk, ResModelBean resModelBen) {

                                if (isOk) {//获取到网络数据
                                    lpd.cancel();

                                    ResModelDao resModelDao = new ResModelDao(mContext);
                                    resModelDao.addOrUpdate(resModelBen);
                                    String getfJsonData = resModelBen.getfJsonData();
                                    Gson mGson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm").create();
                                    List<DongTaiFormBean> newFormList = mGson.fromJson(getfJsonData, new TypeToken<List<DongTaiFormBean>>() {
                                    }.getType());

                                    resDataManageAdapter = new ResDataManageAdapter(mContext, mJingDians, picList, soundList, projectBean, newFormList, ischeck);
                                    lv_jingdian.setAdapter(resDataManageAdapter);
                                    selectDB(projectBean.getId());

                                }


                            }
                        }).request();


                    }


                } else {
                    lpd.cancel();
                    if (!Utils.isEmpty(msg)) {
                        Utils.showDialog(mContext,msg);
                    }
                    findViewById(R.id.bn_add).setBackgroundColor(getResources().getColor(R.color.hui_q));
                    findViewById(R.id.bn_add).setEnabled(false);
                }

            }
        }).request();
    }


    // 查询
    private void selectDB(final String jqId) {
        Mlog.d("jqId = " + jqId);
        mJingDians.clear();

        resModelList.clear();

        String classifyIds = projectBean.getfTaskmodel();
        if (classifyIds != null) {

            String[] ids = classifyIds.split(",");
            ResModelDao resModelDao = new ResModelDao(mContext);
            for (int i = 0; i < ids.length; i++) {
                ResModelBean resModelBean = resModelDao.getDatById(ids[i]);
                if (resModelBean != null) {

                    resModelList.add(resModelBean);
                }
            }


        }

        if (resModelList == null || resModelList.size() == 0) {

            new ResModelListAPI(userInfo.getAccess_token(), new ResModelListAPI.ResModelListIF() {
                @Override
                public void onResModelListResult(boolean isOk, List<ResModelBean> projectBeanList) {

                    if (isOk) {
                        ResModelDao resModelDao = new ResModelDao(mContext);
                        resModelList.clear();
                        resModelList.addAll(projectBeanList);
                        resModelDao.addOrUpdate(resModelList);

                        ResModelBean resModelBean = resModelList.get(0);


                        String formData = resModelBean.getfJsonData();
                        formBeanList.clear();
                        try {
                            Gson mGson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm").create();
                            List<DongTaiFormBean> dictList = mGson.fromJson(formData,
                                    new TypeToken<List<DongTaiFormBean>>() {
                                    }.getType());
                            formBeanList.addAll(dictList);
                        } catch (JsonIOException e) {
                            e.printStackTrace();
                        }

                        JDPicDao jdPicDao = new JDPicDao(mContext);
                        for (int i = 0; i < mJingDians.size(); i++) {
                            ResDataBean resDataBean = mJingDians.get(i);

                            List<JDPicInfo> jdPicInfoList = jdPicDao.getDataByJDId(resDataBean.getId() + "");

                            resDataBean.setJdPicInfos(jdPicInfoList);

                        }


                        new GetNetResListAPI(userInfo, projectBean, new GetNetResListAPI.DatadicListIF() {
                            @Override
                            public void datadicListResult(boolean isOk, List<ResDataBean> netDataList) {


                                List<String> idList = new ArrayList<>();

                                for (ResDataBean resDataBean : mJingDians) {

                                    idList.add(resDataBean.getId());
                                }


                                if (isOk) {
                                    if (netDataList != null && netDataList.size() > 0) {
                                        int j = 0;

                                        for (int i = 0; i < netDataList.size(); i++) {

                                            ResDataBean resDataBean = netDataList.get(i);

                                            if (!idList.contains(resDataBean.getId())) {
                                                String fd_resposition = resDataBean.getFd_resposition();

                                                if (!Utils.isEmpty(fd_resposition)) {

                                                    String[] split = fd_resposition.split(",");
                                                    resDataBean.setLatitude(split[0]);
                                                    resDataBean.setLongitude(split[1]);

                                                }


                                                mJingDians.add(resDataBean);

                                                j = j + 1;

                                                projectBean.setNetCount(10);
                                            }
                                        }

                                        new ProjectDao(mContext).addOrUpdate(projectBean);
                                        ProjectBean dataByID = new ProjectDao(mContext).getDataByID(projectBean.getId());

                                        dataByID.getNetCount();
                                    }
                                }


                                resDataManageAdapter.notifyDataSetChanged();


                            }
                        }).request();


                    }


                }
            }).request();


        } else {
            ResModelBean resModelBean = resModelList.get(0);


            String formData = resModelBean.getfJsonData();
            formBeanList.clear();
            try {
                Gson mGson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm").create();
                List<DongTaiFormBean> dictList = mGson.fromJson(formData,
                        new TypeToken<List<DongTaiFormBean>>() {
                        }.getType());
                formBeanList.addAll(dictList);
            } catch (JsonIOException e) {
                e.printStackTrace();
            }


            ResDataDao resDataDao = new ResDataDao(getApplication());
            List<ResDataBean> resDataBeans = resDataDao.getData(jqId + "");
            if (resDataBeans != null && resDataBeans.size() > 0) {

                mJingDians.addAll(resDataBeans);
            }


            JDPicDao jdPicDao = new JDPicDao(mContext);
            for (int i = 0; i < mJingDians.size(); i++) {
                ResDataBean resDataBean = mJingDians.get(i);

                List<JDPicInfo> jdPicInfoList = jdPicDao.getDataByJDId(resDataBean.getId() + "");

                resDataBean.setJdPicInfos(jdPicInfoList);

            }


            new GetNetResListAPI(userInfo, projectBean, new GetNetResListAPI.DatadicListIF() {
                @Override
                public void datadicListResult(boolean isOk, List<ResDataBean> netDataList) {


                    List<String> idList = new ArrayList<>();

                    for (ResDataBean resDataBean : mJingDians) {

                        idList.add(resDataBean.getId());
                    }


                    if (isOk) {
                        if (netDataList != null && netDataList.size() > 0) {
                            int j = 0;

                            for (int i = 0; i < netDataList.size(); i++) {

                                ResDataBean resDataBean = netDataList.get(i);

                                if (!idList.contains(resDataBean.getId())) {
                                    String fd_resposition = resDataBean.getFd_resposition();

                                    if (!Utils.isEmpty(fd_resposition)) {

                                        String[] split = fd_resposition.split(",");
                                        resDataBean.setLatitude(split[0]);
                                        resDataBean.setLongitude(split[1]);

                                    }


                                    mJingDians.add(resDataBean);

                                    j = j + 1;

                                    projectBean.setNetCount(10);
                                }
                            }

                            new ProjectDao(mContext).addOrUpdate(projectBean);
                            ProjectBean dataByID = new ProjectDao(mContext).getDataByID(projectBean.getId());

                            dataByID.getNetCount();
                        }
                    }


                    resDataManageAdapter.notifyDataSetChanged();


                }
            }).request();
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


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 4) {

            if (resultCode == 1) {


                selectDB(projectBean.getId());

            }
        }
        //在地图页面修改了地图修改
        if (requestCode == 6) {

            if (resultCode == 1) {


                selectDB(projectBean.getId());

            }
        }
    }


}
