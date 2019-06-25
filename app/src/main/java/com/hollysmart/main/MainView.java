package com.hollysmart.main;

import android.widget.ImageButton;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.model.LatLng;

import java.util.HashMap;

/**
 * Created by Lenovo on 2019/3/7.
 */

public interface MainView extends BaiduMap.OnMarkerClickListener,BaiduMap.OnMapClickListener,BaiduMap.OnMapStatusChangeListener {

    BaiduMap getBaiDuMap();

    MapView getMapView();

    ImageButton getWeiXingView();

    HashMap<Integer, Marker>  getMarker();


    HashMap<Integer, Overlay> getOverLays();





    @Override
    void onMapClick(LatLng latLng);

    @Override
    boolean onMapPoiClick(MapPoi mapPoi);

    @Override
    void onMapStatusChangeStart(MapStatus mapStatus);

    @Override
    void onMapStatusChangeStart(MapStatus mapStatus, int i);

    @Override
    void onMapStatusChange(MapStatus mapStatus);

    @Override
    void onMapStatusChangeFinish(MapStatus mapStatus);

    @Override
    boolean onMarkerClick(Marker marker);
}
