package com.hollysmart.videocall;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;

import com.gqt.bean.CallType;
import com.gqt.helper.GQTHelper;
import com.hollysmart.park.R;

public class MonitorServer extends Service {

	private View view;
	private WindowManager.LayoutParams wmParams;
	private WindowManager mWindowManager;
	private SurfaceView loadSurfaceView;
	
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		GQTHelper.getInstance().getCallEngine().answerCall(CallType.VIDEOCALL, "");
		view = LayoutInflater.from(getApplicationContext()).inflate(
				R.layout.monitorcameratranscribe, null);
		loadSurfaceView = (SurfaceView)view.findViewById(R.id.localvideoView);
		loadSurfaceView.getHolder().addCallback(new Callback() {
			
			@Override
			public void surfaceDestroyed(SurfaceHolder arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void surfaceCreated(SurfaceHolder arg0) {
				// TODO Auto-generated method stub
				GQTHelper.getInstance().getCallEngine().startVideo(loadSurfaceView, null,false);
			}
			
			@Override
			public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
				// TODO Auto-generated method stub
				
			}
		});
		mWindowManager = (WindowManager) getApplicationContext()
				.getSystemService("window");
		wmParams = new WindowManager.LayoutParams();
		// �������ṩ���û���������������Ӧ�ó����Ϸ���������״̬������
		wmParams.type = WindowManager.LayoutParams.TYPE_PHONE;
		// �������κΰ����¼�
		wmParams.flags |= WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
		wmParams.x = 1;
		wmParams.y = 1;
		wmParams.width = 1;
		wmParams.height = 1;
		wmParams.format = PixelFormat.RGBA_8888;

		mWindowManager.addView(view, wmParams);
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		GQTHelper.getInstance().getCallEngine().stopVideo();
		if(mWindowManager!=null && view!=null)
			mWindowManager.removeView(view);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		return super.onStartCommand(intent, flags, startId);
	}

}
