package com.hollysmart.videocall;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.util.Log;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Chronometer;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gqt.bean.CallType;
import com.gqt.bean.ScreenDirection;
import com.gqt.helper.GQTHelper;
import com.gqt.sipua.SipUAApp;
import com.gqt.sipua.ui.PhotoCallback;
import com.gqt.video.DeviceVideoInfo;
import com.gqt.video.VideoManagerService;
import com.hollysmart.park.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class TranscribeActivity extends Activity implements PhotoCallback {
	private TextView numname,nummber,calltitle,textview;
	ImageView btnoutend;
	Chronometer mElapsedTime;
	FrameLayout mCallouting;
	LinearLayout videoLayout;
	SurfaceView mSurfaceView;
	LinearLayout outbut,inbut;
	int type;
	int state;
	boolean isDestroyed = false;
    private BroadcastReceiver br = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			if("com.gqt.videoaccept".equals(intent.getAction())){
				mCallouting.setVisibility(View.GONE);
				videoLayout.setVisibility(View.VISIBLE);
				if(type== CallType.DISPATCH){
					findViewById(R.id.button2).setVisibility(View.VISIBLE);
				}
				Log.e("jiangkai", " dir =  "+ GQTHelper.getInstance().getSetEngine().getScreenDir());
				if(GQTHelper.getInstance().getSetEngine().getScreenDir().equals(ScreenDirection.LAND)){
					setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
				}else{
					setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
				}
			}else if("com.gqt.hangup".equals(intent.getAction())){
				TranscribeActivity.this.finish();
		}else if(DeviceVideoInfo.ACTION_RESTART_CAMERA.equals(intent.getAction())){
			//�Զ���ת 
			GQTHelper.getInstance().getCallEngine().setVideoInfo();
		}
		}
	};
//	public void enableLocalRecord(boolean enable) {
//        Intent intent = new Intent("com.pg.software.NOTIFACATION_MSG_TO_RECODER");
//        intent.putExtra("msg", enable ? "startRecord" : "stopRecord");
//        sendBroadcast(intent);
//    }
     @Override
    protected void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	requestWindowFeature(Window.FEATURE_NO_TITLE);
//    	enableLocalRecord(false);
    	setContentView(R.layout.videocall);
    	init();
    	VideoManagerService.getDefault().setContainAudio(true);
    	AudioManager am = (AudioManager) SipUAApp.mContext.getSystemService(Context.AUDIO_SERVICE);
		if(!am.isSpeakerphoneOn()){
			am.setSpeakerphoneOn(true);
		}
    	String number = getIntent().getStringExtra("num");
    	String name = getIntent().getStringExtra("name");
    	 type = getIntent().getIntExtra("type", -1);
    	nummber.setText(number);
    	numname.setText(name);
    	String typetext="";
    	switch (type) {
		case CallType.UPLOADVIDEO:
			typetext="����������Ƶ�ϴ�";
			break;
		case CallType.MONITORVIDEO:
			typetext="����������Ƶ���";
			break;
		case CallType.TRANSCRIBE:
			typetext="����������Ƶ�ش�";
			break;
		case CallType.DISPATCH:
			typetext="����������Ƶ�ַ�";
			break;
		default:
			break;
		}
    	state=getIntent().getIntExtra("state", -1);
    	if(state==1){
    		outbut.setVisibility(View.VISIBLE);
    		inbut.setVisibility(View.GONE);
    	}else{
    		outbut.setVisibility(View.GONE);
    		inbut.setVisibility(View.VISIBLE);
    	}
    	calltitle.setText(typetext);
    	registerReceiver(br, new IntentFilter("com.gqt.videoaccept"));
    	registerReceiver(br, new IntentFilter("com.gqt.hangup"));
    	registerReceiver(br, new IntentFilter(DeviceVideoInfo.ACTION_RESTART_CAMERA));
    	if (mElapsedTime != null) {
			mElapsedTime.setBase(SystemClock.elapsedRealtime());
			mElapsedTime.start();
		}
    	GQTHelper.getInstance().getCallEngine().setPhotoCallback(this);
//    	 GQTHelper.getInstance().getCallEngine().answerCall(CallType.VIDEOCALL, "");
    	
    }
     
     


	private void init(){
    	 //����Z1������Ƶͨ��ʱҪ�ȹر�
    	 Intent intent = new Intent();
    		intent.setAction("com.hmct.policedispatchapp.action.STOP_CAMERA");
    		this.sendBroadcast(intent);
    	 numname = (TextView)this.findViewById(R.id.callname);
    	 nummber = (TextView)this.findViewById(R.id.callnum);
    	 btnoutend=(ImageView) findViewById(R.id.out_end_call);
    	 calltitle = (TextView)this.findViewById(R.id.calltip);
    	 textview = (TextView)this.findViewById(R.id.textview);
    	 mElapsedTime = (Chronometer) findViewById(R.id.elapsedTime);
    	 btnoutend.setOnClickListener(btnoutendlistener);
    	 mCallouting= (FrameLayout)findViewById(R.id.callouting);
    	 videoLayout = (LinearLayout)findViewById(R.id.videoLayout);
    	 mSurfaceView = (SurfaceView)findViewById(R.id.surfaceView1);
    	 outbut = (LinearLayout)findViewById(R.id.line_outcall);
    	 inbut = (LinearLayout)findViewById(R.id.line_incall);
    	 findViewById(R.id.end_call).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				GQTHelper.getInstance().getCallEngine().hangupCall(CallType.VIDEOCALL, "TranscribeActivity end_call");
				finish();
			}
		});
    	 findViewById(R.id.accept_call).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				 GQTHelper.getInstance().getCallEngine().answerCall(CallType.VIDEOCALL, "");
			}
		});
    	 findViewById(R.id.button1).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				GQTHelper.getInstance().getCallEngine().photoGraph();
//				GQTHelper.getInstance().getCallEngine().resetDecode();
			}
		});
    	 findViewById(R.id.button2).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				AudioManager am = (AudioManager) SipUAApp.mContext.getSystemService(Context.AUDIO_SERVICE);
				if(am.isSpeakerphoneOn()){
					am.setSpeakerphoneOn(false);
				}else{
					am.setSpeakerphoneOn(true);
				}
			}
		});
    	 mSurfaceView.getHolder().addCallback(new Callback() {
 			@Override
 			public void surfaceDestroyed(SurfaceHolder holder) {
 				Log.e("jiangkai", "surfaceDestroyed w ");
 				isDestroyed = true;
 			}
 			
 			@Override
 			public void surfaceCreated(SurfaceHolder holder) {
 				Log.e("jiangkai", "surfaceCreated w ");
 				if(state==1){
 				switch (type) {
 				case CallType.UPLOADVIDEO:
 				case CallType.TRANSCRIBE:
 					if(isDestroyed){
 	 					isDestroyed = false;
 	 					GQTHelper.getInstance().getCallEngine().resetStartCamera(false,null);
 	 					return;
 	 				}
 				GQTHelper.getInstance().getCallEngine().startVideo(mSurfaceView, null,false);
 					break;
 				case CallType.MONITORVIDEO:
 					if(isDestroyed){
 	 					isDestroyed = false;
 	 					GQTHelper.getInstance().getCallEngine().resetRemoteSurface(null);
 	 					return;
 	 				}
 				GQTHelper.getInstance().getCallEngine().startVideo(null, mSurfaceView,false);
 					break;
 				default:
 					break;
 				}
 				}else{
 					switch (type) {
 					case CallType.DISPATCH:
 	 				case CallType.UPLOADVIDEO:
 	 					if(isDestroyed){
 	 	 					isDestroyed = false;
 	 	 					GQTHelper.getInstance().getCallEngine().resetRemoteSurface(null);
 	 	 					return;
 	 	 				}
 	 					GQTHelper.getInstance().getCallEngine().startVideo(null, mSurfaceView,false);
 	 					break;
 	 				case CallType.MONITORVIDEO:
 	 					if(isDestroyed){
 	 	 					isDestroyed = false;
 	 	 					GQTHelper.getInstance().getCallEngine().resetStartCamera(false,null);
 	 	 					return;
 	 	 				}
 	 					GQTHelper.getInstance().getCallEngine().startVideo(mSurfaceView, null,false);
 	 					break;
 	 				default:
 	 					break;
 	 				}
 				}
 			
 			}
 			
 			@Override
 			public void surfaceChanged(SurfaceHolder holder, int format, int width,
                                       int height) {
 				Log.e("jiangkai", "surfacechanged w "+width+" h "+height);
 			}
 		});
     }
     OnClickListener btnoutendlistener = new OnClickListener() {
		
		@Override
		public void onClick(View arg0) {
			GQTHelper.getInstance().getCallEngine().hangupCall(CallType.VIDEOCALL, "TranscribeActivity btnoutendlistener");
			TranscribeActivity.this.finish();
		}
	};
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode== KeyEvent.KEYCODE_BACK){
			GQTHelper.getInstance().getCallEngine().hangupCall(CallType.VIDEOCALL, "TranscribeActivity onKeyDown");
		}
		return super.onKeyDown(keyCode, event);
	}
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		
		GQTHelper.getInstance().getCallEngine().stopVideo();
		GQTHelper.getInstance().getCallEngine().hangupCall(CallType.VIDEOCALL, "TranscribeActivity onDestroy");
		unregisterReceiver(br);
	}
	@Override
	public void onPhoto(Bitmap bitmap) {
		// TODO Auto-generated method stub
		 File f = new File( Environment.getExternalStorageDirectory(), "tesePhoto.jpg");
		  if (f.exists()) {
		   f.delete();
		  }
		  try {
		   FileOutputStream out = new FileOutputStream(f);
		   bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
		   out.flush();
		   out.close();
			GQTHelper.getInstance().getMessageEngine().sendMultiMessage(nummber.getText().toString().trim(), "1", "image/jpg", f.getAbsolutePath());
		  } catch (FileNotFoundException e) {
		   // TODO Auto-generated catch block
		   e.printStackTrace();
		  } catch (IOException e) {
		   // TODO Auto-generated catch block
		   e.printStackTrace();
		  }
	}
	
	
}
