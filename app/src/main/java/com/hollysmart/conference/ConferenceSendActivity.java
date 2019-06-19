package com.hollysmart.conference;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gqt.bean.CallType;
import com.gqt.bean.GroupCallListener;
import com.gqt.bean.GroupState;
import com.gqt.bean.GrpMember;
import com.gqt.bean.PttGroup;
import com.gqt.bean.RegisterListener;
import com.gqt.customgroup.CustomGroupParserListener;
import com.gqt.customgroup.CustomGroupResult;
import com.gqt.customgroup.CustomGroupType;
import com.gqt.helper.CallEngine;
import com.gqt.helper.Constant;
import com.gqt.helper.GQTHelper;
import com.gqt.helper.GroupEngine;
import com.gqt.helper.RegisterEngine;
import com.gqt.utils.BaseVisualizerView;
import com.gqt.utils.LoadingAnimation;
import com.gqt.video.VideoManagerService;
import com.hollysmart.groupcall.GroupCallActivity;
import com.hollysmart.groupcall.GroupIncomingNotifyActivity;
import com.hollysmart.groupcall.VideoSizeSetting;
import com.hollysmart.park.R;
import com.hollysmart.park.TipSoundPlayer;
import com.hollysmart.style.StyleAnimActivity;
import com.hollysmart.tools.SharedPreferenceTools;
import com.hollysmart.utils.Mlog;
import com.hollysmart.utils.Utils;
import com.hollysmart.videocall.MonitorServer;
import com.hollysmart.videocall.TranscribeActivity;
import com.hollysmart.videocall.VideoCallInComingActivity;
import com.hollysmart.videocall.VideoCallOutGoingActivity;
import com.hollysmart.voicecall.VoiceCallInComingActivity;
import com.hollysmart.voicecall.VoiceCallOutGoingActivity;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.hollysmart.groupcall.GroupCallActivity.CUSTOM_GROUP_ACTION_RESULT_STATE;

public class ConferenceSendActivity extends StyleAnimActivity implements GroupCallListener, CustomGroupParserListener, View.OnClickListener {

     private EditText et1,et2,et3,et4,et5,et6;
     private Button sendconf,sendbrd;

	RegisterEngine registerEngine = null;

	private LoadingAnimation mLoadingAnimation;

	boolean isAddressBook = false;

	BaseVisualizerView mBaseVisualizerView;


	GroupEngine groupEngine = null;
	CallEngine callEngine = null;


    private BroadcastReceiver br = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			if("com.gqt.accept".equals(intent.getAction())){
//				Intent voiceIntent = new Intent(ConferenceSendActivity.this,ConferenceCallInCallActivity.class);
//				voiceIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                startActivity(voiceIntent);
//				ConferenceSendActivity.this.finish();
				
			}else if("com.gqt.hangup".equals(intent.getAction())){
//				ConferenceOutGoingActivity.this.finish();
			}
		}
	};


	private BroadcastReceiver broadcastReceiver = new BroadcastReceiver(){

		@Override
		public void onReceive(Context arg0, Intent intent) {
			// TODO Auto-generated method stub
			if("com.gqt.loginout".equals(intent.getAction())){
				ConferenceSendActivity.this.finish();
			}
		}

	};

	@Override
	public int layoutResID() {
		return R.layout.conference;
	}

	@Override
	public void findView() {
		registerEngine = GQTHelper.getInstance().getRegisterEngine();

		if (!registerEngine.isRegister()) {

			registerEngine.initRegisterInfo("8017", "8017", "39.106.172.189", 7080, null);

			registerEngine.register(ConferenceSendActivity.this, new RegisterListener() {
				@Override
				public void onRegisterSuccess() {
//					Utils.showToast(mContext,"success");
					Mlog.d( "registerEngine.register--------onRegisterSuccess==" );
				}

				@Override
				public void onRegisterFailded(String s) {

					Mlog.d( "registerEngine.register--------onRegisterFailded==" + s);

				}
			});
			GQTHelper.getInstance().getCallEngine().registerCallListener(new MyCallListener(callHander));

			groupEngine = GQTHelper.getInstance().getGroupEngine();
			callEngine = GQTHelper.getInstance().getCallEngine();

		}


		GQTHelper.getInstance().getSetEngine().setOutGroupOnCallClosed(true);
		registerReceiver(broadcastReceiver, new IntentFilter("com.gqt.loginout"));


		et1 = (EditText)this.findViewById(R.id.user1);
		et2 = (EditText)this.findViewById(R.id.user2);
//         et3 = (EditText)this.findViewById(R.id.user3);
//         et4 = (EditText)this.findViewById(R.id.user4);
//         et5 = (EditText)this.findViewById(R.id.user5);
//         et6 = (EditText)this.findViewById(R.id.user6);
		sendconf = (Button)this.findViewById(R.id.sendconference);
		sendbrd = (Button)this.findViewById(R.id.sendbroad);
		sendconf.setOnClickListener(this);
		sendbrd.setOnClickListener(this);
		initMusicLine();
	}
	@Override
	public void init(){
		IntentFilter filter = new IntentFilter();
		filter.addAction("com.gqt.accept");
		filter.addAction("com.gqt.hangup");
		registerReceiver(br, filter);


     }



	Handler callHander = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {

				case 0://incall
					Utils.showToast(mContext,"msg.what=====" + msg.what);
					Mlog.d( "callHander--------msg.what=="+msg.what );
					if (msg.arg1 == CallType.VIDEOCALL || msg.arg1 == CallType.UPLOADVIDEO || msg.arg1 == CallType.TRANSCRIBE || msg.arg1 == CallType.MONITORVIDEO || msg.arg1 == CallType.DISPATCH) {
						sendBroadcast(new Intent("com.gqt.videoaccept"));
						//弹出通话接听界面
						if (msg.arg1 == CallType.VIDEOCALL) {
							VideoSizeSetting.getVideoSizeSetting(VideoManagerService.ACTION_VIDEO_CALL).setVideoSize();
						} else {
							VideoSizeSetting.getVideoSizeSetting(VideoManagerService.ACTION_VIDEO_UPLOAD).setVideoSize();
						}
					} else if (msg.arg1 == CallType.VOICECALL) {
						sendBroadcast(new Intent("com.gqt.accept"));
					} else if (msg.arg1 == CallType.BROADCAST) {
						Toast.makeText(ConferenceSendActivity.this, "broadcast incall", Toast.LENGTH_SHORT).show();
					} else if (msg.arg1 == CallType.CONFERENCE) {
						Toast.makeText(ConferenceSendActivity.this, "conference incall", Toast.LENGTH_SHORT).show();
					}
					break;
				case 1:
					Mlog.d( "callHander--------msg.what=="+msg.what );
					stopService(new Intent(ConferenceSendActivity.this, MonitorServer.class));
					Toast.makeText(ConferenceSendActivity.this, "state idle", Toast.LENGTH_SHORT).show();
					sendBroadcast(new Intent("com.gqt.hangup"));
					break;
				//呼出trt312
				case 2:
					Mlog.d( "callHander--------msg.what=="+msg.what );
					Utils.showToast(mContext,"msg.what=====" + msg.what);

					Map<String, String> member = null;
					String mname = "";
					if (isAddressBook) {
						member = GQTHelper.getInstance().getGroupEngine().getMember((String) msg.obj);
					}
					if (member != null) {
						mname = member.get("mname");
					} else {
						mname = (String) msg.obj;
					}
					if (msg.arg1 == CallType.VOICECALL) {
						Intent voiceIntent = new Intent(ConferenceSendActivity.this, VoiceCallOutGoingActivity.class);
						voiceIntent.putExtra("num", mname);
						startActivity(voiceIntent);
					} else if (msg.arg1 == CallType.VIDEOCALL) {
						VideoSizeSetting.getVideoSizeSetting(VideoManagerService.ACTION_VIDEO_CALL).setVideoSize();
						Intent videoIntent = new Intent(ConferenceSendActivity.this, VideoCallOutGoingActivity.class);
						videoIntent.putExtra("num", mname);
						startActivity(videoIntent);
//					} else if (msg.arg1 == CallType.CONFERENCE) {
//						Toast.makeText(ConferenceSendActivity.this, "conference outgoing", Toast.LENGTH_SHORT).show();
//						Intent videoIntent = new Intent(ConferenceSendActivity.this, ConferenceCallInCallActivity.class);
//						videoIntent.putExtra("num", mname);
//						startActivity(videoIntent);
//					} else if (msg.arg1 == CallType.BROADCAST) {
//						Toast.makeText(ConferenceSendActivity.this, "broadcast outgoing", Toast.LENGTH_SHORT).show();
//						Intent videoIntent = new Intent(ConferenceSendActivity.this, ConferenceCallInCallActivity.class);
//						videoIntent.putExtra("num", mname);
//						startActivity(videoIntent);
					} else if (msg.arg1 == CallType.TRANSCRIBE || msg.arg1 == CallType.UPLOADVIDEO || msg.arg1 == CallType.MONITORVIDEO) {
						if (msg.arg1 == CallType.UPLOADVIDEO || msg.arg1 == CallType.TRANSCRIBE) {
							VideoSizeSetting.getVideoSizeSetting(VideoManagerService.ACTION_VIDEO_UPLOAD).setVideoSize();
						}
						Intent videoIntent = new Intent(ConferenceSendActivity.this, TranscribeActivity.class);
						videoIntent.putExtra("type", msg.arg1);
						videoIntent.putExtra("num", mname);
						videoIntent.putExtra("state", 1);
						startActivity(videoIntent);
					}

					new SharedPreferenceTools(ConferenceSendActivity.this).putValues(mname);
					break;
				//呼入
				case 3:
					Mlog.d( "callHander--------msg.what=="+msg.what );
					Utils.showToast(mContext,"msg.what=====" + msg.what);
					String name = msg.getData().getString("name");
					String num = msg.getData().getString("num");
					if (msg.arg1 == CallType.VOICECALL) {
						Intent invoiceIntent = new Intent(ConferenceSendActivity.this, VoiceCallInComingActivity.class);
						invoiceIntent.putExtra("name", name);
						invoiceIntent.putExtra("num", num);
						startActivity(invoiceIntent);
					} else if (msg.arg1 == CallType.VIDEOCALL) {
						VideoSizeSetting.getVideoSizeSetting(VideoManagerService.ACTION_VIDEO_CALL).setVideoSize();
						Intent invideoIntent = new Intent(ConferenceSendActivity.this, VideoCallInComingActivity.class);
						invideoIntent.putExtra("name", name);
						invideoIntent.putExtra("num", num);
						startActivity(invideoIntent);
					} else if (msg.arg1 == CallType.SENDONLY_VOICECALL) {
						GQTHelper.getInstance().getCallEngine().answerCall(CallType.VOICECALL, "");
					} else if (msg.arg1 == CallType.SENDONLY_VIDEOCALL) {
						VideoSizeSetting.getVideoSizeSetting(VideoManagerService.ACTION_VIDEO_MONITOR).setVideoSize();
						startService(new Intent(ConferenceSendActivity.this, MonitorServer.class));
					} else if (msg.arg1 == CallType.UPLOADVIDEO || msg.arg1 == CallType.MONITORVIDEO || msg.arg1 == CallType.DISPATCH) {
						Intent videoIntent = new Intent(ConferenceSendActivity.this, TranscribeActivity.class);
						if (msg.arg1 == CallType.MONITORVIDEO) {
							VideoSizeSetting.getVideoSizeSetting(VideoManagerService.ACTION_VIDEO_MONITOR).setVideoSize();
						}
						videoIntent.putExtra("name", name);
						videoIntent.putExtra("type", msg.arg1);
						videoIntent.putExtra("num", num);
						videoIntent.putExtra("state", 0);
						startActivity(videoIntent);
					}
					break;
				case 99:
					Utils.showToast(mContext,"msg.what=====" + msg.what);
					Toast.makeText(ConferenceSendActivity.this, (String) msg.obj, Toast.LENGTH_SHORT).show();
					break;
				case 98:
					switch ((Integer) msg.obj) {
						case 480:
							Toast.makeText(ConferenceSendActivity.this, "用户不在线或无人接听", Toast.LENGTH_SHORT).show();
							break;

						default:
							break;
					}
					sendBroadcast(new Intent("com.gqt.hangup"));
					break;

				default:
					break;
			}
		}
	};


	@Override
	protected void onDestroy() {
		if (br != null) {
			ConferenceSendActivity.this.unregisterReceiver(br);
		}
		if (broadcastReceiver != null) {
			ConferenceSendActivity.this.unregisterReceiver(broadcastReceiver);
		}

		super.onDestroy();

	}

	@Override
	public void onClick(View v) {
		String num1 = et1.getText().toString();
		String num2 = et2.getText().toString();

		switch (v.getId()) {


			case R.id.sendconference:
				// TODO Auto-generated method stub
//			sendconf.setTextColor(Color.RED);

//			String num3 = et3.getText().toString();
//			String num4 = et4.getText().toString();
//			String num5 = et4.getText().toString();
//			String num6 = et6.getText().toString();
				if (!TextUtils.isEmpty(num1) && !TextUtils.isEmpty(num2)) {
					GQTHelper.getInstance().getCallEngine().makeCall(CallType.CONFERENCE, num1+" "+num2);
				}else if (TextUtils.isEmpty(num1)&& !TextUtils.isEmpty(num2)){
					GQTHelper.getInstance().getCallEngine().makeCall(CallType.CONFERENCE, num2);
				}
				else if (!TextUtils.isEmpty(num1)&& TextUtils.isEmpty(num2)){
					callEngine.makeCall(CallType.CONFERENCE, num1);
				}
				else if (TextUtils.isEmpty(num1)&& TextUtils.isEmpty(num2)) {
					Toast.makeText(ConferenceSendActivity.this, "????????????????", Toast.LENGTH_SHORT).show();
					return;
				}
				Intent intent = new Intent();
				intent.setClass(ConferenceSendActivity.this,ConferenceCallInCallActivity.class);
				Bundle bundle = new Bundle();
				intent.putExtra("num1", num1);
				intent.putExtra("num2", num2);
				intent.putExtras(bundle);
				startActivity(intent);

				break;


			case R.id.sendbroad:
				// TODO Auto-generated method stub
//			sendbrd.setTextColor(Color.RED);
				if (!TextUtils.isEmpty(num1) && !TextUtils.isEmpty(num2)) {
					GQTHelper.getInstance().getCallEngine().makeCall(CallType.BROADCAST, num1+" "+num2);
				}else if (TextUtils.isEmpty(num1)&& !TextUtils.isEmpty(num2)){
					GQTHelper.getInstance().getCallEngine().makeCall(CallType.BROADCAST, num2);
				}
				else if (!TextUtils.isEmpty(num1)&& TextUtils.isEmpty(num2)){
					GQTHelper.getInstance().getCallEngine().makeCall(CallType.BROADCAST, num1);
				}
				else if (TextUtils.isEmpty(num1)&& TextUtils.isEmpty(num2)) {
					Toast.makeText(ConferenceSendActivity.this, "????????????????", Toast.LENGTH_SHORT).show();
					return;
				}
				Intent intent2 = new Intent();
				intent2.setClass(ConferenceSendActivity.this,ConferenceCallInCallActivity.class);
				Bundle bundle2 = new Bundle();
				intent2.putExtra("num1", num1);
				intent2.putExtra("num2", num2);
				intent2.putExtras(bundle2);
				startActivity(intent2);

				break;
		}

	}


	@Override
	public void onPttRequestSuccess() {
		TipSoundPlayer.getInstance().play(TipSoundPlayer.Sound.PTT_ACCEPT);
	}

	@Override
	public void onPttRequestFailed(String reason) {
		myHandler.sendMessage(myHandler.obtainMessage(8, reason));
	}

	@Override
	public void onPttReleaseSuccess() {

		TipSoundPlayer.getInstance().play(TipSoundPlayer.Sound.PTT_RELEASE);

	}

	@Override
	public void onGroupCallInComing(PttGroup grp) {
		myHandler.sendMessage(myHandler.obtainMessage(GroupIncoming, grp));
	}

	@Override
	public void onGrpChanged(PttGroup grp) {
		myHandler.sendMessage(myHandler.obtainMessage(GroupChanged, grp));
		myHandler.sendMessage(myHandler.obtainMessage(GroupMemChanged, groupEngine.getGrpMembers(grp)));
	}

	@Override
	public void onAllGrpsChanged(List<PttGroup> groups) {
		if (groups.size() == 0) {
			myHandler.sendMessage(myHandler.obtainMessage(GroupChanged, null));
		} else {
			myHandler.sendMessage(myHandler.obtainMessage(GroupListChanged, groups));
		}
	}

	@Override
	public void onCurGrpMemberChanged(PttGroup grp, List<GrpMember> members) {
		if (!grp.equals(groupEngine.getCurGrp())) {
			myHandler.sendMessage(myHandler.obtainMessage(GroupMemChanged, groupEngine.getGrpMembers(groupEngine.getCurGrp())));
		} else {
			myHandler.sendMessage(myHandler.obtainMessage(GroupMemChanged, members));
		}
	}

	@Override
	public void groupStateChanged(PttGroup group) {

		PttGroup curGrp = groupEngine.getCurGrp();
		if (curGrp == null) {
			//do nothing
		} else {
			if (!curGrp.equals(group)) return;
			myHandler.sendMessage(myHandler.obtainMessage(GroupStatusChanged, group));
		}
	}

	Handler myHandler = new Handler() {
		@SuppressWarnings("unchecked")
		public void handleMessage(final android.os.Message msg) {
			switch (msg.what) {
				case GroupStatusChanged: {
					PttGroup grp = (PttGroup) (msg.obj);
					stopCurrentAnimation();

//					tv_group_speaker
//							.setText(ShowSpeakerStatus(((PttGroup) (msg.obj)).getCurSpeakerName(), ((PttGroup) (msg.obj)).getCurSpeakerNum()));
//					tv_group_status.setText(mStatus
//							+ ShowPttStatus(((PttGroup) (msg.obj)).getCurState()));
//					if ((grp.getCurState() == GroupState.REQUESTING)) {
//						mLoadingAnimation = new LoadingAnimation();
//						mLoadingAnimation.setAppendCount(3).startAnimation(
//								tv_group_status);
//					}
				}
				break;
				case GroupChanged: {
					PttGroup grp = (PttGroup) (msg.obj);
//					if (grp == null) {
//						group_name_title.setText("对讲");
//						tv_group_status.setText(mStatus);
//						tv_group_speaker.setText("无");
//						if (mGroupNameAdapter != null) {
//							mGroupNameAdapter.refreshNameList(null);
//							mGroupNameAdapter.notifyDataSetChanged();
//						}
//						if (mGroupMemberAdapter != null) {
//							mGroupMemberAdapter.refreshList(null);
//							mGroupMemberAdapter.notifyDataSetChanged();
//						}
//					} else {
//						group_name_title.setText(grp.getGrpName());
//						tv_group_speaker.setText(ShowSpeakerStatus(grp.getCurSpeakerName(),
//								grp.getCurSpeakerNum()));
//						tv_group_status.setText(mStatus + ShowPttStatus(grp.getCurState()));
//						if (mGroupNameAdapter != null) {
//							mGroupNameAdapter.notifyDataSetChanged();
//						}
//					}
				}
				break;
				case GroupListChanged: {
//					List<PttGroup> groups = (List<PttGroup>) (msg.obj);
//					mPttGroups = groups;
//					if (mGroupNameAdapter != null) {
//						mGroupNameAdapter.refreshNameList(groups);
//						mGroupNameAdapter.notifyDataSetChanged();
//					}
				}
				break;
				case GroupMemChanged:
//					updateMemberList((ArrayList<GrpMember>) msg.obj);
					break;
				case GroupIncoming:
					//		Toast.makeText(GroupCallActivity.this, "组来电，组名称:"+((PttGroup)msg.obj).getGrpName(),Toast.LENGTH_SHORT).show();
					final PttGroup incomingGroup = (PttGroup) msg.obj;
					Intent intent = new Intent(ConferenceSendActivity.this, GroupIncomingNotifyActivity.class);
					intent.putExtra("incomingGroupNum", incomingGroup.getGrpNum());
					intent.putExtra("incomingGroupName", incomingGroup.getGrpName());
					startActivity(intent);
					break;
				case 8:
					Toast.makeText(ConferenceSendActivity.this, (String) msg.obj, Toast.LENGTH_LONG).show();
					break;
			}
		}

		;
	};


	private void initMusicLine() {
		mBaseVisualizerView = new BaseVisualizerView(this);
		mBaseVisualizerView.setLayoutParams(new ViewGroup.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.MATCH_PARENT));
//		new_music.addView(mBaseVisualizerView);
	}



	public String ShowSpeakerStatus(String strName, String userNum) {
		if (TextUtils.isEmpty(strName)) {
			mBaseVisualizerView.setTimes(-1);
			return "无";
		} else if (userNum.equals(Constant.userName)/* &&isPttPressing */) {
			return "自己";
		} else {
			return "讲话人" + "（"
					+ strName + "）";
		}
	}


	private void stopCurrentAnimation() {
		if (mLoadingAnimation != null) {
			mLoadingAnimation.stopAnimation();
		}
	}

	private static final int GroupStatusChanged = 1;
	private static final int GroupChanged = 2;
	private static final int GroupListChanged = 3;
	private static final int GroupMemChanged = 4;
	private static final int GroupIncoming = 5;
	boolean showMemList = false;


//	private synchronized void updateMemberList(ArrayList<GrpMember> list) {
//		if (mGroupMemberAdapter != null) {
//			mGroupMemberAdapter.refreshList(list);
//			mGroupMemberAdapter.notifyDataSetChanged();
//		}
//	}


	public class MyGroupNameAdapter extends BaseAdapter {
		private Context context_;
		private String groupName;
		private LayoutInflater layoutInflater;
		private List<PttGroup> pttGrps;
		private int length;

		public MyGroupNameAdapter(Context context) {
			this.context_ = context;
			layoutInflater = LayoutInflater.from(context_);
			pttGrps = new ArrayList<PttGroup>();
		}

		public void refreshNameList(List<PttGroup> pttGrps) {
			this.pttGrps = pttGrps;
		}

		// 得到总的数量
		public int getCount() {
			return pttGrps != null ? pttGrps.size() : 0;
		}

		// 根据ListView位置返回View
		public Object getItem(int position) {
			return this.pttGrps.get(position);
		}

		// 根据ListView位置得到List中的ID
		public long getItemId(int position) {
			return position;
		}

		// 根据位置得到View对象
		public View getView(int position, View convertView, ViewGroup parent) {
//			if (convertView == null) {
//				convertView = layoutInflater.inflate(
//						R.layout.aa_list_item_group_name, null);
//			}
//			TextView tv1 = (TextView) convertView
//					.findViewById(R.id.aa_list_item_groupname);
//			length = pttGrps.size();
//			int a = group_name_list.getHeight();
//			if (length >= 3) {
//				tv1.setHeight(a / 3);
//			} else if (length == 2) {
//				tv1.setHeight(a / 2);
//			} else if (length == 1) {
//				tv1.setHeight(a);
//			}
//			if (groupEngine.getCurGrp() != null && pttGrps.get(position) != null && groupEngine.getCurGrp() != null
//					&& (groupEngine.getCurGrp().equals(pttGrps.get(position)))) {
//				convertView.setBackgroundResource(R.color.font_color);
//				tv1.setTextColor(getResources().getColor(R.color.black));
//			} else {
//				convertView.setBackgroundResource(R.color.font_color2);
//				tv1.setTextColor(getResources().getColor(R.color.white));
//			}
//
//			groupName = pttGrps.get(position).getGrpName();
//			groupName = formatGroupName(groupName, length);
//			tv1.setText(groupName);
//			return convertView;
			return null;
		}
	}


	public class MyGroupMemberAdapter extends BaseAdapter implements
			View.OnClickListener {
		private Context context_;
		private LayoutInflater layoutInflater;

		ArrayList<GrpMember> list;

		public MyGroupMemberAdapter(Context context) {
			this.context_ = context;

			layoutInflater = LayoutInflater.from(context_);

			this.list = new ArrayList<GrpMember>();
		}

		public void refreshList(ArrayList<GrpMember> list) {
			this.list = list;
		}

		// 得到总的数量
		public int getCount() {

			return this.list != null ? this.list.size() : 0;
		}

		// 根据ListView位置返回View
		public Object getItem(int position) {

			return this.list.get(position);
		}

		// 根据ListView位置得到List中的ID
		public long getItemId(int position) {

			return position;
		}

		// 根据位置得到View对象
		public View getView(final int position, View convertView,
							ViewGroup parent) {
			if (convertView == null) {
				convertView = layoutInflater.inflate(
						R.layout.aa_list_item_group_member, null);
			}
			convertView.setBackgroundColor(getResources().getColor(
					R.color.black_));
			TextView tv1 = (TextView) convertView
					.findViewById(R.id.member_list_name);
			GrpMember member = list.get(position);
			if (TextUtils.isEmpty(member.getMemberName())) {
				tv1.setText(member.getMemberNum());
			} else {
				tv1.setText(member.getMemberName());
			}
			if (member.getState() == 0) {
				tv1.setTextColor(getResources().getColor(R.color.notOnLine));
			} else {
				tv1.setTextColor(getResources().getColor(R.color.onLine));
			}
			return convertView;

		}

		@Override
		public void onClick(final View v) {

		}
	}



	// 获取当前时间格式HH:mm:ss  jibingeng 2015-09-23
	public String getTime() {
		try {
			SimpleDateFormat formatter = new SimpleDateFormat(" HHmmss ");
			Date curDate = new Date(System.currentTimeMillis());
			String strTime = formatter.format(curDate);
			return strTime;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}


	boolean isPaused = false;

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		groupEngine.makeGroupCall(false);
		isPaused = true;
		super.onPause();
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		groupEngine.regGroupEngineListener(this);
	}

	@Override
	public void showCurrentVolume(int time) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onAddressBook(boolean isSuccess) {
		// TODO Auto-generated method stub
		isAddressBook = isSuccess;

	}

	@Override
	public void onAddressBookUpdateVersion(String version) {
		// TODO Auto-generated method stub
		String olderVersion = GQTHelper.getInstance().getGroupEngine().getAddressBookVersion();
		if (Integer.parseInt(version) > Integer.parseInt(olderVersion)) {
			isAddressBook = false;
			GQTHelper.getInstance().getGroupEngine().getAddressBook();
		}
	}

	@Override
	public void onTempGroupCallState(int state) {
		// TODO Auto-generated method stub

	}


	public void onTempGrpMemberChanged(List<String> members) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onTempGroupCallInComing(String grpname, List<String> members) {
		// TODO Auto-generated method stub
		Intent intent = new Intent(ConferenceSendActivity.this, GroupIncomingNotifyActivity.class);
		intent.putExtra("incomingGroupName", grpname);
		intent.putStringArrayListExtra("members", (ArrayList<String>) members);
		intent.putExtra("istmp", true);
		startActivity(intent);
	}

	@Override
	public void onCustomGroupResultState(CustomGroupResult result, int code, String groupNum, List<GrpMember> members) {
		// TODO Auto-generated method stub
		switch (result) {
			case UPDATE_GROUP_INFO:
			case CREATE_SUCCESS:
				myHandler.sendMessage(myHandler.obtainMessage(GroupListChanged, groupEngine.getAllPttGrps()));
				break;
			case UPDATE_GROUP_MEMBER_INFO:
				if (groupEngine.getCurGrp().getGrpNum().equals(groupNum)) {
					myHandler.sendMessage(myHandler.obtainMessage(GroupMemChanged, members));
				}
				break;
			case DESTROY_SUCCESS:
			case MODIFY_SUCCESS:
			case LEAVE_SUCCESS:
				GQTHelper.getInstance().getGroupEngine().SendCustomGroupMessage(CustomGroupType.GET_CUSTOM_GROUP, null, null, null);
				break;
			case GET_GROUP_NUMBER_LIST_TIME_OUT:
			case GET_GROUP_MEMBER_INFO_TIME_OUT:
			case REQUEST_TIME_OUT:
				Toast.makeText(ConferenceSendActivity.this, result + " 超时！", Toast.LENGTH_SHORT).show();

				break;
			case LEAVE_FAILURE:
			case MODIFY_FAILURE:
			case DESTROY_FAILURE:
				Toast.makeText(ConferenceSendActivity.this, result + " 失败！原因 :" + showFailureReason(code), Toast.LENGTH_SHORT).show();
				break;
			default:
				break;
		}
		Intent intent = new Intent(CUSTOM_GROUP_ACTION_RESULT_STATE);
		intent.putExtra("result", result);
		intent.putExtra("code", code);
		intent.putExtra("groupNum", groupNum);
		sendBroadcast(intent);
	}

	/**
	 * 显示服务器请求失败信息
	 *
	 * @param code    请求错误码
	 */
	public String showFailureReason(int code) {
		String result = "未知错误！code：" + code;
		switch (code) {
			case 450:
				result = "该对讲组已存在，无法创建";
				break;

			case 451:
				result = "未选择创建者自己";
				break;

			case 452:
				result = "新增加成员已存在";
				break;

			case 453:
				result = "不是创建者，无法进行该操作";
				break;

			case 454:
				result = "不能删除创建者";
				break;

			case 455:
				result = "成员不存在，无法删除";
				break;

			case 456:
				result = "不能退出调度台创建的对讲组";
				break;
		}
		return result;
	}

	@Override
	public void parseDeleteMemberInfoCompleted(String groupCreatorName,
											   String groupNum, String groupName, List<String> memberList) {
		// TODO Auto-generated method stub
		String ss = "";
		ss += groupCreatorName + "将";
		for (String num : memberList) {
			if (num.equals(Constant.userName)) {
				ss += "我 ";
			} else {
				ss += num + " ";
			}
		}
		ss += " 移出  " + groupName;
		Toast.makeText(this, ss, Toast.LENGTH_SHORT).show();
	}

	@Override
	public void parseDestroyCustomGroupInfoCompleted(String groupCreatorName,
													 String groupNum, String groupName) {
		// TODO Auto-generated method stub
		String ss = "";
		ss += groupCreatorName + " 解散组 " + groupName;
		Toast.makeText(this, ss, Toast.LENGTH_SHORT).show();
	}

	@Override
	public void parseAddMemberInfoCompleted(String groupCreatorName,
											String groupName, List<String> memberList) {
		// TODO Auto-generated method stub
		String ss = "";
		ss += groupCreatorName + "将";
		for (String num : memberList) {
			if (num.equals(Constant.userName)) {
				ss += " 我 ";
			} else {
				ss += num + " ";
			}
		}
		ss += " 邀请进  " + groupName;
		Toast.makeText(this, ss, Toast.LENGTH_SHORT).show();
	}

	@Override
	public void parseLeaveCustomGroupInfoCompleted(String groupCreatorName,
												   String groupName, String leaveNumber) {
		// TODO Auto-generated method stub
		String ss = "";
		ss += leaveNumber + " 退出 " + groupName;
		Toast.makeText(this, ss, Toast.LENGTH_SHORT).show();
	}



}
