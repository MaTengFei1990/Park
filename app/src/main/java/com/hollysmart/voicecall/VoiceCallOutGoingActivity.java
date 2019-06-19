package com.hollysmart.voicecall;



import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.TextView;

import com.gqt.bean.CallType;
import com.gqt.helper.GQTHelper;
import com.hollysmart.park.R;

public class VoiceCallOutGoingActivity extends Activity {

	private TextView numname,nummber,calltitle,textview;
	ImageView btnoutend;
	Chronometer mElapsedTime;
    private BroadcastReceiver br = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			if("com.gqt.accept".equals(intent.getAction())){
				Intent voiceIntent = new Intent(VoiceCallOutGoingActivity.this,VoiceCallInCallActivity.class);
				voiceIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(voiceIntent);
				VoiceCallOutGoingActivity.this.finish();
				
			}else if("com.gqt.hangup".equals(intent.getAction())){
				VoiceCallOutGoingActivity.this.finish();
			}
		}
	};
     @Override
    protected void onCreate(Bundle savedInstanceState) {
    	// TODO Auto-generated method stub
    	super.onCreate(savedInstanceState);
    	requestWindowFeature(Window.FEATURE_NO_TITLE);
    	setContentView(R.layout.callscreen);
    	init();
    	String number = getIntent().getStringExtra("num");
    	nummber.setText(number);
    	calltitle.setText("正在请求语音呼出");
    	registerReceiver(br, new IntentFilter("com.gqt.accept"));
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
			// TODO Auto-generated method stub
			GQTHelper.getInstance().getCallEngine().hangupCall(CallType.VIDEOCALL, "xxxx");
			VoiceCallOutGoingActivity.this.finish();
		}
	};
	@Override
	protected void onDestroy() {
		if (br != null) {
			VoiceCallOutGoingActivity.this.unregisterReceiver(br);
		}
		
		super.onDestroy();
	}
}
