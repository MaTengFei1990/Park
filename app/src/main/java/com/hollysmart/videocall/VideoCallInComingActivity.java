package com.hollysmart.videocall;


import android.app.Activity;
import android.app.KeyguardManager;
import android.app.KeyguardManager.KeyguardLock;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
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

public class VideoCallInComingActivity extends Activity {
	private TextView numname,nummber,calltitle,textview;
	ImageView btnoutend;
	private Button ending,accept;
	Chronometer mElapsedTime;
	public NotificationManager mNotificationManager;
	 NotificationCompat.Builder mBuilder;
    private BroadcastReceiver br = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			if("com.gqt.videoaccept".equals(intent.getAction())){
				VideoCallInComingActivity.this.finish();
				Intent vodeoIntent = new Intent(VideoCallInComingActivity.this,VideoCallInCallActivity.class);
                startActivity(vodeoIntent);
				
			}else if("com.gqt.hangup".equals(intent.getAction())){
				VideoCallInComingActivity.this.finish();
		}
		}
	};
     @Override
    protected void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	requestWindowFeature(Window.FEATURE_NO_TITLE);
    	setContentView(R.layout.invoicecall);
    	init();
//    	mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
//    	 initNotify();
    	wakeAndUnlock(true);
    	String name =getIntent().getStringExtra("name");
    	String number = getIntent().getStringExtra("num");
    	nummber.setText(name+"   "+number);
    	new SharedPreferenceTools(this).putValues(number);
    	calltitle.setText("�Ƿ������Ƶ����");
    	if (mElapsedTime != null) {
			mElapsedTime.setBase(SystemClock.elapsedRealtime());
			mElapsedTime.start();
		}
        registerReceiver(br, new IntentFilter("com.gqt.videoaccept"));
   	    registerReceiver(br, new IntentFilter("com.gqt.hangup"));
//   	    Notification notification = mBuilder.build();
//		notification.defaults |= Notification.DEFAULT_SOUND;
//		mNotificationManager.notify(100, notification);
//		try {
//			Thread.sleep(300);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		 GQTHelper.getInstance().getCallEngine().answerCall(CallType.VIDEOCALL, "");
    	
    }
    
     /** ��ʼ��֪ͨ�� */
 	private void initNotify(){
 		mBuilder = new NotificationCompat.Builder(this);
 		mBuilder.setContentTitle("���Ա���")
 				.setContentText("��������")
 				.setContentIntent(getDefalutIntent(Notification.FLAG_AUTO_CANCEL))
// 				.setNumber(number)//��ʾ����
 				.setTicker("����֪ͨ����")//֪ͨ�״γ�����֪ͨ��������������Ч����
 				.setWhen(System.currentTimeMillis())//֪ͨ������ʱ�䣬����֪ͨ��Ϣ����ʾ
 				.setPriority(Notification.PRIORITY_DEFAULT)//���ø�֪ͨ���ȼ�
// 				.setAutoCancel(true)//���������־���û��������Ϳ�����֪ͨ���Զ�ȡ��  
 				.setOngoing(false)//ture��������Ϊһ�����ڽ��е�֪ͨ������ͨ����������ʾһ����̨����,�û���������(�粥������)����ĳ�ַ�ʽ���ڵȴ�,���ռ���豸(��һ���ļ�����,ͬ������,������������)
 				.setDefaults(Notification.DEFAULT_VIBRATE)//��֪ͨ������������ƺ���Ч������򵥡���һ�µķ�ʽ��ʹ�õ�ǰ���û�Ĭ�����ã�ʹ��defaults���ԣ�������ϣ�
 				//Notification.DEFAULT_ALL  Notification.DEFAULT_SOUND ������� // requires VIBRATE permission
 				.setSmallIcon(R.mipmap.ic_launcher);
 	}
     private void init(){
    	 numname = (TextView)this.findViewById(R.id.callname);
    	 nummber = (TextView)this.findViewById(R.id.callnum);
    	 btnoutend=(ImageView) findViewById(R.id.out_end_call);
    	 calltitle = (TextView)this.findViewById(R.id.calltip);
    	 textview = (TextView)this.findViewById(R.id.textview);
    	 accept = (Button)this.findViewById(R.id.jieting);
    	 ending = (Button)this.findViewById(R.id.guaduan);
    	 mElapsedTime = (Chronometer) findViewById(R.id.elapsedTime);
    	 ending.setOnClickListener(btnoutendlistener);
    	 accept.setOnClickListener(acceptlistenner);
      }
     OnClickListener btnoutendlistener = new OnClickListener() {
		
		@Override
		public void onClick(View arg0) {
			GQTHelper.getInstance().getCallEngine().hangupCall(CallType.VIDEOCALL, "xxxx");
			ending.setBackgroundResource(R.drawable.main_tab_item_select1);
			VideoCallInComingActivity.this.finish();
		}
	};
		OnClickListener acceptlistenner = new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
	            GQTHelper.getInstance().getCallEngine().answerCall(CallType.VIDEOCALL, "");
	            accept.setBackgroundResource(R.drawable.main_tab_item_select1);
				
			}
		};
		@Override
		public boolean onKeyDown(int keyCode, KeyEvent event) {
			if(keyCode== KeyEvent.KEYCODE_BACK){
				GQTHelper.getInstance().getCallEngine().hangupCall(CallType.VIDEOCALL, "xxxx");
			}
			return super.onKeyDown(keyCode, event);
		}
		private KeyguardManager km;
		private KeyguardLock kl;
		private PowerManager pm;
		private PowerManager.WakeLock wl;
		private void wakeAndUnlock(boolean bolean){
		       if(bolean){
		              pm=(PowerManager) getSystemService(Context.POWER_SERVICE);
		 	          wl = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "bright");
		              //������Ļ
		              wl.acquire();
		              //�õ�����������������
		              km= (KeyguardManager)getSystemService(Context.KEYGUARD_SERVICE);
		              kl = km.newKeyguardLock("unLock");	 
		              //����
		              kl.disableKeyguard();
		       }else{
		              //����
		              kl.reenableKeyguard();
		             
		              //�ͷ�wakeLock���ص�
		              wl.release();
		       }	      
		}
		public PendingIntent getDefalutIntent(int flags){
			PendingIntent pendingIntent= PendingIntent.getActivity(this, 1, new Intent(), flags);
			return pendingIntent;
		}

		@Override
		protected void onDestroy() {
			// TODO Auto-generated method stub
			super.onDestroy();
//			mNotificationManager.cancelAll();
		}
		
}
