package com.hollysmart.videocall;



import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.TextView;

import com.gqt.bean.CallType;
import com.gqt.helper.GQTHelper;
import com.hollysmart.park.R;

public class VideoCallOutGoingActivity extends Activity {
	private TextView numname,nummber,calltitle,textview;
	ImageView btnoutend;
	Chronometer mElapsedTime;
    private BroadcastReceiver br = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			if("com.gqt.videoaccept".equals(intent.getAction())){
				VideoCallOutGoingActivity.this.finish();
				Intent vodeoIntent = new Intent(VideoCallOutGoingActivity.this,VideoCallInCallActivity.class);
                startActivity(vodeoIntent);
				
			}else if("com.gqt.hangup".equals(intent.getAction())){
				android.util.Log.e("jiangkai", "VideoCallOutGoingActivity hangup");
				VideoCallOutGoingActivity.this.finish();
		}
		}
	};
     @Override
    protected void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	requestWindowFeature(Window.FEATURE_NO_TITLE);
    	setContentView(R.layout.videocall);
    	init();
    	String number = getIntent().getStringExtra("num");
    	nummber.setText(number);
    	calltitle.setText("����������Ƶ����");
    	registerReceiver(br, new IntentFilter("com.gqt.videoaccept"));
    	registerReceiver(br, new IntentFilter("com.gqt.hangup"));
    	if (mElapsedTime != null) {
			mElapsedTime.setBase(SystemClock.elapsedRealtime());
			mElapsedTime.start();
		}
    	
    }
     private void init(){
    	 numname = (TextView)this.findViewById(R.id.callname);
    	 nummber = (TextView)this.findViewById(R.id.callnum);
    	 btnoutend=(ImageView) findViewById(R.id.out_end_call);
    	 calltitle = (TextView)this.findViewById(R.id.calltip);
    	 textview = (TextView)this.findViewById(R.id.textview);
    	 mElapsedTime = (Chronometer) findViewById(R.id.elapsedTime);
    	 btnoutend.setOnClickListener(btnoutendlistener);
     }
     OnClickListener btnoutendlistener = new OnClickListener() {
		
		@Override
		public void onClick(View arg0) {
			GQTHelper.getInstance().getCallEngine().hangupCall(CallType.VIDEOCALL, "xxxx");
			VideoCallOutGoingActivity.this.finish();
		}
	};
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode== KeyEvent.KEYCODE_BACK){
			GQTHelper.getInstance().getCallEngine().hangupCall(CallType.VIDEOCALL, "xxxx");
		}
		return super.onKeyDown(keyCode, event);
	}
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		unregisterReceiver(br);
	}
	
}
