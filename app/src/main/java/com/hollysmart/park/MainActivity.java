package com.hollysmart.park;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.gqt.bean.CallType;
import com.gqt.bean.RegisterListener;
import com.gqt.helper.CallEngine;
import com.gqt.helper.GQTHelper;
import com.gqt.helper.RegisterEngine;
import com.hollysmart.apis.GetUserInfoAPI;
import com.hollysmart.apis.UpDateVersionAPI;
import com.hollysmart.apis.UpdateDeviceTokenAPI;
import com.hollysmart.apis.UpdateInfoAPI;
import com.hollysmart.apis.UploadAvatarPicAPI;
import com.hollysmart.apis.UploadParkCoverPicAPI;
import com.hollysmart.apis.UserLoginAPI;
import com.hollysmart.beans.CallUserBean;
import com.hollysmart.beans.PicBean;
import com.hollysmart.beans.VoiceInfoBean;
import com.hollysmart.conference.MyCallListener;
import com.hollysmart.db.UserInfo;
import com.hollysmart.db.VoiceInfoDao;
import com.hollysmart.dialog.ButtomDialogView;
import com.hollysmart.formlib.ResDataListActivity;
import com.hollysmart.formlib.activitys.Cai_AddPicActivity;
import com.hollysmart.formlib.activitys.EditPicActivity;
import com.hollysmart.imgdownLoad.DownLoadImageService;
import com.hollysmart.imgdownLoad.ImageDownLoadCallBack;
import com.hollysmart.popuwindow.MoreWindow;
import com.hollysmart.service.DownloadService;
import com.hollysmart.style.App_Cai;
import com.hollysmart.style.StyleAnimActivity;
import com.hollysmart.tools.SharedPreferenceTools;
import com.hollysmart.utils.ACache;
import com.hollysmart.utils.BaiDuLatLng;
import com.hollysmart.utils.CCM_Bitmap;
import com.hollysmart.utils.Mlog;
import com.hollysmart.utils.OtherMap;
import com.hollysmart.utils.UMengShareUtil;
import com.hollysmart.utils.Utils;
import com.hollysmart.utils.fastBlur.FastBlur;
import com.hollysmart.utils.loctionpic.ImageItem;
import com.hollysmart.value.UserToken;
import com.hollysmart.value.Values;
import com.hollysmart.videocall.MonitorServer;
import com.hollysmart.voicecall.VoiceCallOutGoingActivity;
import com.umeng.socialize.UMShareAPI;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.FileCallBack;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import okhttp3.Call;

import static com.baidu.mapapi.BMapManager.getContext;

public class MainActivity extends StyleAnimActivity implements UpDateVersionAPI.UpdateVersionIF {

    private static final String URL = Values.SERVIXE_SHOUYE;
    private static final int DRAFT_FLAG = 4;
    private static final int DRAFT_REQUEST_CODE = 9;
    private static final int DRAFT_RESULT_CODE = 8;
    private OtherMap otherMap;
    private ButtomDialogView buttomDialogView;

    RegisterEngine registerEngine = null;
    boolean isAddressBook = false;
    CallEngine callEngine = null;

    private BroadcastReceiver br = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if("com.gqt.accept".equals(intent.getAction())){

            }else if("com.gqt.hangup".equals(intent.getAction())){
            }
        }
    };

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver(){

        @Override
        public void onReceive(Context arg0, Intent intent) {
            // TODO Auto-generated method stub
            if("com.gqt.loginout".equals(intent.getAction())){
//                MainActivity.this.finish();
            }
        }

    };




    Handler callHander = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {

                case 0://incall
//                    Utils.showToast(mContext,"msg.what=====" + msg.what);
                    Mlog.d( "callHander--------msg.what=="+msg.what );
                    if (msg.arg1 == CallType.VIDEOCALL || msg.arg1 == CallType.UPLOADVIDEO || msg.arg1 == CallType.TRANSCRIBE || msg.arg1 == CallType.MONITORVIDEO || msg.arg1 == CallType.DISPATCH) {
                    } else if (msg.arg1 == CallType.VOICECALL) {
                        sendBroadcast(new Intent("com.gqt.accept"));
                    } else if (msg.arg1 == CallType.CONFERENCE) {
//                        Toast.makeText(MainActivity.this, "conference incall", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case 1:
                    Mlog.d( "callHander--------msg.what=="+msg.what );
                    stopService(new Intent(MainActivity.this, MonitorServer.class));
//                    Toast.makeText(MainActivity.this, "state idle", Toast.LENGTH_SHORT).show();
                    sendBroadcast(new Intent("com.gqt.hangup"));
                    break;
                //呼出trt312
                case 2:
                    Mlog.d( "callHander--------msg.what=="+msg.what );
//                    Utils.showToast(mContext,"msg.what=====" + msg.what);

                    Map<String, String> member = null;
                    String mname = "";
                    if (isAddressBook) {
                        member = GQTHelper.getInstance().getGroupEngine().getMember((String) msg.obj);
                    }
                    if (member != null) {
                        mname = member.get("mname");
                    } else {
                        mname = (String) msg.obj;
                    }
                    if (msg.arg1 == CallType.VOICECALL) {
                        Intent voiceIntent = new Intent(MainActivity.this, VoiceCallOutGoingActivity.class);
                        voiceIntent.putExtra("num", mname);
                        startActivity(voiceIntent);
                    }

                    new SharedPreferenceTools(MainActivity.this).putValues(mname);
                    break;
                //呼入
                case 3:
                    Mlog.d( "callHander--------msg.what=="+msg.what );
//                    Utils.showToast(mContext,"msg.what=====" + msg.what);
                    String name = msg.getData().getString("name");
                    String num = msg.getData().getString("num");
                    if (msg.arg1 == CallType.VOICECALL) {
                        Intent invoiceIntent = new Intent(MainActivity.this, VoiceCallComingActivity.class);
                        invoiceIntent.putExtra("name", name);
                        invoiceIntent.putExtra("num", num);
                        startActivity(invoiceIntent);
                    } else if (msg.arg1 == CallType.SENDONLY_VOICECALL) {
                        GQTHelper.getInstance().getCallEngine().answerCall(CallType.VOICECALL, "");
                    }
                    break;
                case 99:
//                    Utils.showToast(mContext,"msg.what=====" + msg.what);
//                    Toast.makeText(MainActivity.this, (String) msg.obj, Toast.LENGTH_SHORT).show();
                    break;
                case 98:
                    switch ((Integer) msg.obj) {
                        case 480:
                            Toast.makeText(MainActivity.this, "用户不在线或无人接听", Toast.LENGTH_SHORT).show();
                            break;

                        default:
                            break;
                    }
                    sendBroadcast(new Intent("com.gqt.hangup"));
                    break;

                default:
                    break;
            }
        }
    };





    // 获取当前时间格式HH:mm:ss  jibingeng 2015-09-23
    public String getTime() {
        try {
            SimpleDateFormat formatter = new SimpleDateFormat(" HHmmss ");
            Date curDate = new Date(System.currentTimeMillis());
            String strTime = formatter.format(curDate);
            return strTime;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    @Override
    public int layoutResID() {
        return R.layout.activity_main;
    }

    private ImageView iv_yindao;
    private Intent callIntent = new Intent(Intent.ACTION_CALL);

    private  int type;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void findView() {
        iv_yindao = (ImageView) findViewById(R.id.iv_yindao);
        findViewById(R.id.view_top).setOnClickListener(this);

        iv_yindao.setOnClickListener(this);
        iv_yindao.setVisibility(View.GONE);
        webView = findViewById(R.id.webview);
        webView.getSettings().setDomStorageEnabled(true);

        //设置编码
        webView.getSettings().setDefaultTextEncodingName("utf-8");

        // 设置与Js交互的权限
        webView.getSettings().setJavaScriptEnabled(true);

        webView.loadUrl(URL);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);

        }


        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, final String url) {
                if (url != null) {
//
                    getWebView(webView, url);

                    return true;

                }
                return super.shouldOverrideUrlLoading(view, url);



            }

//            @Override
//            public void onPageFinished(WebView view, String url) {
//                toTopAndRefresh(webView ,url);
//            }
        });


        webView.setWebChromeClient(new WebChromeClient() {


            @Override
            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
                uploadMessageAboveL = filePathCallback;
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.addCategory(Intent.CATEGORY_OPENABLE);
                i.setType("*/*");
                MainActivity.this.startActivityForResult(
                        Intent.createChooser(i, "File Browser"),
                        FILECHOOSER_RESULTCODE);
                return true;

            }

            //For Android  >= 4.1
            public void openFileChooser(ValueCallback<Uri> valueCallback, String acceptType, String capture) {

                uploadMessage = valueCallback;
                //调用系统相机或者相册
                uploadPicture();
            }

        });

       type = getIntent().getIntExtra("type", 0);
        if (type == 3) {
            String link = getIntent().getStringExtra("link");
            jumpto(webView, link);
        }


        iv_yindao.setVisibility(View.VISIBLE);


    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void getWebView(WebView webView, final String url) {

        if (url.contains("pubinfo.html")) {
            pubinfo(webView, url);

        } else if (url.contains("repost.html")) {
            repost(webView, url);

        } else if (url.contains("upavatar.html")) {
            upavatar(webView);

        } else if (url.contains("upparkcover?iid=")) {
            upparkcover(webView,url);

        } else if (url.contains("download.html")) {
            download(webView, url);

        } else if (url.contains("preview.html")) {
            preview(webView, url);

        } else if (url.contains("my.html")) {
            my(webView, url);

        } else if (url.contains("task.html")) {
            task(webView, url);

        } else if (url.contains("map.html")) {
            map(url);

        } else if (url.contains("xx.html?loginok")) {
            loginok(webView, url);

        } else if (url.contains("shareimg.html?")) {
            shareImag(webView, url);

        } else if (url.contains("chat.html?")) {
            chat(webView, url);

        } else if (url.contains("tel")) {
            tel(url);

        } else if (url.contains("scada.html")) {
           startFormActivity(url);

        } else if (url.contains("updateapp.html")) {
            Intent intent = new Intent(mContext, AboutUsActivity.class);
            startActivity(intent);

        } else if (url.contains("resource.html?")) {
            startResourceListActivity(url);

        } else if (url.contains("xx.html?loginout")) {
            loginOut(url);

        } else {
            webView.loadUrl(url);

        }


    }

    private void loginOut(String url) {

        GQTHelper.getInstance().quit();

        VoiceInfoDao voiceInfoDao = new VoiceInfoDao(this);
        voiceInfoDao.clearData();

        webView.loadUrl(url);


    }

    /**
     * 进入资源列表页面;
     */

    private void startResourceListActivity(String url) {

        String[] scada = url.split("resource.html\\?");

        Map<String, String> map = new HashMap<String , String>();
        if (scada!=null&&scada.length>1) {

            String[] valuse = scada[1].split("&");



            if (valuse.length > 0) {

                for (int i = 0; i < valuse.length; i++) {

                    String s = valuse[i];

                    String[] split = s.split("=");
                    try {
                        map.put(split[0], URLDecoder.decode(split[1],"UTF-8"));
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }


                }


            }



        }


        Intent intent = new Intent(mContext, ResDataListActivity.class);
        intent.putExtra("exter", (Serializable) map);
        intent.putExtra("ischeck", true);
        startActivity(intent);
    }




    /**
     * 进入项目管理页面;
     */

    private void startFormActivity(String url) {

        String[] scada = url.split("scada.html\\?");
        Map<String, String> map = new HashMap<String , String>();
        if (scada!=null&&scada.length>1) {

            String[] valuse = scada[1].split("&");



            if (valuse.length > 0) {

                for (int i = 0; i < valuse.length; i++) {

                    String s = valuse[i];

                    String[] split = s.split("=");
                    try {
                        map.put(split[0], URLDecoder.decode(split[1],"UTF-8"));
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }


                }


            }



        }


        Intent intent = new Intent(mContext, ResDataListActivity.class);
        intent.putExtra("exter", (Serializable) map);
        intent.putExtra("ischeck", false);
        startActivity(intent);
    }

    /***
     * 分享图片
     * @param webView
     * @param url
     */

    private String shareimgUrl;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void shareImag(WebView webView, String url) {

        String[] shareids = url.split("shareimg.html\\?");
        if (shareids!=null&&shareids.length>1) {

            shareimgUrl = shareids[1];
        }

        webView.evaluateJavascript("javascript:app.gettoken()", new ValueCallback<String>() {
            @Override
            public void onReceiveValue(String value) {
                if (!Utils.isEmpty(value)) {
                    String substring = value.substring(1, value.length() - 1);
                    String token = "Bearer " + substring;
                    UserToken.getUserToken().setToken(token);

                    new UpdateDeviceTokenAPI(App_Cai.deviceToken, new UpdateDeviceTokenAPI.UpdateDeviceTokenIF() {
                        @Override
                        public void updateDeviceTokenResult(boolean isOk) {

                        }
                    }).request();
                }

               buttomDialogView.show();


            }
        });

    }

    /**
     * 语音会议
     * @param webView
     * @param url
     */
    private void chat(WebView webView, String url) {


        //检测是否有录音权限
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            Mlog.d("Main", "默认无录音权限");
            if (Build.VERSION.SDK_INT >= 23) {
                Mlog.d("Main", "系统版本不低于android6.0 ，需要动态申请权限");
                requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO}, 1001);
            }
        } else {
            Mlog.d("Main", "默认有录音权限");
            String chatId = "";
            String taskname = "";

//        chat.html?uname=东城区|朝阳区&uid=8020|8022&taskname=test注册风景区
            List<CallUserBean> localList = new ArrayList<>();

            String[] chat = url.split("chat.html\\?");
            if (chat!=null&&chat.length>1) {

                chatId = chat[1];


                String[] split = chatId.split("&");

                if (split.length == 3) {
                    String[] unames = split[0].split("=")[1].split("\\|");
                    String[] uIds = split[1].split("=")[1].split("\\|");
                    try {
                        taskname =URLDecoder.decode(split[2].split("=")[1],"UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }

                    if (unames.length > 0) {

                        for (int i = 0; i < unames.length; i++) {
                            try {
                                CallUserBean callUserBean = new CallUserBean();
                                callUserBean.setUid(uIds[i]);
                                callUserBean.setUname( URLDecoder.decode(unames[i],"UTF-8"));
                                localList.add(callUserBean);
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }



                        }


                    }


                }



            }

            Intent intent = new Intent();
            intent.setClass(MainActivity.this, VoiceCallInCallActivity.class);
            Bundle bundle = new Bundle();
            intent.putExtra("localList", (Serializable) localList);
            intent.putExtra("taskname", taskname);
            intent.putExtras(bundle);
            startActivity(intent);
        }

    }

    /***
     * 上传公园的照片
     * @param webView
     */

    private String upparkcoverid;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void upparkcover(WebView webView ,final String url) {
        String[] ids = url.split("iid=");
        if (ids!=null&&ids.length>1) {

            upparkcoverid = ids[1];
            String[] strings = upparkcoverid.split("\\.");
            upparkcoverid = strings[0];
        }

        webView.evaluateJavascript("javascript:app.gettoken()", new ValueCallback<String>() {
            @Override
            public void onReceiveValue(String value) {
                if (!Utils.isEmpty(value)) {
                    String substring = value.substring(1, value.length() - 1);
                    String token = "Bearer " + substring;
                    UserToken.getUserToken().setToken(token);

                    new UpdateDeviceTokenAPI(App_Cai.deviceToken, new UpdateDeviceTokenAPI.UpdateDeviceTokenIF() {
                        @Override
                        public void updateDeviceTokenResult(boolean isOk) {

                        }
                    }).request();
                }
                Intent intent = new Intent(MainActivity.this, Cai_AddPicActivity.class);
                intent.putExtra("num", 1);
                startActivityForResult(intent, 90);

            }
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @JavascriptInterface
    private void toTopAndRefresh(final WebView webView, String url) {
                    String method ="javascript:toTopAndRefresh()" ;
                    webView.evaluateJavascript(method, new ValueCallback<String>() {
                        @Override
                        public void onReceiveValue(String value) {
                            value.toString();

                        }
                    });

    }

    /***
     * 任务
     * @param webView
     * @param url
     */
    private void task(WebView webView, String url) {
        webView.loadUrl(url);

        iv_yindao.setImageResource(R.mipmap.yindao_task);
        iv_yindao.setVisibility(View.VISIBLE);

        String isFrist = ACache.get(getApplicationContext(), Values.CACHE_SECOND).getAsString(Values.CACHE_ISECOND);
        if (!Utils.isEmpty(isFrist)) {
            iv_yindao.setVisibility(View.GONE);
        }
        ACache.get(getApplicationContext(), Values.CACHE_SECOND).put(Values.CACHE_ISECOND, "OK");
    }

    /***
     * 拨打网页上的电话；
     * @param url
     */
    private void tel(String url) {
        String mobile = url.substring(url.lastIndexOf("/") + 1);
        Log.e("mobile----------->", mobile);
        Uri data = Uri.parse(mobile);
        callIntent.setData(data);
        //Android6.0以后的动态获取打电话权限
        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
            startActivity(callIntent);
            //这个超连接,java已经处理了，webview不要处理
        } else {
            //申请权限
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CALL_PHONE}, MY_PERMISSIONS_REQUEST_CALL);
        }
    }

    /****
     * 登录完成后调用
     * @param webView
     * @param url
     */
    String voicepwd = "";
    String voiceuser = "";


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void loginok(WebView webView, String url) {

        webView.evaluateJavascript("javascript:app.loginInfo()", new ValueCallback<String>() {
            @Override
            public void onReceiveValue(String value) {
                value.toString();

                if (value.contains("roleid")) {
                    roleid = 0;

                } else {
                    roleid = 1;
                }

                try {
                    Mlog.d("loginInfo = " + value);
                    JSONObject jsonObject = new JSONObject(value);


                    if (value.contains("voicepwd")) {
                        voicepwd  = jsonObject.getString("voicepwd");
                    }
                    if (value.contains("voiceuser")) {

                        voiceuser = jsonObject.getString("voiceuser");

                    }


                    VoiceInfoBean voiceInfoBean = new VoiceInfoBean();

                    voiceInfoBean.setId(1);
                    voiceInfoBean.setUserName(voiceuser);
                    voiceInfoBean.setUserPasWord(voicepwd);

                    VoiceInfoDao voiceInfoDao = new VoiceInfoDao(mContext);
                    voiceInfoDao.addOrUpdate(voiceInfoBean);

                    regitsterVoiceIP();


                } catch (JSONException e) {
                    e.printStackTrace();
                }



            }
        });
        webView.evaluateJavascript("javascript:app.getLoginInfo()", new ValueCallback<String>() {
            @Override
            public void onReceiveValue(String value) {
                if (!Utils.isEmpty(value)) {

                    value= value.substring(1,value.length()-1);

                    String[] split = value.split("\\|");

                    new UserLoginAPI(split[0], split[1], new UserLoginAPI.LoginInfoIF() {
                        @Override
                        public void loginResult(boolean isOk, String msg, String access_token, String token_type) {

                            if (isOk) {
                                UserToken.getUserToken().setFormToken(token_type + " " + access_token);
                                UserInfo userInfoBean = new UserInfo();
                                userInfoBean.setAccess_token(token_type + " " + access_token);

                                new GetUserInfoAPI(userInfoBean, new GetUserInfoAPI.UserInfoIF() {
                                    @Override
                                    public void userResult(boolean isOk, UserInfo userInfo) {
                                        if (isOk) {
                                            String userPath = Values.SDCARD_FILE(Values.SDCARD_CACHE) + Values.CACHE_USER;
                                            File file = new File(userPath);
                                            ACache.get(file).put(Values.CACHE_USERINFO, userInfo);

                                        }


                                    }
                                }).request();

                            }

                        }
                    }).request();
                }

            }
        });


        webView.evaluateJavascript("javascript:app.gettoken()", new ValueCallback<String>() {
            @Override
            public void onReceiveValue(String value) {
                if (!Utils.isEmpty(value)) {
                    String substring = value.substring(1, value.length() - 1);
                    String token = "Bearer " + substring;
                    UserToken.getUserToken().setToken(token);

                    new UpdateDeviceTokenAPI(App_Cai.deviceToken, new UpdateDeviceTokenAPI.UpdateDeviceTokenIF() {
                        @Override
                        public void updateDeviceTokenResult(boolean isOk) {

                        }
                    }).request();
                }
            }
        });


        webView.loadUrl(url);
    }




    private void regitsterVoiceIP() {
        VoiceInfoDao voiceInfoDao = new VoiceInfoDao(this);


        VoiceInfoBean latestBean = voiceInfoDao.getLatestBean();

        if (latestBean != null) {

            registerEngine = GQTHelper.getInstance().getRegisterEngine();


            if (!Utils.isEmpty(latestBean.getUserName()) &&! Utils.isEmpty(latestBean.getUserPasWord())) {


                registerEngine.unRegister();

                registerEngine.initRegisterInfo(latestBean.getUserName(), latestBean.getUserPasWord(), Values.SERVICE_URL_VOICE, Values.SERVICE_URL_VOICE_PORT, null);

                registerEngine.register(MainActivity.this, new RegisterListener() {
                    @Override
                    public void onRegisterSuccess() {
                        Mlog.d("registerEngine.register--------onRegisterSuccess==");
                    }

                    @Override
                    public void onRegisterFailded(String s) {

                        Mlog.d("registerEngine.register--------onRegisterFailded==" + s);

                    }
                });
                GQTHelper.getInstance().getCallEngine().registerCallListener(new MyCallListener(callHander));

            }


            callEngine = GQTHelper.getInstance().getCallEngine();

            GQTHelper.getInstance().getSetEngine().setOutGroupOnCallClosed(true);

        }


    }

    /***
     * 进入地图页面
     * @param url
     */
    private void map(String url) {
        String[] splits = url.split("\\?");
        String idtype = splits[1];
        String[] split = idtype.split("&");
        final double longitude = new Double(split[0]);
        final double latitude = new Double(split[1]);
        final String name = split[2];

        otherMap = new OtherMap(mContext);
        otherMap.selectDialog(new OtherMap.MapIf() {
            @Override
            public void selectMap(int tag) {
                String unitName = name;
                if (tag == otherMap.BAIDUTAG) {
                    otherMap.startBaiduMap(latitude, longitude, unitName);
                }
                if (tag == otherMap.GAODETAG) {
                    String[] latlans = new BaiDuLatLng().bToG(latitude, longitude);
                    double mlat = Double.parseDouble(latlans[0]);
                    double mlng = Double.parseDouble(latlans[1]);
                    otherMap.startGaoDeMap(mlat, mlng, unitName);
                }
            }
        });
    }

    /**
     * 进入我的页面
     *
     * @param webView
     * @param url
     */
    private void my(WebView webView, String url) {
        webView.loadUrl(url);
        if (UserToken.getUserToken().getToken() != null) {
            if (roleid == 0) {
                iv_yindao.setImageResource(R.mipmap.yindao_wode);
                iv_yindao.setVisibility(View.VISIBLE);
            } else if(roleid==1) {
                iv_yindao.setImageResource(R.mipmap.yindao_wode_waibu);
                iv_yindao.setVisibility(View.VISIBLE);
            }


            String isFrist = ACache.get(getApplicationContext(), Values.CACHE_THRID).getAsString(Values.CACHE_ISTHRID);
            if (!Utils.isEmpty(isFrist)) {
                iv_yindao.setVisibility(View.GONE);
            }
            ACache.get(getApplicationContext(), Values.CACHE_THRID).put(Values.CACHE_ISTHRID, "OK");
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void preview(WebView webView, final String url) {
        webView.evaluateJavascript("javascript:app.gettoken()", new ValueCallback<String>() {
            @Override
            public void onReceiveValue(String value) {
                Values.SDCARD_FILE(Values.SDCARD_FUJIAN);
                String fileName = "";
                String[] split = url.split("name=");
                if (split != null) {
                    fileName = split[1];
                }

                String directory = Values.SDCARD_FILE(Values.SDCARD_FUJIAN);
                File file = new File(directory + fileName);
                if (file.exists()) {
                    openFile(file.getAbsolutePath());
                } else {
                    if (!Utils.isEmpty(url)) {
                        String[] substring = url.split("preview.html\\?");
                        String downLoadUrl = substring[1];
//                                        downloadBinder.startDownload(downLoadUrl);
                        String fileName1 = "";
                        String[] fsplit = downLoadUrl.split("name=");
                        if (fsplit != null) {
                            fileName1 = fsplit[1];

                            java.net.URLDecoder urlDecoder = new java.net.URLDecoder();
                            try {
                                fileName1 = urlDecoder.decode(fileName1, "UTF-8");
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                        }

                        downloadData(fileName1, downLoadUrl);
                    }

                }

            }
        });
    }
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @JavascriptInterface
    private void jumpto(final WebView webView, final String link) {
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, final String url) {
                if (url != null) {
//
                    getWebView(webView, url);
                    return true;

                }
                return super.shouldOverrideUrlLoading(view, url);

            }

            @Override
            public void onPageFinished(WebView view, String url) {

                if (type == 3) {
                    String method ="javascript:jumpto('"+link+"')" ;
                    webView.evaluateJavascript(method, null);
                    type=0;
                }
                }
        });



    }

    /***
     * 园景通app需要实现一个附件下载的功能，监听这个url：download.html?http://10.2.8.60/Public/Download?path=C7C353BE-4F1F-4662-9BC0-2F8F0433C2BE&name=info-4.xlsx
     ，第一个问号后面的是下载文件的路径
     * @param webView
     * @param url
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void download(WebView webView, final String url) {
        webView.evaluateJavascript("javascript:app.gettoken()", new ValueCallback<String>() {
            @Override
            public void onReceiveValue(String value) {
                if (!Utils.isEmpty(url)) {
                    String[] substring = url.split("download.html\\?");
                    String downLoadUrl = substring[1];
                    String fileName1 = "";
                    String[] fsplit = downLoadUrl.split("name=");
                    if (fsplit != null) {
                        fileName1 = fsplit[1];

                        java.net.URLDecoder urlDecoder = new java.net.URLDecoder();
                        try {
                            fileName1 = urlDecoder.decode(fileName1, "UTF-8");
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                    }

                    downloadData(fileName1, downLoadUrl);
                }

            }
        });
    }

    /***
     * 上传头像
     * @param webView
     */

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void upavatar(WebView webView) {
        webView.evaluateJavascript("javascript:app.gettoken()", new ValueCallback<String>() {
            @Override
            public void onReceiveValue(String value) {
                if (!Utils.isEmpty(value)) {
                    String substring = value.substring(1, value.length() - 1);
                    String token = "Bearer " + substring;
                    UserToken.getUserToken().setToken(token);

                    new UpdateDeviceTokenAPI(App_Cai.deviceToken, new UpdateDeviceTokenAPI.UpdateDeviceTokenIF() {
                        @Override
                        public void updateDeviceTokenResult(boolean isOk) {

                        }
                    }).request();
                }
                Intent intent = new Intent(MainActivity.this, Cai_AddPicActivity.class);
                intent.putExtra("num", 1);
                startActivityForResult(intent, 1);

            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void repost(WebView webView, final String url) {
        webView.evaluateJavascript("javascript:app.gettoken()", new ValueCallback<String>() {
            @Override
            public void onReceiveValue(String value) {
                if (!Utils.isEmpty(value)) {
                    String substring = value.substring(1, value.length() - 1);
                    String token = "Bearer " + substring;
                    UserToken.getUserToken().setToken(token);


                    new UpdateDeviceTokenAPI(App_Cai.deviceToken, new UpdateDeviceTokenAPI.UpdateDeviceTokenIF() {
                        @Override
                        public void updateDeviceTokenResult(boolean isOk) {

                        }
                    }).request();


                }
                animType = 2;
                String[] splits = url.split("\\?");
                String idtype = splits[1];
                String[] split = idtype.split("\\|");
                String id = split[0];
                String type = split[1];
                if (type.equals("1")) {
                    Intent intent = new Intent(MainActivity.this, EditTextActivity.class);
                    intent.putExtra("id", id);
                    intent.putExtra("type", type);
                    intent.putExtra("draftFlag", DRAFT_FLAG);
                    intent.putExtra("roleid", roleid);
                    startActivityForResult(intent,DRAFT_REQUEST_CODE);

                } else {
                    Intent intent = new Intent(MainActivity.this, EditPicActivity.class);
                    intent.putExtra("id", id);
                    intent.putExtra("type", type);
                    intent.putExtra("draftFlag", DRAFT_FLAG);
                    intent.putExtra("roleid", roleid);
                    startActivityForResult(intent,DRAFT_REQUEST_CODE);
                }
            }
        });
    }
    private int roleid=0;//外部用户，内部用户；
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void pubinfo(WebView webView, final String url) {
        webView.evaluateJavascript("javascript:app.loginInfo()", new ValueCallback<String>() {
            @Override
            public void onReceiveValue(String value) {
                value.toString();

                if (value.contains("roleid")) {
                    roleid = 0;

                } else {
                    roleid = 1;
                }
            }
        });
        webView.evaluateJavascript("javascript:app.gettoken()", new ValueCallback<String>() {
            @Override
            public void onReceiveValue(String value) {
                if (!Utils.isEmpty(value)) {
                    String substring = value.substring(1, value.length() - 1);
                    String token = "Bearer " + substring;
                    UserToken.getUserToken().setToken(token);

                    new UpdateDeviceTokenAPI(App_Cai.deviceToken, new UpdateDeviceTokenAPI.UpdateDeviceTokenIF() {
                        @Override
                        public void updateDeviceTokenResult(boolean isOk) {

                        }
                    }).request();

                }

                if (url.contains("pubinfo.html?")) {

                    String[] strings = url.split("pubinfo.html\\?");
                    String id = strings[1];
                    String neiRong = "";
                    if (id.contains("|")) {
                        String[] splits = id.split("\\|");

                        id = splits[0];

                        neiRong = splits[1];
                        java.net.URLDecoder urlDecoder = new java.net.URLDecoder();
                        try {
                            neiRong = urlDecoder.decode(neiRong, "UTF-8");
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                    }

                    Intent intent = new Intent(MainActivity.this, ZhuanFaActivity.class);
                    intent.putExtra("id", id);
                    intent.putExtra("neiRong", neiRong);
                    intent.putExtra("type", 1);
                    startActivity(intent);

                } else {
                    if (!Utils.isEmpty(value)) {
                        String substring = value.substring(1, value.length() - 1);
                        String token = "Bearer " + substring;
                        UserToken.getUserToken().setToken(token);
                    }
                    animType = 2;
                    View getBackground = new GetBackground().invoke();

                    showMoreWindow(getBackground);


                }


            }
        });
    }


    /**
     * 第三种方法
     */
    private  long firstTime=0;
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && webView.canGoBack()) {
            long secondTime = System.currentTimeMillis();
            if (secondTime - firstTime > 2000) {
                firstTime = secondTime;
                webView.goBack();// 返回前一个页面
                return true;
            } else{
                finish();
                return false;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    private static final int REQUEST_CODE_ALBUM = 0x08;
    private static final int REQUEST_CODE_CAMERA = 0x07;
    private static final int REQUEST_CODE_PERMISSION_CAMERA = 0x09;
    private static final int FILECHOOSER_RESULTCODE = 0x29;
    private ValueCallback<Uri> uploadMessage;
    private ValueCallback<Uri[]> uploadMessageAboveL;
    private String mCurrentPhotoPath;
    private String mLastPhothPath;
    private Thread mThread;


    private ImageView image;
    private MoreWindow mMoreWindow;

    private UMengShareUtil uMengShareUtil;

    private void showMoreWindow(View view) {
        if (null == mMoreWindow) {
            mMoreWindow = new MoreWindow(this, roleid);
            mMoreWindow.init();
        } else {
            mMoreWindow.setRoleid(roleid);
        }

        mMoreWindow.showMoreWindow(view, 100);
    }

    /**
     * 选择相机或者相册
     */
    public void uploadPicture() {

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("请选择图片上传方式");

        //取消对话框
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {

                //一定要返回null,否则<input type='file'>
                if (uploadMessage != null) {
                    uploadMessage.onReceiveValue(null);
                    uploadMessage = null;
                }
                if (uploadMessageAboveL != null) {
                    uploadMessageAboveL.onReceiveValue(null);
                    uploadMessageAboveL = null;

                }
            }
        });


        builder.setPositiveButton("相机", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {


                if (!TextUtils.isEmpty(mLastPhothPath)) {
                    //上一张拍照的图片删除
                    mThread = new Thread(new Runnable() {
                        @Override
                        public void run() {

                            File file = new File(mLastPhothPath);
                            if (file != null) {
                                file.delete();
                            }
                            mHandler.sendEmptyMessage(1);

                        }
                    });

                    mThread.start();


                } else {

                    //请求拍照权限
                    if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                        takePhoto();
                    } else {
                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA}, REQUEST_CODE_PERMISSION_CAMERA);
                    }
                }


            }
        });
        builder.setNegativeButton("相册", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                chooseAlbumPic();


            }
        });

        builder.create().show();

    }

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            takePhoto();
        }
    };

    /**
     * 拍照
     */
    private void takePhoto() {

        StringBuilder fileName = new StringBuilder();
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        fileName.append(UUID.randomUUID()).append("_upload.png");
        File tempFile = new File(mContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES), fileName.toString());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Uri uri = FileProvider.getUriForFile(mContext, BuildConfig.APPLICATION_ID + ".fileProvider", tempFile);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        } else {
            Uri uri = Uri.fromFile(tempFile);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        }

        mCurrentPhotoPath = tempFile.getAbsolutePath();
        startActivityForResult(intent, REQUEST_CODE_CAMERA);


    }

    /**
     * 选择相册照片
     */
    private void chooseAlbumPic() {
        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.addCategory(Intent.CATEGORY_OPENABLE);
        i.setType("image/*");
        startActivityForResult(Intent.createChooser(i, "Image Chooser"), REQUEST_CODE_ALBUM);

    }


    boolean openFile(String path) {
        try {
            Mlog.d("文件路径 ：" + path);
            File file = new File(path);
            Uri uri = null;
            String type = null;
            if (Build.VERSION.SDK_INT >= 24) {
                uri = FileProvider.getUriForFile(mContext, mContext.getPackageName() + ".fileprovider", file);
            } else {
                uri = Uri.fromFile(file);
            }
            if (path.contains("doc") || path.contains("wps")) {
                type = "application/msword";
            } else if (path.contains("xls")) {
                type = "application/vnd.ms-excel";
            } else if (path.contains("pdf")) {
                type = "application/pdf";
            } else if (path.contains("txt")) {
                type = "text/html";
            } else if (path.contains("jpg")) {
                type = "image/jpeg";
            }

            Intent intent = new Intent();
            intent.setAction(android.content.Intent.ACTION_VIEW);

            intent.setDataAndType(uri, type);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            try {
                mContext.startActivity(intent);
                return true;
            } catch (ActivityNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(mContext, "请下载相关应用程序打开文件", Toast.LENGTH_LONG).show();
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private DownloadService.DownloadBinder downloadBinder;

    private ServiceConnection connection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            downloadBinder = (DownloadService.DownloadBinder) service;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    public void init() {
        String isFrist = ACache.get(getApplicationContext(), Values.CACHE_FRIST).getAsString(Values.CACHE_ISFRIST);
        if (!Utils.isEmpty(isFrist)) {
            iv_yindao.setVisibility(View.GONE);
        }
        ACache.get(getApplicationContext(), Values.CACHE_FRIST).put(Values.CACHE_ISFRIST, "OK");
        registerBR();
        CreateDir();

        Intent intent = new Intent(this, DownloadService.class);
        // 启动服务
        startService(intent);
        // 绑定服务
        bindService(intent, connection, BIND_AUTO_CREATE);
        myPermission();

        broadCastHandler = new Handler() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case 0:
                        webView.evaluateJavascript("location.reload()", new ValueCallback<String>() {
                            @Override
                            public void onReceiveValue(String value) {
                            }
                        });
                        break;

                }
            }
        };

        PackageManager packageManager = getPackageManager();
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(getPackageName(), 0);
            int versionCode = packageInfo.versionCode;
            checkUpdata(versionCode);

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        uMengShareUtil = new UMengShareUtil(this);

        buttomDialogView = new ButtomDialogView(mContext, getLayoutInflater().inflate(R.layout.dialog_layout_bottom, null), true, true, new ButtomDialogView.shareListener() {
            @Override
            public void share() {
                buttomDialogView.dismiss();

                uMengShareUtil.openShare(shareimgUrl);


            }
        }, new ButtomDialogView.SaveLocalListener() {
            @Override
            public void saveLocal() {

                onDownLoad(shareimgUrl);

            }
        });

    }


    /**
     * 启动图片下载线程
     */
    private void onDownLoad(String url) {
        DownLoadImageService service = new DownLoadImageService(getApplicationContext(),
                url,
                new ImageDownLoadCallBack() {

                    @Override
                    public void onDownLoadSuccess(File file) {
                    }
                    @Override
                    public void onDownLoadSuccess(Bitmap bitmap) {
                        // 在这里执行图片保存方法
                        Message message = new Message();
                        message.what = MSG_VISIBLE;
                        handler.sendMessageDelayed(message, delayTime);
                    }

                    @Override
                    public void onDownLoadFailed() {
                        // 图片保存失败
                        Message message = new Message();
                        message.what = MSG_ERROR;
                        handler.sendMessageDelayed(message, delayTime);
                    }
                });
        //启动图片下载线程
        new Thread(service).start();
    }

    private static final int MSG_VISIBLE=23;
    private static final int MSG_ERROR=24;
    private int delayTime=100;

    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_VISIBLE:// 成功；
                    Utils.showToast(mContext, "图片保存成功");
                    buttomDialogView.dismiss();
                    break;
                case MSG_ERROR:// 失败

                    Utils.showToast(mContext, "图片保存失败");
                    buttomDialogView.dismiss();
                    break;

            }

        }
    };



    private void checkUpdata(int versionCode) {
        new UpDateVersionAPI(versionCode, this).request();
    }

    @Override
    public void getUpdateVersion(boolean result, String downLoadURL, String remark) {
        if (result) {
            updataDialog(false, downLoadURL, remark);
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

    private void CreateDir() {
        CreateDir(Values.SDCARD_DIR);
        CreateDir(Values.SDCARD_FILE(Values.SDCARD_PIC));
        CreateDir(Values.SDCARD_FILE(Values.SDCARD_FUJIAN));
        CreateDir(Values.SDCARD_FILE(Values.SDCARD_PIC_WODE));
        CreateDir(Values.SDCARD_FILE("apk"));
    }

    private File CreateDir(String folder) {
        File dir = new File(folder);
        dir.mkdirs();
        return dir;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_yindao:
                iv_yindao.setVisibility(View.GONE);
                break;
            case R.id.view_top:
                toTopAndRefresh(webView, null);
                break;
        }

    }

    private final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1;
    private final int MY_PERMISSIONS_REQUEST_CALL = 2;

    @TargetApi(23)
    private void myPermission() {
        if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(mContext, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(mContext, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(mContext, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.CALL_PHONE,
                    Manifest.permission.READ_PHONE_STATE,
                    Manifest.permission.ACCESS_COARSE_LOCATION}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
        } else {
            CreateDir();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    // 权限请求成功的操作
                    CreateDir();
                } else {
                    // 权限请求失败的操作
                    Utils.showToast(mContext, "请在权限管理中设置存储权限，不然会影响正常使用");
                }
                break;
            case MY_PERMISSIONS_REQUEST_CALL:
                // 权限请求成功的操作
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                    startActivity(callIntent);
                    return;
                } else {
                    // 权限请求失败的操作
                    Utils.showToast(mContext, "请在权限管理中设置打电话权限，不然会影响正常使用");
                }
                break;
        }

    }

    @SuppressLint("HandlerLeak")
    private Handler reciviHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            String value = (String) msg.obj;
            if (!Utils.isEmpty(value)) {
                String substring = value.substring(1, value.length() - 1);
                String token = "Bearer " + substring;
                UserToken.getUserToken().setToken(token);
            }
        }
    };


    private WebView webView;

    private String cacheUrl;


    public void setUrl(String url) {
        webView.loadUrl(url);
    }


    public String getCacheUrl() {
        return cacheUrl;
    }

    public void setCacheUrl(String cacheUrl) {
        this.cacheUrl = cacheUrl;
    }


    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();



        regitsterVoiceIP();
    }


    /**
     * 选择完图片后的回调
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1:
                if (resultCode == 1) {
                    String picPath = data.getStringExtra("picPath");
                    Uri mUri;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        mUri = FileProvider.getUriForFile(MainActivity.this, BuildConfig.APPLICATION_ID + ".fileprovider", new File(picPath));
                    } else {
                        mUri = FileProvider.getUriForFile(MainActivity.this, BuildConfig.APPLICATION_ID + ".fileprovider", new File(picPath));
                    }
                    startPhotoZoom(mUri);
                } else if (resultCode == 2) {
                    List<ImageItem> picPaths = (List<ImageItem>) data.getSerializableExtra("picPath");
                    if (picPaths != null && picPaths.size() > 0) {

                        String picPath = picPaths.get(0).imagePath;
                        Uri mUri;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            mUri = FileProvider.getUriForFile(MainActivity.this, BuildConfig.APPLICATION_ID + ".fileprovider", new File(picPath));
                        } else {
                            mUri = FileProvider.getUriForFile(MainActivity.this, BuildConfig.APPLICATION_ID + ".fileprovider", new File(picPath));
                        }
                        startPhotoZoom(mUri);
                    }
                }
                break;

            case 3:
                setPicToView(data);
                break;
            case 91:
                setPicToView2(data);
                break;
            case DRAFT_REQUEST_CODE:
                if (resultCode == DRAFT_RESULT_CODE) {

                    toTopAndRefresh(webView, null);
                }
                break;
            case 90:
                if (resultCode == 1) {
                    String picPath = data.getStringExtra("picPath");
                    Uri mUri;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        mUri = FileProvider.getUriForFile(MainActivity.this, BuildConfig.APPLICATION_ID + ".fileprovider", new File(picPath));
                    } else {
                        mUri = FileProvider.getUriForFile(MainActivity.this, BuildConfig.APPLICATION_ID + ".fileprovider", new File(picPath));
                    }
                    startPhotoZoom(mUri,91);
                } else if (resultCode == 2) {
                    List<ImageItem> picPaths = (List<ImageItem>) data.getSerializableExtra("picPath");
                    if (picPaths != null && picPaths.size() > 0) {

                        String picPath = picPaths.get(0).imagePath;
                        Uri mUri;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            mUri = FileProvider.getUriForFile(MainActivity.this, BuildConfig.APPLICATION_ID + ".fileprovider", new File(picPath));
                        } else {
                            mUri = FileProvider.getUriForFile(MainActivity.this, BuildConfig.APPLICATION_ID + ".fileprovider", new File(picPath));
                        }
                        startPhotoZoom(mUri,91);
                    }
                }
                break;

        }

        if (requestCode == FILECHOOSER_RESULTCODE) {
            if (null == uploadMessage && null == uploadMessageAboveL) return;
            Uri result = data == null || resultCode != RESULT_OK ? null : data.getData();
            if (uploadMessageAboveL != null) {
                onActivityResultAboveL(requestCode, resultCode, data);
            } else if (uploadMessage != null) {
                uploadMessage.onReceiveValue(result);
                uploadMessage = null;
            }
        }


        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);

    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void onActivityResultAboveL(int requestCode, int resultCode, Intent data) {
        if (requestCode != FILECHOOSER_RESULTCODE
                || uploadMessageAboveL == null) {
            return;
        }
        Uri[] results = null;
        if (resultCode == Activity.RESULT_OK) {
            if (data == null) {
            } else {
                String dataString = data.getDataString();
                ClipData clipData = data.getClipData();
                if (clipData != null) {
                    results = new Uri[clipData.getItemCount()];
                    for (int i = 0; i < clipData.getItemCount(); i++) {
                        ClipData.Item item = clipData.getItemAt(i);
                        results[i] = item.getUri();
                    }
                }
                if (dataString != null)
                    results = new Uri[]{Uri.parse(dataString)};
            }
        }
        uploadMessageAboveL.onReceiveValue(results);
        uploadMessageAboveL = null;
        return;
    }


    Uri uritempFile;

    /**
     * 裁剪图片方法实现
     *
     * @param uri
     */
    public void startPhotoZoom(Uri uri) {
        /*
         * 至于下面这个Intent的ACTION是怎么知道的，大家可以看下自己路径下的如下网页
         * yourself_sdk_path/docs/reference/android/content/Intent.html
         * 直接在里面Ctrl+F搜：CROP ，之前小马没仔细看过，其实安卓系统早已经有自带图片裁剪功能,
         * 是直接调本地库的，小马不懂C C++  这个不做详细了解去了，有轮子就用轮子，不再研究轮子是怎么
         * 制做的了...吼吼
         */
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.setDataAndType(uri, "image/*");
        //下面这个crop=true是设置在开启的Intent中设置显示的VIEW可裁剪
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 150);
        intent.putExtra("outputY", 150);
        intent.putExtra("return-data", true);
        //裁剪后的图片Uri路径，uritempFile为Uri类变量
        uritempFile = Uri.parse("file://" + "/" + Environment.getExternalStorageDirectory().getPath() + "/" + "wodeIcon.jpg");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uritempFile);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        startActivityForResult(intent, 3);
    }
    /**
     * 裁剪图片方法实现
     *
     * @param uri
     */
    public void startPhotoZoom(Uri uri,int requestCode) {
        /*
         * 至于下面这个Intent的ACTION是怎么知道的，大家可以看下自己路径下的如下网页
         * yourself_sdk_path/docs/reference/android/content/Intent.html
         * 直接在里面Ctrl+F搜：CROP ，之前小马没仔细看过，其实安卓系统早已经有自带图片裁剪功能,
         * 是直接调本地库的，小马不懂C C++  这个不做详细了解去了，有轮子就用轮子，不再研究轮子是怎么
         * 制做的了...吼吼
         */
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.setDataAndType(uri, "image/*");
        //下面这个crop=true是设置在开启的Intent中设置显示的VIEW可裁剪
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 7);
        intent.putExtra("aspectY", 4);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 350);
        intent.putExtra("outputY", 200);
        intent.putExtra("return-data", true);
        //裁剪后的图片Uri路径，uritempFile为Uri类变量
        uritempFile = Uri.parse("file://" + "/" + Environment.getExternalStorageDirectory().getPath() + "/" + "wodeIcon.jpg");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uritempFile);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        startActivityForResult(intent, requestCode);
    }

    /**
     * 保存裁剪之后的图片数据
     *
     * @param picdata
     */
    private void setPicToView(Intent picdata) {
        if (!Utils.isEmpty(uritempFile.toString())) {

            try {
                Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(uritempFile));
                final String picPath = Values.SDCARD_FILE(Values.SDCARD_PIC_WODE) + System.currentTimeMillis() + ".jpg";
                CCM_Bitmap.getBitmapToFile(bitmap, picPath);
                final PicBean bean = new PicBean(null, picPath, null, 0, "false");
                new UploadAvatarPicAPI(bean, new UploadAvatarPicAPI.UploadAvatarPicIF() {
                    @Override
                    public void uploadAvatarPicResult() {
                        new UpdateInfoAPI(bean, new UpdateInfoAPI.UpdateInfoIF() {
                            @Override
                            public void UpdateInfoResult(boolean isOk) {
                                if (isOk) {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                                        webView.evaluateJavascript("javascript:location.reload()", new ValueCallback<String>() {
                                            @Override
                                            public void onReceiveValue(String value) {
                                            }
                                        });
                                    }

                                }
                            }
                        }).request();
                    }
                }).request();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
    /**
     * 保存裁剪之后的图片数据
     *
     * @param picdata
     */
    private void setPicToView2(Intent picdata) {
        if (!Utils.isEmpty(uritempFile.toString())) {

            try {
                Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(uritempFile));
                final String picPath = Values.SDCARD_FILE(Values.SDCARD_PIC_WODE) + System.currentTimeMillis() + ".jpg";
                CCM_Bitmap.getBitmapToFile(bitmap, picPath);
                final PicBean bean = new PicBean(null, picPath, null, 0, "false");
                new UploadParkCoverPicAPI(bean, new UploadParkCoverPicAPI.UploadParkCoverPicIF() {
                    @Override
                    public void uploadParkCoverResult() {

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                            String method ="javascript:updateimg('"+bean.getUrlpath()+"')" ;
                            webView.evaluateJavascript(method, null);
                        }
                    }

                }).request();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }


    private void downloadData(final String fileName, String fileUrl) {

        final View view = LayoutInflater.from(mContext).inflate(R.layout.view_court_progress, null);
        final AlertDialog alertDialog = new AlertDialog.Builder(mContext).setTitle("数据下载中").setView(view).create();
        alertDialog.setCancelable(false);
        final TextView textView = view.findViewById(R.id.tv_dialog);
        final ProgressBar progressBar = view.findViewById(R.id.pb_dialog);
        textView.setText("连接中，请稍候...");
        alertDialog.show();

        OkHttpUtils.get().url(fileUrl)
                .build().execute(new FileCallBack(Values.SDCARD_FILE(Values.SDCARD_FUJIAN), fileName) {
            @Override
            public void inProgress(float progress, long total, int id) {
                super.inProgress(progress, total, id);
                Mlog.d("progress = " + progress + "    total = " + total);
                textView.setText("已完成：" + Utils.bToKbToMb((long) (progress * total)) + "/" + Utils.bToKbToMb(total));
                progressBar.setProgress((int) (100 * progress));
            }

            @Override
            public void onError(Call call, Exception e, int id) {
                e.printStackTrace();
                alertDialog.cancel();
            }

            @Override
            public void onResponse(File response, int id) {
                alertDialog.cancel();
                if (response.exists()) {
                    openFile(response.getAbsolutePath());
                }
            }
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        //清空所有Cookie
        CookieSyncManager.createInstance(getContext());  //Create a singleton CookieSyncManager within a context
        CookieManager cookieManager = CookieManager.getInstance(); // the singleton CookieManager instance
        cookieManager.removeAllCookie();// Removes all cookies.
        CookieSyncManager.getInstance().sync(); // forces sync manager to sync now

        webView.setWebChromeClient(null);
        webView.setWebViewClient(null);
        webView.getSettings().setJavaScriptEnabled(false);
        webView.clearCache(true);
        unRegisterBR();
    }

    private class GetBackground {

        private DialogFragment newFragment;
        private byte[] bitmapByte;
        private View bgView;

        public View getBgView() {
            return invoke();
        }

        public byte[] getBitmapByte() {
            return bitmapByte;
        }

        public View invoke() {
            bgView = getWindow().getDecorView();
            bgView.setDrawingCacheEnabled(true);
            bgView.buildDrawingCache(true);


            /**
             * 获取当前窗口快照，相当于截屏
             */
            Bitmap bmp1 = bgView.getDrawingCache();


            Bitmap scaledBitmap = Bitmap.createScaledBitmap(bmp1,
                    (int) (bmp1.getWidth()),
                    (int) (bmp1.getHeight()),
                    false);


            scaledBitmap = FastBlur.toBlur(scaledBitmap, (int) 15);


//            newFragment =  CustomViewAnyPositionDialog.getInstance(mContext);


            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            scaledBitmap.compress(Bitmap.CompressFormat.PNG, 80, baos);
            bitmapByte = baos.toByteArray();
            bgView.setDrawingCacheEnabled(false);
            return bgView;
        }
    }

    private Handler broadCastHandler;
    private JianCeBroadcastReceiver receiver;

    private class JianCeBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            int resultType = intent.getIntExtra("resultType", 0);
            broadCastHandler.sendEmptyMessage(resultType);
        }
    }

    private void registerBR() {
        receiver = new JianCeBroadcastReceiver();
        IntentFilter filter = new IntentFilter(Values.RELOAD_DATA);
        getContext().registerReceiver(receiver, filter);


        registerReceiver(broadcastReceiver, new IntentFilter("com.gqt.loginout"));

        IntentFilter filter2 = new IntentFilter();
        filter2.addAction("com.gqt.accept");
        filter2.addAction("com.gqt.hangup");
        registerReceiver(br, filter2);

    }


    private void unRegisterBR() {
        if (receiver != null){
            getContext().unregisterReceiver(receiver);

        }

        if (br != null) {
            MainActivity.this.unregisterReceiver(br);
        }
        if (broadcastReceiver != null) {
            MainActivity.this.unregisterReceiver(broadcastReceiver);
        }
    }


    /**
     * 判断用户登录状态，登录获取用户信息
     */
    private UserInfo userInfo;

    public boolean isLogin() {
        if (userInfo != null)
            return true;
        Object obj = ACache.get(getApplicationContext(), Values.CACHE_USER).getAsObject(Values.CACHE_USERINFO);
        if (obj != null) {
            userInfo = (UserInfo) obj;
            return true;
        }
        return false;
    }



}
