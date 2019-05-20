package com.hollysmart.park;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Build;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.hollysmart.apis.ResDataGetAPI;
import com.hollysmart.beans.ResDataBean;
import com.hollysmart.db.UserInfo;
import com.hollysmart.style.StyleAnimActivity;
import com.hollysmart.utils.ACache;
import com.hollysmart.utils.Mlog;
import com.hollysmart.utils.Utils;
import com.hollysmart.value.Values;

import java.io.File;
import java.io.Serializable;

import cn.bingoogolapple.qrcode.core.QRCodeView;
import cn.bingoogolapple.qrcode.zxing.ZXingView;

public class Ma_ScanActivity extends StyleAnimActivity implements QRCodeView.Delegate {

    private static final String TAG = Ma_ScanActivity.class.getSimpleName();
    private QRCodeView mQRCodeView;
    private Context mContext;
    @Override
    public int layoutResID() {
        mContext = this;
        return R.layout.activity_ma_scan;
    }

    @Override
    public void init() {
        isLogin();
        tv_title.setText("扫一扫");
    }

    private TextView tv_title;

    @Override

    public void findView() {
        tv_title = (TextView) findViewById(R.id.tv_title);
        mQRCodeView = (ZXingView) findViewById(R.id.zxingview);
        findViewById(R.id.iv_fanhui).setOnClickListener(this);
        String[] perms = {"android.permission.CAMERA"};

        int permsRequestCode = 200;

        /**
         * 判断系统的版本大于是否23
         */
        if (Build.VERSION.SDK_INT >= 23) {
            if (hasPermission("android.permission.CAMERA")) {
                mQRCodeView.setDelegate(this);
                mQRCodeView.startCamera();
                mQRCodeView.startSpot();

            } else {
                requestPermissions(perms, permsRequestCode);
            }
        } else {
            mQRCodeView.setDelegate(this);
            mQRCodeView.startCamera();
            mQRCodeView.startSpot();
        }


    }

    @Override
    protected void onRestart() {
        super.onRestart();
        mQRCodeView.startCamera();
        mQRCodeView.startCamera(Camera.CameraInfo.CAMERA_FACING_FRONT);
        mQRCodeView.showScanRect();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mQRCodeView.startCamera();
        mQRCodeView.startCamera(Camera.CameraInfo.CAMERA_FACING_FRONT);

        mQRCodeView.showScanRect();
    }

    @Override
    protected void onStop() {
        mQRCodeView.stopCamera();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        mQRCodeView.onDestroy();
        super.onDestroy();
    }


    private void vibrate() {
        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        vibrator.vibrate(200);
    }

    @Override
    public void onScanQRCodeSuccess(String result) {
        mQRCodeView.stopSpot();
        mQRCodeView.stopCamera();
        mQRCodeView.onDestroy();
        Mlog.d("扫描的数据ScanResult=", result);
        check(result);

    }

    private String id;
    private String fdTaskId;
    private String fdResModelId;

    /**
     * 判断扫描的结果是不是以   DataCollection,1556421003699,4642ceb6b1e349a9a22e77ad9086359d,04492deb83d04fda98388bc2e38a3ffa2
     * @param response 扫描的结果
     */
    private void check(String response) {

        if (!Utils.isEmpty(response)) {

            String[] results = response.split(",");

            if (results.length > 0) {

                if (!Utils.isEmpty(results[0])) {

                    if (results[0].equals("DataCollection")) {

                    }

                }
                if (!Utils.isEmpty(results[1])) {
                    id = results[1];

                }
                if (!Utils.isEmpty(results[2])) {

                    fdTaskId = results[2];

                }
                if (!Utils.isEmpty(results[3])) {

                    fdResModelId = results[3];

                }


            }

        }


        ResDataBean resDataBean = new ResDataBean();


        resDataBean.setId(id);

        resDataBean.setFdTaskId(fdTaskId);

        resDataBean.setFd_resmodelid(fdResModelId);


        new ResDataGetAPI(Values.TOKEN, resDataBean, new ResDataGetAPI.ResDataDeleteIF() {
            @Override
            public void onResDataDeleteResult(boolean isOk, ResDataBean resDataBen) {

                if (isOk) {

                    Intent intent = new Intent();
                    intent.putExtra("resDataBean", (Serializable) resDataBen);
                    setResult(3, intent);
                    finish();

                }

            }
        }).request();


    }












    @Override
    public void onScanQRCodeOpenCameraError() {
        Log.e(TAG, "打开相机出错");
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_fanhui:
                mQRCodeView.onDestroy();
                finish();
                break;
        }

    }


    private boolean hasPermission(String Premission) {
        if (canMaakSores()) {
            if (Build.VERSION.SDK_INT >= 23) {
                return (checkSelfPermission(Premission) == PackageManager.PERMISSION_GRANTED);
            }
        }

        return true;
    }

    private boolean canMaakSores() {
        return (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case 200:

                boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                if (cameraAccepted) {
                    //授权成功之后，调用系统相机进行拍照操作等
                    mQRCodeView.setDelegate(this);
                    mQRCodeView.startCamera();
                    mQRCodeView.startSpot();
                } else {
                    //用户授权拒绝之后，友情提示一下就可以了
                    Toast.makeText(mContext, "权限未开启，扫描功能暂不可以使用哦", Toast.LENGTH_LONG).show();
                    finish();
                }

                break;
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
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }



}
