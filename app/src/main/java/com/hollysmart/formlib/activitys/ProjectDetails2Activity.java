package com.hollysmart.formlib.activitys;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.hollysmart.formlib.apis.GetNetResListAPI;
import com.hollysmart.formlib.apis.ResDataGetAPI;
import com.hollysmart.formlib.apis.SaveResRouateAPI;
import com.hollysmart.beans.GPS;
import com.hollysmart.beans.LatLngToJL;
import com.hollysmart.beans.LuXianInfo;
import com.hollysmart.beans.PointInfo;
import com.hollysmart.formlib.beans.ProjectBean;
import com.hollysmart.formlib.beans.ResDataBean;
import com.hollysmart.db.ProjectDao;
import com.hollysmart.db.UserInfo;
import com.hollysmart.dialog.SheetDialogFragment;
import com.hollysmart.main.MainPresenter;
import com.hollysmart.main.MainView;
import com.hollysmart.park.R;
import com.hollysmart.style.StyleAnimActivity;
import com.hollysmart.utils.ACache;
import com.hollysmart.utils.CCM_DateTime;
import com.hollysmart.utils.CCM_Delay;
import com.hollysmart.utils.GPSConverterUtils;
import com.hollysmart.utils.Mlog;
import com.hollysmart.utils.Utils;
import com.hollysmart.utils.taskpool.OnNetRequestListener;
import com.hollysmart.value.Values;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class ProjectDetails2Activity extends StyleAnimActivity implements OnClickListener,MainView {

    private Context context;
    // 定位相关
    LocationClient mLocClient;
    public MyLocationListener myListener = new MyLocationListener();
    private LatLng mLatLng;
    BaiduMap mBaiduMap;
    MapView mMapView = null;
    boolean isFirstLoc = true;// 是否首次定位
    BitmapDescriptor bdA = BitmapDescriptorFactory.fromResource(R.mipmap.resflag_add);

    @Override
    public int layoutResID() {
        return R.layout.activity_project_details2;
    }


    @Override
    public void findView() {
        ButterKnife.bind(this);
        context = mContext;
        try {
            iv_back.setOnClickListener(this);
            bn_weixing.setOnClickListener(this);
            bn_dingwei.setOnClickListener(this);
            bn_fangda.setOnClickListener(this);
            bn_suoxiao.setOnClickListener(this);
            bn_add.setOnClickListener(this);
            bn_all.setOnClickListener(this);
            bn_more.setOnClickListener(this);
            bn_xialu.setOnClickListener(this);
            imagbtn_startOrContinue.setOnClickListener(this);
            bn_jieshu.setOnClickListener(this);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    @BindView(R.id.tv_projectName)
    TextView tv_projectName;
    @BindView(R.id.iv_back)
    ImageView iv_back;
    @BindView(R.id.bmapView)
    MapView bmapView;
    @BindView(R.id.shizi)
    ImageView shizi;
    @BindView(R.id.imagbtn_startOrContinue)
    ImageButton imagbtn_startOrContinue;
    @BindView(R.id.imagbtn_end)
    ImageButton bn_jieshu;
    @BindView(R.id.linearLayout1)
    LinearLayout linearLayout1;
    @BindView(R.id.bn_weixing)
    ImageButton bn_weixing;
    @BindView(R.id.bn_dingwei)
    ImageButton bn_dingwei;
    @BindView(R.id.imagbtn_enlarge)
    ImageButton bn_fangda;
    @BindView(R.id.imagbtn_zoomOut)
    ImageButton bn_suoxiao;
    @BindView(R.id.imagbtn_route)
    ImageButton bn_xialu;
    @BindView(R.id.bn_add)
    LinearLayout bn_add;
    @BindView(R.id.bn_all)
    LinearLayout bn_all;
    @BindView(R.id.bn_more)
    LinearLayout bn_more;
    @BindView(R.id.rl_bottom)
    RelativeLayout rl_bottom;
    @Nullable
    @BindView(R.id.iv_del)
    ImageView iv_del;
    @Nullable
    @BindView(R.id.ll_add_jingdian)
    LinearLayout ll_add_jingdian;
    @Nullable
    @BindView(R.id.ll_add_fenxiang)
    LinearLayout ll_add_fenxiang;
    @Nullable
    @BindView(R.id.panel_card_view)
    RelativeLayout panel_card_view;
    @BindView(R.id.image_luyin)
    @Nullable
    ImageView image_luyin;
    @Nullable
    @BindView(R.id.imageButton_luyin)
    ImageButton imageButton_luyin;
    @Nullable
    @BindView(R.id.button_luyin)
    Button button_luyin;
    @Nullable
    @BindView(R.id.layout_bt_luyin)
    LinearLayout layout_bt_luyin;
    @BindView(R.id.layout_luyin)
    View luyin;

    private List<String> luxianList;// 当前路线

    private ProjectBean projectBean;

    /**
     * 使用地图sdk前需先初始化BMapManager. BMapManager是全局的，可为多个MapView共用，它需要地图模块创建前创建，
     * 并在地图地图模块销毁后销毁，只要还有地图模块在使用，BMapManager就不应该销毁
     */
    private List<ResDataBean> resDatalist;
    private int mIndex = -1;
    private HashMap<Integer, Marker> mMarkers;
    private HashMap<Integer, Overlay> mOverlays;
    private LatLng dingWeiDian;
    private MainPresenter mainPresenter;

    @Override
    public void init() {
        requestPermisson();
        isLogin();
        mainPresenter=new MainPresenter(context,this);
        initMap();

        projectBean = (ProjectBean) getIntent().getSerializableExtra("projectBean");
        tv_projectName.setText(projectBean.getfTaskname());


        sheetDialogFragment = SheetDialogFragment.getInstance();

        ShowResDataDialog(true,null,false);

        sheetDialogFragment.setSeeBarRangeListener(new SheetDialogFragment.SeeBarRangeListener() {
            @Override
            public void onChange(int progress) {
                mainPresenter.getCoordinates(progress, mIndex);
            }
        });

        sheetDialogFragment.setDismissListener(new SheetDialogFragment.DismissListener() {
            @Override
            public void dismisse() {
                rl_bottom.setVisibility(View.VISIBLE);
                mBaiduMap.clear();
                mainPresenter.drawRange(projectBean.getfRange());
                if (mIndex >= resDatalist.size()) {
                    Mlog.d("还未生成数据库文件");

                    if (mOverlays.get(mIndex) != null) {
                        mOverlays.get(mIndex).remove();
                        mOverlays.remove(mIndex);
                    }
                    if (mMarkers.get(mIndex) != null) {
                        mMarkers.get(mIndex).remove();
                        mMarkers.remove(mIndex);
                    }
                    mBaiduMap.hideInfoWindow();
                    jingdianGone();
                }

                initResDataList(projectBean.getId());

            }
        });






        mBaiduMap.clear();
        mBaiduMap.hideInfoWindow();

        mMarkers = new HashMap<Integer, Marker>();
        mOverlays = new HashMap<Integer, Overlay>();

        resDatalist = new ArrayList<ResDataBean>();


        mainPresenter.drawRange(projectBean.getfRange());
        initResDataList(projectBean.getId());

    }

    /***
     * 初始化地图
     */

    private void initMap() {
        // 地图初始化
        mMapView =  findViewById(R.id.bmapView);
        mMapView.showZoomControls(false);
        mBaiduMap = mMapView.getMap();
        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);

        MapStatusUpdate u = MapStatusUpdateFactory.zoomTo(18);
        mBaiduMap.animateMapStatus(u);
        // 开启定位图层
        mBaiduMap.setMyLocationEnabled(true);
        // 定位初始化
        mLocClient = new LocationClient(getApplicationContext());
        //声明LocationClient类
        mLocClient.registerLocationListener(myListener);
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true);// 打开gps
        option.setLocationNotify(true);
        option.setIgnoreKillProcess(false);
        option.setWifiCacheTimeOut(5 * 60 * 1000);
        option.setCoorType("bd09ll"); // 设置坐标类型
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setScanSpan(3000);
        mLocClient.setLocOption(option);
        mLocClient.start();
        mBaiduMap.setOnMarkerClickListener(this);
        mBaiduMap.setOnMapClickListener(this);

        // 编辑取景
        mBaiduMap.setOnMapStatusChangeListener(this);
    }


    private final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1;
    private static final int REQUEST_CODE_PERMISSION_CAMERA = 0x09;
    private final int MY_PERMISSIONS_REQUEST_CALL = 2;
    private final int MY_PERMISSIONS_REQUEST_LOCATION = 0x10;
    private void requestPermisson() {

        if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(ProjectDetails2Activity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_COARSE_LOCATION}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
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
                    Utils.showToast(mContext, "请在权限管理中设置存储权限,不然会影响正常使用");
                }
                break;
            case MY_PERMISSIONS_REQUEST_CALL:
                // 权限请求成功的操作
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                    return;
                } else {
                    // 权限请求失败的操作
                    Utils.showToast(mContext, "请在权限管理中设置打电话权限,不然会影响正常使用");
                }
                break;
            case MY_PERMISSIONS_REQUEST_LOCATION:
                // 权限请求成功的操作
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    return;
                } else {
                    // 权限请求失败的操作
                    Utils.showToast(mContext, "请在权限管理中设置打电话权限,不然会影响正常使用");
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

    private double juli;


    private int lastPositionflag;//上一坐标的位置；

    @Override
    public BaiduMap getBaiDuMap() {
        return mBaiduMap;
    }

    @Override
    public MapView getMapView() {
        return mMapView;
    }

    @Override
    public ImageButton getWeiXingView() {
        return bn_weixing;
    }

    @Override
    public HashMap<Integer, Marker> getMarker() {
        return mMarkers;
    }



    @Override
    public HashMap<Integer, Overlay> getOverLays() {
        return mOverlays;
    }

    @Override
    public boolean onMarkerClick(final Marker marker) {
        Mlog.d("该点以显示");
        if (spotEditFlag) {
            if (mIndex != -1) {
                if (mOverlays.containsKey(mIndex)) {
                    mOverlays.get(mIndex).remove();
                }
                mMarkers.get(mIndex).remove();
                mMarkers.remove(mIndex);
                mBaiduMap.hideInfoWindow();
            }
        }
        jingdianGone();
        if (mIndex != marker.getZIndex()) {
            mIndex = marker.getZIndex();
            ResDataBean resDataBean = resDatalist.get(mIndex);//要编辑的景点信息；
            new CCM_Delay(300, new CCM_Delay.DelayIF() {
                @Override
                public void operate() {
                    LatLng mll = marker.getPosition();
                    MapStatusUpdate u = MapStatusUpdateFactory
                            .newLatLng(mll);
                    mBaiduMap.animateMapStatus(u);
                }
            });

            if (resDataBean.getFormData() == null) {


                new ResDataGetAPI(userInfo.getAccess_token(), resDataBean, new ResDataGetAPI.ResDataDeleteIF() {
                    @Override
                    public void onResDataDeleteResult(boolean isOk, ResDataBean resDataBen) {

                        if (isOk) {

                            ShowResDataDialog(false, resDataBen, true);

                        }

                    }
                }).request();

            } else {

                ShowResDataDialog(false, resDataBean,true);
            }



        }
        return true;
    }

    @Override
    public boolean onMapPoiClick(MapPoi arg0) {
        return false;
    }

    @Override
    public void onMapClick(LatLng arg0) {
        if (spotEditFlag) {
            if (mIndex != -1) {
                if (mOverlays.containsKey(mIndex)) {
                    mOverlays.get(mIndex).remove();
                }
                mMarkers.get(mIndex).remove();
                mMarkers.remove(mIndex);
                mBaiduMap.hideInfoWindow();
            }
        }
        jingdianGone();
    }

    @Override
    public void onMapStatusChangeStart(MapStatus arg0) {
        Mlog.d("开始 latLng = " + arg0.target.latitude);
    }

    @Override
    public void onMapStatusChangeStart(MapStatus mapStatus, int i) {

    }

    @Override
    public void onMapStatusChangeFinish(MapStatus arg0) {
        Mlog.d("结束 latLng = " + arg0.target.latitude);
        arg0.getClass();
        dingWeiDian = new LatLng(arg0.target.latitude,
                arg0.target.longitude);


        if (sheetDialogFragment != null) {

            sheetDialogFragment.setlongitudeAndlatitude(arg0.target.longitude + "", arg0.target.latitude + "");
        }
    }

    @Override
    public void onMapStatusChange(MapStatus arg0) {

        zoom = arg0.zoom;

        if (zoom > 15) {
            if (!showzhiShiPai) {
                showzhiShiPai=true;
                mainPresenter.drowLine(luxianpointsList,new LatLng(0, 0) );
                mainPresenter.showJuLiFlag(luxianpointsList,new LatLng(0, 0) );

                initResDataList(projectBean.getId());
            }

        } else {
            if (showzhiShiPai) {
                showzhiShiPai = false;
                mBaiduMap.clear();
                mainPresenter.drowLine(luxianpointsList,new LatLng(0, 0));
                initResDataList(projectBean.getId());
            }
        }

        mainPresenter.drawRange(projectBean.getfRange());


    }


    private float zoom;

    private boolean showzhiShiPai = false;

    /**
     * 定位SDK监听函数
     */
    public class MyLocationListener extends BDAbstractLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            int locType = location.getLocType();
            // map view 销毁后不在处理新接收的位置
            if (location == null || mMapView == null||locType==BDLocation.TypeServerError||locType==BDLocation.TypeCriteriaException)
                return;
            if (location.getSatelliteNumber() != -1) {
                if (sheetDialogFragment != null) {

                    sheetDialogFragment.setlongitudeAndlatitude(location.getLongitude() + "", location.getLatitude() + "");
                }
            }
            MyLocationData locData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())
                    // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(100).latitude(location.getLatitude())
                    .longitude(location.getLongitude()).build();

            mBaiduMap.setMyLocationData(locData);
            mLatLng = new LatLng(location.getLatitude(),
                    location.getLongitude());

            if (isFirstLoc) {
                MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(mLatLng);
                mBaiduMap.animateMapStatus(u);

                sheetDialogFragment.setlongitudeAndlatitude(location.getLongitude() + "", location.getLatitude() + "");

                PointInfo pointInfo = new PointInfo();
                pointInfo.setLatitude(location.getLatitude());
                pointInfo.setLongitude(location.getLongitude());
                pointInfo.setTime(new CCM_DateTime().getMinAndSecond(location.getTime()));
                luxianpointsList.add(pointInfo);

                isFirstLoc = false;
                return;
            }




            //路线地点记载

            if (route_OnOff && luxianpointsList != null) {

                if ( mainPresenter.isNetworkConnected(mContext) ) {

                    LatLng loc = new LatLng(location.getLatitude(), location.getLongitude());

                    PointInfo pointInfo = new PointInfo();
                    pointInfo.setLatitude(location.getLatitude());
                    pointInfo.setLongitude(location.getLongitude());
                    pointInfo.setTime(new CCM_DateTime().getMinAndSecond(location.getTime()));

                    juli = new LatLngToJL().gps2String(luxianpointsList.get(lastPositionflag).getLatitude(), luxianpointsList.get(lastPositionflag).getLongitude(), pointInfo.getLatitude(), pointInfo.getLongitude());

                    if (juli >= 10) {
                        luxianpointsList.add(pointInfo);
                        mainPresenter.drowLine(luxianpointsList,loc);
                        lastPositionflag ++;

                    }

                } else {

                    Mlog.d("gps信号较差");
                }
            }



        }


    }






    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.bn_weixing:
                mainPresenter.MapTypeChange();
                break;
            case R.id.bn_dingwei:
                MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(mLatLng);
                mBaiduMap.animateMapStatus(u);
                break;
            case R.id.imagbtn_enlarge:
                mainPresenter.ZoomChange(true);
                break;
            case R.id.imagbtn_zoomOut:
                mainPresenter.ZoomChange(false);
                break;
            case R.id.bn_add:
                newAddResData(dingWeiDian);
                break;
            case R.id.bn_all:
                Intent intent2 = new Intent(context, ResDataManageActivity.class);
                intent2.putExtra("projectBean", projectBean);
                startActivityForResult(intent2, 2);
                break;
            case R.id.bn_more:
                mainPresenter.showMoreDialog(context,projectBean);
                break;
            case R.id.imagbtn_startOrContinue:
                startOrPause();
                break;
            case R.id.imagbtn_end:
                saveRoutes();
                break;
            case R.id.imagbtn_route:
                Intent intent = new Intent(context, TaskTrackActivity.class);
                intent.putExtra("resmodelid", "0");
                intent.putExtra("TaskId", projectBean.getId());
                startActivityForResult(intent,3);
                break;
        }
    }


    private void startOrPause() {

        if (isNewLuXian) {    //新路线
            isNewLuXian = !isNewLuXian;
        }
        route_OnOff=!route_OnOff;
        if (route_OnOff) {
            imagbtn_startOrContinue.setImageResource(R.mipmap.zanting);
        } else {
            imagbtn_startOrContinue.setImageResource(R.mipmap.kaishi);

        }
    }


    /**
     * 线路
     */

    private boolean route_OnOff = false;//路线记录是否开始
    private List<PointInfo> luxianpointsList=new ArrayList<>();      //储存线路点数据
    private boolean isNewLuXian = true;   //true 新路线   false原路线


    /***
     * 保存轨迹线路
     */
    private void saveRoutes() {
        LuXianInfo info = mainPresenter.saveRoutes("0",projectBean,luxianpointsList, context);
        route_OnOff = false;
        isNewLuXian = true;
        if (info != null) {
            new SaveResRouateAPI(mContext,userInfo.getAccess_token(), info, new OnNetRequestListener() {
                @Override
                public void onFinish() {

                }

                @Override
                public void OnNext() {

                }

                @Override
                public void OnResult(boolean isOk, String msg, Object object) {
                    if (isOk) {
                        Utils.showDialog(mContext, "轨迹保存成功");
                    }

                }
            }).request();

        }



    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("isNewLuXian", isNewLuXian);
        outState.putBoolean("route_OnOff", route_OnOff);
        outState.putSerializable("luxianpointsList", (Serializable) luxianpointsList);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        resDatalist = new ArrayList<>();
        mBaiduMap.clear();
        mBaiduMap.hideInfoWindow();
        mainPresenter.drawRange(projectBean.getfRange());
        switch (requestCode) {
            case 2:
                initResDataList(projectBean.getId());
                if (resultCode == 1) {
                    mIndex = data.getIntExtra("index",-1);

                    ResDataBean resDataBean = (ResDataBean) data.getSerializableExtra("resDataBean");


                    LatLng latLng = new LatLng(new Double(resDataBean.getLatitude()), new Double(resDataBean.getLongitude()));

                    MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(latLng);
                    mBaiduMap.animateMapStatus(u);

                    if (resDataBean.getFormData() == null) {


                        new ResDataGetAPI(userInfo.getAccess_token(), resDataBean, new ResDataGetAPI.ResDataDeleteIF() {
                            @Override
                            public void onResDataDeleteResult(boolean isOk, ResDataBean resDataBen) {

                                if (isOk) {

                                    ShowResDataDialog(false, resDataBen, true);

                                }

                            }
                        }).request();

                    } else {

                        ShowResDataDialog(false, resDataBean,true);
                    }








                } else if (resultCode == 2) {
                    newAddResData(dingWeiDian);
                }
                break;
            case 3:
                initResDataList(projectBean.getId());
                if (resultCode == 3) {


                    List<LuXianInfo> routes = (List<LuXianInfo>) data.getSerializableExtra("routes");


                    if (routes != null && routes.size() > 0) {
                        for(int i=0;i<routes.size();i++) {
                            LuXianInfo luXianInfo = routes.get(i);
                            String xianluDian = luXianInfo.getLineCoordinates();
                            luxianpointsList.clear();



                            String[] lines = xianluDian.split(" ");

                            for(int j=0;j<lines.length;j++) {

                                String[] point = lines[j].split(",");


                                PointInfo pointInfo = new PointInfo();


                                GPS Gcj02_gps = GPSConverterUtils.Gps84_To_bd09(new Double(point[1]), new Double(point[0]));

                                pointInfo.setLongitude(Gcj02_gps.getLon());
                                pointInfo.setLatitude(Gcj02_gps.getLat());
                                pointInfo.setTime(point[3]);

                                luxianpointsList.add(pointInfo);


                            }

                            mainPresenter.drowLine(luxianpointsList,new LatLng(0, 0));
                            mainPresenter.showJuLiFlag(luxianpointsList,new LatLng(0, 0) );
                            mainPresenter.lineShowOnCenter(luxianpointsList,new LatLng(0, 0));
                        }


                    }

                }
                break;

            case 5:

                if (resultCode == 3) {
                    ResDataBean resDataBean = (ResDataBean) data.getSerializableExtra("resDataBean");


                    if (projectBean.getfTaskmodel().contains(resDataBean.getFd_resmodelid())) {


                        if (dingWeiDian == null) {
                            return;
                        }
                        OverlayOptions ooA = new MarkerOptions().position(dingWeiDian).icon(bdA).zIndex(resDatalist.size());
                        Marker mMarker = (Marker) (mBaiduMap.addOverlay(ooA));

                        mIndex = resDatalist.size();
                        mMarkers.put(mIndex, mMarker);

                        ShowResDataDialog(false, resDataBean, true);
                        mainPresenter.getCoordinates(jdFanwei, mIndex);
                    } else {


                        Utils.showDialog(mContext, "该项目下没有该分类;");
                    }


                } else {
                    if (resultCode == 4) {
                        Utils.showDialog(mContext, "该项目下没有该分类;");
                    }
                }
                break;

        }
    }

    /***
     * 获取项目下的资源列表
     * @param taskid
     */
    private void initResDataList(String taskid) {
        mainPresenter.getAllSpotOfArea(taskid,context, resDatalist);

        new GetNetResListAPI(userInfo, projectBean, new GetNetResListAPI.DatadicListIF() {
            @Override
            public void datadicListResult(boolean isOk, List<ResDataBean> netDataList) {


                List<String> idList = new ArrayList<>();

                for (ResDataBean resDataBean : resDatalist) {

                    idList.add(resDataBean.getId());
                }


                if (isOk) {
                    if (netDataList != null && netDataList.size() > 0) {
                        int j=0;

                        for (int i = 0; i < netDataList.size(); i++) {

                            ResDataBean resDataBean = netDataList.get(i);

                            if (!idList.contains(resDataBean.getId())) {
                                String fd_resposition = resDataBean.getFd_resposition();

                                if (!Utils.isEmpty(fd_resposition)) {

                                    String[] split = fd_resposition.split(",");
                                    resDataBean.setLatitude(split[0]);
                                    resDataBean.setLongitude(split[1]);

                                }


                                resDatalist.add(resDataBean);

                                j = j + 1;

                                projectBean.setNetCount(10);
                            }
                        }

                        new ProjectDao(mContext).addOrUpdate(projectBean);
                        ProjectBean dataByID = new ProjectDao(mContext).getDataByID(projectBean.getId());

                        dataByID.getNetCount();
                    }
                }


                for (int i = 0; i < resDatalist.size(); i++) {

                    LatLng llA = new LatLng(Double.parseDouble(resDatalist.get(i).getLatitude()),
                            Double.parseDouble(resDatalist.get(i).getLongitude()));
                    OverlayOptions ooA = new MarkerOptions().position(llA)
                            .icon(bdA).zIndex(i);
                    Marker marker = (Marker) (mBaiduMap.addOverlay(ooA));
                    mMarkers.put(i, marker);
                    int fanwei = resDatalist.get(i).getScope();
                    mainPresenter.getCoordinates(fanwei, i);
                }





            }
        }).request();



    }


    private boolean spotEditFlag = true; // ture 新添加 false 修改

    private SheetDialogFragment sheetDialogFragment;


    /***
     * 显示资源对话框
     * @param editFlag 新添加ture      修改false
     * @param resDataBean 新添加null      修改当前的资源实体
     * @param isShow 是否显示
     */

    private void ShowResDataDialog(boolean editFlag,ResDataBean resDataBean,boolean isShow) {

        SheetDialogFragment.Builder builder = new SheetDialogFragment.Builder(ProjectDetails2Activity.this);
        builder.setDingWeiDian(dingWeiDian);
        builder.setCurrentProJectBean(projectBean);
        builder.setSportEditFlag(editFlag);
        if (!editFlag) {
            builder.setResDataBean(resDataBean);
        }

        if (isShow) {

            sheetDialogFragment.show(getSupportFragmentManager(), "dialog");
        }


    }



    // marker点击进人
    private void clickMarker2Edit(ResDataBean resDataBean ) {
        ShowResDataDialog(false, resDataBean,true);
    }

    // 隐藏景点编辑界面
    private void jingdianGone() {
        rl_bottom.setVisibility(View.GONE);
        rl_bottom.setVisibility(View.VISIBLE);
        mIndex = -1;
    }


    private int jdFanwei=10;


    /***
     * 添加资源
     * @param mLatLng
     */
    private void newAddResData(LatLng mLatLng) {
        if (mLatLng == null) {
            return;
        }
        OverlayOptions ooA = new MarkerOptions().position(mLatLng).icon(bdA).zIndex(resDatalist.size());
        Marker mMarker = (Marker) (mBaiduMap.addOverlay(ooA));

        mIndex = resDatalist.size();
        mMarkers.put(mIndex, mMarker);

        mainPresenter.getCoordinates(jdFanwei, mIndex);
        ShowResDataDialog(true, null,true);
    }


    @Override
    public void onPause() {
        mMapView.onPause();
        super.onPause();
    }

    @Override
    public void onResume() {
        mMapView.onResume();
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        mLocClient.stop();
        mBaiduMap.setMyLocationEnabled(false);
        mMapView.onDestroy();
        mMapView = null;
        super.onDestroy();
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
