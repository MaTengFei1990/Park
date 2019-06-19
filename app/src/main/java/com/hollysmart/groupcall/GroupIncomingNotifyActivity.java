package com.hollysmart.groupcall;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.gqt.bean.PttGroup;
import com.gqt.helper.GQTHelper;
import com.hollysmart.park.R;

import java.util.ArrayList;


public class GroupIncomingNotifyActivity extends Activity {
	private Button ok,cancel;
	private TextView message;
	private String incomingGroupNum,incomingGroupName;
	private static final int MAX_WAIT_TIME = 8000;
	private TimeCount countTimer;
	private boolean isTmp=false;
	private ArrayList<String> members;
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	// TODO Auto-generated method stub
    	super.onCreate(savedInstanceState);
    	requestWindowFeature(Window.FEATURE_NO_TITLE);
    	setContentView(R.layout.dialog_item);
    	ok = (Button)this.findViewById(R.id.ok);
    	cancel = (Button)this.findViewById(R.id.cancel);
    	message = (TextView)this.findViewById(R.id.message);
    	incomingGroupNum = getIntent().getStringExtra("incomingGroupNum");
    	incomingGroupName = getIntent().getStringExtra("incomingGroupName");
    	isTmp=getIntent().getBooleanExtra("istmp", false);
    	members = getIntent().getStringArrayListExtra("members");
    	message.setText("�Խ��飺"+incomingGroupName);
    	ok.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if(isTmp){
					GQTHelper.getInstance().getGroupEngine().answerTmpGrpCall();
					Intent intent = new Intent();
					intent.setClass(GroupIncomingNotifyActivity.this,
							TempGrpCallActivity.class);
					intent.putStringArrayListExtra("groupMemberList",
							members);
					intent.putExtra("tempGroupName", incomingGroupName);
					startActivity(intent);
				}else{
				GQTHelper.getInstance().getGroupEngine().answerGroupCall(new PttGroup(incomingGroupNum,incomingGroupName));
				}
				GroupIncomingNotifyActivity.this.finish();
			}
		});
    	cancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if(isTmp){
					GQTHelper.getInstance().getGroupEngine().rejectTmpGrpCall();
				}else{
				GQTHelper.getInstance().getGroupEngine().rejectGroupCall(new PttGroup(incomingGroupNum,incomingGroupName));
				}
				GroupIncomingNotifyActivity.this.finish();
			}
		});
    	countTimer = new TimeCount(MAX_WAIT_TIME, 1000);

		countTimer.start();
		Log.v("huangfujian", "@@@@@@@@@@countTimer.start()");
    }
    class TimeCount extends CountDownTimer {

		public TimeCount(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);// ��������Ϊ��ʱ��,�ͼ�ʱ��ʱ����
		}

		@Override
		public void onFinish() {// ��ʱ���ʱ����
			if(isTmp){
				GQTHelper.getInstance().getGroupEngine().rejectTmpGrpCall();
			}else{
			GQTHelper.getInstance().getGroupEngine().rejectGroupCall(new PttGroup(incomingGroupNum,incomingGroupName));
			}
			Log.v("huangfujian", "******onFinish()");
			GroupIncomingNotifyActivity.this.finish();
		}

		@Override
		public void onTick(long millisUntilFinished) {// ��ʱ������ʾ
		}
	}
    @Override
    protected void onDestroy() {
    	// TODO Auto-generated method stub
    	if (countTimer != null) {
			countTimer.cancel();
			Log.v("huangfujian", "=====countTimer.cancel()");
		}
    	super.onDestroy();
    }
  
} 
