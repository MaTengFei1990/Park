package com.hollysmart.style;

import android.app.Application;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Vibrator;
import android.widget.Toast;

import com.hollysmart.park.MainActivity;
import com.hollysmart.park.R;
import com.hollysmart.utils.Mlog;
import com.squareup.leakcanary.LeakCanary;
import com.umeng.commonsdk.UMConfigure;
import com.umeng.message.IUmengRegisterCallback;
import com.umeng.message.PushAgent;
import com.umeng.message.UmengNotificationClickHandler;
import com.umeng.message.entity.UMessage;
import com.umeng.socialize.PlatformConfig;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.log.LoggerInterceptor;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

public class App_Cai extends Application {

    private static App_Cai app_cai;
    private static Handler sHandler;
    public Vibrator mVibrator;
    public  static String  deviceToken;

    @Override
    public void onCreate() {
        super.onCreate();
        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }
        LeakCanary.install(this);
        Mlog.TAG = "com.test";
        Mlog.OPENLOG = true;

        /**
         * 初始化common库
         * 参数1:上下文，不能为空
         * 参数2:【友盟+】 AppKey
         * 参数3:【友盟+】 Channel
         * 参数4:设备类型，UMConfigure.DEVICE_TYPE_PHONE为手机、UMConfigure.DEVICE_TYPE_BOX为盒子，默认为手机
         * 参数5:Push推送业务的secret
         */
        UMConfigure.init(this, "5c74e86ff1f556811d000813", "Umeng", UMConfigure.DEVICE_TYPE_PHONE, "467e0e340982ae78c4fc0b1fd2779e73");

        PushAgent mPushAgent = PushAgent.getInstance(this);
        //注册推送服务，每次调用register方法都会回调该接口
        mPushAgent.register(new IUmengRegisterCallback() {

            @Override
            public void onSuccess(String deviceToken) {
                //注册成功会返回device token
//                deviceToken.toString();
                App_Cai.this.deviceToken=deviceToken;
            }

            @Override
            public void onFailure(String s, String s1) {

            }
        });
        app_cai = this;
        sHandler = new Handler();
        initOkHttpUtils();
        mVibrator = (Vibrator) getApplicationContext().getSystemService(Service.VIBRATOR_SERVICE);


        UmengNotificationClickHandler notificationClickHandler = new UmengNotificationClickHandler() {

            @Override
            public void dealWithCustomAction(Context context, UMessage msg) {
                Map<String, String> extra = msg.extra;
                String link = extra.get("link");
                Intent intent = new Intent(context, MainActivity.class);
                intent.putExtra("link",link);
                intent.putExtra("type",3);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);

            }
        };
        mPushAgent.setNotificationClickHandler(notificationClickHandler);



        PlatformConfig.setWeixin(getString(R.string.weixinAppId), getString(R.string.weixinAppKey));
        PlatformConfig.setQQZone(getString(R.string.qqAppId), getString(R.string.qqAppKey));
//        PlatformConfig.setSinaWeibo(getString(R.string.sinaAppKey), getString(R.string.sinaAppSecret));


    }
    private void initOkHttpUtils() {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(new LoggerInterceptor("com.http"))
                .connectTimeout(60000L, TimeUnit.MILLISECONDS)
                .readTimeout(60000L, TimeUnit.MILLISECONDS)
                //其他配置
                .build();
        OkHttpUtils.initClient(okHttpClient);
    }


    public static Application getApplictation() {
        return app_cai;
    }

    public static Context getContext() {
        return app_cai;
    }

    public static Handler getHandler() {
        return sHandler;
    }

}























