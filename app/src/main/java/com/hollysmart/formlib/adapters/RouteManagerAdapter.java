package com.hollysmart.formlib.adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.d.lib.slidelayout.SlideLayout;
import com.d.lib.slidelayout.SlideManager;
import com.d.lib.xrv.adapter.CommonAdapter;
import com.d.lib.xrv.adapter.CommonHolder;
import com.hollysmart.apis.ResRouteDataGetAPI;
import com.hollysmart.apis.ResRouteDeleteAPI;
import com.hollysmart.formlib.apis.SaveResRouateAPI;
import com.hollysmart.beans.LuXianInfo;
import com.hollysmart.beans.PointInfo;
import com.hollysmart.db.DatabaseHelper;
import com.hollysmart.db.LuXianDao;
import com.hollysmart.db.UserInfo;
import com.hollysmart.park.R;
import com.hollysmart.tools.JSONTool;
import com.hollysmart.tools.KMLTool;
import com.hollysmart.utils.CCM_DateTime;
import com.hollysmart.utils.Utils;
import com.hollysmart.utils.taskpool.OnNetRequestListener;
import com.hollysmart.utils.zip.XZip;
import com.hollysmart.value.Values;
import com.qiniu.android.common.FixedZone;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.Configuration;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UploadManager;
import com.qiniu.util.Auth;

import org.json.JSONObject;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

/**
 * Created by Lenovo on 2019/3/7.
 */

public class RouteManagerAdapter extends CommonAdapter<LuXianInfo> {

    private SlideManager manager;

    private List<LuXianInfo> luXianInfos;

    private Context context;

    private LuXianDao luXianDao;

    private boolean longClickState = false;

    public LongclickListener longclickListener;

    private UserInfo userInfo;

    public RouteManagerAdapter(LuXianDao luXianDao, UserInfo userInfo, Context context, List<LuXianInfo> datas, int layoutId) {
        super(context, datas, layoutId);
        this.context = context;
        this.luXianDao = luXianDao;
        this.userInfo = userInfo;
        this.luXianInfos = datas;
        manager = new SlideManager();
    }




    public boolean isLongClickState() {
        return longClickState;
    }

    public void setLongClickState(boolean longClickState) {
        this.longClickState = longClickState;
    }


    public void setLongclickListener(LongclickListener longclickListener) {
        this.longclickListener = longclickListener;
    }




    @Override
    public void convert(final int position, final CommonHolder holder, final LuXianInfo item) {
        holder.setText(R.id.tv_jingquName, item.getName());
        final SlideLayout slSlide = holder.getView(R.id.sl_slide);
        slSlide.setOpen(item.isOpen, false);

        if (longClickState) {
            ((ImageView)holder.getView(R.id.iv_gouxuankuang)).setVisibility(View.VISIBLE);
        } else {
            ((ImageView)holder.getView(R.id.iv_gouxuankuang)).setVisibility(View.GONE);
        }

        if (luXianInfos.get(position).isSelect()) {
            ((ImageView)holder.getView(R.id.iv_gouxuankuang)).setImageResource(R.mipmap.xuanzhong);
        } else {
            ((ImageView)holder.getView(R.id.iv_gouxuankuang)).setImageResource(R.mipmap.gouxuankuang);
        }
        if (luXianInfos.get(position).getIsUpload().equals("true")) {

            ((TextView) holder.getView(R.id.tv_syncText)).setText("已同步");
        } else {
            ((TextView) holder.getView(R.id.tv_syncText)).setText("未同步");
        }



        if (!Utils.isEmpty(userInfo.getResdataDelete()) && userInfo.getResdataDelete().equals("resdata:delete")) {
            holder.getView(R.id.tv_delete).setVisibility(VISIBLE);
        } else {
            holder.getView(R.id.tv_delete).setVisibility(GONE);
        }

        if (!Utils.isEmpty(userInfo.getResdataUpdate()) && userInfo.getResdataUpdate().equals("resdata:update")) {
            holder.getView(R.id.tv_edit).setVisibility(VISIBLE);
        } else {
            holder.getView(R.id.tv_edit).setVisibility(GONE);
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


        // 同步到后台
        holder.setViewOnClickListener(R.id.tv_tongbu, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SaveResRouateAPI(mContext,userInfo.getAccess_token(), luXianInfos.get(position), new OnNetRequestListener() {
                    @Override
                    public void onFinish() {

                    }

                    @Override
                    public void OnNext() {

                    }

                    @Override
                    public void OnResult(boolean isOk, String msg, Object object) {
                        if (isOk) {
                            Utils.showDialog(mContext, msg);
                            notifyDataSetChanged();
                        }

                    }
                }).request();

                slSlide.setOpen(false, false);

            }
        });

        // 上传到七牛云
        holder.setViewOnClickListener(R.id.tv_upload, new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                outPutKML(position);

                KMLTool kmlTool = new KMLTool(mContext);
                String fileName = item.getName() + new CCM_DateTime().Date_No() + ".kml";

                boolean kml = kmlTool.createKML(item.getId(), Values.SDCARD_FILE(Values.SDCARD_FILE) + "/"
                        + item.getName(), fileName, item.getPointInfos());

                if (kml) {

                    upLoadToqiNiu(new File(Values.SDCARD_FILE(Values.SDCARD_FILE) + "/"
                            + item.getName()+"/"+fileName));
                } else {
                    Toast.makeText(mContext, "KML导出失败", Toast.LENGTH_LONG)
                            .show();
                }



                slSlide.setOpen(false, false);

            }
        });
        // 删除
        holder.setViewOnClickListener(R.id.tv_delete, new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new ResRouteDeleteAPI(userInfo.getAccess_token(), luXianInfos.get(position), new ResRouteDeleteAPI.ResDataDeleteIF() {
                    @Override
                    public void onResDataDeleteResult(boolean isOk, String msg) {
                        if (isOk) {
                            delJQ(item.getId(), item.getName());
                            luXianInfos.remove(position);
                            Utils.showDialog(mContext,"删除成功！");
                            notifyDataSetChanged();
                            slSlide.close();
                        }

                    }
                }).request();

            }
        });

        // 分享
        holder.setViewOnClickListener(R.id.tv_share, new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                shareToQQ(item);
                slSlide.close();
            }
        });

        // 修改
        holder.setViewOnClickListener(R.id.tv_edit, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UpdateJQ(item.getId(), item.getName());
            }
        });


        holder.getView(R.id.sl_slide).setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
//                holder.setViewVisibility(R.id.tv_jingquName, View.VISIBLE);
//                notifyDataSetChanged();
//                longClickState = true;
//                if (longclickListener != null) {
//                    longclickListener.longclick();
//                }
                return false;
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
                    if (!luXianInfos.get(position).isSelect()) {
                        ((ImageView)holder.getView(R.id.iv_gouxuankuang)).setImageResource(R.mipmap.xuanzhong);
                        luXianInfos.get(position).setSelect(true);
                    } else {
                        ((ImageView)holder.getView(R.id.iv_gouxuankuang)).setImageResource(R.mipmap.gouxuankuang);
                        luXianInfos.get(position).setSelect(false);
                    }

                } else {

                    if (slSlide.isOpen()) {
                        slSlide.close();
                    } else {



                        if (Utils.isEmpty(luXianInfos.get(position).getLineCoordinates())) {
                            final LuXianInfo luXianInfo = new LuXianInfo();

                            luXianInfo.setFd_restaskid(luXianInfos.get(position).getFd_restaskid());
                            luXianInfo.setId(luXianInfos.get(position).getId());
                            luXianInfo.setFd_resmodelid(luXianInfos.get(position).getFd_resmodelid());
                            luXianInfo.setFd_resmodelname(luXianInfos.get(position).getFd_resmodelname());

                            new ResRouteDataGetAPI(userInfo.getAccess_token(), luXianInfo, new ResRouteDataGetAPI.LuXianDetailsIF() {
                                @Override
                                public void onLuXianDetailsResult(boolean isOk, LuXianInfo resDataBean) {



                                    Intent intentData = new Intent();
                                    List<LuXianInfo> showList = new ArrayList<>();
                                    showList.add(resDataBean);
                                    intentData.putExtra("routes", (Serializable) showList);

                                    Activity activity = (Activity) context;
                                    activity.setResult(3, intentData);
                                    activity.finish();

                                }
                        }).request();

                        }






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









    private void shareToQQ(LuXianInfo item) {

        Toast toast = Toast.makeText(context,
                "正在打包分享中……", Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        LinearLayout toastView = (LinearLayout) toast.getView();
        ImageView imageCodeProject = new ImageView(
                context);
        imageCodeProject.setImageResource(R.drawable.ic_launcher_background);
        toastView.addView(imageCodeProject, 0);
        toast.show();

        KMLTool kmlTool = new KMLTool(context);

        String lineCoordinates = item.getLineCoordinates();
        if (lineCoordinates != null) {
            List<PointInfo> xianlulist = new ArrayList<>();

            String[] lines = lineCoordinates.split(" ");

            for(int i=0;i<lines.length;i++) {

                String[] point = lines[i].split(",");


                PointInfo pointInfo = new PointInfo();

                pointInfo.setLongitude(new Double(point[0]));
                pointInfo.setLatitude(new Double(point[1]));
                pointInfo.setTime(point[3]);

                xianlulist.add(pointInfo);


            }

            if (kmlTool.createRouteKML(Values.SDCARD_FILE(Values.SDCARD_LUXIAN), item.getCreatetime(), xianlulist)) {
                try {
                    XZip.ZipFolder(
                            Values.SDCARD_FILE(Values.SDCARD_LUXIAN)
                                    + "/"
                                    + item.getName(),
                            Values.SDCARD_FILE(Values.SDCARD_LUXIAN)
                                    + "/"
                                    + item.getName()
                                    + ".zip");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (new JSONTool(context).createJSON(
                        item.getId(),
                        item.getName(),
                        item.getCreatetime(),
                        Values.SDCARD_FILE(Values.SDCARD_PIC))) {
                    Toast.makeText(context, "导出成功", Toast.LENGTH_LONG)
                            .show();

                } else {
                    Toast.makeText(context, "JSON导出失败",
                            Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(context, "KML导出失败", Toast.LENGTH_LONG)
                        .show();
            }

            Intent share = new Intent(Intent.ACTION_SEND);
            File file = new File(Values.SDCARD_FILE(Values.SDCARD_LUXIAN)
                    + "/" + item.getName() + ".zip");
            System.out.println("file " + file.exists());
            if (file.exists()) {
                share.putExtra(Intent.EXTRA_STREAM, getFileUri(context, file));
                share.setType("*/*");
                Activity activity = (Activity) context;
                activity.startActivity(Intent.createChooser(share, "发送"));

            } else {
                Utils.showToast(context,"分享失败，文件不存在");
            }
        }



    }


    private void delJQ(String id, String name) {

        boolean deletById = luXianDao.deletById(id);


//        SQLiteDatabase db = new DatabaseHelper(context).getWritableDatabase();
//        if (deletById) {
//            Toast.makeText(context, "路线删除成功", Toast.LENGTH_SHORT).show();
//            selectDB();
//        } else {
//            Toast.makeText(context, "路线删除失败", Toast.LENGTH_SHORT).show();
//        }
//        db.close();
    }


    // 查询
    private void selectDB() {
        luXianInfos.clear();

        List<LuXianInfo> luXianInfoList = luXianDao.getData();
        luXianInfos.addAll(luXianInfoList);
        notifyDataSetChanged();
    }


    /**
     * 导出kml文件
     *
     * @param position
     */
    private void outPutKML(int position) {
        KMLTool kmlTool = new KMLTool(context);

        List<PointInfo> xianlulist = new ArrayList<>();

        String lineCoordinates = luXianInfos.get(position).getLineCoordinates();
        String[] lines = lineCoordinates.split(" ");
        if (lines != null&&lines.length>0) {
            for(int i=0;i<lines.length;i++) {
                String[] split = lines[i].split(",");
                PointInfo pointInfo = new PointInfo();
                pointInfo.setLatitude(new Double(split[0]));
                pointInfo.setLongitude(new Double(split[1]));
                pointInfo.setTime(split[3]);

                xianlulist.add(pointInfo);

            }
        }


        if (kmlTool.createRouteKML(Values.SDCARD_FILE(Values.SDCARD_LUXIAN), luXianInfos
                .get(position).getCreatetime(), xianlulist)) {
            try {
                XZip.ZipFolder(
                        Values.SDCARD_FILE(Values.SDCARD_LUXIAN)
                                + "/"
                                + luXianInfos.get(position).getName(),
                        Values.SDCARD_FILE(Values.SDCARD_LUXIAN)
                                + "/"
                                + luXianInfos.get(position).getName()
                                + ".zip");
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (new JSONTool(context).createJSON(
                    luXianInfos.get(position).getId(),
                    luXianInfos.get(position).getName(),
                    luXianInfos.get(position).getCreatetime(),
                    Values.SDCARD_FILE(Values.SDCARD_PIC))) {
                Toast.makeText(context, "导出成功", Toast.LENGTH_LONG)
                        .show();

            } else {
                Toast.makeText(context, "JSON导出失败",
                        Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(context, "KML导出失败", Toast.LENGTH_LONG)
                    .show();
        }
    }

    private void UpdateJQ(final String id, final String oldName) {
        LayoutInflater factory = LayoutInflater.from(context);
        final View textEntryView = factory.inflate(R.layout.view_add_jingqu,
                null);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("修改路线名称");
        builder.setView(textEntryView);
        builder.setPositiveButton("修改", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                EditText editText = (EditText) textEntryView
                        .findViewById(R.id.ed_jingquName);
                if (editText.getText().toString().equals("")) {
                    Toast.makeText(context, "路线名称不能为空", Toast.LENGTH_SHORT)
                            .show();
                    return;
                }
                updateDb(oldName, editText.getText().toString(), id);
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
            }
        });
        builder.create().show();
    }

    // 修改
    private void updateDb(String oldName, String name, String id) {
        SQLiteDatabase db = new DatabaseHelper(context).getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", name);
        if (db.update(DatabaseHelper.LUXIAN_NAME, values, "id=?",
                new String[]{String.valueOf(id)}) != -1) {
            Toast.makeText(context, "路线名称修改成功", Toast.LENGTH_SHORT).show();
            selectDB();
        } else {
            Toast.makeText(context, "路线名称修改失败", Toast.LENGTH_SHORT).show();
        }
        db.close();




    }


    public interface LongclickListener{
        void longclick();
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

}
