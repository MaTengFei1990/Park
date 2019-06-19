package com.hollysmart.groupcall;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AbsListView.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.gqt.bean.CallType;
import com.gqt.bean.FenceOperate;
import com.gqt.bean.FenceOperateListener;
import com.gqt.bean.GisManager;
import com.gqt.bean.GroupCallListener;
import com.gqt.bean.GroupState;
import com.gqt.bean.GrpMember;
import com.gqt.bean.PttGroup;
import com.gqt.customgroup.CustomGroupParserListener;
import com.gqt.customgroup.CustomGroupResult;
import com.gqt.customgroup.CustomGroupType;
import com.gqt.helper.CallEngine;
import com.gqt.helper.Constant;
import com.gqt.helper.GQTHelper;
import com.gqt.helper.GroupEngine;
import com.gqt.location.FenceInfo;
import com.gqt.net.util.NetChecker;
import com.gqt.testui.MyCallListener;
import com.gqt.utils.BaseVisualizerView;
import com.gqt.utils.LoadingAnimation;
import com.gqt.video.VideoManagerService;
import com.hollysmart.conference.ConferenceCallInCallActivity;
import com.hollysmart.park.R;
import com.hollysmart.park.TipSoundPlayer;
import com.hollysmart.tools.CustomPopWindow;
import com.hollysmart.tools.GQTUtils;
import com.hollysmart.tools.SharedPreferenceTools;
import com.hollysmart.videocall.MonitorServer;
import com.hollysmart.videocall.TranscribeActivity;
import com.hollysmart.videocall.VideoCallInComingActivity;
import com.hollysmart.videocall.VideoCallOutGoingActivity;
import com.hollysmart.voicecall.VoiceCallInComingActivity;
import com.hollysmart.voicecall.VoiceCallOutGoingActivity;

import org.zoolu.tools.GroupListInfo;

import java.lang.reflect.Field;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GroupCallActivity extends Activity implements GroupCallListener, OnClickListener, CustomGroupParserListener {
    boolean isAddressBook = false;
    GroupEngine groupEngine = null;
    CallEngine callEngine = null;
    boolean isChangeMemaber = false;
    private int groupBodyMumber;
    private String groupOnlineBodyMumber;
    private TextView new_member_text;
    static boolean mHasPttGrp;
    private TextView group_name_title;
    ;
    private static ImageView group_button_ptt;
    private ListView group_name_list;
    private ListView group_member_list;
    private TextView tv_group_status;
    private TextView tv_group_speaker;
    private MyGroupNameAdapter mGroupNameAdapter;
    private MyGroupMemberAdapter mGroupMemberAdapter;
    ArrayList<GroupListInfo> arrayList;
    private String mStatus = "";// 我的状态
    private LoadingAnimation mLoadingAnimation;
    private View mRootView;
    List<PttGroup> mPttGroups = null;
    private ImageView new_down_up;
    private LinearLayout new_music;
    private Button reJoinGroupCall, rejectGroupCall;
    BaseVisualizerView mBaseVisualizerView;
    public static final String CUSTOM_GROUP_ACTION_RESULT_STATE = "action_CustomGroupResultState";
    private CustomPopWindow mCustomPopWindow;
    private TextView create, modify, add, del, destroy, exit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        mRootView = getLayoutInflater().inflate(R.layout.aa_new, null);
        setContentView(mRootView);
        View new_open_close = findViewById(R.id.new_open_close);
        new_open_close.setOnClickListener(this);
        mStatus = "我的状态";
        group_name_list = (ListView) findViewById(R.id.new_group_name_list);
        group_name_list.setVerticalScrollBarEnabled(false);
        group_member_list = (ListView) findViewById(R.id.new_group_member_list);
        group_member_list.setVerticalScrollBarEnabled(true);
        group_member_list.setVisibility(View.GONE);
        new_down_up = (ImageView) this.findViewById(R.id.new_down_up);
        tv_group_status = (TextView) findViewById(R.id.new_tv_group_status);
        tv_group_speaker = (TextView) findViewById(R.id.new_tv_group_speaker);
        new_member_text = (TextView) findViewById(R.id.new_member_text);
        group_button_ptt = (ImageView) findViewById(R.id.new_group_button_ptt);
        new_music = (LinearLayout) findViewById(R.id.new_music);
        findViewById(R.id.tmp_group_button).setOnClickListener(this);
        findViewById(R.id.custom_group_button).setOnClickListener(this);
        group_name_title = (TextView) findViewById(R.id.new_group_name_title);
        group_button_ptt.setOnTouchListener(new OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        if (mGroupNameAdapter != null && mGroupNameAdapter.getCount() == 0) {
                            Toast.makeText(GroupCallActivity.this, "不在任何组", Toast.LENGTH_SHORT).show();
                            break;
                        }
                        if (!(GQTUtils.isNetworkAvailable(GroupCallActivity.this))) {
                            break;
                        }
                        TipSoundPlayer.getInstance().play(TipSoundPlayer.Sound.PTT_DOWN);
                        group_button_ptt.setImageResource(R.drawable.group_list_ptt_down);
                        groupEngine.makeGroupCall(true);
                        break;
                    case MotionEvent.ACTION_UP:
                        if (!isPaused && (mGroupNameAdapter != null && mGroupNameAdapter.getCount() > 0)) {
                            TipSoundPlayer.getInstance().play(TipSoundPlayer.Sound.PTT_DOWN);
                        }
                        if (!(GQTUtils.isNetworkAvailable(GroupCallActivity.this))) {
                            break;
                        }
                        mBaseVisualizerView.setTimes(-1);
                        group_button_ptt.setImageResource(R.drawable.group_list_ptt_up);
                        groupEngine.makeGroupCall(false);
                        break;
                }

                return true;
            }
        });

        mGroupNameAdapter = new MyGroupNameAdapter(GroupCallActivity.this);
        group_name_list.setAdapter(mGroupNameAdapter);
        mGroupNameAdapter.notifyDataSetChanged();
        mGroupMemberAdapter = new MyGroupMemberAdapter(GroupCallActivity.this);
        group_member_list.setAdapter(mGroupMemberAdapter);
        group_name_list.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View v,
                                    final int position, long arg3) {
                if (!NetChecker.check(GroupCallActivity.this)) {
                    return;
                }
                PttGroup grp = mPttGroups.get(position);
                if (grp != null) {
                    groupEngine.setCurGrp(grp, true);
                }
            }
        });
        if (groupOnlineBodyMumber != null) {
            if (isChangeMemaber) {
                new_member_text.setText("(" + groupOnlineBodyMumber + "/"
                        + groupBodyMumber + ")");
            } else {
                new_member_text.setText("(" + groupBodyMumber + ")");
            }
        }
        reJoinGroupCall = (Button) findViewById(R.id.rejoin);
        reJoinGroupCall.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                PttGroup curGrp = GQTHelper.getInstance().getGroupEngine().getCurGrp();
                if (curGrp != null)
                    GQTHelper.getInstance().getGroupEngine().setCurGrp(curGrp, true);
            }
        });
        rejectGroupCall = (Button) findViewById(R.id.exit);
        rejectGroupCall.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                PttGroup curGrp = GQTHelper.getInstance().getGroupEngine().getCurGrp();
                if (curGrp != null)
                    GQTHelper.getInstance().getGroupEngine().rejectGroupCall(curGrp);

            }
        });
        super.onCreate(savedInstanceState);
        groupEngine = GQTHelper.getInstance().getGroupEngine();


        callEngine = GQTHelper.getInstance().getCallEngine();
        callEngine.registerCallListener(new MyCallListener(callHander));
        groupEngine.setOnParseCompledtedListener(this);
        initMusicLine();
        myHandler.sendMessage(myHandler.obtainMessage(GroupStatusChanged, groupEngine.getCurGrp()));
        GQTHelper.getInstance().getGroupEngine().SendCustomGroupMessage(CustomGroupType.GET_CUSTOM_GROUP, null, null, null);
        GisManager.getInstance().setFenceOperateListener(new FenceOperateListener() {

                                                             @Override
                                                             public void onFenceOperate(final FenceOperate operate, final FenceInfo fenceInfo) {
                                                                 // TODO Auto-generated method stub
                                                                 runOnUiThread(new Runnable() {

                                                                     @Override
                                                                     public void run() {
                                                                         // TODO Auto-generated method stub

                                                                         Toast.makeText(GroupCallActivity.this, "电子围栏 operate   " + operate + "  fenceInfo " + fenceInfo.toString(), Toast.LENGTH_SHORT).show();
                                                                     }
                                                                 });

                                                             }
                                                         }
        );

        GQTHelper.getInstance().getSetEngine().setOpenAutoFocus(false);

    }

    private void initMusicLine() {
        mBaseVisualizerView = new BaseVisualizerView(this);
        mBaseVisualizerView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        new_music.addView(mBaseVisualizerView);
    }

    Handler callHander = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0://incall
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
                        Toast.makeText(GroupCallActivity.this, "broadcast incall", Toast.LENGTH_SHORT).show();
                    } else if (msg.arg1 == CallType.CONFERENCE) {
                        Toast.makeText(GroupCallActivity.this, "conference incall", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case 1:
                    stopService(new Intent(GroupCallActivity.this, MonitorServer.class));
                    Toast.makeText(GroupCallActivity.this, "state idle", Toast.LENGTH_SHORT).show();
                    sendBroadcast(new Intent("com.gqt.hangup"));
                    break;
                //呼出trt312
                case 2:
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
                        Intent voiceIntent = new Intent(GroupCallActivity.this, VoiceCallOutGoingActivity.class);
                        voiceIntent.putExtra("num", mname);
                        startActivity(voiceIntent);
                    } else if (msg.arg1 == CallType.VIDEOCALL) {
                        VideoSizeSetting.getVideoSizeSetting(VideoManagerService.ACTION_VIDEO_CALL).setVideoSize();
                        Intent videoIntent = new Intent(GroupCallActivity.this, VideoCallOutGoingActivity.class);
                        videoIntent.putExtra("num", mname);
                        startActivity(videoIntent);
                    } else if (msg.arg1 == CallType.CONFERENCE) {
                        Toast.makeText(GroupCallActivity.this, "conference outgoing", Toast.LENGTH_SHORT).show();
                        Intent videoIntent = new Intent(GroupCallActivity.this, ConferenceCallInCallActivity.class);
                        videoIntent.putExtra("num", mname);
                        startActivity(videoIntent);
                    } else if (msg.arg1 == CallType.BROADCAST) {
                        Toast.makeText(GroupCallActivity.this, "broadcast outgoing", Toast.LENGTH_SHORT).show();
                        Intent videoIntent = new Intent(GroupCallActivity.this, ConferenceCallInCallActivity.class);
                        videoIntent.putExtra("num", mname);
                        startActivity(videoIntent);
                    } else if (msg.arg1 == CallType.TRANSCRIBE || msg.arg1 == CallType.UPLOADVIDEO || msg.arg1 == CallType.MONITORVIDEO) {
                        if (msg.arg1 == CallType.UPLOADVIDEO || msg.arg1 == CallType.TRANSCRIBE) {
                            VideoSizeSetting.getVideoSizeSetting(VideoManagerService.ACTION_VIDEO_UPLOAD).setVideoSize();
                        }
                        Intent videoIntent = new Intent(GroupCallActivity.this, TranscribeActivity.class);
                        videoIntent.putExtra("type", msg.arg1);
                        videoIntent.putExtra("num", mname);
                        videoIntent.putExtra("state", 1);
                        startActivity(videoIntent);
                    }

                    new SharedPreferenceTools(GroupCallActivity.this).putValues(mname);
                    break;
                //呼入
                case 3:
                    String name = msg.getData().getString("name");
                    String num = msg.getData().getString("num");
                    if (msg.arg1 == CallType.VOICECALL) {
                        Intent invoiceIntent = new Intent(GroupCallActivity.this, VoiceCallInComingActivity.class);
                        invoiceIntent.putExtra("name", name);
                        invoiceIntent.putExtra("num", num);
                        startActivity(invoiceIntent);
                    } else if (msg.arg1 == CallType.VIDEOCALL) {
                        VideoSizeSetting.getVideoSizeSetting(VideoManagerService.ACTION_VIDEO_CALL).setVideoSize();
                        Intent invideoIntent = new Intent(GroupCallActivity.this, VideoCallInComingActivity.class);
                        invideoIntent.putExtra("name", name);
                        invideoIntent.putExtra("num", num);
                        startActivity(invideoIntent);
                    } else if (msg.arg1 == CallType.SENDONLY_VOICECALL) {
                        GQTHelper.getInstance().getCallEngine().answerCall(CallType.VOICECALL, "");
                    } else if (msg.arg1 == CallType.SENDONLY_VIDEOCALL) {
                        VideoSizeSetting.getVideoSizeSetting(VideoManagerService.ACTION_VIDEO_MONITOR).setVideoSize();
                        startService(new Intent(GroupCallActivity.this, MonitorServer.class));
                    } else if (msg.arg1 == CallType.UPLOADVIDEO || msg.arg1 == CallType.MONITORVIDEO || msg.arg1 == CallType.DISPATCH) {
                        Intent videoIntent = new Intent(GroupCallActivity.this, TranscribeActivity.class);
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
                    Toast.makeText(GroupCallActivity.this, (String) msg.obj, Toast.LENGTH_SHORT).show();
                    break;
                case 98:
                    switch ((Integer) msg.obj) {
                        case 480:
                            Toast.makeText(GroupCallActivity.this, "用户不在线或无人接听", Toast.LENGTH_SHORT).show();
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
            if (convertView == null) {
                convertView = layoutInflater.inflate(
                        R.layout.aa_list_item_group_name, null);
            }
            TextView tv1 = (TextView) convertView
                    .findViewById(R.id.aa_list_item_groupname);
            length = pttGrps.size();
            int a = group_name_list.getHeight();
            if (length >= 3) {
                tv1.setHeight(a / 3);
            } else if (length == 2) {
                tv1.setHeight(a / 2);
            } else if (length == 1) {
                tv1.setHeight(a);
            }
            if (groupEngine.getCurGrp() != null && pttGrps.get(position) != null && groupEngine.getCurGrp() != null
                    && (groupEngine.getCurGrp().equals(pttGrps.get(position)))) {
                convertView.setBackgroundResource(R.color.font_color);
                tv1.setTextColor(getResources().getColor(R.color.black));
            } else {
                convertView.setBackgroundResource(R.color.font_color2);
                tv1.setTextColor(getResources().getColor(R.color.white));
            }

            groupName = pttGrps.get(position).getGrpName();
            groupName = formatGroupName(groupName, length);
            tv1.setText(groupName);
            return convertView;
        }
    }

    String formatGroupName(String grpName, int grpCount) {
        String result = grpName;
        if (grpName != null) {
            if (grpCount == 1) {
                if (grpName.length() > 12) {
                    result = grpName.substring(0, 12);
                    result = addReturn(result) + "\n...";
                } else {
                    result = addReturn(result);
                }
            } else if (grpCount == 2) {
                if (grpName.length() > 6) {
                    result = grpName.substring(0, 6);
                    result = addReturn(result) + "\n...";
                } else {
                    result = addReturn(result);
                }
            } else if (grpCount > 2) {
                if (grpName.length() > 4) {
                    result = grpName.substring(0, 2);
                    result = addReturn(result) + "\n...";
                } else {
                    result = addReturn(result);
                }
            }
        }
        return result;
    }

    String addReturn(String s) {
        String addResult = "";
        for (int i = 0; i < s.length(); i++) {
            addResult += s.charAt(i) + "\n";
        }
        if (addResult.length() > 1)
            addResult = addResult.substring(0, addResult.length() - 1);// 去掉最后一个换行
        return addResult;
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

    public String ShowPttStatus(int pttState) {
        switch (pttState) {
            case GroupState.CLOSED:
                return "关闭";
            case GroupState.IDLE:
                return "空闲";
            case GroupState.TALKING:
                return "讲话";
            case GroupState.LISTEN:
                return "听讲";
            case GroupState.QUEUE:
                return "排队";
            case GroupState.REQUESTING:
                return "发起中";
        }
        return "错误";
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

    private synchronized void updateMemberList(ArrayList<GrpMember> list) {
        if (mGroupMemberAdapter != null) {
            mGroupMemberAdapter.refreshList(list);
            mGroupMemberAdapter.notifyDataSetChanged();
        }
    }

    private void stopCurrentAnimation() {
        if (mLoadingAnimation != null) {
            mLoadingAnimation.stopAnimation();
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

                    tv_group_speaker
                            .setText(ShowSpeakerStatus(((PttGroup) (msg.obj)).getCurSpeakerName(), ((PttGroup) (msg.obj)).getCurSpeakerNum()));
                    tv_group_status.setText(mStatus
                            + ShowPttStatus(((PttGroup) (msg.obj)).getCurState()));
                    if ((grp.getCurState() == GroupState.REQUESTING)) {
                        mLoadingAnimation = new LoadingAnimation();
                        mLoadingAnimation.setAppendCount(3).startAnimation(
                                tv_group_status);
                    }
                }
                break;
                case GroupChanged: {
                    PttGroup grp = (PttGroup) (msg.obj);
                    if (grp == null) {
                        group_name_title.setText("对讲");
                        tv_group_status.setText(mStatus);
                        tv_group_speaker.setText("无");
                        if (mGroupNameAdapter != null) {
                            mGroupNameAdapter.refreshNameList(null);
                            mGroupNameAdapter.notifyDataSetChanged();
                        }
                        if (mGroupMemberAdapter != null) {
                            mGroupMemberAdapter.refreshList(null);
                            mGroupMemberAdapter.notifyDataSetChanged();
                        }
                    } else {
                        group_name_title.setText(grp.getGrpName());
                        tv_group_speaker.setText(ShowSpeakerStatus(grp.getCurSpeakerName(),
                                grp.getCurSpeakerNum()));
                        tv_group_status.setText(mStatus + ShowPttStatus(grp.getCurState()));
                        if (mGroupNameAdapter != null) {
                            mGroupNameAdapter.notifyDataSetChanged();
                        }
                    }
                }
                break;
                case GroupListChanged: {
                    List<PttGroup> groups = (List<PttGroup>) (msg.obj);
                    mPttGroups = groups;
                    if (mGroupNameAdapter != null) {
                        mGroupNameAdapter.refreshNameList(groups);
                        mGroupNameAdapter.notifyDataSetChanged();
                    }
                }
                break;
                case GroupMemChanged:
                    updateMemberList((ArrayList<GrpMember>) msg.obj);
                    break;
                case GroupIncoming:
                    //		Toast.makeText(GroupCallActivity.this, "组来电，组名称:"+((PttGroup)msg.obj).getGrpName(),Toast.LENGTH_SHORT).show();
                    final PttGroup incomingGroup = (PttGroup) msg.obj;
                    Intent intent = new Intent(GroupCallActivity.this, GroupIncomingNotifyActivity.class);
                    intent.putExtra("incomingGroupNum", incomingGroup.getGrpNum());
                    intent.putExtra("incomingGroupName", incomingGroup.getGrpName());
                    startActivity(intent);
                    break;
                case 8:
                    Toast.makeText(GroupCallActivity.this, (String) msg.obj, Toast.LENGTH_LONG).show();
                    break;
            }
        }

        ;
    };

    private static final int GroupStatusChanged = 1;
    private static final int GroupChanged = 2;
    private static final int GroupListChanged = 3;
    private static final int GroupMemChanged = 4;
    private static final int GroupIncoming = 5;
    boolean showMemList = false;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.new_open_close:
                showMemList = !showMemList;
                if (showMemList) {
                    group_member_list.setVisibility(View.VISIBLE);
                    new_down_up.setImageDrawable(getResources().getDrawable(
                            R.drawable.new_down));
                    groupEngine.getGrpMembers(groupEngine.getCurGrp());
                } else {
                    group_member_list.setVisibility(View.GONE);
                    new_down_up.setImageDrawable(getResources().getDrawable(
                            R.drawable.new_up));
                }

                break;
            case R.id.tmp_group_button:
                makeTempGrpCall();
                break;

            case R.id.custom_group_button:
                createPop(v);
                break;
            default:
                break;
        }

    }

    private void createPop(View v) {
        if (mCustomPopWindow == null) {
            View contentView = LayoutInflater.from(this).inflate(R.layout.pop_menu, null);
            //处理popWindow 显示内容
            handleLogic(contentView);
            //创建并显示popWindow
            mCustomPopWindow = new CustomPopWindow.PopupWindowBuilder(this)
                    .setView(contentView)
                    .create()
                    .showAsDropDown(v, 0, 20);
        } else {
            mCustomPopWindow.showAsDropDown(v, 0, 20);
        }
        PttGroup pttGroup = groupEngine.getCurGrp();
        if (pttGroup != null && pttGroup.getType() == 1) {
            if (groupEngine.getCustomCreatorNum(pttGroup.getGrpNum()).equals(Constant.userName)) {
                modify.setVisibility(View.VISIBLE);
                add.setVisibility(View.VISIBLE);
                del.setVisibility(View.VISIBLE);
                destroy.setVisibility(View.VISIBLE);
                exit.setVisibility(View.GONE);
            } else {
                add.setVisibility(View.GONE);
                modify.setVisibility(View.GONE);
                del.setVisibility(View.GONE);
                destroy.setVisibility(View.GONE);
                exit.setVisibility(View.VISIBLE);
            }
        } else {
            add.setVisibility(View.GONE);
            modify.setVisibility(View.GONE);
            del.setVisibility(View.GONE);
            destroy.setVisibility(View.GONE);
            exit.setVisibility(View.GONE);
        }
    }

    /**
     * 处理弹出显示内容、点击事件等逻辑
     *
     * @param contentView
     */
    private void handleLogic(View contentView) {
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCustomPopWindow != null) {
                    mCustomPopWindow.dissmiss();
                }
                String showContent = "";
                PttGroup pttGroup = groupEngine.getCurGrp();
                switch (v.getId()) {
                    case R.id.create:
                        showContent = "create";
                        makeCustomGrpCall(CustomGroupType.CREATE);
                        break;
                    case R.id.modify:
                        showContent = "modify";
                        makeCustomGrpCall(CustomGroupType.MODIFY);
                        break;
                    case R.id.add:
                        showContent = "add";
                        startGroupActivity(CustomGroupType.ADD, pttGroup.getGrpName(), pttGroup.getGrpNum());
                        break;
                    case R.id.del:
                        showContent = "del";
                        startGroupActivity(CustomGroupType.DELETE, pttGroup.getGrpName(), pttGroup.getGrpNum());
                        break;
                    case R.id.destroy:
                        showContent = "destroy";
                        GQTHelper.getInstance().getGroupEngine().SendCustomGroupMessage(CustomGroupType.DESTROY, pttGroup.getGrpName(), pttGroup.getGrpNum(), null);
                        break;
                    case R.id.exit:
                        showContent = "exit";
                        GQTHelper.getInstance().getGroupEngine().SendCustomGroupMessage(CustomGroupType.EXIT_CURRENT_CUSTOM_GROUP, pttGroup.getGrpName(), pttGroup.getGrpNum(), null);
                        break;
                }
                Toast.makeText(GroupCallActivity.this, showContent, Toast.LENGTH_SHORT).show();
            }
        };
        create = (TextView) contentView.findViewById(R.id.create);
        modify = (TextView) contentView.findViewById(R.id.modify);
        add = (TextView) contentView.findViewById(R.id.add);
        del = (TextView) contentView.findViewById(R.id.del);
        destroy = (TextView) contentView.findViewById(R.id.destroy);
        exit = (TextView) contentView.findViewById(R.id.exit);

        create.setOnClickListener(listener);
        modify.setOnClickListener(listener);
        add.setOnClickListener(listener);
        del.setOnClickListener(listener);
        destroy.setOnClickListener(listener);
        exit.setOnClickListener(listener);
    }

    private void makeCustomGrpCall(final CustomGroupType type) {
        // TODO Auto-generated method stub
        final EditText et_name = new EditText(this);
        et_name.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT));
        //et_name.setHint(R.string.temp_group_call_name);
        Dialog dialog = new AlertDialog.Builder(this)
                .setTitle(R.string.make_temp_group_call)
                .setIcon(R.drawable.icon64)
                .setView(et_name)
                .setPositiveButton(R.string.ok,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                if (!TextUtils.isEmpty(et_name.getText().toString().trim())) {
                                    setDialogClosable(dialog, true);
                                    if (type == CustomGroupType.CREATE) {
                                        startGroupActivity(CustomGroupType.CREATE, et_name.getText().toString().trim(), null);
                                    } else {
                                        PttGroup pttGroup = groupEngine.getCurGrp();
                                        GQTHelper.getInstance().getGroupEngine().SendCustomGroupMessage(CustomGroupType.MODIFY, et_name.getText().toString().trim(), pttGroup.getGrpNum(), null);
                                    }

                                } else {
                                    Toast.makeText(GroupCallActivity.this, R.string.input_tmpgrp_tip, Toast.LENGTH_SHORT).show();
                                    setDialogClosable(dialog, false);
                                }
                            }
                        })
                .setNegativeButton(R.string.cancel,
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int arg1) {
                                setDialogClosable(dialog, true);
                            }
                        }).create();
        dialog.show();
    }

    private void startGroupActivity(CustomGroupType type, String groupName, String groupNum) {
        Intent intent = new Intent();
        intent.putExtra("type",
                type);
        intent.putExtra("custom_grp_name",
                groupName);
        intent.putExtra("custom_grp_num",
                groupNum);
        intent.setClass(GroupCallActivity.this,
                SelectPersonsActivity.class);
        startActivity(intent);
    }

    /**
     * 打开临时对讲对话框
     */
    public void makeTempGrpCall() {
        final EditText et_name = new EditText(this);
        et_name.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT));
        //et_name.setHint(R.string.temp_group_call_name);
        //设置弹出提示框时默认会有临时对讲名称  jibingeng 2015-09-23
        et_name.setText(getTemporaryName());
        Dialog dialog = new AlertDialog.Builder(this)
                .setTitle(R.string.make_temp_group_call)
                .setIcon(R.drawable.icon64)
                .setView(et_name)
                .setPositiveButton(R.string.ok,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                if (!TextUtils.isEmpty(et_name.getText().toString().trim())) {
                                    setDialogClosable(dialog, true);
                                    Intent intent = new Intent();
                                    intent.putExtra("tempGroupName",
                                            et_name.getText().toString().trim());
                                    intent.setClass(GroupCallActivity.this,
                                            SelectPersonsActivity.class);
                                    startActivity(intent);
                                } else {
                                    Toast.makeText(GroupCallActivity.this, R.string.input_tmpgrp_tip, Toast.LENGTH_SHORT).show();
                                    setDialogClosable(dialog, false);
                                }
                            }
                        })
                .setNegativeButton(R.string.cancel,
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int arg1) {
                                setDialogClosable(dialog, true);
                            }
                        }).create();
        dialog.show();
    }


    //获取临时对讲名称 jibingeng 2015-09-23
    private String getTemporaryName() {

        return getResources().getString(R.string.ptt_grp) + getTime();

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

    /**
     * 通过反射机制设置Dialog是否可关闭
     *
     * @param dialog
     * @param isClosable
     */
    private void setDialogClosable(DialogInterface dialog, boolean isClosable) {
        try {
            Field field = dialog.getClass()
                    .getSuperclass()
                    .getDeclaredField("mShowing");
            field.setAccessible(true);
            field.set(dialog, isClosable);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    boolean isPaused = false;

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        groupEngine.makeGroupCall(false);
        isPaused = true;
        mBaseVisualizerView.setTimes(-1);
        super.onPause();
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
//		boolean changedflag = new SharedPreferenceTools(this).getFlag();
//		memlistvision = changedflag;
//		Log.v("huangfujian", "changedflag===resume"+changedflag);
        super.onResume();
        groupEngine.regGroupEngineListener(this);
    }

    @Override
    public void showCurrentVolume(int time) {
        // TODO Auto-generated method stub
        mBaseVisualizerView.setTimes(time);
    }

    @Override
    public void onAddressBook(boolean isSuccess) {
        // TODO Auto-generated method stub
        isAddressBook = isSuccess;
//		List<Map<String, String>> list = GQTHelper.getInstance().getGroupEngine().getMembers("mtype = '"+UserType.SVP.convert()+"'");
//		if(list.size()>0){
//			String s="SVP: ";
//			for(Map<String,String> m:list){
//				s+="名称："+m.get("mname")+" 号码："+m.get("number");
//			}
//			myHandler.obtainMessage(8, s).sendToTarget();
//		}

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
        Intent intent = new Intent(GroupCallActivity.this, GroupIncomingNotifyActivity.class);
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
                Toast.makeText(GroupCallActivity.this, result + " 超时！", Toast.LENGTH_SHORT).show();

                break;
            case LEAVE_FAILURE:
            case MODIFY_FAILURE:
            case DESTROY_FAILURE:
                Toast.makeText(GroupCallActivity.this, result + " 失败！原因 :" + showFailureReason(code), Toast.LENGTH_SHORT).show();
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
     * @param context
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
