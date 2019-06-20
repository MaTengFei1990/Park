package com.hollysmart.park;

import android.app.KeyguardManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.PowerManager;
import android.os.SystemClock;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gqt.bean.CallType;
import com.gqt.helper.GQTHelper;
import com.hollysmart.style.StyleAnimActivity;
import com.hollysmart.tools.SharedPreferenceTools;
import com.hollysmart.voicecall.VocieCallInCall2Activity;

public class VoiceCallComingActivity extends StyleAnimActivity {


    @Override
    public int layoutResID() {
        return R.layout.activity_voice_call_coming;
    }

    @Override
    public void findView() {

        numname = (TextView) this.findViewById(R.id.callname);
        nummber = (TextView) this.findViewById(R.id.callnum);
        accept = (LinearLayout) this.findViewById(R.id.ll_jieTing);
        ending = (LinearLayout) this.findViewById(R.id.ll_guanduan);
        calltitle = (TextView) this.findViewById(R.id.calltip);
//        textview = (TextView)this.findViewById(R.id.textview);
        mElapsedTime = (Chronometer) findViewById(R.id.elapsedTime);
        ending.setOnClickListener(this);
        accept.setOnClickListener(this);

    }

    private TextView numname, nummber, calltitle, textview;
    ImageView btnoutend;
    private LinearLayout accept, ending;
    private String name, number;
    Chronometer mElapsedTime;
    private String mScreanWakeLockKey = TAG;
    private final static String TAG = "DemoCallScreen";
    private BroadcastReceiver br = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if ("com.gqt.hangup".equals(intent.getAction())) {
                VoiceCallComingActivity.this.finish();

            } else if ("com.gqt.accept".equals(intent.getAction())) {
                Intent voiceIntent = new Intent(VoiceCallComingActivity.this, VocieCallInCall2Activity.class);
                voiceIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                Log.v("huangfujian", "0000000000000");
                startActivity(voiceIntent);
                VoiceCallComingActivity.this.finish();
            }
        }
    };

    @Override
    public void init() {
        wakeAndUnlock(true);
        number = getIntent().getStringExtra("num");
        name = getIntent().getStringExtra("name");
        nummber.setText(name + "   " + number);
        Log.v("huangfujian", "number=" + number);
        new SharedPreferenceTools(this).putValues(number);
        calltitle.setText("是否接收语音呼入");
        registerReceiver(br, new IntentFilter("com.gqt.accept"));
        registerReceiver(br, new IntentFilter("com.gqt.hangup"));
        registerReceiver(br, new IntentFilter("com.gqt.groupIncoming"));
        Intent numintent = new Intent("send_call_number");
        numintent.putExtra("number", number);
        sendBroadcast(numintent);
        if (mElapsedTime != null) {
            mElapsedTime.setBase(SystemClock.elapsedRealtime());
            mElapsedTime.start();
        }
    }

    private KeyguardManager km;
    private KeyguardManager.KeyguardLock kl;
    private PowerManager pm;
    private PowerManager.WakeLock wl;

    private void wakeAndUnlock(boolean bolean) {
        if (bolean) {
            pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
            wl = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "bright");
            //点亮屏幕
            wl.acquire();
            //得到键盘锁管理器对象
            km = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
            kl = km.newKeyguardLock("unLock");
            //解锁
            kl.disableKeyguard();
        } else {
            //锁屏
            kl.reenableKeyguard();

            //释放wakeLock，关灯
            wl.release();
        }
    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.ll_jieTing:
                GQTHelper.getInstance().getCallEngine().answerCall(CallType.VOICECALL, "");
                break;


            case R.id.ll_guanduan:
                GQTHelper.getInstance().getCallEngine().hangupCall(CallType.VOICECALL, "xxxx");
                VoiceCallComingActivity.this.finish();
                break;
        }

    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            GQTHelper.getInstance().getCallEngine().hangupCall(CallType.VOICECALL, "xxxx");

            //return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        if (br != null) {
            VoiceCallComingActivity.this.unregisterReceiver(br);
        } else if (mElapsedTime != null) {
            mElapsedTime.stop();
        }

        super.onDestroy();
    }
}
