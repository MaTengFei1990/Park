package com.hollysmart.conference;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.gqt.bean.CallType;
import com.gqt.helper.GQTHelper;
import com.hollysmart.park.R;
import com.hollysmart.style.StyleAnimActivity;

public class ConferenceCallInCallActivity extends StyleAnimActivity {
    private TextView tv1, tv2, tv3, tv4, tv5, tv6, textview;
    private Button btncacel;
    private BroadcastReceiver br = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
//            if ("com.gqt.hangup".equals(intent.getAction())) {
//                ConferenceCallInCallActivity.this.finish();
//            }
        }
    };

    @Override
    public int layoutResID() {
        return R.layout.conferencein;
    }

    @Override
    public void findView() {

        tv1 = (TextView) this.findViewById(R.id.count1);
        tv2 = (TextView) this.findViewById(R.id.count2);
//		tv3 = (TextView)this.findViewById(R.id.count3);
//		tv4 = (TextView)this.findViewById(R.id.count4);
//		tv5 = (TextView)this.findViewById(R.id.count5);
//		tv6 = (TextView)this.findViewById(R.id.count6);
        textview = (TextView) this.findViewById(R.id.textview);
        btncacel = (Button) this.findViewById(R.id.cancleconf);
        btncacel.setOnClickListener(this);

    }

    @Override
    public void init() {
        registerReceiver(br, new IntentFilter("com.gqt.hangup"));
        registerReceiver(br, new IntentFilter("com.gqt.conaccept"));
        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            String number1 = bundle.getString("num1");
            String number2 = bundle.getString("num2");
//		Log.v("huangfujian", "AAAAAA" + number1);
//		Log.v("huangfujian", "BBBBBB" + number2);
            if (!TextUtils.isEmpty(number1)) {
                tv1.setText(number1);
            }
            if (!TextUtils.isEmpty(number2)) {
                tv2.setText(number2);
            }
        }


    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {


            case R.id.cancleconf:


                // TODO Auto-generated method stub
//			Intent intent = new Intent(ConferenceCallInCallActivity.this,ConferenceOutGoingActivity.class);
//			startActivity(intent);
                btncacel.setTextColor(Color.RED);
                GQTHelper.getInstance().getCallEngine()
                        .hangupCall(CallType.CONFERENCE, " ");

//			ConferenceCallInCallActivity.this.finish();

                break;
        }

    }


    @Override
    public void onResume() {
        super.onResume();
        textview.setText("Í¨»°ÖÐ");
    }

    @Override
    protected void onDestroy() {
        if (br != null) {
            ConferenceCallInCallActivity.this.unregisterReceiver(br);
        }
        super.onDestroy();
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            GQTHelper.getInstance().getCallEngine()
                    .hangupCall(CallType.CONFERENCE, "xxxx");
        }
        return super.onKeyDown(keyCode, event);
    }
}
