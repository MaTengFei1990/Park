package com.hollysmart.park;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.TestLooperManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.hollysmart.apis.UpDateVersionAPI;
import com.hollysmart.dialog.LoadingProgressDialog;
import com.hollysmart.style.StyleAnimActivity;
import com.hollysmart.utils.Utils;
import com.hollysmart.value.Values;

public class AboutUsActivity extends StyleAnimActivity implements UpDateVersionAPI.UpdateVersionIF {


    @Override
    public int layoutResID() {
        return R.layout.activity_about_us;
    }


    private TextView tv_version;
    private TextView tv_haveRes;

    private LoadingProgressDialog lpd;
    @Override
    public void findView() {

        tv_version = findViewById(R.id.tv_version);
        tv_haveRes = findViewById(R.id.tv_haveRes);
        tv_haveRes.setVisibility(View.GONE);

        findViewById(R.id.rl_updateVersion).setOnClickListener(this);
        findViewById(R.id.tv_fanhui).setOnClickListener(this);

    }

    @Override
    public void init() {

        setLpd();

        PackageManager packageManager = getPackageManager();
        PackageInfo packageInfo = null;
        try {
            packageInfo = packageManager.getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        String versionName = packageInfo.versionName;
        tv_version.setText("版本：" + versionName);
        tv_haveRes.setText("v：" + versionName);




    }

    private void setLpd() {
        lpd = new LoadingProgressDialog();
        lpd.setMessage("正在退出当前账户，请稍等...");
        lpd.create(this, lpd.STYLE_SPINNER);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_updateVersion:


                lpd.setMessage("正在检查版本，请稍等...");
                lpd.show();
                PackageManager packageManager = getPackageManager();
                try {
                    PackageInfo packageInfo = packageManager.getPackageInfo(getPackageName(), 0);
                    int versionCode = packageInfo.versionCode;
                    checkUpdata(versionCode);

                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }

                break;

            case R.id.tv_fanhui:

                finish();
                break;
        }

    }


    private void checkUpdata(int versionCode) {
        new UpDateVersionAPI(versionCode, this).request();
    }

    @Override
    public void getUpdateVersion(boolean result, String downLoadURL, String remark) {
        lpd.cancel();
        if (result) {
            updataDialog(false, downLoadURL, remark);
        } else {
            Utils.showDialog(mContext,"暂无新版本");
        }
    }

    private void updataDialog(final boolean mastUpdate, final String downLoadURL, String remark) {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("发现新版本");
        builder.setMessage(remark);
        builder.setPositiveButton("更新", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String substring = Values.SERVICE_URL.substring(0, Values.SERVICE_URL.length() - 1);
                Uri uri = Uri.parse(substring + downLoadURL);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });
        if (mastUpdate) {
            builder.setCancelable(false);

        } else {
            builder.setNegativeButton("暂不更新", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });

        }
        builder.create().show();
    }

}
