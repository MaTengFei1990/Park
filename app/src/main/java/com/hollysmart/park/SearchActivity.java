package com.hollysmart.park;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.d.lib.xrv.LRecyclerView;
import com.hollysmart.adapter.ProjectItemAdapter;
import com.hollysmart.beans.ProjectBean;
import com.hollysmart.beans.UserInfoBean;
import com.hollysmart.db.ProjectDao;
import com.hollysmart.db.UserInfo;
import com.hollysmart.dialog.LoadingProgressDialog;
import com.hollysmart.style.StyleAnimActivity;
import com.hollysmart.utils.ACache;
import com.hollysmart.utils.Utils;
import com.hollysmart.value.Values;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends StyleAnimActivity {


    @Override
    public int layoutResID() {
        return R.layout.activity_search;
    }

    private LRecyclerView recy_view;

    private EditText et_context;


    private ProjectItemAdapter projectItemAdapter;

    @Override
    public void findView() {
        findViewById(R.id.iv_fanhui).setOnClickListener(this);
        TextView tv_title = findViewById(R.id.tv_title);
        tv_title.setText("查询");
        et_context = findViewById(R.id.et_context);

        findViewById(R.id.iv_fanhui).setOnClickListener(this);
        recy_view = findViewById(R.id.recy_view);
    }

    private LoadingProgressDialog lpd;



    private ProjectDao projectDao;

    private List<ProjectBean> projectBeanList = new ArrayList<>();

    private void initListData() {

        projectDao = new ProjectDao(mContext);

        projectItemAdapter = new ProjectItemAdapter(this, userInfo,lpd,projectBeanList, R.layout.adapter_slide);

        recy_view.setAdapter(projectItemAdapter);


    }



    @Override
    public void init() {
        isLogin();
        setLpd();
        initListData();


        et_context.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                chuaXun(et_context);
            }
        });

    }


    private void setLpd() {
        lpd = new LoadingProgressDialog();
        lpd.setMessage("正在获取列表，请稍等...");
        lpd.create(this, lpd.STYLE_SPINNER);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_fanhui:
                finish();
                break;

        }

    }

    private void chuaXun(EditText et_context) {
        String context = et_context.getText().toString();
        if (!Utils.isEmpty(context)) {
            List<ProjectBean> lsitData = projectDao.searchDataByName(context);
            projectBeanList.clear();
            projectBeanList.addAll(lsitData);
            projectItemAdapter.notifyDataSetChanged();
        }

    }


    /**
     * 判断用户登录状态，登录获取用户信息
     */
    private UserInfoBean userInfo;

    public boolean isLogin() {
        if (userInfo != null)
            return true;
        try {
            String userPath = Values.SDCARD_FILE(Values.SDCARD_CACHE) + Values.CACHE_USER;
            Object obj = ACache.get(new File(userPath)).getAsObject(Values.CACHE_USERINFO);
            if (obj != null) {
                userInfo = (UserInfoBean) obj;
                return true;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }


}
