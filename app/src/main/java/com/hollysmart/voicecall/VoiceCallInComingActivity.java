package com.hollysmart.voicecall;


import android.app.Activity;
import android.app.KeyguardManager;
import android.app.KeyguardManager.KeyguardLock;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.SystemClock;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.TextView;

import com.gqt.bean.CallType;
import com.gqt.helper.GQTHelper;
import com.hollysmart.park.R;
import com.hollysmart.tools.SharedPreferenceTools;

public class VoiceCallInComingActivity extends Activity {
	private TextView numname,nummber,calltitle,textview;
	ImageView btnoutend;
	private Button accept,ending;
	private String name,number;
	Chronometer mElapsedTime;
	private String mScreanWakeLockKey = TAG;
	private final static String TAG = "DemoCallScreen";
    private BroadcastReceiver br = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
		if("com.gqt.hangup".equals(intent.getAction())){
				VoiceCallInComingActivity.this.finish();

		}else if("com.gqt.accept".equals(intent.getAction())){
			Intent voiceIntent = new Intent(VoiceCallInComingActivity.this,VocieCallInCall2Activity.class);
			voiceIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			Log.v("huangfujian", "0000000000000");
            startActivity(voiceIntent);
            VoiceCallInComingActivity.this.finish();
			}
		}
	};

     @Override
    protected void onCreate(Bundle savedInstanceState) {
    	// TODO Auto-generated method stub
    	super.onCreate(savedInstanceState);
    	requestWindowFeature(Window.FEATURE_NO_TITLE);
    
    	setContentView(R.layout.invoicecall);
    	
    	init();
     	wakeAndUnlock(true);
    	number =getIntent().getStringExtra("num");
    	name =getIntent().getStringExtra("name");
    	nummber.setText(name+"   "+number);
    	Log.v("huangfujian","number="+number);
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
     private void init(){
    	 numname = (TextView)this.findViewById(R.id.callname);
    	 nummber = (TextView)this.findViewById(R.id.callnum);
    	 accept = (Button)this.findViewById(R.id.jieting);
    	 ending = (Button)this.findViewById(R.id.guaduan);
    	 calltitle = (TextView)this.findViewById(R.id.calltip);
    	 textview = (TextView)this.findViewById(R.id.textview);
 		 mElapsedTime = (Chronometer) findViewById(R.id.elapsedTime);
    	 ending.setOnClickListener(btnoutendlistener);
    	 accept.setOnClickListener(acceptlistenner);
     }
     OnClickListener btnoutendlistener = new OnClickListener() {
		
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			GQTHelper.getInstance().getCallEngine().hangupCall(CallType.VOICECALL, "xxxx");
			ending.setBackgroundResource(R.drawable.main_tab_item_select1);
			VoiceCallInComingActivity.this.finish();
		}
	};
	OnClickListener acceptlistenner = new OnClickListener() {
		
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
            GQTHelper.getInstance().getCallEngine().answerCall(CallType.VOICECALL, "");
			accept.setBackgroundResource(R.drawable.main_tab_item_select1);
		}
	};
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if(keyCode== KeyEvent.KEYCODE_BACK){
			GQTHelper.getInstance().getCallEngine().hangupCall(CallType.VOICECALL, "xxxx");

			//return false;
		}
		return super.onKeyDown(keyCode, event);
	}
	@Override
	protected void onDestroy() {
		if (br != null) {
			VoiceCallInComingActivity.this.unregisterReceiver(br);
		}
		else if (mElapsedTime != null) {
			mElapsedTime.stop();
		}
		
		super.onDestroy();
	}
	private KeyguardManager km;
	private KeyguardLock kl;
	private PowerManager pm;
	private PowerManager.WakeLock wl;
	private void wakeAndUnlock(boolean bolean){
	       if(bolean){
	              pm=(PowerManager) getSystemService(Context.POWER_SERVICE);
	 	          wl = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "bright");
	              //点亮屏幕
	              wl.acquire();
	              //得到键盘锁管理器对象
	              km= (KeyguardManager)getSystemService(Context.KEYGUARD_SERVICE);
	              kl = km.newKeyguardLock("unLock");	 
	              //解锁
	              kl.disableKeyguard();
	       }else{
	              //锁屏
	              kl.reenableKeyguard();
	             
	              //释放wakeLock，关灯
	              wl.release();
	       }	      
	}
  }

