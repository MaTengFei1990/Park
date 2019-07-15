package com.hollysmart.formlib;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.hollysmart.apis.GetResModelAPI;
import com.hollysmart.beans.ResModelBean;
import com.hollysmart.db.ResModelDao;
import com.hollysmart.formlib.adapters.ResDataManageAdapter;
import com.hollysmart.formlib.apis.GetNetResListAPI;
import com.hollysmart.formlib.apis.ResDataDeleteAPI;
import com.hollysmart.formlib.apis.getResTaskListAPI;
import com.hollysmart.beans.JDPicInfo;
import com.hollysmart.formlib.beans.DongTaiFormBean;
import com.hollysmart.formlib.beans.ProjectBean;
import com.hollysmart.formlib.beans.ResDataBean;
import com.hollysmart.db.DatabaseHelper;
import com.hollysmart.db.JDPicDao;
import com.hollysmart.db.ProjectDao;
import com.hollysmart.db.ResDataDao;
import com.hollysmart.db.UserInfo;
import com.hollysmart.park.R;
import com.hollysmart.style.StyleAnimActivity;
import com.hollysmart.utils.ACache;
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


    private List<JDPicInfo> picList; // 当前景点图片集
    private List<String> soundList; // 当前景点录音集

    private ProjectBean projectBean;

    @Override
    public void findView() {
        ButterKnife.bind(this);
        ib_back.setOnClickListener(this);
        iv_maplsit.setOnClickListener(this);
    }

    private List<ResDataBean> mJingDians;
    private ResDataManageAdapter resDataManageAdapter;
    Map<String, String> map = new HashMap<String , String>();


    @Override
    public void init() {
        isLogin();
        picList = new ArrayList<>();
        soundList = new ArrayList<>();
        mJingDians = new ArrayList<>();

        map = (Map<String, String>) getIntent().getSerializableExtra("exter");

        selectDB();
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ib_back:
                finish();
                break;
            case R.id.iv_maplsit:
                if (projectBean != null) {
                    Intent intent = new Intent(mContext, ListShowOnMapActivity.class);
                    intent.putExtra("projectBean", projectBean);
                    intent.putExtra("exter", (Serializable) map);
                    startActivity(intent);
                } else {
                    Utils.showDialog(mContext,"暂无资源列表");
                }
                break;
        }
    }


    // 查询
    private void selectDB() {
        mJingDians.clear();
        if (map != null && map.size() > 0) {

            new getResTaskListAPI(userInfo.getAccess_token(), map.get("id"), 10000, new getResTaskListAPI.ResTaskListIF() {
                @Override
                public void onResTaskListResult(boolean isOk, List<ProjectBean> projectBeanList, int count) {

                    if (isOk) {
                        if (projectBeanList != null && projectBeanList.size() > 0) {

                            projectBean = projectBeanList.get(0);

                            new GetResModelAPI(userInfo.getAccess_token(), projectBean.getfTaskmodel(), new GetResModelAPI.GetResModelIF() {
                                @Override
                                public void ongetResModelIFResult(boolean isOk, ResModelBean resModelBen) {

                                    if (isOk) {//获取到网络数据

                                        ResModelDao resModelDao = new ResModelDao(mContext);
                                        resModelDao.addOrUpdate(resModelBen);
                                        String getfJsonData = resModelBen.getfJsonData();
                                        Gson mGson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm").create();
                                        List<DongTaiFormBean> newFormList = mGson.fromJson(getfJsonData, new TypeToken<List<DongTaiFormBean>>() {
                                        }.getType());

                                        resDataManageAdapter = new ResDataManageAdapter(mContext, mJingDians, picList, soundList, projectBean,newFormList);
                                        lv_jingdian.setAdapter(resDataManageAdapter);

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
                            }).request();



                        }




                    }

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








//    private class ResDataManageAdapter extends BaseAdapter {
//
//        private LayoutInflater inflater;
//
//        private List<ResDataBean> mJingDians;
//        private List<JDPicInfo> picList; // 当前景点图片集
//        private List<String> soundList; // 当前景点录音集
//
//        private String jqId = "";
//        private String namestr;
//        private Context context;
//
//        public ResDataManageAdapter(Context context, List<ResDataBean> mJingDians, List<JDPicInfo> picList, List<String> soundList, ProjectBean projectBean) {
//            this.context = context;
//            inflater = LayoutInflater.from(context);
//            this.picList = picList;
//            this.soundList = soundList;
//            this.mJingDians = mJingDians;
//            this.namestr = projectBean.getfTaskname();
//            this.jqId = projectBean.getId();
//            isLogin();
//        }
//
//        @Override
//        public int getCount() {
//            return mJingDians.size();
//        }
//
//        @Override
//        public Object getItem(int arg0) {
//            return null;
//        }
//
//        @Override
//        public long getItemId(int arg0) {
//            return 0;
//        }
//
//        @Override
//        public View getView(final int position, View convertView, ViewGroup arg2) {
//
//            ResDataManageAdapter.ViewHolder holder;
//            if (convertView != null && convertView.getTag() != null) {
//                holder = (ResDataManageAdapter.ViewHolder) convertView.getTag();
//            } else {
//                convertView = inflater.inflate(R.layout.item_jingqu, null);
//                holder = new ResDataManageAdapter.ViewHolder();
//                holder.tv_name = convertView
//                        .findViewById(R.id.tv_jingquName);
//                holder.ll_bianji = convertView
//                        .findViewById(R.id.ll_bianji);
//                holder.ll_shangchuan = convertView
//                        .findViewById(R.id.ll_shangchuan);
//                holder.ll_xiugai = convertView
//                        .findViewById(R.id.ll_xiugai);
//                holder.ll_shanchu = convertView
//                        .findViewById(R.id.ll_shanchu);
//                holder.ll_shanchu.setVisibility(View.GONE);
//                holder.tv_bianji = convertView
//                        .findViewById(R.id.tv_bianji);
//                holder.ll_fenxiang = convertView.findViewById(R.id.ll_fenxiang);
//                convertView.setTag(holder);
//            }
//            final TextView mTv_bianji = holder.tv_bianji;
//            final LinearLayout mLl_bianji = holder.ll_bianji;
//            final LinearLayout ll_fenxiang = holder.ll_fenxiang;
//            final LinearLayout ll_shangchuan = holder.ll_shangchuan;
//
//            holder.tv_name.setText(mJingDians.get(position).getFd_resname());
//            mTv_bianji.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View arg0) {
//                    mTv_bianji.setVisibility(View.GONE);
//                    mLl_bianji.setVisibility(View.VISIBLE);
//                    ll_fenxiang.setVisibility(View.GONE);
//                    ll_shangchuan.setVisibility(View.GONE);
//                }
//            });
//
//            convertView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View arg0) {
//
//                    Intent data = new Intent(mContext, ResDetailsActivity.class);
//                    data.putExtra("resDataBean", mJingDians.get(position));
//                    data.putExtra("index", position);
//                    startActivity(data);
//
//                }
//            });
//
//            holder.ll_shangchuan.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View arg0) {
//
//                }
//            });
//
//            holder.ll_xiugai.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View arg0) {
//                    Intent data = new Intent();
//                    data.putExtra("jdId", mJingDians.get(position).getId()); // 1代表修改
//                    data.putExtra("index", position);
//                    data.putExtra("resDataBean", mJingDians.get(position));
//                    data.putExtra("name", mJingDians.get(position).getFd_resname());
//                    Activity activity = (Activity) context;
//                    activity.setResult(1, data); // 1代表修改
//                    activity.finish();
//                }
//            });
//            holder.ll_shanchu.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View arg0) {
//                    delJQ(mJingDians.get(position));
//                }
//            });
//            return convertView;
//        }
//
//        class ViewHolder {
//            TextView tv_name;
//            LinearLayout ll_bianji;
//            LinearLayout ll_shangchuan;
//            LinearLayout ll_xiugai;
//            LinearLayout ll_shanchu;
//            LinearLayout ll_fenxiang;
//            TextView tv_bianji;
//        }
//
//
//        // 删除
//        private void delJQ(final ResDataBean deleteBean) {
//            AlertDialog.Builder builder = new AlertDialog.Builder(context);
//            builder.setTitle("删除资源");
//            builder.setMessage("确定要删除此资源吗？");
//            builder.setPositiveButton("删除", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface arg0, int arg1) {
//                    delDb(deleteBean);
//
//
//                    new ResDataDeleteAPI(userInfo.getAccess_token(), deleteBean, new ResDataDeleteAPI.ResDataDeleteIF() {
//                        @Override
//                        public void onResDataDeleteResult(boolean isOk, String msg) {
//
//                        }
//                    }).request();
//                }
//            });
//            builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface arg0, int arg1) {
//                }
//            });
//            builder.create().show();
//        }
//
//        private void delDb(ResDataBean deleteBean) {
//            SQLiteDatabase db = new DatabaseHelper(context).getWritableDatabase();
//            //资源数据库删除
//            ResDataDao resDataDao = new ResDataDao(context);
//            boolean resDatadelFlag = resDataDao.deletDataById(deleteBean.getId());
//
//            //资源数据库删除
//            JDPicDao jdPicDao = new JDPicDao(context);
//            boolean resPicdelFlag = jdPicDao.deletByResId(deleteBean.getId());
//
//
//            if (resDatadelFlag) {
//
//                if (resPicdelFlag) {
//                    Mlog.d("图片删除成功");
//                }
//                String file = Values.SDCARD_FILE(Values.SDCARD_FILE) + namestr + "/" + Values.SDCARD_PIC;
//                String file3 = Values.SDCARD_FILE(Values.SDCARD_FILE) + namestr + "/" + Values.SDCARD_SOUNDS;
//                picFile(file, deleteBean.getFd_resname());
//                soundFile(file3, deleteBean.getFd_resname());
//
//                if (picList.size() > 0) {
//                    for (int i = 0; i < picList.size(); i++) {
//                        File file2 = new File(file + "/" + picList.get(i));
//                        if (file2.exists()) {
//                            file2.delete();
//                        }
//                    }
//                }
//                if (soundList.size() > 0) {
//                    for (int j = 0; j < soundList.size(); j++) {
//                        File file4 = new File(file3 + "/" + soundList.get(j));
//                        if (file4.exists()) {
//                            file4.delete();
//                        }
//                    }
//                }
//                Toast.makeText(context, "资源删除成功", Toast.LENGTH_SHORT).show();
//                selectDB(jqId);
//
//            } else {
//                Toast.makeText(context, "资源删除失败", Toast.LENGTH_SHORT).show();
//            }
//            db.close();
//        }
//
//
//        //递归查找的所有音频文件
//        private void soundFile(String strPath, String name) {
//            soundList = new ArrayList<String>();
//            String filename;//文件名
//            String suf;//文件后缀
//            File dir = new File(strPath);//文件夹dir
//            File[] files = dir.listFiles();//文件夹下的所有文件或文件夹
//
//            if (files == null)
//                return;
//
//            for (int i = 0; i < files.length; i++) {
//
//                if (files[i].isDirectory()) {
//                    soundFile(files[i].getAbsolutePath(), name);//递归文件夹！！！
//
//                } else {
//                    filename = files[i].getName();
//                    int j = filename.lastIndexOf(".");
//                    int k = filename.lastIndexOf("-");
//                    suf = filename.substring(k + 1, j);//得到文件后缀
//
//
//                    if (suf.equalsIgnoreCase(name))//判断是不是后缀的文件
//                    {
////		                    String strFileName = files[i].getAbsolutePath().toLowerCase();
//                        soundList.add(files[i].getName());//对于文件才把它加到list中
//                    }
//                }
//
//            }
//        }
//
//        //递归查找的所有图片文件
//        private void picFile(String strPath, String name) {
//            picList = new ArrayList<>();
//            String filename;//文件名
//            String suf;//文件后缀
//            File dir = new File(strPath);//文件夹dir
//            File[] files = dir.listFiles();//文件夹下的所有文件或文件夹
//            if (files == null)
//                return;
//
//            for (int i = 0; i < files.length; i++) {
//
//                if (files[i].isDirectory()) {
//                    picFile(files[i].getAbsolutePath(), name);//递归文件夹！！！
//
//                } else {
////		                filename = files[i].getName();
////		                int j = filename.lastIndexOf(".");
////		                suf = filename.substring(j+1);//得到文件后缀
//                    filename = files[i].getName();
//                    int j = filename.lastIndexOf(".");
//                    int k = filename.lastIndexOf("-");
//                    suf = filename.substring(k + 1, j);//得到文件后缀
//
////		                if(suf.equalsIgnoreCase("jpg")||suf.equalsIgnoreCase("png"))
//                    if (suf.equalsIgnoreCase(name))//判断是不是后缀的文件
//                    {
////		                    String strFileName = files[i].getAbsolutePath().toLowerCase();
////                    picList.add(new JdP);//对于文件才把它加到list中
//                    }
//                }
//
//            }
//        }
//
//        // 查询
//        private void selectDB(String jqId) {
//            Mlog.d("jqId = " + jqId);
//            mJingDians.clear();
//
//            ResDataDao resDataDao = new ResDataDao(context);
//            List<ResDataBean> resDataBeans = resDataDao.getData(jqId + "");
//            mJingDians.addAll(resDataBeans);
//            notifyDataSetChanged();
//
//
//        }
//    }
}
