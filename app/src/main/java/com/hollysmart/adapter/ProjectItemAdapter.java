package com.hollysmart.adapter;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.d.lib.slidelayout.SlideLayout;
import com.d.lib.slidelayout.SlideManager;
import com.d.lib.xrv.adapter.CommonAdapter;
import com.d.lib.xrv.adapter.CommonHolder;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.hollysmart.apis.RestaskDeleteAPI;
import com.hollysmart.apis.SaveResTaskAPI;
import com.hollysmart.beans.LuXianInfo;
import com.hollysmart.beans.PointInfo;
import com.hollysmart.beans.ProjectBean;
import com.hollysmart.beans.UserInfoBean;
import com.hollysmart.db.LuXianDao;
import com.hollysmart.db.ProjectDao;
import com.hollysmart.db.UserInfo;
import com.hollysmart.dialog.LoadingProgressDialog;
//import com.hollysmart.park.NewAddProjectActivity;
import com.hollysmart.park.ProjectDetails2Activity;
import com.hollysmart.park.R;
import com.hollysmart.service.SubmitFormService;
import com.hollysmart.tools.JSONTool;
import com.hollysmart.tools.KMLTool;
import com.hollysmart.utils.CCM_DateTime;
import com.hollysmart.utils.Utils;
import com.hollysmart.utils.zip.XZip;
import com.hollysmart.value.UserToken;
import com.hollysmart.value.Values;
import com.qiniu.android.common.FixedZone;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.Configuration;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UploadManager;
import com.qiniu.util.Auth;
import com.umeng.commonsdk.statistics.common.MLog;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lenovo on 2019/4/9.
 */

public class ProjectItemAdapter extends CommonAdapter<ProjectBean> {
    private SlideManager manager;

    private List<PointInfo> pointInfos=new ArrayList<>();


    private LoadingProgressDialog lpd;


    private boolean longClickState = false;

    private ProjectDao projectDao;

    private Context context;

    boolean move=false;

    private long downTime= 0;
    private List<ProjectBean> projectBeanList;




    // 普通布局
    private final int TYPE_ITEM = 1;
    // 脚布局
    private final int TYPE_FOOTER = 2;
    // 当前加载状态，默认为加载完成
    private int loadState = 2;
    // 正在加载
    public final int LOADING = 1;
    // 加载完成
    public final int LOADING_COMPLETE = 2;
    // 加载到底
    public final int LOADING_END = 3;

    public LongclickListener longclickListener;

    public ProjectItemAdapter(Context context, UserInfoBean userInfo, LoadingProgressDialog lpd, List<ProjectBean> datas, int layoutId) {
        super(context, datas, layoutId);
        this.context=context;
        this.projectBeanList=datas;
        this.lpd=lpd;
        manager = new SlideManager();
        projectDao = new ProjectDao(mContext);
    }

    public boolean isLongClickState() {
        return longClickState;
    }

    public void setLongClickState(boolean longClickState) {
        this.longClickState = longClickState;
    }


    public LongclickListener getLongclickListener() {
        return longclickListener;
    }

    public void setLongclickListener(LongclickListener longclickListener) {
        this.longclickListener = longclickListener;
    }

    @Override
    public int getItemViewType(int position) {

        if (position + 1 == getItemCount()) {
            return TYPE_FOOTER;
        } else {
            return TYPE_ITEM;
        }
    }

    @Override
    public int getItemCount() {
        if (projectBeanList.size() == 0) {
            return 0;
        } else {

            return projectBeanList.size() + 1;
        }

    }

    @Override
    public CommonHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_FOOTER) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_footer, parent, false);
            return new FootViewHolder(view);
        } else {
            return super.onCreateViewHolder(parent, viewType);
        }
    }

    @Override
    public void onBindViewHolder(CommonHolder holder, int position) {

         if (holder instanceof FootViewHolder) {
            FootViewHolder footViewHolder = (FootViewHolder) holder;
            switch (loadState) {
                case LOADING: // 正在加载
                    footViewHolder.pbLoading.setVisibility(View.VISIBLE);
                    footViewHolder.tvLoading.setVisibility(View.VISIBLE);
                    footViewHolder.ll_loading.setVisibility(View.VISIBLE);
                    footViewHolder.llEnd.setVisibility(View.GONE);
                    break;

                case LOADING_COMPLETE: // 加载完成
                    footViewHolder.pbLoading.setVisibility(View.INVISIBLE);
                    footViewHolder.tvLoading.setVisibility(View.INVISIBLE);
                    footViewHolder.ll_loading.setVisibility(View.INVISIBLE);
                    footViewHolder.llEnd.setVisibility(View.GONE);
                    break;

                case LOADING_END: // 加载到底
                    footViewHolder.pbLoading.setVisibility(View.GONE);
                    footViewHolder.tvLoading.setVisibility(View.GONE);
                    footViewHolder.ll_loading.setVisibility(View.GONE);
                    footViewHolder.llEnd.setVisibility(View.VISIBLE);
                    break;

                default:
                    break;
            }
        } else {
            super.onBindViewHolder(holder, position);
        }




    }

    @Override
    public void convert(final int position, final CommonHolder holder, final ProjectBean item) {
        holder.setText(R.id.tv_projectTitle, item.getfTaskname());
        holder.setText(R.id.tv_startTime, item.getfBegindate());
        holder.setText(R.id.tv_endTime, item.getfEnddate());
        if ("1".equals(item.getfState())) {
            holder.setText(R.id.tv_state, "待办");
            holder.setImageResource(R.id.iv_state, R.mipmap.icon_daiban);
        }
        if ("2".equals(item.getfState())) {
            holder.setText(R.id.tv_state, "进行中");
            holder.setImageResource(R.id.iv_state, R.mipmap.icon_jinxingzhong);
        }
        if ("3".equals(item.getfState())) {
            holder.setText(R.id.tv_state, "已完成");
            holder.setImageResource(R.id.iv_state, R.mipmap.icon_wancheng);
        }


        holder.setText(R.id.tv_allCount, "总数量:"+item.getAllConunt()+"条");
        holder.setText(R.id.tv_syncCount, "已同步:"+item.getSyncCount()+"条");


        final SlideLayout slSlide = holder.getView(R.id.sl_slide);
        slSlide.setOpen(item.isOpen, false);

        if (longClickState) {
            (holder.getView(R.id.iv_gouxuankuang)).setVisibility(View.VISIBLE);
        } else {
            (holder.getView(R.id.iv_gouxuankuang)).setVisibility(View.GONE);
        }

        if (item.isSelect()) {
            ((ImageView)holder.getView(R.id.iv_gouxuankuang)).setImageResource(R.mipmap.xuanzhong);
        } else {
            ((ImageView)holder.getView(R.id.iv_gouxuankuang)).setImageResource(R.mipmap.gouxuankuang);
        }

        slSlide.setOnStateChangeListener(new SlideLayout.OnStateChangeListener() {
            @Override
            public void onChange(SlideLayout layout, boolean isOpen) {
                item.isOpen = isOpen;
                manager.onChange(layout, isOpen);
            }

            @Override
            public boolean closeAll(SlideLayout layout) {
                return manager.closeAll(layout);
            }
        });


        holder.getView(R.id.sl_slide).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {

                    downTime = System.currentTimeMillis();

                    MLog.d("ACTION_DOWN time=="+downTime);

                }

                if (event.getAction() == MotionEvent.ACTION_UP) {

                    long upTime = System.currentTimeMillis();

                    MLog.d("ACTION_UP time=="+upTime);

                    long l = upTime - downTime;
                    MLog.d("ACTION_UP -ACTION_DOWN time=="+l);

                    if ((upTime - downTime) > 2000) {
                        if (move) {
                            move = false;
                            return false;

                        } else {

                            if (!item.isOpen) {
                                manager.closeAll(slSlide);
                                notifyDataSetChanged();
                                longClickState = true;
                                if (longclickListener != null) {
                                    longclickListener.longclick();
                                }

                            }
                        }

                    }


                }


                if (event.getAction() == MotionEvent.ACTION_MOVE) {

                    move=true;

                }


                return false;


            }
        });

        // 同步
        holder.setViewOnClickListener(R.id.tv_tongbu, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(mContext, NewAddProjectActivity.class);
//                intent.putExtra("projectBean", item);
//                intent.putExtra("editFlag", 2);
//                mContext.startActivity(intent);
            }
        });


        // 上传七牛云平台
        holder.setViewOnClickListener(R.id.tv_upload, new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                KMLTool kmlTool = new KMLTool(mContext);
                String fileName = item.getfTaskname() + new CCM_DateTime().Date_No() + ".kml";

                boolean kml = kmlTool.createKML(item.getId(), Values.SDCARD_FILE(Values.SDCARD_FILE) + "/"
                        + item.getfTaskname(), fileName, pointInfos);

                if (kml) {

                    upLoadToqiNiu(new File(Values.SDCARD_FILE(Values.SDCARD_FILE) + "/"
                            + item.getfTaskname()+"/"+fileName));
                } else {
                    Toast.makeText(mContext, "KML导出失败", Toast.LENGTH_LONG)
                            .show();
                }




            }
        });






        // 删除
        holder.setViewOnClickListener(R.id.tv_delete, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final List<ProjectBean> delList = new ArrayList<>();
                delList.add(mDatas.get(position));
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);
                builder.setTitle("是否删除该项目？");
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        slSlide.close();

                        new RestaskDeleteAPI(Values.TOKEN, delList, new RestaskDeleteAPI.RestaskDeleteIF() {
                            @Override
                            public void onRestaskDeleteResult(boolean isOk, String msg) {

                                if (isOk) {
                                    lpd.cancel();
                                    projectDao.deletByProjectId(mDatas.get(position).getId());
                                    mDatas.remove(position);
                                    notifyItemRemoved(position);
                                    notifyItemRangeChanged(position, mDatas.size());
                                } else {
                                    Utils.showDialog(mContext,"项目删除失败");
                                }

                            }
                        }).request();

                    }
                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                builder.create().show();




            }
        });

        // 分享
        holder.setViewOnClickListener(R.id.tv_share, new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Toast toast = Toast.makeText(mContext,
                        "正在打包分享中……", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                LinearLayout toastView = (LinearLayout) toast.getView();
                ImageView imageCodeProject = new ImageView(
                        mContext);
                imageCodeProject.setImageResource(R.drawable.ic_launcher_background);
                toastView.addView(imageCodeProject, 0);
                toast.show();

                KMLTool kmlTool = new KMLTool(mContext);
                biaodian(item);
                if (kmlTool.createKML(item.getId(),
                        Values.SDCARD_FILE(Values.SDCARD_FILE) + "/"
                                + item.getfTaskname(), item.getfTaskname(), pointInfos)) {
                    try {
                        XZip.ZipFolder(
                                Values.SDCARD_FILE(Values.SDCARD_FILE)
                                        + "/"
                                        + item.getfTaskname(),
                                Values.SDCARD_FILE(Values.SDCARD_FILE)
                                        + "/"
                                        + item.getfTaskname()
                                        + ".zip");
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    if (new JSONTool(mContext).createJSON(
                            item.getId(),
                            item.getfTaskname(),
                            item.getCreateDate(),
                            Values.SDCARD_FILE(Values.SDCARD_FILE) + "/"
                                    + item.getfTaskname())) {
                        Toast.makeText(mContext, "导出成功", Toast.LENGTH_LONG)
                                .show();

                    } else {
                        Toast.makeText(mContext, "JSON导出失败",
                                Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(mContext, "KML导出失败", Toast.LENGTH_LONG)
                            .show();
                }

                Intent share = new Intent(Intent.ACTION_SEND);
                File file = new File(Values.SDCARD_FILE(Values.SDCARD_FILE)
                        + "/" + item.getfTaskname() + ".zip");
                System.out.println("file " + file.exists());
                if (file.exists()) {
                    share.putExtra(Intent.EXTRA_STREAM, getFileUri(mContext, file));
                    share.setType("*/*");
                    Activity activity = (Activity) mContext;
                    activity.startActivity(Intent.createChooser(share, "发送"));

                } else {
                    Utils.showToast(mContext,"分享失败，文件不存在");
                }

                slSlide.close();
            }
        });


//        // 修改
//        holder.setViewOnClickListener(R.id.tv_edit, new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(mContext, NewAddProjectActivity.class);
//                intent.putExtra("projectBean", item);
//                intent.putExtra("editFlag", 2);
//                mContext.startActivity(intent);
//            }
//        });
        // 完成
        holder.setViewOnClickListener(R.id.tv_finish, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                item.setfState("3");

                new SaveResTaskAPI(Values.TOKEN, item, new SaveResTaskAPI.SaveResTaskIF() {
                    @Override
                    public void onSaveResTaskResult(boolean isOk, ProjectBean projectBean) {
                        if (isOk) {
                            Utils.showDialog(mContext,"项目已完成");
                        }

                    }
                }).request();


            }
        });

        // 同步
        holder.setViewOnClickListener(R.id.tv_tongbu, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, SubmitFormService.class);
                intent.putExtra("type", SubmitFormService.TYPE_BIANLI);
                mContext.startService(intent);

            }
        });

        holder.setViewOnClickListener(R.id.sl_slide, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (slSlide.isOpen()) {
                    slSlide.close();
                    return;
                }

                if (longClickState) {
                    if (!item.isSelect()) {
                        ((ImageView)holder.getView(R.id.iv_gouxuankuang)).setImageResource(R.mipmap.xuanzhong);
                        item.setSelect(true);
                    } else {
                        ((ImageView)holder.getView(R.id.iv_gouxuankuang)).setImageResource(R.mipmap.gouxuankuang);
                        item.setSelect(false);
                    }

                } else {

                    if (slSlide.isOpen()) {
                        slSlide.close();
                    } else {
                        Intent intent = new Intent(mContext, ProjectDetails2Activity.class);
                        intent.putExtra("projectName", item.getfTaskname());
                        intent.putExtra("projectId", item.getId());
                        intent.putExtra("classifyIds", item.getfTaskmodel());
                        intent.putExtra("projectBean", item);
                        mContext.startActivity(intent);
                    }

                }




            }
        });
    }



    /**
     * 上传到七牛
     */
    public void upLoadToqiNiu(File file) {


        String token = getToken();


        Configuration config = new Configuration.Builder()
                .zone(FixedZone.zone0)
                .build();
        UploadManager uploadManager = new UploadManager(config);
//        String token = <从服务端SDK获取>;
        uploadManager.put(file, file.getName(), token,
                new UpCompletionHandler() {
                    @Override
                    public void complete(String key, ResponseInfo info, JSONObject res) {
                        //res包含hash、key等信息，具体字段取决于上传策略的设置
                        if(info.isOK()) {
                            Log.i("qiniu", "Upload Success");
                        } else {
                            Log.i("qiniu", "Upload Fail");
                            //如果失败，这里可以把info信息上报自己的服务器，便于后面分析上传错误原因
                        }
                        Log.i("qiniu", key + ",\r\n " + info + ",\r\n " + res);
                    }
                }, null);





    }

    /**
     * 获取上传的token;
     */

    private String getToken() {


        String AccessKey = "28fPX_YqJodo-uikTzYZWDv0koVKcqvx1erzEHks";
        String SecretKey = "iZZJ7B1WFJ-Ulsry6xASIX6nL2itJAC7CkWQd-Mj";

        Auth auth = Auth.create(AccessKey, SecretKey);

        String token = auth.uploadToken("caidian");

        return token;


    }



    public class FootViewHolder extends CommonHolder {

        ProgressBar pbLoading;
        TextView tvLoading;
        LinearLayout llEnd;
        LinearLayout ll_loading;

        FootViewHolder(View itemView) {
            super(mContext, itemView, R.layout.item_footer);
            ll_loading = (LinearLayout) itemView.findViewById(R.id.ll_loading);
            pbLoading = (ProgressBar) itemView.findViewById(R.id.pb_loading);
            tvLoading = (TextView) itemView.findViewById(R.id.tv_loading);
            llEnd = (LinearLayout) itemView.findViewById(R.id.ll_end);
        }
    }

    /**
     * 设置上拉加载状态
     *
     * @param loadState 0.正在加载 1.加载完成 2.加载到底
     */
    public void setLoadState(int loadState) {
        this.loadState = loadState;
        notifyDataSetChanged();
    }





    private void biaodian(ProjectBean item) {
        File file = new File(Values.SDCARD_FILE(Values.SDCARD_FILE) + "/"
                + item.getfTaskname()+"/"
                + item.getfTaskname());


        if(file.exists()){

            file.delete();

        }

        LuXianDao luXianDao = new LuXianDao(mContext);
        List<LuXianInfo> listData = luXianDao.getData(item.getId() + "");

        try {
            ArrayList<PointInfo> pointInfoss = new ArrayList<PointInfo>();
            if (listData != null && listData.size() > 0) {


                for(int i=0;i<listData.size();i++) {
                    String luxianstr = listData.get(i).getName();
                    if (!Utils.isEmpty(luxianstr)) {
                        Gson mGson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm").create();
                        List<PointInfo> xianlulist = mGson.fromJson(luxianstr, new TypeToken<List<PointInfo>>() {
                        }.getType());
                        pointInfoss.addAll(xianlulist);
                    }


                }

            }
            pointInfos.addAll(pointInfoss);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }





    public static Uri getFileUri(Context context, File file){
        Uri uri;
        // 低版本直接用 Uri.fromFile
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            uri = Uri.fromFile(file);
        }else {
            uri = FileProvider.getUriForFile(context,"com.hollysmart.smart_newcaidian.fileprovider",file);

            ContentResolver cR = context.getContentResolver();
            if (uri != null && !TextUtils.isEmpty(uri.toString())) {
                String fileType = cR.getType(uri);
                // 使用 MediaStore 的 content:// 而不是自己 FileProvider 提供的uri，不然有些app无法适配
                if (!TextUtils.isEmpty(fileType)){
                    if (fileType.contains("video/")){
                        uri = getVideoContentUri(context, file);
                    }else if (fileType.contains("image/")){
                        uri = getImageContentUri(context, file);
                    }else if (fileType.contains("audio/")){
                        uri = getAudioContentUri(context, file);
                    }
                }
            }
        }
        return uri;
    }




    /**
     * Gets the content:// URI from the given corresponding path to a file
     *
     * @param context
     * @param imageFile
     * @return content Uri
     */
    public static Uri getImageContentUri(Context context, File imageFile) {
        String filePath = imageFile.getAbsolutePath();
        Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[] { MediaStore.Images.Media._ID }, MediaStore.Images.Media.DATA + "=? ",
                new String[] { filePath }, null);
        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
            Uri baseUri = Uri.parse("content://media/external/images/media");
            return Uri.withAppendedPath(baseUri, "" + id);
        } else {
            if (imageFile.exists()) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DATA, filePath);
                return context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            } else {
                return null;
            }
        }
    }

    /**
     * Gets the content:// URI from the given corresponding path to a file
     *
     * @param context
     * @param videoFile
     * @return content Uri
     */
    public static Uri getVideoContentUri(Context context, File videoFile) {
        String filePath = videoFile.getAbsolutePath();
        Cursor cursor = context.getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                new String[] { MediaStore.Video.Media._ID }, MediaStore.Video.Media.DATA + "=? ",
                new String[] { filePath }, null);
        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
            Uri baseUri = Uri.parse("content://media/external/video/media");
            return Uri.withAppendedPath(baseUri, "" + id);
        } else {
            if (videoFile.exists()) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Video.Media.DATA, filePath);
                return context.getContentResolver().insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, values);
            } else {
                return null;
            }
        }
    }

    /**
     * Gets the content:// URI from the given corresponding path to a file
     *
     * @param context
     * @param audioFile
     * @return content Uri
     */
    public static Uri getAudioContentUri(Context context, File audioFile) {
        String filePath = audioFile.getAbsolutePath();
        Cursor cursor = context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                new String[] { MediaStore.Audio.Media._ID }, MediaStore.Audio.Media.DATA + "=? ",
                new String[] { filePath }, null);
        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
            Uri baseUri = Uri.parse("content://media/external/audio/media");
            return Uri.withAppendedPath(baseUri, "" + id);
        } else {
            if (audioFile.exists()) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Audio.Media.DATA, filePath);
                return context.getContentResolver().insert(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, values);
            } else {
                return null;
            }
        }
    }


    public interface LongclickListener{
        void longclick();
    }
}
