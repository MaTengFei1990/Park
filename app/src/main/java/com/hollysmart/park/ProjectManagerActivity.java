package com.hollysmart.park;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.d.lib.xrv.LRecyclerView;
import com.hollysmart.adapter.ProjectItemAdapter;
import com.hollysmart.adapter.TitleViewAdapter;
import com.hollysmart.apis.ResModelListAPI;
import com.hollysmart.apis.RestaskDeleteAPI;
import com.hollysmart.apis.SaveResTaskAPI;
import com.hollysmart.apis.formlib.getResTaskListAPI;
import com.hollysmart.beans.ProjectBean;
import com.hollysmart.beans.ResDataBean;
import com.hollysmart.beans.ResModelBean;
import com.hollysmart.beans.StateBean;
import com.hollysmart.db.ProjectDao;
import com.hollysmart.db.ResDataDao;
import com.hollysmart.db.ResModelDao;
import com.hollysmart.db.UserInfo;
import com.hollysmart.dialog.LoadingProgressDialog;
import com.hollysmart.listener.TextClickListener;
import com.hollysmart.style.StyleAnimActivity;
import com.hollysmart.utils.ACache;
import com.hollysmart.utils.Utils;
import com.hollysmart.value.Values;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.support.v7.widget.RecyclerView.*;


/***
 * 项目管理界面
 */


public class ProjectManagerActivity extends StyleAnimActivity implements TextClickListener,getResTaskListAPI.ResTaskListIF,ResModelListAPI.ResModelListIF {


    @Override
    public int layoutResID() {
        return R.layout.activity_project_manager;
    }



    public static final int  ALL_PROJECT_TYPE=0;     //全部

    public static final int  WAITING_PROJECT_TYPE=1; //待办

    public static final int  BEING_PROJECT_TYPE=2;   //进行中

    public static final int  FINISH_PROJECT_TYPE=3;  //已完成

    public static final int ADD_PROJECT_FLAG=0x11;
    public static final int ADD_PROJECT_FLAG_OK=0x12;

    public static final int ADD_RESDATA_FLAG=0x13;
    public static final int ADD_RESDATA_FLAG_OK=0x14;

    private LoadingProgressDialog lpd;

    private SwipeRefreshLayout swipe;

    private LinearLayout ll_tip;

    private int CurrentState=0;

    private List<StateBean> stateBeanList = new ArrayList<>();

    @Override
    public void findView() {
        requestPermisson();
        isLogin();
        setLpd();

        findViewById(R.id.iBtn).setOnClickListener(this);

        findViewById(R.id.iv_back).setOnClickListener(this);
        findViewById(R.id.iv_search).setOnClickListener(this);

        findViewById(R.id.tv_allupload).setOnClickListener(this);
        findViewById(R.id.tv_allDel).setOnClickListener(this);
        findViewById(R.id.tv_cansal).setOnClickListener(this);

        recy_title = findViewById(R.id.recy_title);
        rcy_view = findViewById(R.id.rcy_view);
        swipe = findViewById(R.id.swipe);
        ll_tip = findViewById(R.id.ll_tip);


        layoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        rcy_view.setLayoutManager(layoutManager);

        swipe.setColorSchemeColors(
                getResources().getColor(R.color.bg_lan),
                getResources().getColor(R.color.lv),
                getResources().getColor(R.color.baise));
        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (CurrentState == ALL_PROJECT_TYPE) {
                    pageNo = 1;
                    isLoadMore = false;
                    isRefresh = true;
                    allprojectList.clear();
                    projectItemAdapter.notifyDataSetChanged();
                    new getResTaskListAPI( userInfo.getAccess_token(),map.get("id"), pageNo, ProjectManagerActivity.this).request();

                } else {
                    swipe.setRefreshing(false);
                }


            }
        });

        rcy_view.addOnScrollListener(new EndLessRecyclerOnScrollListener() {
            @Override
            public void onLoadMore() {
                if (CurrentState == ALL_PROJECT_TYPE) {

                    isLoadMore=true;
                    isRefresh=false;
                    if (projectShowList.size() < allCount) {
                        nextPage();

                    }
                }

            }
        });




    }

    private void nextPage() {
        if (isLoadMore) {
            new getResTaskListAPI( userInfo.getAccess_token(),map.get("id"),pageNo,this).request();
        }


    }




    Map<String, String> map = new HashMap<String , String>();
    @Override
    public void init() {
        isLogin();

        map = (Map<String, String>) getIntent().getSerializableExtra("exter");


        newAddProject();


        initTitle();
        setTitle();
        initListData();

    }

    private void newAddProject() {
        map.toString();

        ProjectBean projectBean = new ProjectBean();

        projectBean.setRemarks("");
        projectBean.setfTaskname(map.get("name"));
        projectBean.setfTaskmodel(map.get("type"));
        projectBean.setfBegindate(map.get("btime"));
        projectBean.setfEnddate(map.get("etime"));
        projectBean.setfState("2");
        projectBean.setfRange("");
        projectBean.setId(map.get("id"));
        projectBean.setfOfficeId(userInfo.getOffice().getId());
        projectBean.setfTaskmodelnames(map.get("typename"));
        projectBean.setfDescription("");


        new SaveResTaskAPI(userInfo.getAccess_token(), projectBean, new SaveResTaskAPI.SaveResTaskIF() {
            @Override
            public void onSaveResTaskResult(boolean isOk, ProjectBean projectBean) {

                if (isOk) {
                    new getResTaskListAPI( userInfo.getAccess_token(),map.get("id"),pageNo,ProjectManagerActivity.this).request();

                }





            }
        }).request();

    }

    private void setLpd() {
        lpd = new LoadingProgressDialog();
        lpd.setMessage("正在保存，请稍等...");
        lpd.create(this, lpd.STYLE_SPINNER);
    }




    private void initTitle() {

        StateBean bean1 = new StateBean();
        bean1.setTitle("全部");
        bean1.setSelect(true);
        bean1.setState(ALL_PROJECT_TYPE);
        stateBeanList.add(bean1);


        StateBean bean2 = new StateBean();
        bean2.setTitle("待办");
        bean2.setState(WAITING_PROJECT_TYPE);
        stateBeanList.add(bean2);

        StateBean bean3 = new StateBean();
        bean3.setTitle("进行中");
        bean3.setState(BEING_PROJECT_TYPE);
        stateBeanList.add(bean3);

        StateBean bean4 = new StateBean();
        bean4.setTitle("已完成");
        bean4.setState(FINISH_PROJECT_TYPE);
        stateBeanList.add(bean4);
    }


    private TitleViewAdapter titleViewAdapter;

    private void setTitle() {

         titleViewAdapter = new TitleViewAdapter(mContext, stateBeanList);

        GridLayoutManager manager = new GridLayoutManager(this, stateBeanList.size());
        manager.setOrientation(GridLayoutManager.VERTICAL);
        recy_title.setLayoutManager(manager);

        recy_title.setAdapter(titleViewAdapter);

        titleViewAdapter.setTextClickListener(this);

    }






    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.iv_search:
                Intent searchintent = new Intent(getApplicationContext(), SearchActivity.class);

//                searchintent.putExtra("alllist", (Serializable) projectShowList);

                startActivity(searchintent);
                break;
            case R.id.iBtn:
//                Intent intent = new Intent(getApplicationContext(), NewAddProjectActivity.class);
//                startActivity(intent);
                break;
            case R.id.tv_allupload:
                allupLoad();
                break;
            case R.id.tv_allDel:
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
                builder.setTitle("是否删除该项目？");
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        lpd.setMessage("正在删除项目中...");
                        lpd.show();
                       allDelete();


                    }
                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                builder.create().show();

                break;
            case R.id.tv_cansal:
                allcansal();
                break;
        }

    }

    private void allcansal() {

        for (ProjectBean info : allprojectList) {
            info.setSelect(false);
            if (projectItemAdapter.isLongClickState()) {
                projectItemAdapter.setLongClickState(false);
            }
            projectItemAdapter.notifyDataSetChanged();
            findViewById(R.id.ll_bottom).setVisibility(View.GONE);
        }
    }


    /**
     * 全部加载
     */
    private void allShow() {
        List<ProjectBean> showList = new ArrayList<>();

        for (int i = 0; i< allprojectList.size(); i++) {
            ProjectBean info = allprojectList.get(i);
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
        List<ProjectBean> delList = new ArrayList<>();
        ProjectDao projectDao = new ProjectDao(mContext);

        for (int i = 0; i< allprojectList.size(); i++) {
            ProjectBean info = allprojectList.get(i);
            if (info.isSelect()) {
                delList.add(info);
            }
        }

        if (delList.size() == 0) {
            Utils.showToast(mContext,"请您选择要删除的路线");
            return;
        }


        allprojectList.removeAll(delList);
        for(int i=0;i<delList.size();i++) {
            ProjectBean delinfo = delList.get(i);
            projectDao.deletByProjectId(delinfo.getId());
        }

        new RestaskDeleteAPI(userInfo.getAccess_token(), delList, new RestaskDeleteAPI.RestaskDeleteIF() {
            @Override
            public void onRestaskDeleteResult(boolean isOk, String msg) {

                if (isOk) {

                    lpd.cancel();
                }

            }
        }).request();


        if (projectItemAdapter.isLongClickState()) {
            projectItemAdapter.setLongClickState(false);
        }

        projectItemAdapter.notifyDataSetChanged();
        findViewById(R.id.ll_bottom).setVisibility(View.GONE);
    }

    /***
     * 全部上传
     */

    private void allupLoad() {

        List<ProjectBean> uploadList = new ArrayList<>();

        for (int i = 0; i< allprojectList.size(); i++) {
            ProjectBean info = allprojectList.get(i);
            if (info.isSelect()) {
                uploadList.add(info);
            }
        }

        if (uploadList.size() == 0) {
            Utils.showToast(mContext,"请您选择要删除的路线");
            return;
        }


        {

            //上传逻辑
        }


        if (projectItemAdapter.isLongClickState()) {
            projectItemAdapter.setLongClickState(false);
        }

        projectItemAdapter.notifyDataSetChanged();
        findViewById(R.id.ll_bottom).setVisibility(View.GONE);
    }



    private final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1;
    private static final int REQUEST_CODE_PERMISSION_CAMERA = 0x09;
    private final int MY_PERMISSIONS_REQUEST_CALL = 2;
    private void requestPermisson() {

        if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(ProjectManagerActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_COARSE_LOCATION}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    // 权限请求成功的操作
                } else {
                    // 权限请求失败的操作
                    Utils.showToast(mContext, "请在权限管理中设置存储权限，不然会影响正常使用");
                }
                break;
            case MY_PERMISSIONS_REQUEST_CALL:
                // 权限请求成功的操作
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                    return;
                } else {
                    // 权限请求失败的操作
                    Utils.showToast(mContext, "请在权限管理中设置打电话权限，不然会影响正常使用");
                }
                break;
            case REQUEST_CODE_PERMISSION_CAMERA:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                } else {
                    Toast.makeText(this, "请授权使用camera权限", Toast.LENGTH_SHORT).show();
                }
                break;
        }

    }








    private LRecyclerView rcy_view;
    private RecyclerView recy_title;

    private LayoutManager layoutManager;

    private ProjectItemAdapter projectItemAdapter;

    private List<ProjectBean> projectShowList = new ArrayList<>();
    private List<ProjectBean> allprojectList = new ArrayList<>();




    private void initListData() {

        projectItemAdapter = new ProjectItemAdapter(this,userInfo,lpd, projectShowList, R.layout.adapter_slide);

        rcy_view.setAdapter(projectItemAdapter);

        notifyDataChange(CurrentState);

        projectItemAdapter.setLongclickListener(new ProjectItemAdapter.LongclickListener() {
            @Override
            public void longclick() {
                if (projectItemAdapter.isLongClickState()) {
                    findViewById(R.id.ll_bottom).setVisibility(View.VISIBLE);
                }

            }
        });
        projectItemAdapter.setRefreshDataChangeListener(new ProjectItemAdapter.RefreshDataChangeListener() {
            @Override
            public void refreshDataChange() {
                notifyDataChange(CurrentState);
            }
        });
        pageNo=1;
        isRefresh = true;

//        new getResTaskListAPI( userInfo.getAccess_token(),map.get("id"),pageNo,this).request();
        new ResModelListAPI(userInfo.getAccess_token(),this).request();


        if (projectShowList.size() == 0) {
            ll_tip.setVisibility(View.VISIBLE);
        } else {
            ll_tip.setVisibility(View.GONE);
        }

    }




    @Override
    public void onClick(int State) {

        CurrentState = State;

        notifyDataChange(State);

    }




    private void notifyDataChange(int state) {

        for (StateBean stateBean : stateBeanList) {
            if (stateBean.getState() == state) {
                stateBean.setSelect(true);
            } else {
                stateBean.setSelect(false);

            }

        }
        titleViewAdapter.notifyDataSetChanged();



        ProjectDao projectDao = new ProjectDao(mContext);
        projectDao.addOrUpdate(allprojectList);
        projectShowList.clear();

        ResDataDao resDataDao = new ResDataDao(mContext);

        for (ProjectBean projectBean : allprojectList) {

            int netCount = 0;
            ProjectBean data = projectDao.getDataByID(projectBean.getId());
            if (data != null) {
                netCount = data.getNetCount();
            }

            List<ResDataBean> syncData = resDataDao.getSyncData(projectBean.getId());
            List<ResDataBean> allResData = resDataDao.getData(projectBean.getId());
            if (syncData == null) {
                projectBean.setAllConunt(0+netCount);

            } else {
                projectBean.setAllConunt(allResData.size()+netCount);
            }
            if (allResData == null) {
                projectBean.setSyncCount(0+netCount);
            } else {
                projectBean.setSyncCount(syncData.size()+netCount);
            }



        }

        if (state == ALL_PROJECT_TYPE) {

            projectShowList.addAll(allprojectList);
            currentCount = allCount;

        } else {

            for (ProjectBean projectBean : allprojectList) {

                if (projectBean.getfState().equals(CurrentState + "")) {

                    projectShowList.add(projectBean);

                }
            }

            currentCount = projectShowList.size();

        }

        projectItemAdapter.notifyDataSetChanged();
        if (projectShowList.size() < currentCount) {

            projectItemAdapter.setLoadState(projectItemAdapter.LOADING);
        } else {
            projectItemAdapter.setLoadState(projectItemAdapter.LOADING_END);
        }

        if (projectShowList.size() == 0) {
            ll_tip.setVisibility(View.VISIBLE);
        } else {
            ll_tip.setVisibility(View.GONE);
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
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }


    private int pageNo ;
    private boolean isRefresh;
    private int allCount;
    private int currentCount;
    private int BeingCount;
    private int FinishCount;
    boolean isLoadMore=true;


    @Override
    public void onResTaskListResult(boolean isOk, List<ProjectBean> projectList,int count) {

        if (isOk && projectList != null) {
            allCount = count;
            allprojectList.addAll(projectList);
            if (isRefresh) {
                swipe.setRefreshing(false);
                notifyDataChange(CurrentState);
                pageNo++;
                return;
            }
            if (projectShowList.size() == currentCount) {
                isLoadMore = false;

            } else{
                pageNo++;
            }
            notifyDataChange(CurrentState);
        }



    }

    @Override
    public void onResModelListResult(boolean isOk, List<ResModelBean> projectBeanList) {


        if (isOk) {
            ResModelDao resModelDao = new ResModelDao(mContext);
            resModelDao.addOrUpdate(projectBeanList);
        }

    }


    public abstract class EndLessRecyclerOnScrollListener extends RecyclerView.OnScrollListener {
        //用来标记是否正在向上滑动
        private boolean isSlidingUpward = false;


        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);

            LinearLayoutManager manager = (LinearLayoutManager) recyclerView.getLayoutManager();
            // 当不滑动时
            if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                //获取最后一个完全显示的itemPosition
                int lastItemPosition = manager.findLastCompletelyVisibleItemPosition();
                int itemCount = manager.getItemCount();

                // 判断是否滑动到了最后一个item，并且是向上滑动
                if (lastItemPosition == (itemCount - 1) && isSlidingUpward) {
                    //加载更多
                    onLoadMore();
                }
            }
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            // 大于0表示正在向上滑动，小于等于0表示停止或向下滑动
            isSlidingUpward = dy > 0;
        }

        /**
         * 加载更多回调
         */
        public abstract void onLoadMore();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {

            case ADD_PROJECT_FLAG:


                if (resultCode == ADD_PROJECT_FLAG_OK) {

                    if (CurrentState == ALL_PROJECT_TYPE) {
                        pageNo = 1;
                        isLoadMore = false;
                        isRefresh = true;
                        allprojectList.clear();
                        projectItemAdapter.notifyDataSetChanged();
                        new getResTaskListAPI(userInfo.getAccess_token(), map.get("id"),pageNo, ProjectManagerActivity.this).request();

                    } else {
                        swipe.setRefreshing(false);
                    }

                }


                break;

            case ADD_RESDATA_FLAG:
                notifyDataChange(CurrentState);
                break;
        }

    }
}

