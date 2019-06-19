package com.hollysmart.conference;


import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.gqt.bean.CallListener;

public class MyCallListener implements CallListener {
	private Handler mHandler = null;
	private final String tag = "MyCallListener";
	public MyCallListener(Handler handler) {
		mHandler = handler;
	}
	@Override
	public void onCallOutGoing(int type, String callInfo) {
		mHandler.sendMessage(mHandler.obtainMessage(2, type, -1,callInfo));
	}

	@Override
	public void onCallInComing(int type, String callInfo) {
		Bundle data = new Bundle();
		data.putString("name", callInfo.split(",")[0]);
		data.putString("num", callInfo.split(",")[1]);
		Message message = mHandler.obtainMessage();
		message.what = 3;
		message.arg1 = type;
		message.setData(data);
	    mHandler.sendMessage(message);
	}

	@Override
	public void onCallInCall(int type, String callInfo) {
		mHandler.sendMessage(mHandler.obtainMessage(0, type, -1,callInfo));
	}

	@Override
	public void onCallIDLE() {
		mHandler.sendMessage(mHandler.obtainMessage(1));
	}

	@Override
	public void onCallError(int type, String errorInfo) {
		mHandler.sendMessage(mHandler.obtainMessage(99,errorInfo));
	}
	@Override
	public void onCallRefused(int refusedCode) {
		// TODO Auto-generated method stub
		mHandler.sendMessage(mHandler.obtainMessage(98,refusedCode));
	}
	
}
