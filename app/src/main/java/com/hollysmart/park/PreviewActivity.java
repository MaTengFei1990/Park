package com.hollysmart.park;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.GroundOverlayOptions;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;
import com.hollysmart.adapter.PreviewAdapter;
import com.hollysmart.beans.JDPicInfo;
import com.hollysmart.beans.ProjectBean;
import com.hollysmart.beans.ResDataBean;
import com.hollysmart.db.JDPicDao;
import com.hollysmart.db.ResDataDao;
import com.hollysmart.style.StyleAnimActivity;
import com.hollysmart.utils.Mlog;
import com.hollysmart.views.MaxGallery;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PreviewActivity extends StyleAnimActivity {


    // 定位相关
    BaiduMap mBaiduMap;
    MapView mMapView = null;

    @Override
    public int layoutResID() {
        return R.layout.activity_preview;
    }

    BitmapDescriptor bdGround = BitmapDescriptorFactory.fromResource(R.mipmap.ground_overlay);
    BitmapDescriptor bdA = BitmapDescriptorFactory.fromResource(R.mipmap.resflag_add);

    private void setBD_a() {
        bdLists = new ArrayList<BitmapDescriptor>();
        bdLists.add(BitmapDescriptorFactory.fromResource(R.mipmap.biao_a_01));
        bdLists.add(BitmapDescriptorFactory.fromResource(R.mipmap.biao_a_02));
        bdLists.add(BitmapDescriptorFactory.fromResource(R.mipmap.biao_a_03));
        bdLists.add(BitmapDescriptorFactory.fromResource(R.mipmap.biao_a_04));
        bdLists.add(BitmapDescriptorFactory.fromResource(R.mipmap.biao_a_06));
    }

    private void setBD_b() {
        bdLists_b = new ArrayList<BitmapDescriptor>();
        bdLists_b.add(BitmapDescriptorFactory
                .fromResource(R.mipmap.biao_b_01));
        bdLists_b.add(BitmapDescriptorFactory
                .fromResource(R.mipmap.biao_b_02));
        bdLists_b.add(BitmapDescriptorFactory
                .fromResource(R.mipmap.biao_b_03));
        bdLists_b.add(BitmapDescriptorFactory
                .fromResource(R.mipmap.biao_b_04));
        bdLists_b.add(BitmapDescriptorFactory
                .fromResource(R.mipmap.biao_b_06));
    }

    private MaxGallery gy_data;
    private List<BitmapDescriptor> bdLists;
    private List<BitmapDescriptor> bdLists_b;

    private HashMap<Integer, Marker> mMarkers;
    private HashMap<Integer, Overlay> mOverlays;

    @Override
    public void findView() {
        gy_data =  findViewById(R.id.gy_data);

        findViewById(R.id.iv_back).setOnClickListener(this);

        mMapView =  findViewById(R.id.preview_bmapView);
        mMapView.showZoomControls(false);
        mBaiduMap = mMapView.getMap();

        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);

        MapStatusUpdate u = MapStatusUpdateFactory.zoomTo(18);
        mBaiduMap.animateMapStatus(u);
        // 开启定位图层
        mBaiduMap.setMyLocationEnabled(true);
    }

    private PreviewAdapter mAdapter;
    private List<ResDataBean> spotInfolist; //当前项目的资源
    private ProjectBean projectBean;

    @Override
    public void init() {
        projectBean = (ProjectBean) getIntent().getSerializableExtra("projectBean");

        mMarkers = new HashMap<Integer, Marker>();
        mOverlays = new HashMap<Integer, Overlay>();

        setBD_a();
        setBD_b();

        initData();

        // 开启定位图层
        mBaiduMap.setMyLocationEnabled(true);
        mAdapter = new PreviewAdapter(mContext,spotInfolist);
        gy_data.setAdapter(mAdapter);
        gy_data.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });

        mBaiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                onMarker(marker);
                return true;
            }
        });

        mBaiduMap.setOnMapClickListener(new BaiduMap.OnMapClickListener() {
            @Override
            public boolean onMapPoiClick(MapPoi arg0) {
                return false;
            }

            @Override
            public void onMapClick(LatLng arg0) {
                if (!menuTag) {
                    redioB(null, nowMarker);
                    gy_data.setSelection(-1);
                }
            }
        });
    }

    /***
     * 实例数据
     */
    private void initData() {

        spotInfolist = new ArrayList<>();


        selectDB(projectBean.getId());

        drowInMap();

    }




    // 查询
    private void selectDB(String jqId) {
        Mlog.d("jqId = " + jqId);
        spotInfolist.clear();

        ResDataDao resDataDao = new ResDataDao(getApplication());
        List<ResDataBean> resDataBeans = resDataDao.getData(jqId + "");
        if (resDataBeans != null && resDataBeans.size() > 0) {

            spotInfolist.addAll(resDataBeans);
        }


        JDPicDao jdPicDao = new JDPicDao(mContext);
        for(int i=0;i<spotInfolist.size();i++) {
            ResDataBean resDataBean = spotInfolist.get(i);

            List<JDPicInfo> jdPicInfoList = jdPicDao.getDataByJDId(resDataBean.getId() + "");

            resDataBean.setJdPicInfos(jdPicInfoList);

        }


    }


    private void drowInMap() {



        mBaiduMap.setOnMapLoadedCallback(new BaiduMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                for (int i = 0; i < spotInfolist.size(); i++) {
                    LatLng llA = new LatLng(Double.parseDouble(spotInfolist.get(i).getLatitude()),
                            Double.parseDouble(spotInfolist.get(i).getLongitude()));
                    OverlayOptions ooA = new MarkerOptions().position(llA)
                            .icon(bdA).zIndex(i);
                    Marker marker = (Marker) (mBaiduMap.addOverlay(ooA));
                    mMarkers.put(i, marker);
                    builder.include(llA);
                    int fanwei = spotInfolist.get(i).getScope();
                    getCoordinates(fanwei, i);
                }

                LatLngBounds bounds = builder.build();
                // 设置显示在屏幕中的地图地理范围
                int  Width = mMapView.getWidth();
                int height = mMapView.getHeight();
                MapStatusUpdate u = MapStatusUpdateFactory.newLatLngBounds(bounds, Width, height);
                mBaiduMap.setMapStatus(u);

            }
        });








    }













    // 景点的范围；
    public String getCoordinates(int jdFanwei, int index) {

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






    @Override
    public void onClick(View arg0) {
        switch (arg0.getId()) {
            case R.id.iv_back:
                finish();
                break;

        }
    }

    private boolean menuTag = true;

    private void menuVib() {
        gy_data.setVisibility(View.GONE);
        menuTag = true;
    }

    private void menuGone() {
        gy_data.setVisibility(View.VISIBLE);
        menuTag = false;
    }





    private void onMarker(Marker marker) {
        Bundle bundle = marker.getExtraInfo();

        mAdapter.notifyDataSetChanged();
    }

    private Marker nowMarker;

    private void redioB(Marker newMarker, Marker oldMarker) {
        if (oldMarker != null) {
            Bundle oldBundle = oldMarker.getExtraInfo();
            oldMarker.setIcon(bdLists.get(oldBundle.getInt("bg_index")));
        }

        if (newMarker != null) {
            Bundle newBundle = newMarker.getExtraInfo();
            newMarker.setIcon(bdLists_b.get(newBundle.getInt("bg_index")));
            nowMarker = newMarker;
            MapStatusUpdate u = MapStatusUpdateFactory.newLatLngZoom(newMarker
                    .getPosition(), 16f);
            mBaiduMap.animateMapStatus(u);
        }
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
    public void onDestroy() {
        mBaiduMap.setMyLocationEnabled(false);
        mMapView.onDestroy();
        mMapView = null;
        super.onDestroy();
    }



}
