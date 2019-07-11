package com.hollysmart.main;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.GroundOverlayOptions;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolygonOptions;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.map.Stroke;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;
import com.hollysmart.beans.GPS;
import com.hollysmart.beans.JDPicInfo;
import com.hollysmart.beans.LuXianInfo;
import com.hollysmart.beans.PointInfo;
import com.hollysmart.formlib.beans.ProjectBean;
import com.hollysmart.formlib.beans.ResDataBean;
import com.hollysmart.db.JDPicDao;
import com.hollysmart.db.LuXianDao;
import com.hollysmart.db.ResDataDao;
import com.hollysmart.formlib.activitys.PreviewActivity;
import com.hollysmart.park.R;
import com.hollysmart.utils.CCM_DateTime;
import com.hollysmart.utils.GPSConverterUtils;
import com.hollysmart.utils.Mlog;
import com.hollysmart.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Lenovo on 2019/3/7.
 */

public class MainPresenter {


    private MainView mainView;
    private Model model;
    private Context mContext;

    BitmapDescriptor bdA = BitmapDescriptorFactory.fromResource(R.mipmap.ic_map);
    BitmapDescriptor bdGround = BitmapDescriptorFactory.fromResource(R.mipmap.ground_overlay);


    public MainPresenter(Context mContext,MainView mainView) {
        this.mainView = mainView;
        this.mContext = mContext;
        model=new Model();
    }

    /**
     * 地图进行缩放
     * @param b
     */
    public void ZoomChange(boolean b){

        BaiduMap mBaiduMap = mainView.getBaiDuMap();
        model.baiDuMapZoomChange(mBaiduMap, b);
    }


    /***
     * 地图图层样式
     */
    public void MapTypeChange(){
        BaiduMap mBaiduMap = mainView.getBaiDuMap();
        ImageButton bn_weixing = mainView.getWeiXingView();

        int mapType = mBaiduMap.getMapType();
        if (mapType!=1) {
            mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
        } else {
            mBaiduMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);
        }
        bn_weixing.setImageResource(R.mipmap.icon1_01);
    }

    /**
     * 判断网络是否通畅
     * @param context
     * @return
     */
    public boolean isNetworkConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        return ni != null && ni.isConnectedOrConnecting();
    }

    /***
     *
     */

    public LuXianInfo saveRoutes(String routesType,ProjectBean projectBean,List<PointInfo> luxianpointsList, Context context) {
        if (luxianpointsList != null && luxianpointsList.size() > 2) {
            StringBuilder allbuilder = new StringBuilder();
            for (int i = 0; i < luxianpointsList.size(); i++) {
                StringBuilder strbuild = new StringBuilder();

                BDLocation location = new BDLocation();
                location.setLongitude(luxianpointsList.get(i).getLongitude());
                location.setLatitude(luxianpointsList.get(i).getLatitude());

                GPS gps = baiDu2GaoDe(new BDLocation(location));

                strbuild.append(gps.getLon());
                strbuild.append(",");
                strbuild.append(gps.getLat());
                strbuild.append(",0,");
                strbuild.append(luxianpointsList.get(i).getTime() );

                if (i != luxianpointsList.size() - 1) {
                    strbuild.append(" ");

                }
                allbuilder.append(strbuild);
            }

            LuXianDao luXianDao = new LuXianDao(context);
            LuXianInfo luXianInfo=new LuXianInfo();
            long id = System.currentTimeMillis();

            luXianInfo.setId(id + "");
            luXianInfo.setCreatetime(new CCM_DateTime().Date());
            luXianInfo.setName(projectBean.getfTaskname() + id+"");
            luXianInfo.setFd_restaskid(projectBean.getId());
            luXianInfo.setIsUpload("false");
            luXianInfo.setFd_restaskname(projectBean.getfTaskname());
            luXianInfo.setFd_resmodelid(routesType);
            luXianInfo.setFd_resmodelname(projectBean.getfTaskname());
            luXianInfo.setStartCoordinate(luxianpointsList.get(0).getJsonObject().toString());
            luXianInfo.setEndCoordinate(luxianpointsList.get(luxianpointsList.size() - 1).getJsonObject().toString());
            luXianInfo.setLineCoordinates( allbuilder.toString());

            boolean addOrUpdate = luXianDao.addOrUpdate(luXianInfo);
            if (addOrUpdate) {
                Toast.makeText(context, "路线记录成功", Toast.LENGTH_LONG).show();
                return luXianInfo;
            } else {
                Toast.makeText(context, "路线记录失败", Toast.LENGTH_SHORT).show();
                return luXianInfo;
            }
        } else {
            Toast.makeText(context, "记录点太少了，下次再多记录点试试", Toast.LENGTH_LONG).show();
            return null;
        }

    }




    /***
     * 百度坐标转gps
     * @param location
     */

    private GPS baiDu2GaoDe(BDLocation location) {
        GPS Gcj02_gps = GPSConverterUtils.bd09_To_Gps84(location.getLatitude(), location.getLongitude());

        return Gcj02_gps;

    }



    public void showMoreDialog(final Context context, final ProjectBean projectBean) {

        final BottomSheetDialog mBottomSheetDialog;
        Activity activity = (Activity) context;

        WindowManager wm;
        wm = activity.getWindowManager();
        int height = wm.getDefaultDisplay().getHeight();

        mBottomSheetDialog = new BottomSheetDialog(context);
        //导入底部reycler布局
        View view = LayoutInflater.from(context).inflate(R.layout.bottom_shell_dialog, null, false);
        mBottomSheetDialog.setContentView(view);

        BottomSheetBehavior mBehavior = BottomSheetBehavior.from((View) view.getParent());
        //设置默认弹出高度为屏幕的0.4倍
        mBehavior.setPeekHeight((int) (0.4 * height));

        mBottomSheetDialog.getWindow().findViewById(R.id.design_bottom_sheet)
                .setBackgroundResource(android.R.color.transparent);

        //设置点击dialog外部不消失
        mBottomSheetDialog.setCanceledOnTouchOutside(false);

        view.findViewById(R.id.tv_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBottomSheetDialog.dismiss();

            }
        });
        view.findViewById(R.id.tv_yulan).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent privewIntent=new Intent(context, PreviewActivity.class);
                privewIntent.putExtra("projectBean", projectBean);
                Activity activity = (Activity) context;
                activity. startActivity(privewIntent);
                mBottomSheetDialog.dismiss();

            }
        });


        mBottomSheetDialog.show();

    }




    // 景点的范围；
    public String getCoordinates(int jdFanwei, int index) {
        BaiduMap mBaiduMap = mainView.getBaiDuMap();
        HashMap<Integer, Marker> mMarkers = mainView.getMarker();
        HashMap<Integer, Overlay> mOverlays = mainView.getOverLays();

        double mOnelat = 0.000005;
        double mOneLng = 0.00001;

        Marker m = mMarkers.get(index);
        if (jdFanwei == 0) {
            jdFanwei = 1;
        }
        double maxLat = m.getPosition().latitude + mOnelat * jdFanwei;
        double minLat = m.getPosition().latitude - mOnelat * jdFanwei;
        double maxlng = m.getPosition().longitude + mOneLng * jdFanwei;
        double minlng = m.getPosition().longitude - mOneLng * jdFanwei;

        Mlog.d("maxLat = " + maxLat);
        Mlog.d("minLat = " + minLat);
        Mlog.d("maxlng = " + maxlng);
        Mlog.d("minlng = " + minlng);

        LatLng southwest = new LatLng(minLat, minlng);
        LatLng northeast = new LatLng(maxLat, maxlng);

        LatLngBounds bounds = new LatLngBounds.Builder().include(northeast)
                .include(southwest).build();

        OverlayOptions ooGround = new GroundOverlayOptions()
                .positionFromBounds(bounds).image(bdGround).transparency(0.8f);

        if (mOverlays.containsKey(index)) {
            mOverlays.get(index).remove();
        }
        Overlay mOverlay = mBaiduMap.addOverlay(ooGround);
        mOverlays.put(index, mOverlay);
        StringBuffer coordinates = new StringBuffer();
        coordinates.append(maxlng).append(",").append(maxLat).append(",0")
                .append(minlng).append(",").append(maxLat).append(",0")
                .append(minlng).append(",").append(minLat).append(",0")
                .append(maxlng).append(",").append(minLat).append(",0");
        return coordinates.toString();
    }




    // 查找景点信息
    public void getAllSpotOfArea(String jqId,Context context,List<ResDataBean> spotInfolist) {

        ResDataDao jinQuDao = new ResDataDao(context);
        JDPicDao jdPicDao = new JDPicDao(context);

        List<ResDataBean> jingDianlist = jinQuDao.getData(jqId + "");

        if (jingDianlist != null && jingDianlist.size() > 0) {

            for (ResDataBean resDataBean : jingDianlist) {

                List<JDPicInfo> picInfos = jdPicDao.getDataByJDId(resDataBean.getId()+"");

                resDataBean.setJdPicInfos(picInfos);


            }

        }

        if (jingDianlist != null && jingDianlist.size() > 0) {

            spotInfolist.addAll(jingDianlist);
        }



    }



    private PolygonOptions mPolygonOptions;

    public void drawRange(String ranges) {
        BaiduMap mBaiduMap = mainView.getBaiDuMap();

        mPolygonOptions = new PolygonOptions();
        mPolygonOptions.fillColor(mContext.getResources().getColor(R.color.touming));

        mPolygonOptions.stroke(new Stroke(5, mContext.getResources().getColor(R.color.bg_lan)));


        if (Utils.isEmpty(ranges)) {

            return;
        }

        //多边形顶点位置
        List<LatLng> points = new ArrayList<>();

        if (ranges.contains("|")) {

            String[] str_ranges = ranges.split("\\|");


            for (int i=0;i<str_ranges.length;i++) {

                String[] latLng = str_ranges[i].split(",");

                LatLng latLng1 = new LatLng(new Double(latLng[0]), new Double(latLng[1]));

                points.add(latLng1);
            }




        }

        if (points.size() < 3) {

            return;
        }
        mPolygonOptions.points(points);

        //在地图上显示多边形
        mBaiduMap.addOverlay(mPolygonOptions);


    }




    private List<OverlayOptions> markList = new ArrayList<>();


    //绘制折线
    public void drowLine(List<PointInfo> luxianpointsList,LatLng loc) {



        BaiduMap mBaiduMap = mainView.getBaiDuMap();
        mBaiduMap.clear();
        if (luxianpointsList == null || luxianpointsList.size() == 0) {
            return;
        }

        if ( luxianpointsList.size() <2) {
            return;
        }

        List<LatLng> points = new ArrayList<LatLng>();
        LatLng p1;
        for (int i = 0; i < luxianpointsList.size(); i++) {
            p1= new LatLng(luxianpointsList.get(i).getLatitude(), luxianpointsList.get(i).getLongitude());
            points.add(p1);

        }
        //地理坐标基本数据结构
        if(points.isEmpty()){
            //添加初始点标志
            // 初始化全局 bitmap 信息，不用时及时 recycle
        } else{
            //位置移动画出两点之间的线
            OverlayOptions ooPolyline2 = new PolylineOptions().width(4).color(0xAAFF0000).points(points);
            mBaiduMap.addOverlay(ooPolyline2);

            OverlayOptions firstLocMark = new MarkerOptions().position(points.get(0)).icon(bdA).zIndex(12).draggable(true);
            mBaiduMap.addOverlay(firstLocMark);
            OverlayOptions lasttLocMark = new MarkerOptions().position(points.get(points.size()-1)).icon(bdA).zIndex(12).draggable(true);
            mBaiduMap.addOverlay(lasttLocMark);



        }


//        int i = luxianpointsList.size() / 50;
//        if (i > 0) {
//
//            LayoutInflater  mLayoutInflater = LayoutInflater.from(mContext);
//            View view = mLayoutInflater.inflate(R.layout.layout_mapview, null, false);
//            TextView tv_licheng = view.findViewById(R.id.tv_licheng);
//            TextView tv_time = view.findViewById(R.id.tv_time);
//
//            for(int j=0;j<i+1;j++) {
//
//                tv_licheng.setText(j*0.5+"km ");
//                tv_time.setText(luxianpointsList.get(49 * j).getTime());
//                BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromView(view);
//                //构建MarkerOption，用于在地图上添加Marker
//                MarkerOptions  optionPosition = new MarkerOptions()
//                        .position(new LatLng(luxianpointsList.get(49 * j).getLatitude(), luxianpointsList.get(49 * j).getLongitude()))
//                        .icon(bitmapDescriptor);
//                //在地图上添加Marker，并显示
//
//                markList.add(optionPosition);
//                if (mBaiduMap.getMapStatus().zoom > 15) {
//                    mBaiduMap.addOverlays(markList);
//
//                }
//
//            }
//
//        }

    }


    /***
     * 显示距离标识
     *
     */


    public void showJuLiFlag(List<PointInfo> luxianpointsList, LatLng loc) {


        BaiduMap mBaiduMap = mainView.getBaiDuMap();
        if (luxianpointsList == null || luxianpointsList.size() == 0) {
            return;
        }

        List<LatLng> points = new ArrayList<LatLng>();
        LatLng p1;
        for (int i = 0; i < luxianpointsList.size(); i++) {
            p1= new LatLng(luxianpointsList.get(i).getLatitude(), luxianpointsList.get(i).getLongitude());
            points.add(p1);

        }
        int i = luxianpointsList.size() / 50;
        if (i > 0) {

            LayoutInflater  mLayoutInflater = LayoutInflater.from(mContext);
            View view = mLayoutInflater.inflate(R.layout.layout_mapview, null, false);
            TextView tv_licheng = view.findViewById(R.id.tv_licheng);
            TextView tv_time = view.findViewById(R.id.tv_time);

            for(int j=0;j<i+1;j++) {

                tv_licheng.setText(j*0.5+"km ");
                tv_time.setText(luxianpointsList.get(49 * j).getTime());
                BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromView(view);
                //构建MarkerOption，用于在地图上添加Marker
                MarkerOptions  optionPosition = new MarkerOptions()
                        .position(new LatLng(luxianpointsList.get(49 * j).getLatitude(), luxianpointsList.get(49 * j).getLongitude()))
                        .icon(bitmapDescriptor);
                //在地图上添加Marker，并显示

                markList.add(optionPosition);
                if (mBaiduMap.getMapStatus().zoom > 15) {
                    mBaiduMap.addOverlays(markList);

                }

            }

        }

    }




    /***
     * 路线居中显示
     *
     */


    public void lineShowOnCenter(List<PointInfo> luxianpointsList,LatLng loc) {

        BaiduMap mBaiduMap = mainView.getBaiDuMap();

        MapView mapView = mainView.getMapView();

        if (luxianpointsList == null || luxianpointsList.size() == 0) {
            return;
        }

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        LatLng p1;
        for (int i = 0; i < luxianpointsList.size(); i++) {
            p1 = new LatLng(luxianpointsList.get(i).getLatitude(), luxianpointsList.get(i).getLongitude());
            builder.include(p1);
        }

        LatLngBounds bounds = builder.build();
        // 设置显示在屏幕中的地图地理范围
        MapStatusUpdate u = MapStatusUpdateFactory.newLatLngBounds(bounds, mapView.getWidth(), mapView.getHeight());
        mBaiduMap.setMapStatus(u);



    }








}
