package com.hollysmart.dialog;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.content.FileProvider;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.model.LatLng;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.hollysmart.apis.SaveResDataAPI;
import com.hollysmart.apis.UpLoadFormPicAPI;
import com.hollysmart.apis.UpLoadSoundAPI;
import com.hollysmart.beans.DongTaiFormBean;
import com.hollysmart.beans.FormModelBean;
import com.hollysmart.beans.ProjectBean;
import com.hollysmart.beans.ResDataBean;
import com.hollysmart.beans.JDPicInfo;
import com.hollysmart.beans.ResModelBean;
import com.hollysmart.beans.SoundInfo;
import com.hollysmart.db.JDPicDao;
import com.hollysmart.db.JDSoundDao;
import com.hollysmart.db.ResDataDao;
import com.hollysmart.db.ResModelDao;
import com.hollysmart.db.UserInfo;
import com.hollysmart.park.BigPicActivity;
import com.hollysmart.park.BuildConfig;
import com.hollysmart.park.Cai_AddPicActivity;
import com.hollysmart.park.DynamicFormActivity;
import com.hollysmart.park.Ma_ScanActivity;
import com.hollysmart.park.R;
import com.hollysmart.park.RecordListActivity;
import com.hollysmart.utils.ACache;
import com.hollysmart.utils.CCM_Bitmap;
import com.hollysmart.utils.CCM_DateTime;
import com.hollysmart.utils.FileTool;
import com.hollysmart.utils.PicYasuo;
import com.hollysmart.utils.Utils;
import com.hollysmart.utils.loctionpic.ImageItem;
import com.hollysmart.utils.taskpool.OnNetRequestListener;
import com.hollysmart.utils.taskpool.TaskPool;
import com.hollysmart.value.Values;
import com.hollysmart.views.linearlayoutforlistview.LinearLayoutBaseAdapter;
import com.hollysmart.views.linearlayoutforlistview.MyLinearLayoutForListView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by Lenovo on 2018/12/3.
 */


public class SheetDialogFragment extends BottomSheetDialogFragment {
    private BottomSheetBehavior mBehavior;
    private final int MAXNUM = 9;

    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 100;

    private List<ResModelBean> resModelList = new ArrayList<ResModelBean>();

    private List<JDPicInfo> resPicList; // 当前景点图片集

    private int sheetHeight;

    private int scope = 10;//范围

    private ResDataBean resDataBean;//当前新添加的资源实体

    private List<DongTaiFormBean> formBeanList;// 当前资源的动态表单

    private List<DongTaiFormBean> tempformBeanList;// 当前资源的动态表单

    private String sureResModelId = "";


    private ProjectBean projectBean;//当前项目

    private ResModelBean selectResModel; //当前项目的分类bean

    private TextView text_fenwei;
    private TextView tv_jingdianWeizi;
    private EditText ed_resouseNumber;
    private EditText ed_jingdianName;
    private EditText et_remark;

    private LatLng dingWeiDian;


    private boolean sportEditFlag = false; // ture 新添加 false 修改
    private Context mContext;


    private PicAdapter picAdapter;

    private ZiYuanAdapter ziYuanAdapter;

    private SeeBarRangeListener seeBarRangeListener;
    private DismissListener dismissListener;

    private LoadingProgressDialog lpd;


    private List<SoundInfo> currentSoundList = new ArrayList<>();//本地已有的录音
    private List<SoundInfo> netaudios=new ArrayList<>();// 网络请求获取得到的录音

    private List<SoundInfo> audios=new ArrayList<>();// 当前资源所有的录音


    private File TempSoundfile;  // 音频的临时文件夹
    private File creenetSoundFile;  // 音频的临时文件夹

    private String tempPicFilePath= Values.SDCARD_FILE(Values.SDCARD_PIC) ;


    private List<SoundInfo> deletlist = new ArrayList<>();

    private List<JDPicInfo> deletPicList = new ArrayList<>();

    private String resDataName;


    private static JDPicInfo picBeannull = new JDPicInfo(0, null, null, null, 1, "false");

    private static SheetDialogFragment single = new SheetDialogFragment();


    // 静态工厂方法
    public static SheetDialogFragment getInstance() {
        return single;
    }

    public void setSeeBarRangeListener(SeeBarRangeListener seeBarRangeListener) {
        this.seeBarRangeListener = seeBarRangeListener;
    }

    public void setDismissListener(DismissListener dismissListener) {
        this.dismissListener = dismissListener;
    }


    public static class Builder {

        public Builder(Context context) {
        }

        public SheetDialogFragment setSportEditFlag(boolean sportEditFlag) {
            single.sportEditFlag = sportEditFlag;//保存title到P中
            return single;
        }

        public SheetDialogFragment setCurrentProJectBean(ProjectBean projectBean) {
            single.projectBean = projectBean;//
            return single;
        }

        public SheetDialogFragment setResDataBean(ResDataBean resDataBean) {
            single.resDataBean = resDataBean;//当前的ResData
            return single;
        }

        public SheetDialogFragment setDingWeiDian(LatLng dingWeiDian) {
            single.dingWeiDian = dingWeiDian;//保存title到P中
            return single;
        }


    }


    public void setlongitudeAndlatitude(String longitude, String latitude) {

        if (tv_jingdianWeizi != null) {
            tv_jingdianWeizi.setText("经:"+saveEightLevel(longitude) + ",纬:" + saveEightLevel(latitude));

        }
    }

    private BottomSheetBehavior.BottomSheetCallback mBottomSheetBehaviorCallback
            = new BottomSheetBehavior.BottomSheetCallback() {

        @Override
        public void onStateChanged(@NonNull View bottomSheet, int newState) {
        }

        @Override
        public void onSlide(@NonNull View bottomSheet, float slideOffset) {

        }
    };


    @Override
    public void show(android.support.v4.app.FragmentManager manager, String tag) {
        try {
            //在每个add事务前增加一个remove事务，防止连续的add
            manager.beginTransaction().remove(this).commit();
            super.show(manager, tag);
        } catch (Exception e) {
            //同一实例使用不同的tag会异常,这里捕获一下
            e.printStackTrace();
        }
    }

    private TextView tv_recordCount;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        isLogin();
        setLpd();
        currentSoundList.clear();
        Window mWindow = this.getActivity().getWindow();
        WindowManager.LayoutParams lp = mWindow.getAttributes();
        lp.dimAmount = 1f;
//        lp.alpha = 0.1f;//参数为0到1之间。0表示完全透明，1就是不透明。按需求调整参数
        mWindow.setAttributes(lp);
        setStyle(BottomSheetDialogFragment.STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme);
        mContext = getContext();
        BottomSheetDialog dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);
        final View view = View.inflate(mContext, R.layout.view_add_jingdian, null);
        MyLinearLayoutForListView ll_jingDianFenLei = view.findViewById(R.id.ll_jingDianFenLei);
        MyLinearLayoutForListView ll_jingdian_pic = view.findViewById(R.id.ll_jingdian_pic);
        ImageButton bt_luyin = view.findViewById(R.id.bt_luyin);
        LinearLayout iv_more = view.findViewById(R.id.ll_more);
        ImageView iv_del = view.findViewById(R.id.iv_del);
        tv_recordCount = view.findViewById(R.id.tv_recordCount);

        ed_resouseNumber = view.findViewById(R.id.ed_resouseNumber);
        tv_jingdianWeizi = view.findViewById(R.id.tv_jingdianWeizi);
        ed_jingdianName = view.findViewById(R.id.ed_jingdianName);
        text_fenwei = view.findViewById(R.id.text_fenwei);
        SeekBar seekBar_fenwei = view.findViewById(R.id.seekBar_fenwei);
        et_remark = view.findViewById(R.id.et_remark);

        resModelList.clear();

        String classifyIds = projectBean.getfTaskmodel();
        if (classifyIds != null) {

            String[] ids = classifyIds.split(",");
            ResModelDao resModelDao = new ResModelDao(mContext);
            for(int i=0;i<ids.length;i++) {
                ResModelBean resModelBean = resModelDao.getDatById(ids[i]);
                if (resModelBean != null) {

                    resModelList.add(resModelBean);
                }
            }


        }
        picfile = Values.SDCARD_FILE(Values.SDCARD_FILE)  + projectBean.getfTaskname() + "/"
                + Values.SDCARD_PIC + "/" ;

        if (resModelList!=null&&resModelList.size() == 1) {
            resModelList.get(0).setSelect(true);
            selectResModel = resModelList.get(0);
        }

        resPicList = new ArrayList<>();
        resPicList.clear();
        resPicList.add(picBeannull);

        formBeanList = new ArrayList<>();
        tempformBeanList = new ArrayList<>();


        if (dingWeiDian != null) {

            tv_jingdianWeizi.setText("经:"+saveEightLevel(dingWeiDian.longitude+"") + ",纬:" + saveEightLevel(dingWeiDian.latitude+""));
        }

        //创建音频临时文件夹
        TempSoundfile = CreateDir(Values.SDCARD_FILE(Values.SDCARD_FILE) + "/" + projectBean.getfTaskname() + "/"
                + Values.SDCARD_SOUNDS + "/" +"templefile" + "");

        File[] files = TempSoundfile.listFiles();
        for (File file : files) {
            file.delete();
        }

        netaudios.clear();




        //修改
        if (!sportEditFlag) {
            if (!Utils.isEmpty(resDataBean.getNumber())) {

                ed_resouseNumber.setText(resDataBean.getNumber());
            }
            if (!Utils.isEmpty(resDataBean.getFd_resname())) {

                ed_jingdianName.setText(resDataBean.getFd_resname());
                resDataName = resDataBean.getFd_resname();
            }
            if (!Utils.isEmpty(resDataBean.getNote())) {

                et_remark.setText(resDataBean.getNote());
            }
            seekBar_fenwei.setProgress(resDataBean.getScope()-10);
            text_fenwei.setText("当前范围：" + resDataBean.getScope() + "米");



            creenetSoundFile = CreateDir(Values.SDCARD_FILE(Values.SDCARD_FILE) + "/" + projectBean.getfTaskname() + "/"
                    + Values.SDCARD_SOUNDS + "/" +resDataBean.getFd_resname() + "");



            if (resDataBean.getPic() != null && resDataBean.getPic().size() > 0) {

                for (JDPicInfo jdPicInfo : resDataBean.getPic()) {
                    jdPicInfo.setIsAddFlag(0);
                }

                resPicList.addAll(0, resDataBean.getPic());


            } else {
                List<JDPicInfo> dataByJDId = new JDPicDao(getContext()).getDataByJDId(resDataBean.getId());
                for (JDPicInfo jdPicInfo : dataByJDId) {
                    jdPicInfo.setIsDownLoad("true");

                }
                resPicList.addAll(0, dataByJDId);

            }


            if (resDataBean.getAudio() != null && resDataBean.getAudio().size() > 0) {
                //网络获取
                netaudios.addAll(resDataBean.getAudio());
                audios.clear();
                audios.addAll(0, resDataBean.getAudio());
                if (audios != null && audios.size() > 0) {
                    tv_recordCount.setText("录音数量：" + audios.size() + "");
                } else {
                    tv_recordCount.setText("录音数量：" + 0 + "");

                    CreateDir(picfile);

                }

            } else {
                //本地加载
                List<SoundInfo> soundInfoList = new JDSoundDao(getContext()).getDataByJDId(resDataBean.getId());
                audios.clear();
                if (soundInfoList != null && soundInfoList.size() > 0) {

                    currentSoundList.clear();
                    currentSoundList.addAll(0, soundInfoList);
                    audios.addAll(currentSoundList);

                    if (audios != null && audios.size() > 0) {
                        tv_recordCount.setText("录音数量：" + audios.size() + "");

                    } else {
                        tv_recordCount.setText("录音数量：" + 0 + "");

                    }
                }

                CreateDir(picfile);
            }




            for(int i = 0; i< resModelList.size(); i++) {
                if (!Utils.isEmpty(resDataBean.getCategoryId())) {
                    if (resModelList.get(i).getId().equals(resDataBean.getCategoryId())) {
                        resModelList.get(i).setSelect(true);
                        selectResModel = resModelList.get(i);

                    }

                }


            }

            String formData = resDataBean.getFormData();

            formBeanList.clear();
            tempformBeanList.clear();

            try {
                JSONObject jsonObject = null;
                jsonObject = new JSONObject(formData);
                Gson mGson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm").create();
                List<DongTaiFormBean> dictList = mGson.fromJson(jsonObject.getString("cgformFieldList"),
                        new TypeToken<List<DongTaiFormBean>>() {}.getType());
                formBeanList.addAll(dictList);
                tempformBeanList.addAll(dictList);
                sureResModelId = resDataBean.getFd_resmodelid();
            } catch (JSONException e) {
                e.printStackTrace();
            }





        } else {
            //
            resDataBean = new ResDataBean();
            formBeanList = new ArrayList<>();
            tempformBeanList = new ArrayList<>();

            CreateDir(picfile);


        }


        view.findViewById(R.id.ll_add_jingdian).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (sportEditFlag) {
                    addRes();
                } else {
                    updateJD();
                }


            }
        });
        view.findViewById(R.id.iv_scan).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(), Ma_ScanActivity.class);
                Activity activity = (Activity) mContext;
                activity.startActivityForResult(intent, 5);
                dismiss();

            }
        });
        view.findViewById(R.id.ll_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dismiss();
                if (sportEditFlag) {
                    FileTool.deteleFiles(TempSoundfile);

                }


            }
        });

        iv_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                List<ResModelBean> list = new ArrayList<>();


                for (ResModelBean resModelBean : resModelList) {

                    if (resModelBean.isSelect()) {

                        list.add(resModelBean);
                    }
                }

                if (list != null && list.size() > 0) {

                    Intent intent = new Intent(mContext, DynamicFormActivity.class);

                    intent.putExtra("selectBean", list.get(0));
                    intent.putExtra("formBeanList", (Serializable) formBeanList);
                    startActivityForResult(intent, 4);
                } else {
                    Utils.showDialog(mContext,"请先选择分类");
                }


            }
        });
        iv_del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resDataBean = null;
                dismiss();
                if (sportEditFlag) {
                    FileTool.deteleFiles(TempSoundfile);

                }

            }
        });

        ziYuanAdapter = new ZiYuanAdapter(mContext, resModelList);
        ll_jingDianFenLei.setAdapter(ziYuanAdapter);

        picAdapter = new PicAdapter(mContext, resPicList);

        ll_jingdian_pic.setAdapter(picAdapter);

        bt_luyin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, RecordListActivity.class);
                intent.putExtra("projectbean", projectBean);
                intent.putExtra("TempSoundfile", TempSoundfile.getAbsolutePath());

                List<SoundInfo> bundSound = new ArrayList<>();
                if (netaudios != null && netaudios.size() > 0 ) {
                    bundSound.addAll(netaudios);

                }
                if (currentSoundList != null && currentSoundList.size() > 0) {
                    bundSound.addAll(currentSoundList);

                }

                intent.putExtra("netaudios", (Serializable) bundSound);
                startActivityForResult(intent,5);

            }
        });

        seekBar_fenwei.setMax(40);
        seekBar_fenwei.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                scope = progress+10;
                text_fenwei.setText("当前范围：" + scope + "米");
                seeBarRangeListener.onChange(progress);
            }
        });


        dialog.setContentView(view);
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        sheetHeight = (int) (mContext.getResources().getDisplayMetrics().heightPixels * 0.75);
        layoutParams.height = sheetHeight;
        view.setLayoutParams(layoutParams);


        mBehavior = BottomSheetBehavior.from((View) view.getParent());


        return dialog;


    }


    private void setLpd() {
        lpd = new LoadingProgressDialog();
        lpd.setMessage("正在保存，请稍等...");
        lpd.create(getContext(), lpd.STYLE_SPINNER);
        lpd.setCancelable(false);
    }








    private File CreateDir(String folder) {
        File dir = new File(folder);
        dir.mkdirs();
        return dir;
    }

    @Override
    public void onStart() {
        super.onStart();

        final View view = View.inflate(getContext(), R.layout.view_add_jingdian, null);
        int w = View.MeasureSpec.makeMeasureSpec(0,
                View.MeasureSpec.UNSPECIFIED);
        int h = View.MeasureSpec.makeMeasureSpec(0,
                View.MeasureSpec.UNSPECIFIED);
        view.measure(w, h);
        int height = view.getMeasuredHeight();

        mBehavior.setPeekHeight(height);
        mBehavior.setBottomSheetCallback(mBottomSheetBehaviorCallback);

    }

    public void doclick(View v) {
        //点击任意布局关闭
        mBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
    }


    // 添加资源 -- 确认添加
    private void addRes() {
        String resNum = ed_resouseNumber.getText().toString();
        if (Utils.isEmpty(resNum)) {
            Toast.makeText(mContext, "资源编号不能为空", Toast.LENGTH_LONG).show();
            return;
        }
        String jdName = ed_jingdianName.getText().toString();
        if (Utils.isEmpty(jdName)) {
            Toast.makeText(mContext, "资源名称不能为空", Toast.LENGTH_LONG).show();
            return;
        }

        if (selectResModel == null || !selectResModel.isSelect()) {
            Toast.makeText(mContext, "请选择项目分类", Toast.LENGTH_LONG).show();
            return;

        }

//        if (formBeanList == null || formBeanList.size() == 0) {
//            Toast.makeText(mContext, "请填写表单详情", Toast.LENGTH_LONG).show();
//
//            return;
//        } else {
//
//            boolean notfill = true;
//
//            for (DongTaiFormBean formBean : formBeanList) {
//
//                if (formBean.getFieldMustInput() && Utils.isEmpty(formBean.getPropertyLabel())) {
//                    notfill = false;
//                }
//
//                List<DongTaiFormBean> propertys = formBean.getPropertys();
//
//                if (propertys != null && propertys.size() > 0) {
//
//                    for (DongTaiFormBean property : propertys) {
//
//                        if (property.getFieldMustInput() && Utils.isEmpty(property.getPropertyLabel())) {
//                            notfill = false;
//                        }
//
//                    }
//
//
//
//                }
//
//            }
//
//            if (!notfill) {
//                Toast.makeText(mContext, "请将表单填写完整", Toast.LENGTH_LONG).show();
//                return;
//            }
//
//
//        }


        String createTime = new CCM_DateTime().Datetime2();
        String coordinate = dingWeiDian.latitude + "," + dingWeiDian.longitude;
        sportEditFlag = true;
        // 添加数据库

        resDataBean.setId(System.currentTimeMillis() + "");
        resDataBean.setFdTaskId(projectBean.getId());
        resDataBean.setNumber(resNum);
        resDataBean.setFd_resmodelid(selectResModel.getId());
        resDataBean.setFd_restaskname(projectBean.getfTaskname());
        resDataBean.setFd_resname(jdName);
        resDataBean.setNote(et_remark.getText().toString());
        resDataBean.setRescode(resNum);
        resDataBean.setScope(scope);
        resDataBean.setCreatedAt(createTime);
        resDataBean.setFd_resdate(createTime);
        resDataBean.setFd_resmodelname(selectResModel.getName());
        resDataBean.setFd_resposition(coordinate);
        resDataBean.setLatitude(dingWeiDian.latitude + "");
        resDataBean.setLongitude(dingWeiDian.longitude + "");
        resDataBean.setJdPicInfos(resPicList);

        FormModelBean formModelBean = new FormModelBean();

        formModelBean.setCgformFieldList(formBeanList);

        resDataBean.setFormModel(formModelBean);

        Gson gson = new Gson();
        String formBeanStr = gson.toJson(formModelBean);

        resDataBean.setFormData(formBeanStr);

        resDataBean.setAudio(audios);


        addDB(resDataBean);

        uploadResData(resDataBean.getId());

        jingdianGone();
    }


    // 修改景点

    private void updateJD() {
        String jdName = ed_jingdianName.getText().toString();
        if (Utils.isEmpty(jdName)) {
            Toast.makeText(mContext, "景点名称不能为空", Toast.LENGTH_LONG).show();
            return;
        }
        String createTime = new CCM_DateTime().Datetime2();

        String resNum = ed_resouseNumber.getText().toString();
        if (Utils.isEmpty(resNum)) {
            Toast.makeText(mContext, "资源编号不能为空", Toast.LENGTH_LONG).show();
            return;
        }

        if (formBeanList == null || formBeanList.size() == 0) {
            Toast.makeText(mContext, "请填写表单详情", Toast.LENGTH_LONG).show();
            return;
        } else {

            boolean notfill = true;

            for (DongTaiFormBean formBean : formBeanList) {

                if (formBean.getFieldMustInput() && Utils.isEmpty(formBean.getPropertyLabel())) {
                    notfill = false;
                }

                List<DongTaiFormBean> propertys = formBean.getPropertys();
                if (formBean.getPropertyLabel() != null) {
                    if (!formBean.getPropertyLabel().equals("否")) {
                        if (propertys != null && propertys.size() > 0) {

                            for (DongTaiFormBean property : propertys) {

                                if (property.getFieldMustInput() && Utils.isEmpty(property.getPropertyLabel())) {
                                    notfill = false;
                                }

                            }



                        }
                    }
                }



            }

            if (!notfill) {
                Toast.makeText(mContext, "请将表单填写完整", Toast.LENGTH_LONG).show();
                return;
            }


        }



        FormModelBean formModelBean = new FormModelBean();

        formModelBean.setCgformFieldList(formBeanList);

        resDataBean.setFormModel(formModelBean);

        Gson gson = new Gson();
        String formBeanStr = gson.toJson(formModelBean);

        resDataBean.setFormData(formBeanStr);


        resDataBean.setFdTaskId(projectBean.getId());
        resDataBean.setFd_restaskname(projectBean.getfTaskname());
        resDataBean.setFd_resmodelid(selectResModel.getId());
        resDataBean.setFd_resname(jdName);
        resDataBean.setNumber(resNum);
        resDataBean.setScope(scope);
        resDataBean.setRescode(resNum);
        resDataBean.setFd_resmodelname(selectResModel.getName());
        resDataBean.setFd_resdate(resDataBean.getCreatedAt());
        resDataBean.setNote(et_remark.getText().toString());
        resDataBean.setCreatedAt(createTime);
        resDataBean.setFd_resposition(resDataBean.getLatitude()+","+resDataBean.getLongitude());
        resDataBean.setJdPicInfos(resPicList);
        updateDb(resDataBean);
    }


    private void updateDb(ResDataBean resDataBean) {

        ResDataDao resDataDao = new ResDataDao(getActivity());

        JDPicDao jdPicDao = new JDPicDao(getActivity());
        JDSoundDao jdSoundDao = new JDSoundDao(getActivity());

//        if (resPicList != null && resPicList.size() > 0) {
//
//            for (JDPicInfo jdPicInfo : resPicList) {
//                if (jdPicInfo.getIsAddFlag() != 1) {
//
//                    jdPicInfo.setJdId(resDataBean.getId());
//                    jdPicInfo.setJqId(resDataBean.getFdTaskId());
//
//                    jdPicDao.addOrUpdate(jdPicInfo);
//                }
//            }
//        }
        savePicFile(jdPicDao);

        moveTemp2CurrentFile();


        if (audios != null) {
            jdSoundDao.deletByResId(resDataBean.getId());
            for (SoundInfo soundInfo : audios) {
                soundInfo.setJdId(resDataBean.getId());
                soundInfo.setJqId(resDataBean.getFdTaskId());
                jdSoundDao.addOrUpdate(soundInfo);
            }

        }

        resDataDao.addOrUpdate(resDataBean);

        uploadResData(resDataBean.getId());

        jingdianGone();

    }


    /**
     * 将资源上传到后台
     */
    private void uploadResData(String uuid) {
        final ResDataDao formDao = new ResDataDao(mContext);
        JDPicDao jdPicDao = new JDPicDao(mContext);
        JDSoundDao jdSoundDao = new JDSoundDao(mContext);
        final TaskPool taskPool = new TaskPool();

        OnNetRequestListener listener= new OnNetRequestListener() {
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

                        } else {
                            Utils.showDialog(mContext, "修改成功");
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



        List<ResDataBean> formBeens = formDao.getUuidDate(uuid);
        for (ResDataBean bean : formBeens) {

            List<JDPicInfo> picList = jdPicDao.getDataByJDId(bean.getId());


            for (JDPicInfo jdPicInfo : picList) {

                if (!Utils.isEmpty(jdPicInfo.getFilePath()) && Utils.isEmpty(jdPicInfo.getImageUrl())) {

                    taskPool.addTask(new PicYasuo(jdPicInfo, listener));
                }
            }

            for (JDPicInfo jdPicInfo : picList) {

                if (!Utils.isEmpty(jdPicInfo.getFilePath()) && jdPicInfo.getIsAddFlag() != 1&&(Utils.isEmpty(jdPicInfo.getImageUrl()))) {

                    taskPool.addTask(new UpLoadFormPicAPI(userInfo.getAccess_token(), jdPicInfo, listener));
                }
            }
            List<SoundInfo> soundInfoList = jdSoundDao.getDataByJDId(bean.getId());

            for (SoundInfo soundInfo : soundInfoList) {

                if (!Utils.isEmpty(soundInfo.getFilePath())&&(Utils.isEmpty(soundInfo.getAudioUrl()))) {
                    taskPool.addTask(new UpLoadSoundAPI(userInfo.getAccess_token(), soundInfo, listener));
                }
            }

            bean.setPic(picList);
            bean.setAudio(soundInfoList);




            taskPool.addTask(new SaveResDataAPI(userInfo.getAccess_token(),bean,listener));



        }
        if (sportEditFlag) {
            lpd.setMessage("正在同步新增的资源,请稍等...");
            lpd.show();

        } else {
            lpd.setMessage("正在同步修改的资源,请稍等...");
            lpd.show();
        }
        taskPool.execute(listener);

    }


    // 数据库操作
    private void addDB(ResDataBean resDataBean) {

        ResDataDao resDataDao = new ResDataDao(getActivity());
        JDPicDao jdPicDao = new JDPicDao(getActivity());
        JDSoundDao jdSoundDao = new JDSoundDao(getActivity());

        resDataDao.addOrUpdate(resDataBean);

        savePicFile(jdPicDao);


        moveTemp2CurrentFile();



        if (audios != null) {
            jdSoundDao.deletByResId(resDataBean.getId());
            for (SoundInfo soundInfo : audios) {
                soundInfo.setJdId(resDataBean.getId());
                soundInfo.setJqId(resDataBean.getFdTaskId());
                jdSoundDao.addOrUpdate(soundInfo);
            }

        }


    }

    /***
     * 保存照片资源文件到
     */

    private void savePicFile( JDPicDao jdPicDao ) {


        picfile = Values.SDCARD_FILE(Values.SDCARD_FILE)  + projectBean.getfTaskname() + "/"
                + Values.SDCARD_PIC + "/"+resDataBean.getFd_resname() + "/" ;

        jdPicDao.deletByResId(resDataBean.getId());





        for(int i=0;i<deletPicList.size();i++) {

            JDPicInfo deletPicInfo = deletPicList.get(i);

            String filePath = deletPicInfo.getFilePath();

            File file = new File(filePath);

            FileTool.deteleFiles(file);



        }


        File picFile = CreateDir(picfile);
        picFile.delete();
        File file = CreateDir(picfile);
        if (file.exists()) {

            if (resPicList != null && resPicList.size() > 0) {
                for (int i = 0; i < resPicList.size(); i++) {
                    JDPicInfo jdPicInfo = resPicList.get(i);
                    if (jdPicInfo.getIsAddFlag() != 1) {
                        jdPicInfo.setJdId(resDataBean.getId());
                        jdPicInfo.setJqId(resDataBean.getFdTaskId());

                        Bitmap bm = BitmapFactory.decodeFile(jdPicInfo.getFilePath());
                        if (bm != null) {
                            CCM_Bitmap.getBitmapToFile(bm, picfile + jdPicInfo.getFilename());
                            jdPicInfo.setFilePath(picfile + jdPicInfo.getFilename());

                        }


                        jdPicDao.addOrUpdate(jdPicInfo);


                    }
                }

            }


        }

        if (!sportEditFlag) {
            if(!resDataName.equals(resDataBean.getFd_resname())){

                String oldPic = Values.SDCARD_FILE(Values.SDCARD_FILE)  + projectBean.getfTaskname() + "/"
                        + Values.SDCARD_PIC + "/"+resDataName + "/" ;
                FileTool.deteleFiles(new File(oldPic));

            }
        }


    }

    /***
     * 将临时文件夹下的音频文件移动到当前的资源文件下；
     */

    private void moveTemp2CurrentFile() {

        File resSound = CreateDir(Values.SDCARD_FILE(Values.SDCARD_FILE) + "/" + projectBean.getfTaskname() + "/"
                + Values.SDCARD_SOUNDS + "/" + resDataBean.getFd_resname() + "");


        for(int i=0;i<deletlist.size();i++) {

            SoundInfo deletsoundInfo = deletlist.get(i);

            String filePath = deletsoundInfo.getFilePath();

            File file = new File(filePath);

            FileTool.deteleFiles(file);



        }






        try {
            FileTool.copy(TempSoundfile.getPath(),resSound.getPath());
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (!sportEditFlag) {

            boolean equals = creenetSoundFile.getPath().equals(resSound.getPath());

            if (!equals) {
                try {
                    FileTool.copy(creenetSoundFile.getPath(),resSound.getPath());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                FileTool.deteleFiles(creenetSoundFile);
            }
        }

        FileTool.deteleFiles(TempSoundfile);

        updateCurrentSoundList(resSound);



    }



    private void updateCurrentSoundList(File resSound) {


        currentSoundList.clear();
        audios.clear();
        audios.addAll(netaudios);
//
        File[] files = resSound.listFiles();
        for (File file : files) {
            if (file.getAbsolutePath().endsWith(".mp3")) {

                SoundInfo soundInfo = new SoundInfo();
                soundInfo.setFilePath(file.getAbsolutePath());
                soundInfo.setFilename(file.getName());
                if (!currentSoundList.contains(soundInfo)) {
                    currentSoundList.add(soundInfo);
                }



            }
        }

        List<String> autoNames = new ArrayList<>();

        for (int j = 0; j < audios.size(); j++) {
            autoNames.add(audios.get(j).getFilename());
        }


        for (int i = 0; i < currentSoundList.size(); i++) {

            if (!autoNames.contains(currentSoundList.get(i).getFilename())) {
                audios.add(currentSoundList.get(i));
            }
        }


    }




    private class PicAdapter extends LinearLayoutBaseAdapter {

        private List<JDPicInfo> jdPicslist;
        private Context contextlist;

        public PicAdapter(Context context, List<JDPicInfo> list) {
            super(context, list);
            this.jdPicslist = list;
            this.contextlist = context;
        }

        @Override
        public View getView(final int position) {
            final JDPicInfo jdPicInfo = jdPicslist.get(position);

            View convertView = View.inflate(contextlist, R.layout.item_jingdian_pic, null);
            ImageView imageView = convertView.findViewById(R.id.photo);
            ImageView iv_del = convertView.findViewById(R.id.iv_del);

            //当前item要加载的图片路径
            //使用谷歌官方提供的Glide加载图片
            if (jdPicInfo.getIsAddFlag() == 1) {
                iv_del.setVisibility(View.GONE);
                if (contextlist != null && imageView != null) {
                    Glide.with(contextlist)
                            .load(R.mipmap.takepic)
                            .centerCrop().into(imageView);
                    imageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && getContext().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSIONS_REQUEST_READ_CONTACTS);
                            } else {
                                Intent intent = new Intent(contextlist, Cai_AddPicActivity.class);
                                intent.putExtra("num", MAXNUM + 1 - jdPicslist.size());
                                startActivityForResult(intent, 1);
                            }

                        }
                    });
                }
            } else {
                if (!Utils.isEmpty(jdPicInfo.getImageUrl())) {
                    Glide.with(contextlist)
                            .load(Values.SERVICE_URL_ADMIN_FORM + jdPicInfo.getImageUrl())
                            .centerCrop().into(imageView);

                    imageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(contextlist, BigPicActivity.class);
                            intent.putExtra("infos", (Serializable) jdPicslist);
                            intent.putExtra("index", position);
                            startActivity(intent);
                        }
                    });

                } else {
                    Glide.with(contextlist)
                            .load(new File(jdPicInfo.getFilePath()))
                            .centerCrop().into(imageView);

                    imageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(contextlist, BigPicActivity.class);
                            intent.putExtra("infos", (Serializable) jdPicslist);
                            intent.putExtra("index", position);
                            startActivity(intent);
                        }
                    });

                }

            }
            iv_del.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    deletPicList.add(jdPicslist.get(position));


                    jdPicslist.remove(position);
                    if (!jdPicslist.contains(picBeannull)) {
                        jdPicslist.add(picBeannull);

                    }
                    notifyDataSetChanged();
                }
            });

            return convertView;
        }


    }




    private String saveEightLevel(String star) {



        DecimalFormat df = new DecimalFormat("########.000000");
        BigDecimal lonX = new BigDecimal(star);
        //保留8位小数
        String formatX =df.format(lonX);

        return formatX;

    }





    private class ZiYuanAdapter extends LinearLayoutBaseAdapter {

        private List<ResModelBean> list;

        public ZiYuanAdapter(Context context, List<?> list) {
            super(context, list);
            this.list = (List<ResModelBean>) list;
        }

        @Override
        public View getView(final int position) {
            View convertView = getLayoutInflater().inflate(R.layout.item_ziyuanview, null);
            LinearLayout ll_all = convertView.findViewById(R.id.ll_all);
            TextView tv_name = convertView.findViewById(R.id.tv_name);
            final ImageView iv_checkBox = convertView.findViewById(R.id.iv_checkBox);
            final ResModelBean resModelBean = list.get(position);
            if (resModelBean.isSelect()) {
                iv_checkBox.setImageResource(R.mipmap.xuanzhong);
            } else {
                iv_checkBox.setImageResource(R.mipmap.gouxuankuang);

            }

            tv_name.setText(resModelBean.getName());

            ll_all.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    for (ResModelBean resModelBean1 : list) {
                        if (resModelBean1 != resModelBean) {

                            resModelBean1.setSelect(false);
                        }

                    }
                    boolean select = resModelBean.isSelect();

                    resModelBean.setSelect(!select);

                    if (resModelBean.isSelect()) {

                        resDataBean.setFd_resmodelname(resModelBean.getName());
                        resDataBean.setFd_resmodelid(resModelBean.getId());
                        resDataBean.setCategoryId(resModelBean.getId());

                        selectResModel=resModelBean;
                        if (!Utils.isEmpty(sureResModelId) && sureResModelId.equals(selectResModel.getId())) {
                            formBeanList.clear();
                            formBeanList.addAll(tempformBeanList);


                        } else {

                            String formData = selectResModel.getfJsonData();

                            formBeanList.clear();

                            try {
                                JSONObject jsonObject = null;
                                jsonObject = new JSONObject(formData);
                                Gson mGson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm").create();
                                List<DongTaiFormBean> dictList = mGson.fromJson(jsonObject.getString("cgformFieldList"),
                                        new TypeToken<List<DongTaiFormBean>>() {}.getType());
                                formBeanList.addAll(dictList);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }


                    }


                    notifyDataSetChanged();

                }
            });
            return convertView;
        }
    }


    // 隐藏景点编辑界面
    private void jingdianGone() {
        this.dismiss();
        if (dismissListener != null) {
            dismissListener.dismisse();
            if (sportEditFlag) {
                TempSoundfile.delete();

            }


        }
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
        if (dismissListener != null) {
            dismissListener.dismisse();
            TempSoundfile.delete();


        }
    }

    @Override
    public void dismiss() {
        super.dismiss();
        if (dismissListener != null) {
            dismissListener.dismisse();
            TempSoundfile.delete();

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_READ_CONTACTS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission Granted
                Intent intent = new Intent(mContext, Cai_AddPicActivity.class);
                intent.putExtra("num", MAXNUM + 1 - resPicList.size());
                startActivityForResult(intent, 1);
            } else {
                Utils.showToast(mContext, "请授权访问权限");
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    public interface SeeBarRangeListener {

        void onChange(int progress);

    }

    public interface DismissListener {

        void dismisse();

    }

    private  String picfile ;

    /**
     * 选择完图片后的回调
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1:
                if (resultCode == 1) {
                    String picPath = data.getStringExtra("picPath");
                    Uri mUri;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        mUri = FileProvider.getUriForFile(getActivity(), BuildConfig.APPLICATION_ID + ".fileprovider", new File(picPath));
                    } else {
                        mUri = FileProvider.getUriForFile(getActivity(), BuildConfig.APPLICATION_ID + ".fileprovider", new File(picPath));
                    }
                    startPhotoZoom(mUri);
                } else if (resultCode == 2) {
                    List<ImageItem> picPaths = (List<ImageItem>) data.getSerializableExtra("picPath");

                    for (ImageItem item : picPaths) {
                        String cameraPath = item.imagePath;

                        String cameraName =item.imageId+ System.currentTimeMillis() + ".jpg";

                        JDPicInfo picBean = new JDPicInfo(0, cameraName, cameraPath, null, 0, "false");
                        resPicList.add(0, picBean);

                    }
                    if (resPicList.size() >= MAXNUM + 1) {
                        resPicList.remove(MAXNUM);
                    }
                    picAdapter.notifyDataSetChanged();
                }
                break;

            case 3:
                setPicToView(data);
                break;
            case 4:
                if (resultCode == 4) {

                    formBeanList = (List<DongTaiFormBean>) data.getSerializableExtra("formBeanList");
                    tempformBeanList.clear();
                    tempformBeanList.addAll(formBeanList);
                    sureResModelId = selectResModel.getId();

                }

                break;
            case 5:
                if (resultCode == 5) {

                    audios = (List<SoundInfo>) data.getSerializableExtra("recordlist");
                    deletlist = (List<SoundInfo>) data.getSerializableExtra("deletList");

                    if (audios != null) {
                        tv_recordCount.setText("录音数量：" + audios.size() + "");
                    }
                }

                break;


        }
    }


    public SheetDialogFragment() {
    }

    Uri uritempFile;

    /**
     * 裁剪图片方法实现
     *
     * @param uri
     */
    public void startPhotoZoom(Uri uri) {
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


    /**
     * 保存裁剪之后的图片数据
     *
     * @param picdata
     */
    private void setPicToView(Intent picdata) {
        if (!Utils.isEmpty(uritempFile.toString())) {

            try {
                String picName = System.currentTimeMillis() + ".jpg";
                Bitmap bitmap = BitmapFactory.decodeStream(getActivity().getContentResolver().openInputStream(uritempFile));

                String path = tempPicFilePath ;

                CCM_Bitmap.getBitmapToFile(bitmap, path + picName);
                JDPicInfo bean = new JDPicInfo(0, picName, path + picName, null, 0, "false");
                resPicList.add(0, bean);
                picAdapter.notifyDataSetChanged();

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        Glide.with(this).pauseRequests();
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


}
