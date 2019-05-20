//package com.hollysmart.park;
//
//import android.content.Intent;
//import android.view.View;
//import android.widget.EditText;
//import android.widget.TextView;
//
//import com.hollysmart.beans.ProjectBean;
//import com.hollysmart.db.ProjectDao;
//import com.hollysmart.db.UserInfo;
//import com.hollysmart.dialog.LoadingProgressDialog;
//import com.hollysmart.style.StyleAnimActivity;
//import com.hollysmart.utils.ACache;
//import com.hollysmart.utils.CCM_DateTime;
//import com.hollysmart.utils.Utils;
//import com.hollysmart.value.Values;
//
//import java.io.File;
//
//import cn.qqtheme.framework.picker.Date2Picker;
//
//
//public class NewAddProjectActivity extends StyleAnimActivity implements Date2Picker.OnYearMonthDayHourMinPickListener {
//
//
//    @Override
//    public int layoutResID() {
//        return R.layout.activity_newadd_project;
//    }
//
//
//    private EditText ed_projectName;
//
//    private TextView tv_startTime;
//    private TextView tv_endTime;
//
//    private EditText et_remark;
//    private TextView tv_classifyNames;
//    private TextView tv_range;
//    private TextView tv_title;
//
//
//    private Date2Picker picker;
//
//    @Override
//    public void findView() {
//
//        findViewById(R.id.rl_range).setOnClickListener(this);
//        findViewById(R.id.iv_back).setOnClickListener(this);
//
//        findViewById(R.id.iv_shure).setOnClickListener(this);
//
//        findViewById(R.id.rl_startTime).setOnClickListener(this);
//        findViewById(R.id.rl_endTime).setOnClickListener(this);
//
//        findViewById(R.id.rl_banLiDanWei).setOnClickListener(this);
//        findViewById(R.id.rl_classifyManager).setOnClickListener(this);
//
//
//        tv_title = findViewById(R.id.tv_title);
//        ed_projectName = findViewById(R.id.ed_projectName);
//        tv_startTime = findViewById(R.id.tv_startTime);
//        tv_endTime = findViewById(R.id.tv_endTime);
//        et_remark = findViewById(R.id.et_remark);
//        tv_classifyNames = findViewById(R.id.tv_classifyNames);
//        tv_range = findViewById(R.id.tv_range);
//
//
//    }
//
//
//    private ProjectDao projectDao;
//
//    private int TimeFalg = 1;// 开始时间 1 ，结束时间 2
//
//    private ProjectBean projectBean;
//
//    private int editFlag;
//    private LoadingProgressDialog lpd;
//
//    @Override
//    public void init() {
//
//        isLogin();
//        setLpd();
//
//        picker = new Date2Picker(this, 4);
//        CCM_DateTime dateTime = new CCM_DateTime();
//
//        int year = dateTime.getYear();
//        int month = dateTime.getMonth();
//        int day = dateTime.getDay();
//        int hour = dateTime.getHour();
//        int minute = dateTime.getMinute();
//
//        picker.setSelectedItemData(year,month,day,hour+"",minute+"");
//        picker.setOnDatePickListener(this);
//
//        projectDao = new ProjectDao(mContext);
//        projectBean = new ProjectBean();
//
//        editFlag = getIntent().getIntExtra("editFlag", -1);
//
//        if (editFlag == 2) {
//            tv_title.setText("项目编辑");
//            projectBean = (ProjectBean) getIntent().getSerializableExtra("projectBean");
//
//            setData2View(projectBean);
//
//
//        } else {
//
//            projectBean = new ProjectBean();
//        }
//
//    }
//
//    private void setData2View(ProjectBean projectBean) {
//
//        ed_projectName = findViewById(R.id.ed_projectName);
//        tv_startTime = findViewById(R.id.tv_startTime);
//        tv_endTime = findViewById(R.id.tv_endTime);
//        et_remark = findViewById(R.id.et_remark);
//        tv_classifyNames = findViewById(R.id.tv_classifyNames);
//        tv_range = findViewById(R.id.tv_range);
//
//        String name = projectBean.getfTaskname();
//
//        String startTime = projectBean.getfBegindate();
//        String endTime = projectBean.getfEnddate();
//        String remark = projectBean.getRemarks();
//        String classifyNames = projectBean.getfTaskmodelnames();
//        String ranges = projectBean.getfRange();
//
//
//        if (!Utils.isEmpty(name)) {
//            ed_projectName.setText(name);
//
//        }
//        if (!Utils.isEmpty(startTime)) {
//            tv_startTime.setText(startTime);
//
//        }
//        if (!Utils.isEmpty(endTime)) {
//            tv_endTime.setText(endTime);
//
//        }
//        if (!Utils.isEmpty(classifyNames)) {
//            tv_classifyNames.setText(classifyNames);
//
//        }
//        if (!Utils.isEmpty(ranges)) {
//            tv_range.setText(ranges);
//
//        }
//        if (!Utils.isEmpty(remark)) {
//            et_remark.setText(remark);
//
//        }
//
//
//    }
//
//    private void setLpd() {
//        lpd = new LoadingProgressDialog();
//        lpd.setMessage("正在保存，请稍等...");
//        lpd.create(this, lpd.STYLE_SPINNER);
//    }
//
//    @Override
//    public void onClick(View v) {
//
//        switch (v.getId()) {
//            case R.id.iv_back:
//                finish();
//                break;
//            case R.id.rl_range:
//                Intent rangeintent = new Intent(mContext, RangeActivity.class);
//                if (editFlag == 2 && !Utils.isEmpty(projectBean.getfRange())) {
//
//                    rangeintent.putExtra("ranges", projectBean.getfRange());
//
//                }
//
//                startActivityForResult(rangeintent, 3);
//                break;
//            case R.id.iv_shure:
//                addProject();
//                break;
//            case R.id.rl_startTime:
//                TimeFalg = 1;
//                pickershow();
//                break;
//            case R.id.rl_endTime:
//                TimeFalg = 2;
//                pickershow();
//                break;
//            case R.id.rl_banLiDanWei:
//                break;
//            case R.id.rl_classifyManager:
//                Intent classifyManagerintent = new Intent(mContext, ClassifyManagerActivity.class);
//
//                if (editFlag == 2 && !Utils.isEmpty(projectBean.getfTaskmodel())) {
//
//                    classifyManagerintent.putExtra("classifyIds", projectBean.getfTaskmodel());
//
//                }
//                startActivityForResult(classifyManagerintent, 2);
//                break;
//
//        }
//
//    }
//
//    private void addProject() {
//
//        final String projectName = ed_projectName.getText().toString();
//
//        String startTime = tv_startTime.getText().toString();
//        String endTime = tv_endTime.getText().toString();
//
//        String remark = et_remark.getText().toString();
//
//
//        if (Utils.isEmpty(projectName)) {
//
//            Utils.showToast(mContext, "请填写项目名称");
//            return;
//        }
//        if (Utils.isEmpty(startTime)) {
//
//            Utils.showToast(mContext, "请选择开始日期");
//            return;
//        }
//        if (Utils.isEmpty(endTime)) {
//
//            Utils.showToast(mContext, "请选择结束日期");
//            return;
//        }
//        if (Utils.isEmpty(remark)) {
//
//            Utils.showToast(mContext, "请填写项目备注");
//            return;
//        }
//
//
//        projectBean.setfTaskname(projectName);
//        projectBean.setfBegindate(startTime);
//        projectBean.setfEnddate(endTime);
//        projectBean.setRemarks(remark);
//        projectBean.setfState("2");
//        if (editFlag == 2) {
//            lpd.setMessage("项目修改中，请稍后...");
//        } else {
//            lpd.setMessage("项目增加中，请稍后...");
//        }
//        lpd.show();
//
//        new SaveResTaskAPI(userInfo.getAccess_token(), projectBean, new SaveResTaskAPI.SaveResTaskIF() {
//            @Override
//            public void onSaveResTaskResult(boolean isOk, ProjectBean projectBean) {
//
//                if (isOk) {
//
//                    boolean addOrUpdate = projectDao.addOrUpdate(projectBean);
//
//                    if (addOrUpdate) {
//                        CreateDir(Values.SDCARD_DIR);
//                        CreateDir(Values.SDCARD_FILE(Values.SDCARD_FILE) + "/" + projectName);
//                        CreateDir(Values.SDCARD_FILE(Values.SDCARD_FILE) + "/" + projectName + "/"
//                                + Values.SDCARD_SOUNDS);
//                        CreateDir(Values.SDCARD_FILE(Values.SDCARD_FILE) + "/" + projectName + "/"
//                                + Values.SDCARD_PIC);
//                        if (editFlag == 2) {
//
//                            Utils.showToast(mContext, "项目修改成功");
//                        } else {
//                            Utils.showToast(mContext, "项目添加成功");
//                        }
//
//                        finish();
//                    } else {
//                        if (editFlag == 2) {
//                            Utils.showToast(mContext, "项目修改失败");
//                        } else {
//                            Utils.showToast(mContext, "项目添加失败");
//                        }
//                    }
//
//
//                    Utils.showToast(mContext,"新建成功");
//
//                }
//
//                lpd.cancel();
//
//            }
//        }).request();
//
//
//
//    }
//
//
//    private File CreateDir(String folder) {
//        File dir = new File(folder);
//        dir.mkdirs();
//        return dir;
//    }
//
//
//    /**
//     * 日期 选择器
//     *
//     * @param
//     */
//    private void pickershow() {
//
//        picker.show();
//    }
//
//    @Override
//    public void onDatePicked(String year, String month, String day, String hour, String min) {
//
//        if (Utils.isEmpty(hour)) {
//            hour = "00";
//        }
//        if (Utils.isEmpty(min)) {
//            min = "00";
//        }
//
//        if (TimeFalg == 1) {
//            tv_startTime.setText(year + "-" + month + "-" + day +"  " + hour + ":" + min);
//        }
//        if (TimeFalg == 2) {
//            tv_endTime.setText(year + "-" + month + "-" + day  + "  " + hour + ":" + min);
//        }
//
//    }
//
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        switch (requestCode) {
//            case 2:
//                if (resultCode==2) {
//                    String classifyNames = data.getStringExtra("classifyNames");
//                    String classifyIds = data.getStringExtra("classifyIds");
//                    String versions = data.getStringExtra("versions");
//                    if (!Utils.isEmpty(classifyNames)) {
//
//                        tv_classifyNames.setText(classifyNames);
//
//                        projectBean.setfTaskmodel(classifyIds);
//                        projectBean.setfTaskmodelnames(classifyNames);
//                        projectBean.setfVersion(versions);
//
//                    }
//
//                }
//
//                break;
//            case 3:
//                if (resultCode == 3) {
//                    String strPoints = data.getStringExtra("strPoints");
//                    if (!Utils.isEmpty(strPoints)) {
//
//                        tv_range.setText(strPoints);
//
//                        projectBean.setfRange(strPoints);
//
//                    }
//
//                }
//
//
//
//                break;
//        }
//    }
//
//    /**
//     * 判断用户登录状态，登录获取用户信息
//     */
//    private UserInfo userInfo;
//
//    public boolean isLogin() {
//        if (userInfo != null)
//            return true;
//        try {
//            String userPath = Values.SDCARD_FILE(Values.SDCARD_CACHE) + Values.CACHE_USER;
//            Object obj = ACache.get(new File(userPath)).getAsObject(Values.CACHE_USERINFO);
//            if (obj != null) {
//                userInfo = (UserInfo) obj;
//                return true;
//            }
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//        return false;
//    }
//
//}
