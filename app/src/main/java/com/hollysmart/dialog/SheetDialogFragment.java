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
import com.hollysmart.beans.DongTaiFormBean;
import com.hollysmart.beans.FormModelBean;
import com.hollysmart.beans.JDPicInfo;
import com.hollysmart.beans.ProjectBean;
import com.hollysmart.beans.ResDataBean;
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
import com.hollysmart.service.SubmitFormService;
import com.hollysmart.utils.ACache;
import com.hollysmart.utils.CCM_Bitmap;
import com.hollysmart.utils.CCM_DateTime;
import com.hollysmart.utils.Utils;
import com.hollysmart.utils.loctionpic.ImageItem;
import com.hollysmart.value.Values;
import com.hollysmart.views.linearlayoutforlistview.LinearLayoutBaseAdapter;
import com.hollysmart.views.linearlayoutforlistview.MyLinearLayoutForListView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by Lenovo on 2018/12/3.
 */


public class SheetDialogFragment extends BottomSheetDialogFragment {
    private BottomSheetBehavior mBehavior;
    private final int MAXNUM = 9;

    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 100;

    private List<ResModelBean> resultList = new ArrayList<ResModelBean>();

    private List<JDPicInfo> resPicList; // 当前景点图片集

    private int sheetHeight;

    private int scope = 10;//范围

    private ResDataBean resDataBean;//当前新添加的资源实体

    private List<DongTaiFormBean> formBeanList;// 当前资源的动态表单

    private ProjectBean projectBean;//当前项目

    private TextView text_fenwei;
    private TextView tv_jingdianWeizi;
    private EditText ed_resouseNumber;
    private EditText ed_jingdianName;
    private EditText et_remark;

    private LatLng dingWeiDian;


    private boolean sportEditFlag = false; // ture 新添加 false 修改
    private Context mContext;
    private List<SoundInfo> audios=new ArrayList<>();// 当前资源的录音

    private List<SoundInfo> netaudios=new ArrayList<>();// 网络请求获取得到的录音

    private PicAdapter picAdapter;

    private ZiYuanAdapter ziYuanAdapter;

    private SeeBarRangeListener seeBarRangeListener;
    private DismissListener dismissListener;


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
            tv_jingdianWeizi.setText(longitude + "," + latitude);

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
        ImageView iv_more = view.findViewById(R.id.iv_more);
        ImageView iv_del = view.findViewById(R.id.iv_del);
        tv_recordCount = view.findViewById(R.id.tv_recordCount);

        ed_resouseNumber = view.findViewById(R.id.ed_resouseNumber);
        tv_jingdianWeizi = view.findViewById(R.id.tv_jingdianWeizi);
        ed_jingdianName = view.findViewById(R.id.ed_jingdianName);
        text_fenwei = view.findViewById(R.id.text_fenwei);
        SeekBar seekBar_fenwei = view.findViewById(R.id.seekBar_fenwei);
        et_remark = view.findViewById(R.id.et_remark);

        resultList.clear();

        if (resultList.size() == 1) {
            resultList.get(0).setSelect(true);
        }

        String classifyIds = projectBean.getfTaskmodel();
        if (classifyIds != null) {

            String[] ids = classifyIds.split(",");
            ResModelDao resModelDao = new ResModelDao(mContext);
            for(int i=0;i<ids.length;i++) {
                ResModelBean resModelBean = resModelDao.getDatById(ids[i]);
                resultList.add(resModelBean);
            }


        }

        resPicList = new ArrayList<>();
        resPicList.clear();
        resPicList.add(picBeannull);

        formBeanList = new ArrayList<>();


        //修改
        if (!sportEditFlag) {
            if (!Utils.isEmpty(resDataBean.getNumber())) {

                ed_resouseNumber.setText(resDataBean.getNumber());
            }
            if (!Utils.isEmpty(resDataBean.getFd_resname())) {

                ed_jingdianName.setText(resDataBean.getFd_resname());
            }
            if (!Utils.isEmpty(resDataBean.getNote())) {

                et_remark.setText(resDataBean.getNote());
            }
            seekBar_fenwei.setProgress(resDataBean.getScope());
            text_fenwei.setText("当前范围：" + resDataBean.getScope() + "米");



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
                netaudios.addAll(resDataBean.getAudio());
                audios.clear();
                audios.addAll(0, resDataBean.getAudio());
                if (audios != null && audios.size() > 0) {
                    tv_recordCount.setText("录音数量：" + audios.size() + "");
                } else {
                    tv_recordCount.setText("录音数量：" + 0 + "");
                }

            } else {

                List<SoundInfo> soundInfoList = new JDSoundDao(getContext()).getDataByJDId(resDataBean.getId());
                audios.clear();
                audios.addAll(0, soundInfoList);
                if (audios != null && audios.size() > 0) {
                    tv_recordCount.setText("录音数量：" + audios.size() + "");
                } else {
                    tv_recordCount.setText("录音数量：" + 0 + "");
                }

            }




            for(int i=0;i<resultList.size();i++) {
                if (!Utils.isEmpty(resDataBean.getCategoryId())) {
                    if (resultList.get(i).getId().equals(resDataBean.getCategoryId())) {
                        resultList.get(i).setSelect(true);

                    }

                }


            }

            String formData = resDataBean.getFormData();

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





        } else {
            //
            resDataBean = new ResDataBean();
            formBeanList = new ArrayList<>();

            //创建音频临时的文件夹：

            TempSoundfile = CreateDir(Values.SDCARD_FILE(Values.SDCARD_FILE) + "/" + projectBean.getfTaskname() + "/"
                    + Values.SDCARD_SOUNDS + "/" + System.currentTimeMillis() + "");


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
                    TempSoundfile.delete();

                }


            }
        });

        iv_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                List<ResModelBean> list = new ArrayList<>();


                for (ResModelBean resModelBean : resultList) {

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
                    TempSoundfile.delete();

                }

            }
        });

        ziYuanAdapter = new ZiYuanAdapter(mContext, resultList);
        ll_jingDianFenLei.setAdapter(ziYuanAdapter);

        picAdapter = new PicAdapter(mContext, resPicList);

        ll_jingdian_pic.setAdapter(picAdapter);

        bt_luyin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, RecordListActivity.class);
                intent.putExtra("projectbean", projectBean);
                intent.putExtra("TempSoundfile", TempSoundfile.getAbsolutePath());
                intent.putExtra("netaudios", (Serializable) netaudios);
                startActivityForResult(intent,5);

            }
        });

        seekBar_fenwei.setMax(50);
        seekBar_fenwei.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                text_fenwei.setText("当前范围：" + progress + "米");
                scope = progress;
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

    private File TempSoundfile;


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
        String createTime = new CCM_DateTime().Datetime2();
        String coordinate = dingWeiDian.longitude + "," + dingWeiDian.latitude
                + ",0";
        sportEditFlag = true;
        // 添加数据库

        resDataBean.setId(System.currentTimeMillis() + "");
        resDataBean.setFdTaskId(projectBean.getId());
        resDataBean.setNumber(resNum);
        resDataBean.setFd_restaskname(projectBean.getfTaskname());
        resDataBean.setFd_resname(jdName);
        resDataBean.setNote(et_remark.getText().toString());
        resDataBean.setRescode(resNum);
        resDataBean.setScope(scope);
        resDataBean.setCreatedAt(createTime);
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


        Intent intent = new Intent(mContext, SubmitFormService.class);
        intent.putExtra("type", SubmitFormService.TYPE_XINZENG);
        intent.putExtra("uuid", resDataBean.getId());
        mContext.startService(intent);

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

        resDataBean.setFdTaskId(projectBean.getId());
        resDataBean.setFd_resname(jdName);
        resDataBean.setNumber(resNum);
        resDataBean.setScope(scope);
        resDataBean.setRescode(resNum);
        resDataBean.setNote(et_remark.getText().toString());
        resDataBean.setCreatedAt(createTime);
        resDataBean.setJdPicInfos(resPicList);
        updateDb(resDataBean);
    }


    private void updateDb(ResDataBean resDataBean) {

        ResDataDao resDataDao = new ResDataDao(getActivity());

        JDPicDao jdPicDao = new JDPicDao(getActivity());
        JDSoundDao jdSoundDao = new JDSoundDao(getActivity());

        if (resPicList != null && resPicList.size() > 0) {

            for (JDPicInfo jdPicInfo : resPicList) {
                if (jdPicInfo.getIsAddFlag() != 1) {

                    jdPicInfo.setJdId(resDataBean.getId());
                    jdPicInfo.setJqId(resDataBean.getFdTaskId());

                    jdPicDao.addOrUpdate(jdPicInfo);
                }
            }
        }

        if (audios != null && audios.size() > 0) {
            for (SoundInfo soundInfo : audios) {
                soundInfo.setJdId(resDataBean.getId());
                soundInfo.setJqId(resDataBean.getFdTaskId());
                jdSoundDao.addOrUpdate(soundInfo);
            }

        }

        resDataDao.addOrUpdate(resDataBean);


        Intent intent = new Intent(mContext, SubmitFormService.class);
        intent.putExtra("type", SubmitFormService.TYPE_XINZENG);
        intent.putExtra("uuid", resDataBean.getId());
        mContext.startService(intent);


        jingdianGone();

    }


    // 数据库操作
    private void addDB(ResDataBean resDataBean) {

        ResDataDao resDataDao = new ResDataDao(getActivity());
        JDPicDao jdPicDao = new JDPicDao(getActivity());
        JDSoundDao jdSoundDao = new JDSoundDao(getActivity());

        resDataDao.addOrUpdate(resDataBean);

        if (resPicList != null && resPicList.size() > 0) {
            for (JDPicInfo jdPicInfo : resPicList) {
                if (jdPicInfo.getIsAddFlag() != 1) {
                    jdPicInfo.setJdId(resDataBean.getId());
                    jdPicInfo.setJqId(resDataBean.getFdTaskId());
                    jdPicDao.addOrUpdate(jdPicInfo);
                }
            }

        }
        if (audios != null && audios.size() > 0) {
            for (SoundInfo soundInfo : audios) {
                    soundInfo.setJdId(resDataBean.getId());
                    soundInfo.setJqId(resDataBean.getFdTaskId());
                    jdSoundDao.addOrUpdate(soundInfo);
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
            JDPicInfo jdPicInfo = jdPicslist.get(position);

            View convertView = View.inflate(contextlist, R.layout.item_jingdian_pic, null);
            ImageView imageView = convertView.findViewById(R.id.photo);
            ImageView iv_del = convertView.findViewById(R.id.iv_del);

            //当前item要加载的图片路径
            //使用谷歌官方提供的Glide加载图片
            if (jdPicInfo.getIsAddFlag() == 1) {
                iv_del.setVisibility(View.GONE);
                if (contextlist != null && imageView != null) {
                    Glide.with(contextlist)
                            .load(R.mipmap.a_v)
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
                    JDPicInfo delPic = jdPicslist.get(position);
                    String path = delPic.getFilePath();
                    File file = new File(path);
                    file.delete();

                    JDPicDao jdPicDao = new JDPicDao(mContext);
                    if (delPic.getId() != 0) {
                        jdPicDao.deletByPicId(delPic.getId());
                    }


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
            if (sportEditFlag) {
                TempSoundfile.delete();

            }


        }
    }

    @Override
    public void dismiss() {
        super.dismiss();
        if (dismissListener != null) {
            dismissListener.dismisse();
            if (sportEditFlag) {
                TempSoundfile.delete();

            }


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

    private final String file = Values.SDCARD_FILE(Values.SDCARD_PIC);

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

                        String cameraName = System.currentTimeMillis() + ".jpg";
                        Bitmap bm = BitmapFactory.decodeFile(item.imagePath);
                        CCM_Bitmap.getBitmapToFile(bm, file + cameraName);
                        cameraPath = file + cameraName;

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
                }

                break;
            case 5:
                if (resultCode == 5) {

                    audios = (List<SoundInfo>) data.getSerializableExtra("recordlist");

                    if (audios != null && audios.size() != 0) {
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
        intent.putExtra("outputX", 150);
        intent.putExtra("outputY", 150);
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

                String filePath = file + projectBean.getfTaskname() + "/" + Values.SDCARD_PIC + "/";
                CCM_Bitmap.getBitmapToFile(bitmap, filePath + picName);
                JDPicInfo bean = new JDPicInfo(0, picName, filePath + picName, null, 0, "false");
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
