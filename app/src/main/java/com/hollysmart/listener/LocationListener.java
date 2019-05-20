package com.hollysmart.listener;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.mapapi.map.MapView;

/**
 * Created by Lenovo on 2019/3/7.
 */

public  class LocationListener extends BDAbstractLocationListener {


    private interfaceLocaton interfaceLocaton;

    private MapView mMapView;

    public LocationListener(LocationListener.interfaceLocaton interfaceLocaton, MapView mMapView) {
        this.interfaceLocaton = interfaceLocaton;
        this.mMapView = mMapView;
    }

    @Override
    public void onReceiveLocation(BDLocation location) {
        int locType = location.getLocType();
        // map view 销毁后不在处理新接收的位置
        if (location == null || mMapView == null||locType== BDLocation.TypeServerError||locType== BDLocation.TypeCriteriaException)
            return;
//        if (location.getSatelliteNumber() != -1) {
//            tv_GPSInfo.setText("GPS卫星连接就绪");
//            tv_GPSNum.setText(location.getSatelliteNumber() + "");
//            tv_jingweidu.setText(location.getLatitude() + "--"
//                    + location.getLongitude());
//        } else {
//            tv_GPSInfo.setText("正在连接GPS卫星...");
//            tv_GPSNum.setText("0");
//            tv_jingweidu.setText(0.0 + "--" + 0.0);
//            tv_jingweidu.setVisibility(View.GONE);
//        }
//        MyLocationData locData = new MyLocationData.Builder()
//                .accuracy(location.getRadius())
//                // 此处设置开发者获取到的方向信息，顺时针0-360
//                .direction(100).latitude(location.getLatitude())
//                .longitude(location.getLongitude()).build();
//
//        mBaiduMap.setMyLocationData(locData);
//        mLatLng = new LatLng(location.getLatitude(),
//                location.getLongitude());
//
//        if (isFirstLoc) {
//            isFirstLoc = false;
//            MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(mLatLng);
//            mBaiduMap.animateMapStatus(u);
//
//
//            PointInfo pointInfo = new PointInfo();
//            pointInfo.setLatitude(location.getLatitude());
//            pointInfo.setLongitude(location.getLongitude());
//            pointInfo.setTime(location.getTime());
//            luxianpointsList.add(pointInfo);
//        }

    }


    public interface interfaceLocaton {

        void loacationSuccess();

        void locationFaild();

    }
}
