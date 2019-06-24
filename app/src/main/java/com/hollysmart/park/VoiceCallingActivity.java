package com.hollysmart.park;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Toast;

import com.gqt.bean.CallType;
import com.gqt.bean.RegisterListener;
import com.gqt.helper.CallEngine;
import com.gqt.helper.GQTHelper;
import com.gqt.helper.RegisterEngine;
import com.hollysmart.conference.MyCallListener;
import com.hollysmart.style.StyleAnimActivity;
import com.hollysmart.tools.SharedPreferenceTools;
import com.hollysmart.utils.Mlog;
import com.hollysmart.utils.Utils;
import com.hollysmart.videocall.MonitorServer;
import com.hollysmart.voicecall.VoiceCallOutGoingActivity;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Map;


public class VoiceCallingActivity extends StyleAnimActivity  implements  View.OnClickListener {


    @Override
    public int layoutResID() {
        return R.layout.activity_voice_calling;
    }


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
                VoiceCallingActivity.this.finish();
            }
        }

    };

    @Override
    public void findView() {
        registerEngine = GQTHelper.getInstance().getRegisterEngine();

        if (!registerEngine.isRegister()) {

            registerEngine.initRegisterInfo("8017", "8017", "39.106.172.189", 7080, null);

            registerEngine.register(VoiceCallingActivity.this, new RegisterListener() {
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

            callEngine = GQTHelper.getInstance().getCallEngine();

        } else {
            callEngine = GQTHelper.getInstance().getCallEngine();
        }

        GQTHelper.getInstance().getSetEngine().setOutGroupOnCallClosed(true);
        registerReceiver(broadcastReceiver, new IntentFilter("com.gqt.loginout"));

        IntentFilter filter = new IntentFilter();
        filter.addAction("com.gqt.accept");
        filter.addAction("com.gqt.hangup");
        registerReceiver(br, filter);

    }
    @Override
    public void init(){
        String num1 = "8016";
        String num2 = "8018";

        GQTHelper.getInstance().getCallEngine().makeCall(CallType.CONFERENCE, num1+" "+num2);

        Intent intent = new Intent();
        intent.setClass(VoiceCallingActivity.this, VoiceCallInCallActivity.class);
        Bundle bundle = new Bundle();
        intent.putExtra("num1", num1);
        intent.putExtra("num2", num2);
        intent.putExtras(bundle);
        startActivity(intent);
        this.finish();


    }



    Handler callHander = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {

                case 0://incall
                    Utils.showToast(mContext,"msg.what=====" + msg.what);
                    Mlog.d( "callHander--------msg.what=="+msg.what );
                    if (msg.arg1 == CallType.VIDEOCALL || msg.arg1 == CallType.UPLOADVIDEO || msg.arg1 == CallType.TRANSCRIBE || msg.arg1 == CallType.MONITORVIDEO || msg.arg1 == CallType.DISPATCH) {
                    } else if (msg.arg1 == CallType.VOICECALL) {
                        sendBroadcast(new Intent("com.gqt.accept"));
                    } else if (msg.arg1 == CallType.BROADCAST) {
                        Toast.makeText(VoiceCallingActivity.this, "broadcast incall", Toast.LENGTH_SHORT).show();
                    } else if (msg.arg1 == CallType.CONFERENCE) {
                        Toast.makeText(VoiceCallingActivity.this, "conference incall", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case 1:
                    Mlog.d( "callHander--------msg.what=="+msg.what );
                    stopService(new Intent(VoiceCallingActivity.this, MonitorServer.class));
                    Toast.makeText(VoiceCallingActivity.this, "state idle", Toast.LENGTH_SHORT).show();
                    sendBroadcast(new Intent("com.gqt.hangup"));
                    break;
                //呼出trt312
                case 2:
                    Mlog.d( "callHander--------msg.what=="+msg.what );
                    Utils.showToast(mContext,"msg.what=====" + msg.what);

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
                        Intent voiceIntent = new Intent(VoiceCallingActivity.this, VoiceCallOutGoingActivity.class);
                        voiceIntent.putExtra("num", mname);
                        startActivity(voiceIntent);
                    }

                    new SharedPreferenceTools(VoiceCallingActivity.this).putValues(mname);
                    break;
                //呼入
                case 3:
                    Mlog.d( "callHander--------msg.what=="+msg.what );
                    Utils.showToast(mContext,"msg.what=====" + msg.what);
                    String name = msg.getData().getString("name");
                    String num = msg.getData().getString("num");
                    if (msg.arg1 == CallType.VOICECALL) {
                        Intent invoiceIntent = new Intent(VoiceCallingActivity.this, VoiceCallComingActivity.class);
                        invoiceIntent.putExtra("name", name);
                        invoiceIntent.putExtra("num", num);
                        startActivity(invoiceIntent);
                    } else if (msg.arg1 == CallType.SENDONLY_VOICECALL) {
                        GQTHelper.getInstance().getCallEngine().answerCall(CallType.VOICECALL, "");
                    }
                    break;
                case 99:
                    Utils.showToast(mContext,"msg.what=====" + msg.what);
                    Toast.makeText(VoiceCallingActivity.this, (String) msg.obj, Toast.LENGTH_SHORT).show();
                    break;
                case 98:
                    switch ((Integer) msg.obj) {
                        case 480:
                            Toast.makeText(VoiceCallingActivity.this, "用户不在线或无人接听", Toast.LENGTH_SHORT).show();
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


    @Override
    protected void onDestroy() {
        if (br != null) {
            VoiceCallingActivity.this.unregisterReceiver(br);
        }
        if (broadcastReceiver != null) {
            VoiceCallingActivity.this.unregisterReceiver(broadcastReceiver);
        }

        super.onDestroy();

    }

    @Override
    public void onClick(View v) {

    }



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

}
