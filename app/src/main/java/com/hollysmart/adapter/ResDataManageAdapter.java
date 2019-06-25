package com.hollysmart.adapter;

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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hollysmart.apis.ResDataDeleteAPI;
import com.hollysmart.beans.JDPicInfo;
import com.hollysmart.beans.ProjectBean;
import com.hollysmart.beans.ResDataBean;
import com.hollysmart.db.DatabaseHelper;
import com.hollysmart.db.JDPicDao;
import com.hollysmart.db.ResDataDao;
import com.hollysmart.db.UserInfo;
import com.hollysmart.park.R;
import com.hollysmart.utils.ACache;
import com.hollysmart.utils.Mlog;
import com.hollysmart.value.Values;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lenovo on 2019/3/7.
 */

public class ResDataManageAdapter extends BaseAdapter {

    private LayoutInflater inflater;

    private List<ResDataBean> mJingDians;
    private List<JDPicInfo> picList; // 当前景点图片集
    private List<String> soundList; // 当前景点录音集

    private String jqId = "";
    private String namestr;
    private Context context;

    public ResDataManageAdapter(Context context, List<ResDataBean> mJingDians, List<JDPicInfo> picList, List<String> soundList, ProjectBean projectBean) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.picList = picList;
        this.soundList = soundList;
        this.mJingDians = mJingDians;
        this.namestr = projectBean.getfTaskname();
        this.jqId = projectBean.getId();
        isLogin();
    }

    @Override
    public int getCount() {
        return mJingDians.size();
    }

    @Override
    public Object getItem(int arg0) {
        return null;
    }

    @Override
    public long getItemId(int arg0) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup arg2) {

        ViewHolder holder;
        if (convertView != null && convertView.getTag() != null) {
            holder = (ViewHolder) convertView.getTag();
        } else {
            convertView = inflater.inflate(R.layout.item_jingqu, null);
            holder = new ViewHolder();
            holder.tv_name =  convertView
                    .findViewById(R.id.tv_jingquName);
            holder.ll_bianji =  convertView
                    .findViewById(R.id.ll_bianji);
            holder.ll_shangchuan =  convertView
                    .findViewById(R.id.ll_shangchuan);
            holder.ll_xiugai =  convertView
                    .findViewById(R.id.ll_xiugai);
            holder.ll_shanchu =  convertView
                    .findViewById(R.id.ll_shanchu);
            holder.tv_bianji =  convertView
                    .findViewById(R.id.tv_bianji);
            holder.ll_fenxiang =  convertView.findViewById(R.id.ll_fenxiang);
            convertView.setTag(holder);
        }
        final TextView mTv_bianji = holder.tv_bianji;
        final LinearLayout mLl_bianji = holder.ll_bianji;
        final LinearLayout ll_fenxiang = holder.ll_fenxiang;
        final LinearLayout ll_shangchuan = holder.ll_shangchuan;

        holder.tv_name.setText(mJingDians.get(position).getFd_resname());
        mTv_bianji.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                mTv_bianji.setVisibility(View.GONE);
                mLl_bianji.setVisibility(View.VISIBLE);
                ll_fenxiang.setVisibility(View.GONE);
                ll_shangchuan.setVisibility(View.GONE);
            }
        });

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                    Intent data = new Intent();
                    data.putExtra("resDataBean", mJingDians.get(position));
                    data.putExtra("index", position);
                    Activity activity = (Activity) context;
                    activity.setResult(1, data); // 1代表修改
                    activity.finish();

            }
        });

        holder.ll_shangchuan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {

            }
        });

        holder.ll_xiugai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent data = new Intent();
                data.putExtra("jdId", mJingDians.get(position).getId()); // 1代表修改
                data.putExtra("index", position);
                data.putExtra("resDataBean", mJingDians.get(position));
                data.putExtra("name", mJingDians.get(position).getFd_resname());
                Activity activity = (Activity) context;
                activity.setResult(1, data); // 1代表修改
                activity.finish();
            }
        });
        holder.ll_shanchu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                delJQ(mJingDians.get(position));
            }
        });
        return convertView;
    }

    class ViewHolder {
        TextView tv_name;
        LinearLayout ll_bianji;
        LinearLayout ll_shangchuan;
        LinearLayout ll_xiugai;
        LinearLayout ll_shanchu;
        LinearLayout ll_fenxiang;
        TextView tv_bianji;
    }


    // 删除
    private void delJQ(final ResDataBean deleteBean) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("删除资源");
        builder.setMessage("确定要删除此资源吗？");
        builder.setPositiveButton("删除", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                delDb(deleteBean);


                new ResDataDeleteAPI(userInfo.getAccess_token(), deleteBean, new ResDataDeleteAPI.ResDataDeleteIF() {
                    @Override
                    public void onResDataDeleteResult(boolean isOk, String msg) {

                    }
                }).request();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
            }
        });
        builder.create().show();
    }

    private void delDb(ResDataBean deleteBean) {
        SQLiteDatabase db = new DatabaseHelper(context).getWritableDatabase();
        //资源数据库删除
        ResDataDao resDataDao = new ResDataDao(context);
        boolean resDatadelFlag = resDataDao.deletDataById(deleteBean.getId());

        //资源数据库删除
        JDPicDao jdPicDao = new JDPicDao(context);
        boolean resPicdelFlag = jdPicDao.deletByResId(deleteBean.getId());


        if (resDatadelFlag) {

            if (resPicdelFlag) {
                Mlog.d("图片删除成功");
            }
            String file = Values.SDCARD_FILE(Values.SDCARD_FILE) + namestr + "/" + Values.SDCARD_PIC;
            String file3 = Values.SDCARD_FILE(Values.SDCARD_FILE) + namestr + "/" + Values.SDCARD_SOUNDS;
            picFile(file, deleteBean.getFd_resname());
            soundFile(file3, deleteBean.getFd_resname());

            if (picList.size() > 0) {
                for (int i = 0; i < picList.size(); i++) {
                    File file2 = new File(file + "/" + picList.get(i));
                    if (file2.exists()) {
                        file2.delete();
                    }
                }
            }
            if (soundList.size() > 0) {
                for (int j = 0; j < soundList.size(); j++) {
                    File file4 = new File(file3 + "/" + soundList.get(j));
                    if (file4.exists()) {
                        file4.delete();
                    }
                }
            }
            Toast.makeText(context, "资源删除成功", Toast.LENGTH_SHORT).show();
            selectDB(jqId);

        } else {
            Toast.makeText(context, "资源删除失败", Toast.LENGTH_SHORT).show();
        }
        db.close();
    }


    //递归查找的所有音频文件
    private void soundFile(String strPath, String name) {
        soundList = new ArrayList<String>();
        String filename;//文件名
        String suf;//文件后缀
        File dir = new File(strPath);//文件夹dir
        File[] files = dir.listFiles();//文件夹下的所有文件或文件夹

        if (files == null)
            return;

        for (int i = 0; i < files.length; i++) {

            if (files[i].isDirectory()) {
                soundFile(files[i].getAbsolutePath(), name);//递归文件夹！！！

            } else {
                filename = files[i].getName();
                int j = filename.lastIndexOf(".");
                int k = filename.lastIndexOf("-");
                suf = filename.substring(k + 1, j);//得到文件后缀


                if (suf.equalsIgnoreCase(name))//判断是不是后缀的文件
                {
//		                    String strFileName = files[i].getAbsolutePath().toLowerCase();
                    soundList.add(files[i].getName());//对于文件才把它加到list中
                }
            }

        }
    }

    //递归查找的所有图片文件
    private void picFile(String strPath, String name) {
        picList = new ArrayList<>();
        String filename;//文件名
        String suf;//文件后缀
        File dir = new File(strPath);//文件夹dir
        File[] files = dir.listFiles();//文件夹下的所有文件或文件夹
        if (files == null)
            return;

        for (int i = 0; i < files.length; i++) {

            if (files[i].isDirectory()) {
                picFile(files[i].getAbsolutePath(), name);//递归文件夹！！！

            } else {
//		                filename = files[i].getName();
//		                int j = filename.lastIndexOf(".");
//		                suf = filename.substring(j+1);//得到文件后缀
                filename = files[i].getName();
                int j = filename.lastIndexOf(".");
                int k = filename.lastIndexOf("-");
                suf = filename.substring(k + 1, j);//得到文件后缀

//		                if(suf.equalsIgnoreCase("jpg")||suf.equalsIgnoreCase("png"))
                if (suf.equalsIgnoreCase(name))//判断是不是后缀的文件
                {
//		                    String strFileName = files[i].getAbsolutePath().toLowerCase();
//                    picList.add(new JdP);//对于文件才把它加到list中
                }
            }

        }
    }

    // 查询
    private void selectDB(String jqId) {
        Mlog.d("jqId = " + jqId);
        mJingDians.clear();

        ResDataDao resDataDao = new ResDataDao(context);
        List<ResDataBean> resDataBeans = resDataDao.getData(jqId + "");
        mJingDians.addAll(resDataBeans);
        notifyDataSetChanged();


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
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }


}
