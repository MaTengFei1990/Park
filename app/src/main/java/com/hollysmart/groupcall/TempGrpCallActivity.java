package com.hollysmart.groupcall;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.gqt.bean.GroupCallListener;
import com.gqt.bean.GroupState;
import com.gqt.bean.GrpMember;
import com.gqt.bean.PttGroup;
import com.gqt.customgroup.CustomGroupResult;
import com.gqt.helper.Constant;
import com.gqt.helper.GQTHelper;
import com.gqt.net.util.NetChecker;
import com.gqt.sipua.PttGrp;
import com.gqt.sipua.PttGrp.E_Grp_State;
import com.gqt.sipua.ui.Receiver;
import com.gqt.sipua.ui.lowsdk.GroupListUtil;
import com.gqt.utils.LoadingAnimation;
import com.hollysmart.park.R;
import com.hollysmart.park.TipSoundPlayer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * ��ʱ�Խ�����
 * 
 * @author Administrator
 * 
 */
public class TempGrpCallActivity extends Activity implements OnClickListener,
        OnItemClickListener, GroupCallListener {
	private TextView mOut, mGroupName;
	private GridView mMembers;
	private ArrayList<String> mMemberList = new ArrayList<String>();
	private List<Map<String, Object>> mDataList = new ArrayList<Map<String, Object>>();
	private static Button mPttBtn;
	private TextView mStatus;
	boolean isCreator = false;
	SimpleAdapter adapter;
	private String tempGroupName = "";
	private static boolean mHasPttGrp;
	private static TempGrpCallActivity mContext;
//	private Dialog mDialog;
	public static boolean isResume;
	private static boolean isPttPressing;
	boolean isOtherCompany = false;
	private static final int GroupStatusChanged = 1;
	private static final int GroupChanged = 2;
	private static final int GroupMemChanged = 4;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_temp_group_call);
		Intent intent = getIntent();
		mMemberList = intent.getStringArrayListExtra("groupMemberList");
		isCreator = intent.getBooleanExtra("isCreator", false);
		tempGroupName = intent.getStringExtra("tempGroupName");
		boolean callee = intent.getBooleanExtra("callee", false);
		mOut = (TextView) findViewById(R.id.tv_out);
		mOut.setOnClickListener(this);
		mContext = this;
		GQTHelper.getInstance().getGroupEngine().regGroupEngineListener(this);
		if(callee && GQTHelper.getInstance().getGroupEngine().isTmpCallClosed()){
			finish();
			return;
		}
		mGroupName = (TextView) findViewById(R.id.tv_group_call_name);
		mGroupName.setText(tempGroupName);

		mMembers = (GridView) findViewById(R.id.gv_members);

		mStatus = (TextView) findViewById(R.id.tv_status);

		mPttBtn = (Button) findViewById(R.id.btn_temp_grp_ptt);

		mPttBtn.setOnTouchListener(new OnTouchListener() {

			public boolean onTouch(View v, MotionEvent event) {

				if (!NetChecker.check(TempGrpCallActivity.this)) {
					return false;
				}
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					mPttBtn.setBackgroundResource(R.color.loginoutpress);
					isPttPressing = true;
					GQTHelper.getInstance().getGroupEngine().makeGroupCall(true);
					break;
				case MotionEvent.ACTION_UP:
					PttGrp pttGrp = Receiver.GetCurUA().GetCurGrp();
					if (pttGrp != null) {
						if (pttGrp.state == E_Grp_State.GRP_STATE_INITIATING) {
							mStatus.setText(ShowPttStatus(E_Grp_State.GRP_STATE_IDLE));
						}
					}
					if (isPttPressing) {
						isPttPressing = false;
						GQTHelper.getInstance().getGroupEngine().makeGroupCall(false);
						mPttBtn.setBackgroundResource(R.color.loginoutnormal);
					}
					break;
				}

				return false;
			}
		});
		loadData();
		adapter = new SimpleAdapter(this, mDataList,
				R.layout.item_tempcall_grid, new String[] { "image",
						"memberName" }, new int[] { R.id.iv_member_image,
						R.id.tv_member_info });
		mMembers.setAdapter(adapter);
		if (isCreator&& !isOtherCompany) {
			mMembers.setOnItemClickListener(this);
			mStatus.setText(getString(R.string.temp_group_creating));
		}
		
	}
	
	@Override
	protected void onResume() {
		isResume = true;
		isPttPressing = false;
		PttGrp pttGrp = Receiver.GetCurUA().GetCurGrp();
		mHasPttGrp = pttGrp != null ? true : false;
		setPttBackground(isPttPressing);
		super.onResume();
		
	}
	public static void setPttBackground(boolean pressed) {
		// TODO Auto-generated method stub
		mPttBtn.setBackgroundResource(pressed ? R.color.loginoutpress
				: R.color.loginoutnormal);
	}

	public static TempGrpCallActivity getInstance() {
		// TODO Auto-generated method stub
		return mContext;
	}
	public static boolean checkHasCurrentGrp(Context context) {
		return mHasPttGrp;
	}
	public Handler TepttPressHandler = new Handler() {
		

		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				isPttPressing = false;
				setPttBackground(false);
				break;
			case 1:
				isPttPressing = true;
				setPttBackground(true);
				break;

			default:
				break;
			}
		};
	};
	@Override
	protected void onStop() {
		isResume = false;
		super.onStop();
	}
	private void loadData() {
		mDataList.clear();
		if(mMemberList.size() > 0){
		for (int i = 0; i < mMemberList.size(); i++) {
			Map<String, Object> member = new HashMap<String, Object>();
			member.put("image", R.drawable.icon_contact);
			String showText = GroupListUtil.getGroupListUtil().getUserName(mMemberList.get(i));
			if (TextUtils.isEmpty(showText)) {
				int len = mMemberList.get(i).length();
				if(len > 5){
					showText = mMemberList.get(i).substring(len - 5, len);
				} else {
					showText = mMemberList.get(i);
				}
			}
			member.put("memberName", showText);
			mDataList.add(member);
			}
		}
		if(mMemberList.size() > 1){
//			String firstNum = mMemberList.get(0);
//			if(Constant.userName.equals(firstNum)){
//				isOtherCompany = isOtherCompany(mMemberList.get(1));
//			}else{
//				isOtherCompany = isOtherCompany(mMemberList.get(0));
//			}
			if (isCreator && !isOtherCompany) {
				Map<String, Object> member = new HashMap<String, Object>();
				member.put("image", R.drawable.meeting_invite);
				member.put("memberName", "");
				mDataList.add(member);
			}
		}
	}
	@Override
	public void onItemClick(AdapterView<?> arg0, View view, int position,
                            long id) {
		if (position == mDataList.size() - 1) {
			Intent intent = new Intent();
			intent.setClass(TempGrpCallActivity.this,
					SelectPersonsActivity.class);
			intent.putExtra("isInvite", true);
			intent.putExtra("tempGroupName", tempGroupName);
			intent.putStringArrayListExtra("selectedList", mMemberList);
			startActivityForResult(intent, RESULT_FIRST_USER);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == RESULT_FIRST_USER && resultCode == RESULT_OK) {
			List<String> inviteMembers = data
					.getStringArrayListExtra("inviteMembers");
			for (String member : inviteMembers) {
				mMemberList.add(member);
			}
			loadData();
			adapter.notifyDataSetChanged();
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	private void stopCurrentAnimation() {
		if (mLoadingAnimation != null) {
			mLoadingAnimation.stopAnimation();
		}
	}

	private LoadingAnimation mLoadingAnimation;

	public String ShowPttStatus(PttGrp.E_Grp_State pttState) {
//		switch (pttState) {
//		case GRP_STATE_SHOUDOWN:
//			return getString(R.string.close);
//		case GRP_STATE_IDLE:
//			return getString(R.string.idle);
//		case GRP_STATE_TALKING:
//			return getString(R.string.talking);
//		case GRP_STATE_LISTENING:
//			return getString(R.string.listening);
//		case GRP_STATE_QUEUE:
//			return getString(R.string.queueing);
//		case GRP_STATE_INITIATING:
//			return getString(R.string.ptt_requesting);
//
//		}
		return this.getResources().getString(R.string.error);
	}


	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.tv_out:
		GQTHelper.getInstance().getGroupEngine().hangupTmpGrpCall(true);
			finish();
		}
	}
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			return true;
		}
		return super.onKeyDown(keyCode, event);
	};


	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {

		return super.onKeyUp(keyCode, event);
	}

	@Override
	public void onBackPressed() {
		// super.onBackPressed();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void groupStateChanged(PttGroup group) {
		// TODO Auto-generated method stub
		PttGroup curGrp = GQTHelper.getInstance().getGroupEngine().getCurGrp();
		if(curGrp == null){
			//do nothing
		}else{
			if(!curGrp.equals(group)) return;
			myHandler.sendMessage(myHandler.obtainMessage(GroupStatusChanged, group));
		}
	}

	@Override
	public void onPttRequestSuccess() {
		// TODO Auto-generated method stub
		TipSoundPlayer.getInstance().play(TipSoundPlayer.Sound.PTT_ACCEPT);
		Log.e("jiangkai", "�������Լ�");
	}

	@Override
	public void onPttRequestFailed(String reason) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPttReleaseSuccess() {
		// TODO Auto-generated method stub
		TipSoundPlayer.getInstance().play(TipSoundPlayer.Sound.PTT_RELEASE);
		Log.e("jiangkai", "�ͷųɹ�");
	}

	@Override
	public void onGroupCallInComing(PttGroup grp) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onGrpChanged(PttGroup grp) {
		// TODO Auto-generated method stub
		myHandler.sendMessage(myHandler.obtainMessage(GroupChanged, grp));

	}

	@Override
	public void onAllGrpsChanged(List<PttGroup> groups) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onCurGrpMemberChanged(PttGroup grp, List<GrpMember> members) {
		// TODO Auto-generated method stub

	}

	@Override
	public void showCurrentVolume(int time) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onAddressBook(boolean isSuccess) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onAddressBookUpdateVersion(String version) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onTempGroupCallState(int state) {
		// TODO Auto-generated method stub
		if(state==0){
			runOnUiThread(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					mStatus.setText(getString(R.string.create_timeout));
				}
			});

			finish();
		}else{
			runOnUiThread(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					mStatus.setText("�����ɹ�");
				}
			});

		}
	}

	@Override
	public void onTempGrpMemberChanged(List<String> members) {
		// TODO Auto-generated method stub
		myHandler.sendMessage(myHandler.obtainMessage(GroupMemChanged, members));
	}
	public String ShowPttStatus(int pttState) {
		switch (pttState) {
		case GroupState.CLOSED:
			return "�ر�";
		case GroupState.IDLE:
			return "����";
		case GroupState.TALKING:
			return "����";
		case GroupState.LISTEN:
			return "����";
		case GroupState.QUEUE:
			return "�Ŷ�";
		case GroupState.REQUESTING:
			return "������";
		}
		return "����";
	}

	public String ShowSpeakerStatus(String strName, String userNum) {
		if (TextUtils.isEmpty(strName)) {
			return "��";
		} else if (userNum.equals(Constant.userName)/* &&isPttPressing */) {
			return "�Լ�";
		} else {
			return "������" + "��"
					+ strName + "��";
		}
	}


	Handler myHandler = new Handler(){
		@SuppressWarnings("unchecked")
		public void handleMessage(final android.os.Message msg) {
			switch(msg.what){
			case GroupStatusChanged:
			{
				PttGroup grp = (PttGroup)(msg.obj);
				stopCurrentAnimation();
				mStatus.setText(ShowSpeakerStatus(grp.getCurSpeakerName(), ((PttGroup)(msg.obj)).getCurSpeakerNum())+" �ҵ�״̬  "
						+ ShowPttStatus(((PttGroup)(msg.obj)).getCurState()));
				if((grp.getCurState() == GroupState.REQUESTING)){
					mLoadingAnimation = new LoadingAnimation();
					mLoadingAnimation.setAppendCount(3).startAnimation(
							mStatus);
				}
				}
				break;
			case GroupChanged:
			{
				PttGroup grp = (PttGroup)(msg.obj);
				if(grp == null){
					mStatus.setText("�� �ҵ�״̫ ����");
				}else{
					mStatus.setText(ShowSpeakerStatus(grp.getCurSpeakerName(), ((PttGroup)(msg.obj)).getCurSpeakerNum())+" �ҵ�״̬  "
							+ ShowPttStatus(((PttGroup)(msg.obj)).getCurState()));
				}
				}
				break;
			case GroupMemChanged:
				ArrayList<String> inviteMembers = (ArrayList<String>)msg.obj;
		if (inviteMembers != null && inviteMembers.size() > 0) {
			for (int i = 0; i < inviteMembers.size(); i++) {
				String str = inviteMembers.get(i);
				if(!mMemberList.contains(str)){
					mMemberList.add(str);
				}
			}
			loadData();
			adapter.notifyDataSetChanged();
		}
				break;
			case 8:
				Toast.makeText(TempGrpCallActivity.this, (String)msg.obj, Toast.LENGTH_LONG).show();
				break;
			}
		};
	};


	@Override
	public void onTempGroupCallInComing(String grpname, List<String> members) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onCustomGroupResultState(CustomGroupResult result, int code, String groupNum, List<GrpMember> members) {
		// TODO Auto-generated method stub
		
	}
	
	
}
