package com.hollysmart.main;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.hollysmart.utils.Mlog;

/**
 * Created by Lenovo on 2019/3/7.
 */

public class Model {

    /**
     * 对地图进行缩放
     * @param mBaiduMap
     * @param b
     */
    public void  baiDuMapZoomChange(BaiduMap mBaiduMap, boolean b){

       float zoomLevel = mBaiduMap.getMapStatus().zoom;
        Mlog.d("zoom:" + zoomLevel);
        if (b) {
            if (zoomLevel < mBaiduMap.getMaxZoomLevel()) {
                zoomLevel = zoomLevel + 1;
            }
        } else {
            if (zoomLevel > mBaiduMap.getMinZoomLevel()) {
                zoomLevel = zoomLevel - 1;
            }
        }
        MapStatusUpdate u = MapStatusUpdateFactory.zoomTo(zoomLevel);
        mBaiduMap.animateMapStatus(u);

    }


}
