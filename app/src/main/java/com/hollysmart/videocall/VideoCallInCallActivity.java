package com.hollysmart.videocall;



import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.gqt.bean.CallType;
import com.gqt.bean.ScreenDirection;
import com.gqt.helper.GQTHelper;
import com.gqt.video.DeviceVideoInfo;
import com.gqt.video.VideoInfoInterface;
import com.hollysmart.park.R;
import com.hollysmart.tools.SharedPreferenceTools;

@SuppressWarnings("deprecation")
public class VideoCallInCallActivity extends Activity implements VideoInfoInterface {
	SurfaceView localview,remoteview;
	TextView tv;
	private Button endingvideo;
	boolean flag=false,isFront=false;

	private Camera mCameraDevice;
	private Parameters localParameters;
	private int width,height;
//	private HomeMainActivity mainActivity;
	boolean isDestroyed = false;
	
	private BroadcastReceiver br = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			if("com.gqt.hangup".equals(intent.getAction())){
				android.util.Log.e("jiangkai", "hangup");
				VideoCallInCallActivity.this.finish();
			}else if(DeviceVideoInfo.ACTION_RESTART_CAMERA.equals(intent.getAction())){
				//�Զ���ת
				GQTHelper.getInstance().getCallEngine().setVideoInfo();
			}
		}
	};
	int  rotation=0;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if(GQTHelper.getInstance().getSetEngine().getScreenDir().equals(ScreenDirection.LAND)){
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
			rotation=-90;
		}else{
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
			rotation=0;
		}
//		enableLocalRecord(false);
		setContentView(R.layout.videocalldemo);
		localview = (SurfaceView)findViewById(R.id.locaView);
		remoteview =(SurfaceView)findViewById(R.id.remoteView);
		endingvideo = (Button)this.findViewById(R.id.endingvideo);
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction("com.gqt.hangup");
		intentFilter.addAction(DeviceVideoInfo.ACTION_RESTART_CAMERA);
//		initResultion();
		registerReceiver(br, intentFilter);
		//��ȡ���õ���ǰ�û��Ǻ���
		isFront = GQTHelper.getInstance().getSetEngine().isCameraFacedFront();
		findViewById(R.id.switchCamera).setOnClickListener(new OnClickListener() {

			@SuppressWarnings("deprecation")
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
//				if(mCameraDevice !=null){
//					mCameraDevice.setPreviewCallback(null);
//					mCameraDevice.stopPreview();
//					mCameraDevice.release();
//					mCameraDevice = null;
//				}
			if(isFront){
				isFront = false;
				GQTHelper.getInstance().getCallEngine().switchCamera(Camera.CameraInfo.CAMERA_FACING_BACK);
//				GQTHelper.getInstance().getCallEngine().switchCamera(2);
//				startPreview();
			}else{
				isFront = true;
				GQTHelper.getInstance().getCallEngine().switchCamera(Camera.CameraInfo.CAMERA_FACING_FRONT);
			}
			}
		});
//		mainActivity = new HomeMainActivity(this);
		 Intent intent = new Intent();
 		intent.setAction("com.hmct.policedispatchapp.action.STOP_CAMERA");
 		this.sendBroadcast(intent);
		tv = (TextView)findViewById(R.id.peerNum);
		String callnumber = new SharedPreferenceTools(this).getValues();
		tv.setText(callnumber);
		tv.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				GQTHelper.getInstance().getCallEngine().resetDecode();
			}
		});
		findViewById(R.id.rotation).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				rotation+=90;
				if(rotation==360){
					rotation=0;
				}
				GQTHelper.getInstance().getCallEngine().setDisplayOrientation(rotation);
			}
		});
		endingvideo.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
//				GQTHelper.getInstance().getCallEngine().hangupCall(CallType.VIDEOCALL, "xxxx");
				VideoCallInCallActivity.this.finish();
			}
		});

	
		remoteview.getHolder().addCallback(new Callback() {
			@Override
			public void surfaceDestroyed(SurfaceHolder holder) {
				Log.e("jiangkai", "surfaceDestroyed");
				isDestroyed = true;
			}
			
			@Override
			public void surfaceCreated(SurfaceHolder holder) {
				//��̨�ٷ��ػָ���ʾ
				if(isDestroyed){
	 					isDestroyed = false;
	 					GQTHelper.getInstance().getCallEngine().resetStartCamera(false, null);
	 					GQTHelper.getInstance().getCallEngine().resetRemoteSurface(null);
	 					return;
	 				}
				//���ý��նԶ�H264�ص��ӿ�
//				GQTHelper.getInstance().getCallEngine().receiverH264Data(VideoCallInCallActivity.this);
				GQTHelper.getInstance().getCallEngine().startVideo(localview, remoteview,false);
				//���ò�����������
//				GQTHelper.getInstance().getCallEngine().needToStart(false);
				Log.e("jiangkai", "surfaceCreated");
//				GQTHelper.getInstance().getCallEngine().speaker(true);
			//	startPreview();
				
			}
			
			@Override
			public void surfaceChanged(SurfaceHolder holder, int format, int width,
                                       int height) {
				Log.e("jiangkai", "surfaceChanged");
			}
		});
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode== KeyEvent.KEYCODE_BACK){
			GQTHelper.getInstance().getCallEngine().hangupCall(CallType.VIDEOCALL, "video call onkeydown");
		}
		return super.onKeyDown(keyCode, event);
	}
	
	
//	private void startPreview() {
//		if (mCameraDevice == null) {
//			try {
//				if(isFront){
//					mCameraDevice = Camera.open(Camera.CameraInfo.CAMERA_FACING_FRONT);
//				}else{
//					mCameraDevice = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);
//				}
//					
//			}catch (Exception e) {
//				e.printStackTrace();
//				return;
//			}
//			try {
//				if (DeviceVideoInfo.isHorizontal) {
//					mCameraDevice.setDisplayOrientation(0);
//				} else {
//					mCameraDevice.setDisplayOrientation(90);
//				}
//				localParameters = mCameraDevice.getParameters();
//				localParameters.setPreviewFormat(ImageFormat.NV21);
//				localParameters.setPreviewSize(width,height);
//				mCameraDevice.setParameters(localParameters);
//				mCameraDevice.setPreviewCallbackWithBuffer(null);
//				mCameraDevice
//						.setPreviewCallbackWithBuffer(new Camera.PreviewCallback() {
//							@Override
//							public void onPreviewFrame(byte[] data,
//									Camera camera) {
//								if (data == null)
//									return;
//								camera.addCallbackBuffer(data);
//								try {
//									GQTHelper.getInstance().getCallEngine().pushYUV(data);;
//								} catch (Exception e) {
//									e.printStackTrace();
//								}
//							}
//
//						});
//				mCameraDevice.setPreviewDisplay(localview.getHolder());
//				this.mCameraDevice.startPreview();
//
//			} catch (Exception localThrowable) {
//				localThrowable.printStackTrace();
//				GQTHelper.getInstance().getCallEngine().hangupCall(CallType.VIDEOCALL, "video call Exception");
//			}
//
//		} else {
//		}
//
//	}
//	
//	public void enableLocalRecord(boolean enable) {
//        Intent intent = new Intent("com.pg.software.NOTIFACATION_MSG_TO_RECODER");
//        intent.putExtra("msg", enable ? "startRecord" : "stopRecord");
//        MyLog.e("jiangkai","enableLocalRecord "+ (enable ? "startRecord" : "stopRecord"));
//        sendBroadcast(intent);
//    }
//	private void initResultion(){
//		String resolution = GQTHelper.getInstance().getSetEngine().getResolutionStr();
//		if(resolution.contains("*")){
//			String[] resArray = resolution.split("\\*");
//			if(resArray != null && resArray.length == 2){
//				this.width = Integer.parseInt(resArray[0].trim());
//				this.height = Integer.parseInt(resArray[1].trim());
//			}
//		}
//	}
	
	@Override
	protected void onDestroy() {
		GQTHelper.getInstance().getCallEngine().stopVideo();
		GQTHelper.getInstance().getCallEngine().hangupCall(CallType.VIDEOCALL, " video call onDestroy");
		unregisterReceiver(br);
		super.onDestroy();
	}

	@Override
	public void getVideoSize(int w, int h) {
		// TODO Auto-generated method stub
		
	}
	//���յ�H264�����۾�
	@Override
	public void getH264Data(byte[] buffer) {
		 Log.i("jiangkai", "getH264Data data_length =" + buffer.length);
		// TODO Auto-generated method stub
//		if(mainActivity != null){
//			mainActivity.onGetStreamFrame(buffer);
//		}
	}
}
