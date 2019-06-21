package com.hollysmart.voicecall;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.InputType;
import android.text.Selection;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Chronometer;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.gqt.bean.AudioMode;
import com.gqt.bean.CallType;
import com.gqt.helper.GQTHelper;
import com.hollysmart.park.MyBluetoothManager;
import com.hollysmart.park.R;
import com.hollysmart.style.StyleAnimActivity;
import com.hollysmart.tools.SharedPreferenceTools;

import java.util.HashMap;

public class VocieCallInCall2Activity extends StyleAnimActivity {


    @Override
    public int layoutResID() {
        return R.layout.activity_vocie_call_in_call2;
    }

    @Override
    public void findView() {
        nummber = (TextView) this.findViewById(R.id.callnum);
        hangupLine = (LinearLayout) findViewById(R.id.hangupline);
        mElapsedTime = (Chronometer) findViewById(R.id.elapsedTime);
        findViewById(R.id.speaker).setOnClickListener(this);
        findViewById(R.id.jingyin).setOnClickListener(this);
       String num= getIntent().getStringExtra("num");
        nummber.setText(num);

//        mAudioModeGroup = (RadioGroup)findViewById(R.id.audiomode);
//        switch (GQTHelper.getInstance().getCallEngine().getAudioMode()) {
//            case HOOK:
//                mAudioModeGroup.check(R.id.hook);
//                break;
//            case SPEAKER:
//                mAudioModeGroup.check(R.id.speaker);
//                break;
//            case BLUETOOTH:
//                mAudioModeGroup.check(R.id.bluetooth);
//                break;
//
//            default:
//                break;
//        }
        silence = (LinearLayout) this.findViewById(R.id.jingyin);
        hangupLine.setOnClickListener(btnoutendlistener);
//        mAudioModeGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
//
//            @Override
//            public void onCheckedChanged(RadioGroup arg0, int arg1) {
//                // TODO Auto-generated method stub
//                switch (arg1) {
//                    case R.id.hook:
//                        GQTHelper.getInstance().getCallEngine().setAudioConnectMode(AudioMode.HOOK);
//                        break;
//                    case R.id.speaker:
//                        new Thread(){
//
//                            @Override
//                            public void run() {
//                                // TODO Auto-generated method stub
//                                super.run();
//                                GQTHelper.getInstance().getCallEngine().setAudioConnectMode(AudioMode.SPEAKER);
//                            }
//                        }.start();
//                        break;
//                    case R.id.bluetooth:
//                        if(MyBluetoothManager.getInstance(VocieCallInCall2Activity.this).isconnect())
//                            GQTHelper.getInstance().getCallEngine().setAudioConnectMode(AudioMode.BLUETOOTH);
//                        break;
//                    default:
//                        break;
//                }
//            }
//        });
    }




    @Override
    public void init() {
//        String callnumber = new SharedPreferenceTools(this).getValues();
//        Log.v("huangfujian", "callnumber=" + callnumber);
//        nummber.setText(callnumber);
        registerReceiver(br, new IntentFilter("com.gqt.hangup"));
        if (mElapsedTime != null) {
            mElapsedTime.setBase(SystemClock.elapsedRealtime());
            mElapsedTime.start();
        }
//        silence.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView,
//                                         boolean isChecked) {
//                GQTHelper.getInstance().getCallEngine().mute();
//            }
//        });
    }





    private TextView nummber;
    LinearLayout hangupLine;
    Chronometer mElapsedTime;
//    ToggleButton silence;
    LinearLayout silence;
    private int flag = 1;
    RadioGroup mAudioModeGroup;


    private BroadcastReceiver br = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if ("com.gqt.hangup".equals(intent.getAction())) {
                VocieCallInCall2Activity.this.finish();
            }
        }
    };



    View.OnClickListener btnoutendlistener = new View.OnClickListener() {

        @Override
        public void onClick(View arg0) {
            // TODO Auto-generated method stub
            GQTHelper.getInstance().getCallEngine()
                    .hangupCall(CallType.VIDEOCALL, "xxxx");

            VocieCallInCall2Activity.this.finish();
        }
    };



    public static final HashMap<Character, Integer> mToneMap = new HashMap<Character, Integer>();





    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.speaker:


                GQTHelper.getInstance().getCallEngine().setAudioConnectMode(AudioMode.HOOK);

                new Thread(){

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        super.run();
                        GQTHelper.getInstance().getCallEngine().setAudioConnectMode(AudioMode.SPEAKER);
                    }
                }.start();
                break;

            case R.id.jingyin:
                GQTHelper.getInstance().getCallEngine().mute();
                break;

        }
    }




    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            GQTHelper.getInstance().getCallEngine()
                    .hangupCall(CallType.VOICECALL, "xxxx");
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        if (br != null) {
            unregisterReceiver(br);
        } else if (mElapsedTime != null) {
            mElapsedTime.stop();
        }
        super.onDestroy();
    }




}
