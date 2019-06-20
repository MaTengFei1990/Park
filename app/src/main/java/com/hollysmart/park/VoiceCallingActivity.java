package com.hollysmart.park;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.gqt.bean.CallType;
import com.gqt.bean.GroupCallListener;
import com.gqt.bean.GrpMember;
import com.gqt.bean.PttGroup;
import com.gqt.bean.RegisterListener;
import com.gqt.customgroup.CustomGroupParserListener;
import com.gqt.customgroup.CustomGroupResult;
import com.gqt.helper.CallEngine;
import com.gqt.helper.Constant;
import com.gqt.helper.GQTHelper;
import com.gqt.helper.GroupEngine;
import com.gqt.helper.RegisterEngine;
import com.gqt.utils.BaseVisualizerView;
import com.gqt.video.VideoManagerService;
import com.hollysmart.conference.MyCallListener;
import com.hollysmart.groupcall.VideoSizeSetting;
import com.hollysmart.style.StyleAnimActivity;
import com.hollysmart.tools.SharedPreferenceTools;
import com.hollysmart.utils.Mlog;
import com.hollysmart.utils.Utils;
import com.hollysmart.videocall.MonitorServer;
import com.hollysmart.videocall.TranscribeActivity;
import com.hollysmart.voicecall.VoiceCallOutGoingActivity;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;


public class VoiceCallingActivity extends StyleAnimActivity  implements  View.OnClickListener {


    @Override
    public int layoutResID() {
        return R.layout.activity_voice_calling;
    }


    RegisterEngine registerEngine = null;


    boolean isAddressBook = false;

//    BaseVisualizerView mBaseVisualizerView;


//    GroupEngine groupEngine = null;
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
                VoiceCallingActivity.this.finish();
            }
        }

    };

    @Override
    public void findView() {
        registerEngine = GQTHelper.getInstance().getRegisterEngine();

        if (!registerEngine.isRegister()) {

            registerEngine.initRegisterInfo("8016", "8016", "39.106.172.189", 7080, null);

            registerEngine.register(VoiceCallingActivity.this, new RegisterListener() {
                @Override
                public void onRegisterSuccess() {
//					Utils.showToast(mContext,"success");
                    Mlog.d("registerEngine.register--------onRegisterSuccess==");
                }

                @Override
                public void onRegisterFailded(String s) {

                    Mlog.d("registerEngine.register--------onRegisterFailded==" + s);

                }
            });
            GQTHelper.getInstance().getCallEngine().registerCallListener(new MyCallListener(callHander));

//            groupEngine = GQTHelper.getInstance().getGroupEngine();
            callEngine = GQTHelper.getInstance().getCallEngine();

        } else {
//            groupEngine = GQTHelper.getInstance().getGroupEngine();
            callEngine = GQTHelper.getInstance().getCallEngine();
        }


        GQTHelper.getInstance().getSetEngine().setOutGroupOnCallClosed(true);
        registerReceiver(broadcastReceiver, new IntentFilter("com.gqt.loginout"));


//        initMusicLine();
    }
    @Override
    public void init(){
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.gqt.accept");
        filter.addAction("com.gqt.hangup");
        registerReceiver(br, filter);


        String num1 = "8017";
        String num2 = "8018";



        GQTHelper.getInstance().getCallEngine().makeCall(CallType.CONFERENCE, num1+" "+num2);

        Intent intent = new Intent();
        intent.setClass(VoiceCallingActivity.this, VoiceCallInCallActivity.class);
        Bundle bundle = new Bundle();
        intent.putExtra("num1", num1);
        intent.putExtra("num2", num2);
        intent.putExtras(bundle);
        startActivity(intent);


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
//                        sendBroadcast(new Intent("com.gqt.videoaccept"));
//                        //弹出通话接听界面
//                        if (msg.arg1 == CallType.VIDEOCALL) {
//                            VideoSizeSetting.getVideoSizeSetting(VideoManagerService.ACTION_VIDEO_CALL).setVideoSize();
//                        } else {
//                            VideoSizeSetting.getVideoSizeSetting(VideoManagerService.ACTION_VIDEO_UPLOAD).setVideoSize();
//                        }
                    } else if (msg.arg1 == CallType.VOICECALL) {
                        sendBroadcast(new Intent("com.gqt.accept"));
                    } else if (msg.arg1 == CallType.BROADCAST) {
                        Toast.makeText(VoiceCallingActivity.this, "broadcast incall", Toast.LENGTH_SHORT).show();
                    } else if (msg.arg1 == CallType.CONFERENCE) {
                        Toast.makeText(VoiceCallingActivity.this, "conference incall", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case 1:
                    Mlog.d( "callHander--------msg.what=="+msg.what );
                    stopService(new Intent(VoiceCallingActivity.this, MonitorServer.class));
                    Toast.makeText(VoiceCallingActivity.this, "state idle", Toast.LENGTH_SHORT).show();
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
                        Intent voiceIntent = new Intent(VoiceCallingActivity.this, VoiceCallOutGoingActivity.class);
                        voiceIntent.putExtra("num", mname);
                        startActivity(voiceIntent);
//                    } else if (msg.arg1 == CallType.TRANSCRIBE || msg.arg1 == CallType.UPLOADVIDEO || msg.arg1 == CallType.MONITORVIDEO) {
//                        if (msg.arg1 == CallType.UPLOADVIDEO || msg.arg1 == CallType.TRANSCRIBE) {
//                            VideoSizeSetting.getVideoSizeSetting(VideoManagerService.ACTION_VIDEO_UPLOAD).setVideoSize();
//                        }
//                        Intent videoIntent = new Intent(VoiceCallingActivity.this, TranscribeActivity.class);
//                        videoIntent.putExtra("type", msg.arg1);
//                        videoIntent.putExtra("num", mname);
//                        videoIntent.putExtra("state", 1);
//                        startActivity(videoIntent);
                    }

                    new SharedPreferenceTools(VoiceCallingActivity.this).putValues(mname);
                    break;
                //呼入
                case 3:
                    Mlog.d( "callHander--------msg.what=="+msg.what );
                    Utils.showToast(mContext,"msg.what=====" + msg.what);
                    String name = msg.getData().getString("name");
                    String num = msg.getData().getString("num");
                    if (msg.arg1 == CallType.VOICECALL) {
                        Intent invoiceIntent = new Intent(VoiceCallingActivity.this, VoiceCallComingActivity.class);
                        invoiceIntent.putExtra("name", name);
                        invoiceIntent.putExtra("num", num);
                        startActivity(invoiceIntent);
//                    } else if (msg.arg1 == CallType.VIDEOCALL) {
//                        VideoSizeSetting.getVideoSizeSetting(VideoManagerService.ACTION_VIDEO_CALL).setVideoSize();
//                        Intent invideoIntent = new Intent(VoiceCallingActivity.this, VoiceCallComingActivity.class);
//                        invideoIntent.putExtra("name", name);
//                        invideoIntent.putExtra("num", num);
//                        startActivity(invideoIntent);
                    } else if (msg.arg1 == CallType.SENDONLY_VOICECALL) {
                        GQTHelper.getInstance().getCallEngine().answerCall(CallType.VOICECALL, "");
//                    } else if (msg.arg1 == CallType.SENDONLY_VIDEOCALL) {
//                        VideoSizeSetting.getVideoSizeSetting(VideoManagerService.ACTION_VIDEO_MONITOR).setVideoSize();
//                        startService(new Intent(VoiceCallingActivity.this, MonitorServer.class));
//                    } else if (msg.arg1 == CallType.UPLOADVIDEO || msg.arg1 == CallType.MONITORVIDEO || msg.arg1 == CallType.DISPATCH) {
//                        Intent videoIntent = new Intent(VoiceCallingActivity.this, TranscribeActivity.class);
//                        if (msg.arg1 == CallType.MONITORVIDEO) {
//                            VideoSizeSetting.getVideoSizeSetting(VideoManagerService.ACTION_VIDEO_MONITOR).setVideoSize();
//                        }
//                        videoIntent.putExtra("name", name);
//                        videoIntent.putExtra("type", msg.arg1);
//                        videoIntent.putExtra("num", num);
//                        videoIntent.putExtra("state", 0);
//                        startActivity(videoIntent);
                    }
                    break;
                case 99:
                    Utils.showToast(mContext,"msg.what=====" + msg.what);
                    Toast.makeText(VoiceCallingActivity.this, (String) msg.obj, Toast.LENGTH_SHORT).show();
                    break;
                case 98:
                    switch ((Integer) msg.obj) {
                        case 480:
                            Toast.makeText(VoiceCallingActivity.this, "用户不在线或无人接听", Toast.LENGTH_SHORT).show();
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
            VoiceCallingActivity.this.unregisterReceiver(br);
        }
        if (broadcastReceiver != null) {
            VoiceCallingActivity.this.unregisterReceiver(broadcastReceiver);
        }

        super.onDestroy();

    }

    @Override
    public void onClick(View v) {

    }


//    @Override
//    public void onPttRequestSuccess() {
//        TipSoundPlayer.getInstance().play(TipSoundPlayer.Sound.PTT_ACCEPT);
//    }
//
//    @Override
//    public void onPttRequestFailed(String reason) {
//        myHandler.sendMessage(myHandler.obtainMessage(8, reason));
//    }
//
//    @Override
//    public void onPttReleaseSuccess() {
//
//        TipSoundPlayer.getInstance().play(TipSoundPlayer.Sound.PTT_RELEASE);
//
//    }
//
//    @Override
//    public void onGroupCallInComing(PttGroup grp) {
//        myHandler.sendMessage(myHandler.obtainMessage(GroupIncoming, grp));
//    }
//
//    @Override
//    public void onGrpChanged(PttGroup grp) {
//        myHandler.sendMessage(myHandler.obtainMessage(GroupChanged, grp));
//        myHandler.sendMessage(myHandler.obtainMessage(GroupMemChanged, groupEngine.getGrpMembers(grp)));
//    }
//
//    @Override
//    public void onAllGrpsChanged(List<PttGroup> groups) {
//        if (groups.size() == 0) {
//            myHandler.sendMessage(myHandler.obtainMessage(GroupChanged, null));
//        } else {
//            myHandler.sendMessage(myHandler.obtainMessage(GroupListChanged, groups));
//        }
//    }
//
//    @Override
//    public void onCurGrpMemberChanged(PttGroup grp, List<GrpMember> members) {
//        if (!grp.equals(groupEngine.getCurGrp())) {
//            myHandler.sendMessage(myHandler.obtainMessage(GroupMemChanged, groupEngine.getGrpMembers(groupEngine.getCurGrp())));
//        } else {
//            myHandler.sendMessage(myHandler.obtainMessage(GroupMemChanged, members));
//        }
//    }
//
//    @Override
//    public void groupStateChanged(PttGroup group) {
//
//        PttGroup curGrp = groupEngine.getCurGrp();
//        if (curGrp == null) {
//            //do nothing
//        } else {
//            if (!curGrp.equals(group)) return;
//            myHandler.sendMessage(myHandler.obtainMessage(GroupStatusChanged, group));
//        }
//    }

//    Handler myHandler = new Handler() {
//        @SuppressWarnings("unchecked")
//        public void handleMessage(final android.os.Message msg) {
//            switch (msg.what) {
//                case GroupStatusChanged: {
//                    PttGroup grp = (PttGroup) (msg.obj);
//
//                }
//                break;
//                case GroupChanged: {
//                    PttGroup grp = (PttGroup) (msg.obj);
//                }
//                break;
//                case GroupListChanged: {
//                }
//                break;
//                case GroupMemChanged:
//                    break;
//                case 8:
//                    Toast.makeText(VoiceCallingActivity.this, (String) msg.obj, Toast.LENGTH_LONG).show();
//                    break;
//            }
//        }
//
//        ;
//    };


//    private void initMusicLine() {
//        mBaseVisualizerView = new BaseVisualizerView(this);
//        mBaseVisualizerView.setLayoutParams(new ViewGroup.LayoutParams(
//                ViewGroup.LayoutParams.MATCH_PARENT,
//                ViewGroup.LayoutParams.MATCH_PARENT));
////		new_music.addView(mBaseVisualizerView);
//    }



//    private static final int GroupStatusChanged = 1;
//    private static final int GroupChanged = 2;
//    private static final int GroupListChanged = 3;
//    private static final int GroupMemChanged = 4;
//    private static final int GroupIncoming = 5;
//    boolean showMemList = false;





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
//        groupEngine.makeGroupCall(false);
        isPaused = true;
        super.onPause();
    }

    @Override
    public void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
//        if (groupEngine != null) {
//            groupEngine.regGroupEngineListener(this);
//        }
    }

//    @Override
//    public void showCurrentVolume(int time) {
//        // TODO Auto-generated method stub
//    }
//
//    @Override
//    public void onAddressBook(boolean isSuccess) {
//        // TODO Auto-generated method stub
//        isAddressBook = isSuccess;
//
//    }
//
//    @Override
//    public void onAddressBookUpdateVersion(String version) {
//        // TODO Auto-generated method stub
//        String olderVersion = GQTHelper.getInstance().getGroupEngine().getAddressBookVersion();
//        if (Integer.parseInt(version) > Integer.parseInt(olderVersion)) {
//            isAddressBook = false;
//            GQTHelper.getInstance().getGroupEngine().getAddressBook();
//        }
//    }
//
//    @Override
//    public void onTempGroupCallState(int state) {
//        // TODO Auto-generated method stub
//
//    }
//
//
//    public void onTempGrpMemberChanged(List<String> members) {
//        // TODO Auto-generated method stub
//
//    }
//
//    @Override
//    public void onTempGroupCallInComing(String grpname, List<String> members) {
////        // TODO Auto-generated method stub
////        Intent intent = new Intent(VoiceCallingActivity.this, GroupIncomingNotifyActivity.class);
////        intent.putExtra("incomingGroupName", grpname);
////        intent.putStringArrayListExtra("members", (ArrayList<String>) members);
////        intent.putExtra("istmp", true);
////        startActivity(intent);
//    }
//
//    @Override
//    public void onCustomGroupResultState(CustomGroupResult result, int code, String groupNum, List<GrpMember> members) {
//        // TODO Auto-generated method stub
//    }
//
//
//    @Override
//    public void parseDeleteMemberInfoCompleted(String groupCreatorName,
//                                               String groupNum, String groupName, List<String> memberList) {
//        // TODO Auto-generated method stub
//        String ss = "";
//        ss += groupCreatorName + "将";
//        for (String num : memberList) {
//            if (num.equals(Constant.userName)) {
//                ss += "我 ";
//            } else {
//                ss += num + " ";
//            }
//        }
//        ss += " 移出  " + groupName;
//        Toast.makeText(this, ss, Toast.LENGTH_SHORT).show();
//    }
//
//    @Override
//    public void parseDestroyCustomGroupInfoCompleted(String groupCreatorName,
//                                                     String groupNum, String groupName) {
//        // TODO Auto-generated method stub
//        String ss = "";
//        ss += groupCreatorName + " 解散组 " + groupName;
//        Toast.makeText(this, ss, Toast.LENGTH_SHORT).show();
//    }
//
//    @Override
//    public void parseAddMemberInfoCompleted(String groupCreatorName,
//                                            String groupName, List<String> memberList) {
//        // TODO Auto-generated method stub
//        String ss = "";
//        ss += groupCreatorName + "将";
//        for (String num : memberList) {
//            if (num.equals(Constant.userName)) {
//                ss += " 我 ";
//            } else {
//                ss += num + " ";
//            }
//        }
//        ss += " 邀请进  " + groupName;
//        Toast.makeText(this, ss, Toast.LENGTH_SHORT).show();
//    }
//
//    @Override
//    public void parseLeaveCustomGroupInfoCompleted(String groupCreatorName,
//                                                   String groupName, String leaveNumber) {
//        // TODO Auto-generated method stub
//        String ss = "";
//        ss += leaveNumber + " 退出 " + groupName;
//        Toast.makeText(this, ss, Toast.LENGTH_SHORT).show();
//    }

}
