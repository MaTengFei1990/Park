package com.hollysmart.park;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.d.lib.xrv.LRecyclerView;
import com.hollysmart.adapter.RouteManagerAdapter;
import com.hollysmart.apis.GetRouteListDataAPI;
import com.hollysmart.beans.LuXianInfo;
import com.hollysmart.beans.ResDataBean;
import com.hollysmart.db.LuXianDao;
import com.hollysmart.db.UserInfo;
import com.hollysmart.style.StyleAnimActivity;
import com.hollysmart.tools.KMLTool;
import com.hollysmart.utils.ACache;
import com.hollysmart.utils.Utils;
import com.hollysmart.value.Values;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class TaskTrackActivity extends StyleAnimActivity {


    @Override
    public int layoutResID() {
        return R.layout.activity_task_track;
    }

   private LRecyclerView lv_luxian;

    private SwipeRefreshLayout swipe;


    private LuXianDao luXianDao= new LuXianDao(mContext);

    private RecyclerView.LayoutManager layoutManager;

    @Override
    public void findView() {
        findViewById(R.id.tv_cansal).setOnClickListener(this);
        findViewById(R.id.tv_allDel).setOnClickListener(this);
        findViewById(R.id.tv_allShow).setOnClickListener(this);
        findViewById(R.id.ib_back).setOnClickListener(this);
        findViewById(R.id.tv_imPort).setOnClickListener(this);

        lv_luxian = findViewById(R.id.lv_luxian);

        layoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        lv_luxian.setLayoutManager(layoutManager);



        swipe = findViewById(R.id.swipe);
        swipe.setColorSchemeColors(
                getResources().getColor(R.color.bg_lan),
                getResources().getColor(R.color.lv),
                getResources().getColor(R.color.baise));
        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                selectDB();
                swipe.setRefreshing(false);
            }
        });


    }

    private List<LuXianInfo> luXianInfos=new ArrayList<>();
    private RouteManagerAdapter scenicRouteAdapter;

    private ResDataBean resDataBean;

    @Override
    public void init() {
        isLogin();

        String resmodelid = getIntent().getStringExtra("resmodelid");
        String TaskId = getIntent().getStringExtra("TaskId");
        resDataBean = new ResDataBean();
        resDataBean.setFd_resmodelid(resmodelid);
        resDataBean.setFdTaskId(TaskId);


        scenicRouteAdapter = new RouteManagerAdapter(luXianDao,userInfo,mContext,luXianInfos,R.layout.item_luxian2);
        lv_luxian.setAdapter(scenicRouteAdapter);

        selectDB();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ib_back:
                finish();
                break;
            case R.id.tv_imPort:
                importProject();
                break;
            case R.id.tv_cansal:
                allCancel();
                break;
            case R.id.tv_allDel:
                allDelete();
                break;
            case R.id.tv_allShow:
                allShow();
                break;
        }
    }

    /**
     * 全部加载
     */
    private void allShow() {
        List<LuXianInfo> showList = new ArrayList<>();

        for (int i=0;i<luXianInfos.size();i++) {
            LuXianInfo info = luXianInfos.get(i);
            if (info.isSelect()) {
                showList.add(info);
            }
        }

        if (showList.size() == 0) {
            Utils.showToast(mContext,"请您选择要加载的路线");
            return;
        }

        Intent intentData = new Intent();
        intentData.putExtra("routes", (Serializable) showList);
        setResult(3, intentData);
        finish();
    }

    /**
     * 全部删除
     */
    private void allDelete() {
        List<LuXianInfo> delList = new ArrayList<>();

        for (int i=0;i<luXianInfos.size();i++) {
            LuXianInfo info = luXianInfos.get(i);
            if (info.isSelect()) {
                delList.add(info);
            }
        }

        if (delList.size() == 0) {
            Utils.showToast(mContext,"请您选择要删除的路线");
            return;
        }


        luXianInfos.removeAll(delList);
        for(int i=0;i<delList.size();i++) {
            LuXianInfo delinfo = delList.get(i);
            luXianDao.deletById(delinfo.getId());
        }
        if (scenicRouteAdapter.isLongClickState()) {
            scenicRouteAdapter.setLongClickState(false);
        }

        scenicRouteAdapter.notifyDataSetChanged();
        findViewById(R.id.ll_bottom).setVisibility(View.GONE);
    }

    private void allCancel() {

        for (LuXianInfo info : luXianInfos) {
            info.setSelect(false);
            if (scenicRouteAdapter.isLongClickState()) {
                scenicRouteAdapter.setLongClickState(false);
            }
            scenicRouteAdapter.notifyDataSetChanged();
            findViewById(R.id.ll_bottom).setVisibility(View.GONE);
        }
    }

    /**
     * 导入路线
     */
    private void importProject() {
        String file = Values.SDCARD_FILE(Values.SDCARD_LUXIAN);

        try {
            Uri uri = null;
            if (Build.VERSION.SDK_INT >= 24) {
                uri = FileProvider.getUriForFile(this, "com.hollysmart.smart_newcaidian.fileprovider",new File(file));
            } else {
                uri = Uri.fromFile(new File(file));
            }
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("*/*");//无类型限制
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            startActivityForResult(intent, 2);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    // 查询
    private void selectDB() {

        luXianInfos.clear();

        List<LuXianInfo> unUpLoadDataList = luXianDao.getUnUpLoadDataList(resDataBean);
        if (unUpLoadDataList != null && unUpLoadDataList.size() > 0) {
            luXianInfos.addAll(unUpLoadDataList);
        }
        scenicRouteAdapter.notifyDataSetChanged();


        new GetRouteListDataAPI(userInfo.getAccess_token(), resDataBean, new GetRouteListDataAPI.GetRouteListDataAPIF() {
            @Override
            public void getRouteListResult(boolean isOK, List<LuXianInfo> luXianInfoList) {
                if (isOK) {

                    luXianDao.addOrUpdate(luXianInfoList);
                    luXianInfos.addAll(luXianInfoList);
                    scenicRouteAdapter.notifyDataSetChanged();

                }

            }
        }).request();
    }





    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 2) {
                Uri uri = data.getData();
                Toast.makeText(this, "文件路径：" + uri.getPath().toString(), Toast.LENGTH_SHORT).show();
                String realPathFromUri = getRealPathFromUri(mContext, uri);
                KMLTool kmlTool = new KMLTool(mContext);
                kmlTool.readxml( new File(realPathFromUri));


                selectDB();

            }
        }
    }



    /**
     * 根据Uri获取图片的绝对路径
     *
     * @param context 上下文对象
     * @param uri     图片的Uri
     * @return 如果Uri对应的图片存在, 那么返回该图片的绝对路径, 否则返回null
     */
    public static String getRealPathFromUri(Context context, Uri uri) {
        int sdkVersion = Build.VERSION.SDK_INT;
        if (sdkVersion >= 19) { // api >= 19
            return getRealPathFromUriAboveApi19(context, uri);
        } else { // api < 19
            return getRealPathFromUriBelowAPI19(context, uri);
        }
    }

    /**
     * 适配api19以下(不包括api19),根据uri获取图片的绝对路径
     *
     * @param context 上下文对象
     * @param uri     图片的Uri
     * @return 如果Uri对应的图片存在, 那么返回该图片的绝对路径, 否则返回null
     */
    private static String getRealPathFromUriBelowAPI19(Context context, Uri uri) {
        return getDataColumn(context, uri, null, null);
    }

    /**
     * 适配api19及以上,根据uri获取图片的绝对路径
     *
     * @param context 上下文对象
     * @param uri     图片的Uri
     * @return 如果Uri对应的图片存在, 那么返回该图片的绝对路径, 否则返回null
     */
    @SuppressLint("NewApi")
    private static String getRealPathFromUriAboveApi19(Context context, Uri uri) {
        String filePath = null;
        if (DocumentsContract.isDocumentUri(context, uri)) {
            // 如果是document类型的 uri, 则通过document id来进行处理
            String documentId = DocumentsContract.getDocumentId(uri);
            if (isMediaDocument(uri)) { // MediaProvider
                // 使用':'分割
                String id = documentId.split(":")[1];

                String selection = MediaStore.Images.Media._ID + "=?";
                String[] selectionArgs = {id};
                filePath = getDataColumn(context, MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection, selectionArgs);
            } else if (isDownloadsDocument(uri)) { // DownloadsProvider
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(documentId));
                filePath = getDataColumn(context, contentUri, null, null);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())){
            // 如果是 content 类型的 Uri
            filePath = getDataColumn(context, uri, null, null);
        } else if ("file".equals(uri.getScheme())) {
            // 如果是 file 类型的 Uri,直接获取图片对应的路径
            filePath = uri.getPath();
        }
        return filePath;
    }


    /**
     * 获取数据库表中的 _data 列，即返回Uri对应的文件路径
     * @return
     */
    private static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        String path = null;

        String[] projection = new String[]{MediaStore.Images.Media.DATA};
        Cursor cursor = null;
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                int columnIndex = cursor.getColumnIndexOrThrow(projection[0]);
                path = cursor.getString(columnIndex);
            }
        } catch (Exception e) {
            if (cursor != null) {
                cursor.close();
            }
        }
        return path;
    }

    /**
     * @param uri the Uri to check
     * @return Whether the Uri authority is MediaProvider
     */
    private static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri the Uri to check
     * @return Whether the Uri authority is DownloadsProvider
     */
    private static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
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
