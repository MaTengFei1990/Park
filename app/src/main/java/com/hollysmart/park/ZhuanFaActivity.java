package com.hollysmart.park;

import android.content.DialogInterface;
import android.content.Intent;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.StringSignature;
import com.hollysmart.apis.GetZhuanFaInfoAPI;
import com.hollysmart.apis.LoadTagListAPI;
import com.hollysmart.apis.zhuanFaInfoAPI;
import com.hollysmart.beans.BiaoQianBean;
import com.hollysmart.beans.ZhuanFaInfoBean;
import com.hollysmart.dialog.BsSelectDialog;
import com.hollysmart.dialog.LoadingProgressDialog;
import com.hollysmart.interfaces.SelectIF;
import com.hollysmart.style.StyleAnimActivity;
import com.hollysmart.utils.Utils;
import com.hollysmart.value.Values;

import java.util.ArrayList;
import java.util.List;

public class ZhuanFaActivity extends StyleAnimActivity implements BsSelectDialog.BsSelectIF, LoadTagListAPI.LoadTagListIF, zhuanFaInfoAPI.SaveInfoIF, GetZhuanFaInfoAPI.getZhuanFaInfoIF {
    private final String TYPE_ID = "1";

    @Override
    public int layoutResID() {
        return R.layout.activity_zhuan_fa;
    }


    private TextView tv_biaoQian;
    private TextView tv_location;
    private EditText et_content;
    private ImageView iv_gongkai;
    private ImageView iv_weiZhi;

    private ImageView iv_image;
    private TextView tv_titles;
    private TextView tv_content;


    private String id;
    private String neiRong;
    private LoadingProgressDialog lpd;

    @Override
    public void findView() {
        tv_biaoQian = findViewById(R.id.tv_biaoQian);
        iv_gongkai = findViewById(R.id.iv_gongkai);
        iv_weiZhi = findViewById(R.id.iv_weiZhi);
        tv_location = findViewById(R.id.tv_location);
        et_content = findViewById(R.id.et_content);

        iv_image = findViewById(R.id.iv_image);
        tv_titles = findViewById(R.id.tv_titles);
        tv_content = findViewById(R.id.tv_content);

        findViewById(R.id.tv_fanhui).setOnClickListener(this);
        findViewById(R.id.ll_biaoQian).setOnClickListener(this);
        findViewById(R.id.ll_gongkai).setOnClickListener(this);
        findViewById(R.id.ll_weiZhi).setOnClickListener(this);
        findViewById(R.id.tv_save).setOnClickListener(this);

    }


    private GeoCoder mSearch;
    private List<SelectIF> biaoQianBeanList;


    private List<Integer> aistartlist = new ArrayList<>();
    private List<Integer> moaoHaostartList = new ArrayList<>();


    @Override
    public void init() {

        setLpd();
        id = getIntent().getStringExtra("id");
        neiRong = getIntent().getStringExtra("neiRong");
        mLocationClient = new LocationClient(getApplicationContext());
        //声明LocationClient类
        mLocationClient.registerLocationListener(myListener);
        mLocationClient.start();

        setBaiDudituDingWei();

        mSearch = GeoCoder.newInstance();
        initData();
        //草稿
        if (!Utils.isEmpty(id)) {
            new GetZhuanFaInfoAPI(id, this).request();
        }
        if (!Utils.isEmpty(neiRong)) {

// 新建一个可以添加属性的文本对象
            SpannableStringBuilder builder = new SpannableStringBuilder(neiRong);

            char[] chars = neiRong.toCharArray();
            for(int i=0;i<chars.length;i++) {
                char aChar = chars[i];
                if (aChar == '@') {
                    aistartlist.add(i);
                }
                if (aChar == ':') {
                    moaoHaostartList.add(i);
                }


            }

            for(int j=0;j<aistartlist.size();j++) {

                builder.setSpan(new ForegroundColorSpan(R.color.titleBg), aistartlist.get(j), moaoHaostartList.get(j)+1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }

            et_content.setText(builder);
        }


    }


    private void setLpd() {
        lpd = new LoadingProgressDialog();
        lpd.setMessage("正在保存，请稍等...");
        lpd.create(this, lpd.STYLE_SPINNER);
    }

    public LocationClient mLocationClient = null;


    private void initData() {
        biaoQianBeanList = new ArrayList<>();
        new LoadTagListAPI(this).request();


//        setBaiDudituDingWei();
//
//        mSearch = GeoCoder.newInstance();


    }

    /**
     * 百度地图定位
     */
    private void setBaiDudituDingWei() {
        LocationClientOption option = new LocationClientOption();

        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        //可选，设置定位模式，默认高精度
        //LocationMode.Hight_Accuracy：高精度；
        //LocationMode. Battery_Saving：低功耗；
        //LocationMode. Device_Sensors：仅使用设备；

        option.setCoorType("bd09ll");
        //可选，设置返回经纬度坐标类型，默认gcj02
        //gcj02：国测局坐标；
        //bd09ll：百度经纬度坐标；
        //bd09：百度墨卡托坐标；
        //海外地区定位，无需设置坐标类型，统一返回wgs84类型坐标

        option.setScanSpan(1000);
        //可选，设置发起定位请求的间隔，int类型，单位ms
        //如果设置为0，则代表单次定位，即仅定位一次，默认为0
        //如果设置非0，需设置1000ms以上才有效

        option.setOpenGps(true);
        //可选，设置是否使用gps，默认false
        //使用高精度和仅用设备两种定位模式的，参数必须设置为true

        option.setLocationNotify(true);
        //可选，设置是否当GPS有效时按照1S/1次频率输出GPS结果，默认false

        option.setIgnoreKillProcess(false);
        //可选，定位SDK内部是一个service，并放到了独立进程。
        //设置是否在stop的时候杀死这个进程，默认（建议）不杀死，即setIgnoreKillProcess(true)

        option.SetIgnoreCacheException(false);
        //可选，设置是否收集Crash信息，默认收集，即参数为false

        option.setWifiCacheTimeOut(5 * 60 * 1000);
        //可选，7.2版本新增能力
        //如果设置了该接口，首次启动定位时，会先判断当前WiFi是否超出有效期，若超出有效期，会先重新扫描WiFi，然后定位

        option.setEnableSimulateGps(false);
        //可选，设置是否需要过滤GPS仿真结果，默认需要，即参数为false

        mLocationClient.setLocOption(option);
        //mLocationClient为第二步初始化过的LocationClient对象
        //需将配置好的LocationClientOption对象，通过setLocOption方法传递给LocationClient对象使用
        //更多LocationClientOption的配置，请参照类参考中LocationClientOption类的详细说明
    }

    private MyLocationListener myListener = new MyLocationListener();


    /**
     * 经纬度转地理位置的回调
     */

    OnGetGeoCoderResultListener listener = new OnGetGeoCoderResultListener() {

        public void onGetGeoCodeResult(GeoCodeResult result) {

            if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
                //没有检索到结果
            }

            //获取地理编码结果
        }

        @Override

        public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {

            if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
                //没有找到检索结果
            }

            tv_location.setText(result.getAddressDetail().province + " " + result.getAddressDetail().district + " " + result.getAddressDetail().street);
            //获取反向地理编码结果
            mLocationClient.stop();
        }
    };

    /**
     * 获取标签列表
     *
     * @param isOK
     * @param data
     */
    @Override
    public void loadTagListResult(boolean isOK, List<BiaoQianBean> data) {
        if (isOK) {
            biaoQianBeanList.addAll(data);
//            adapter.notifyDataSetChanged();
        }
    }

    private double latitude;    //获取纬度信息
    private double longitude;    //获取经度信息

    /**
     * 保存返回的数据。
     *
     * @param isOk
     * @param msg
     */
    @Override
    public void saveResult(boolean isOk, String msg) {
        findViewById(R.id.tv_save).setEnabled(true);
        lpd.cancel();
        if (isOk) {
            Utils.showToast(mContext, "转发成功！");
            Intent intent = new Intent(Values.RELOAD_DATA);
            sendBroadcast(intent);
        } else {
            Utils.showToast(mContext, "转发失败！");

        }
        finish();

    }


    @Override
    public void getZhuanFaInfoResult(boolean isOK, ZhuanFaInfoBean bean) {
        if (isOK) {
            tv_titles.setText("@"+bean.getUsername());
            tv_content.setText(bean.getContent());
            if (!Utils.isEmpty(bean.getImgs())) {
                String Imags = bean.getImgs();

                String Path = "";

                String[] Imaglist = Imags.split("\\|");

                if (Imaglist != null && Imaglist.length > 0) {

                    Path = Imaglist[0];
                }

                Glide.with(this)
                        .load(Values.SERVICE_URL + Path)
                        .centerCrop()
                        .error(R.mipmap.pic_del)
                        .signature(new StringSignature("" + System.currentTimeMillis()))
                        .into(iv_image);
            } else {
                iv_image.setVisibility(View.GONE);
            }


        }
    }


    /**
     * 获取当前的经纬度
     */
    public class MyLocationListener extends BDAbstractLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            //此处的BDLocation为定位结果信息类，通过它的各种get方法可获取定位相关的全部结果
            //以下只列举部分获取经纬度相关（常用）的结果信息
            //更多结果信息获取说明，请参照类参考中BDLocation类中的说明

            latitude = location.getLatitude();    //获取纬度信息
            longitude = location.getLongitude();    //获取经度信息
            float radius = location.getRadius();    //获取定位精度，默认值为0.0f

            String coorType = location.getCoorType();
            //获取经纬度坐标类型，以LocationClientOption中设置过的坐标类型为准

            int errorCode = location.getLocType();
            //获取定位类型、定位错误返回码，具体信息可参照类参考中BDLocation类中的说明


            LatLng ptCenter = new LatLng(latitude, longitude);

            mSearch.setOnGetGeoCodeResultListener(listener);

            mSearch.reverseGeoCode(new ReverseGeoCodeOption()
                    .location(ptCenter));
        }

    }


    private boolean isGongkai = true;
    private boolean isUpLoadWeiZhi = true;
    private String state = "1";

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_fanhui:
//                if (close()) {
//                    showdialogs();
//                } else {
                    finish();
//                }
                break;
            case R.id.ll_biaoQian:
                new BsSelectDialog(this).showPopuWindow(mContext, 1, "请选择标签", biaoQianBeanList);
                break;
            case R.id.ll_gongkai:
                if (!isGongkai) {
                    isGongkai = !isGongkai;
                    iv_gongkai.setImageResource(R.mipmap.check_on);
                } else {
                    isGongkai = !isGongkai;
                    iv_gongkai.setImageResource(R.mipmap.check_off);
                }
                break;
            case R.id.ll_weiZhi:
                if (!isUpLoadWeiZhi) {
                    isUpLoadWeiZhi = !isUpLoadWeiZhi;
                    iv_weiZhi.setImageResource(R.mipmap.check_on);
                } else {
                    isUpLoadWeiZhi = !isUpLoadWeiZhi;
                    iv_weiZhi.setImageResource(R.mipmap.check_off);
                }
                break;
            case R.id.tv_save:
                save();
                break;

        }
    }

    /**
     * 弹出是否保存为草稿的对话框。
     */
    private void showdialogs() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("是否保存草稿");
        builder.setPositiveButton("保存", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                state = "-1";
                lpd.show();
                if (isUpLoadWeiZhi) {

                    new zhuanFaInfoAPI(id, null, TYPE_ID, content, latitude + "", longitude + "", position, ispublic, state, tagId, Imags, ZhuanFaActivity.this).request();
                } else {
                    new zhuanFaInfoAPI(id, null, TYPE_ID, content, null, null, null, ispublic, state, tagId, Imags, ZhuanFaActivity.this).request();

                }


            }
        });
        builder.setNegativeButton("不保存", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        builder.create().show();
    }


    /**
     * 保存操作
     */
    private String content = "";
    private String position = "";
    private String ispublic;
    private String Imags = "";

    private void save() {
        content = et_content.getText().toString();
        position = tv_location.getText().toString();

        if (Utils.isEmpty(tagId) || tagId.equals("0")) {
            Utils.showToast(mContext, "请选择标签");
            return;
        }
        if (Utils.isEmpty(position)) {
            Utils.showToast(mContext, "请选择位置");
            return;
        }

        if (isGongkai) {
            ispublic = "1";
        } else {
            ispublic = "0";

        }
        if (Utils.isEmpty(content)) {
            Utils.showToast(mContext, "请输入内容");
            return;
        }
        lpd.show();
        findViewById(R.id.tv_save).setEnabled(false);
        if (isUpLoadWeiZhi) {

            new zhuanFaInfoAPI(id, null, TYPE_ID, content, latitude + "", longitude + "", position, ispublic, state, tagId, null, this).request();
        } else {
            new zhuanFaInfoAPI(id, null, TYPE_ID, content, null, null, null, ispublic, state, tagId, null, this).request();

        }

    }

    /**
     * 点击"取消按钮"
     *
     * @return true 弹出对话框，false 不弹出对话框
     */
    private boolean close() {
        content = et_content.getText().toString();
        position = tv_location.getText().toString();

        if (!Utils.isEmpty(tagId)) {
            return true;
        }
        if (!Utils.isEmpty(content)) {
            return true;
        }

        if (isGongkai) {
            ispublic = "1";
        } else {
            ispublic = "0";
            return true;

        }
        return false;
    }


    private String tagId;

    @Override
    public void onBsSelect(int type, int index) {
        if (type == 1) {
            BiaoQianBean selectIF = (BiaoQianBean) biaoQianBeanList.get(index);
            selectIF.getTitle();
            tv_biaoQian.setText(selectIF.getTitle());
            tagId = selectIF.getId();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSearch.destroy();
    }

}
