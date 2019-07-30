package com.hollysmart.park;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gqt.bean.AudioMode;
import com.gqt.bean.CallType;
import com.gqt.helper.GQTHelper;
import com.hollysmart.beans.CallUserBean;
import com.hollysmart.style.StyleAnimActivity;
import com.hollysmart.utils.Utils;
import com.hollysmart.views.CircleView;

import java.util.ArrayList;
import java.util.List;

public class VoiceCallInCallActivity extends StyleAnimActivity {



    private LinearLayout btncacel;
    private BroadcastReceiver br = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
//            if ("com.gqt.hangup".equals(intent.getAction())) {
//                ConferenceCallInCallActivity.this.finish();
//            }
        }
    };

    private RecyclerView recy_view;
    private LocalAdapter localAdapter;
    private List<CallUserBean> localList = new ArrayList<>();

    private ImageView iv_chat_jingyin;
    private ImageView iv_chat_mianti;

    private TextView tv_title;

    private Chronometer mElapsedTime;

    private boolean isPackerLoad=false;
    private boolean isSelence=false;

    boolean isAddressBook = false;
    @Override
    public int layoutResID() {
        return R.layout.activity_voice_call_in_call;
    }

    @Override
    public void findView() {

        btncacel =  findViewById(R.id.layoutGuaDuan);
         findViewById(R.id.ll_jingyin).setOnClickListener(this);
        btncacel.setOnClickListener(this);

        findViewById(R.id.ib_back).setOnClickListener(this);
        findViewById(R.id.spaker).setOnClickListener(this);

        iv_chat_jingyin=findViewById(R.id.iv_chat_jingyin);
        iv_chat_mianti=findViewById(R.id.iv_chat_mianti);
        mElapsedTime = findViewById(R.id.elapsedTime);
        tv_title = findViewById(R.id.tv_title);

        recy_view = findViewById(R.id.recy_view);

    }

    @Override
    public void init() {
        registerReceiver(br, new IntentFilter("com.gqt.hangup"));
        registerReceiver(br, new IntentFilter("com.gqt.conaccept"));
        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            List<CallUserBean> lolList = (List<CallUserBean>) bundle.getSerializable("localList");
            String taskname = bundle.getString("taskname");
            tv_title.setText(taskname);

            localList.addAll(lolList);


            String CallNums = "";

            if (localList != null && localList.size() > 0) {

                for (int i = 0; i < localList.size(); i++) {

                    if (i == 0) {
                        CallNums = localList.get(i).getUid();
                    } else {
                        CallNums = CallNums + " " + localList.get(i).getUid();
                    }
                }


            }


            GQTHelper.getInstance().getCallEngine().makeCall(CallType.CONFERENCE, CallNums);



        }




        LinearLayoutManager layoutManager = new LinearLayoutManager(this );
        layoutManager.setOrientation(GridLayoutManager.HORIZONTAL);
        recy_view.setLayoutManager(layoutManager);

        localAdapter = new LocalAdapter(mContext, localList);

        recy_view.setAdapter(localAdapter);
        if (mElapsedTime != null) {
            mElapsedTime.setBase(SystemClock.elapsedRealtime());
            mElapsedTime.start();
        }


    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {


            case R.id.layoutGuaDuan:

                GQTHelper.getInstance().getCallEngine()
                        .hangupCall(CallType.CONFERENCE, " ");

                this.finish();

                break;
            case R.id.ib_back:

                GQTHelper.getInstance().getCallEngine()
                        .hangupCall(CallType.CONFERENCE, " ");

                this.finish();

                break;
//            case R.id.spaker:
//
//                if (isPackerLoad) {
//
//                    iv_chat_mianti.setImageResource(R.mipmap.chat_video_mianti_img_normal);
//
//                } else {
//
//                    iv_chat_mianti.setImageResource(R.mipmap.chat_video_mianti_img_select);
//
//                }
//                isPackerLoad = !isPackerLoad;
//
//                new Thread(){
//
//                    @Override
//                    public void run() {
//                        // TODO Auto-generated method stub
//                        super.run();
//                        GQTHelper.getInstance().getCallEngine().setAudioConnectMode(AudioMode.SPEAKER);
//                    }
//                }.start();
//                break;
//            case R.id.ll_jingyin:
//
//                if (isSelence) {
//
//                    iv_chat_jingyin.setImageResource(R.mipmap.chat_video_jingyin_img_normal);
//
//                } else {
//
//                    iv_chat_jingyin.setImageResource(R.mipmap.chat_video_jingyin_img_select);
//
//                }
//                isSelence = !isSelence;
//
//                new Thread(){
//
//                    @Override
//                    public void run() {
//                        // TODO Auto-generated method stub
//                        super.run();
//                        GQTHelper.getInstance().getCallEngine().setAudioConnectMode(AudioMode.SPEAKER);
//                    }
//                }.start();
//                break;

            case R.id.spaker:

                if (isPackerLoad) {

                    iv_chat_mianti.setImageResource(R.mipmap.chat_video_mianti_img_normal);

                } else {

                    iv_chat_mianti.setImageResource(R.mipmap.chat_video_mianti_img_select);

                }
                isPackerLoad = !isPackerLoad;

                new Thread(){

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        super.run();
                        if (isPackerLoad) {
                            GQTHelper.getInstance().getCallEngine().setAudioConnectMode(AudioMode.SPEAKER);

                        } else {

                            GQTHelper.getInstance().getCallEngine().setAudioConnectMode(AudioMode.HOOK);
                        }
                    }
                }.start();
                break;
            case R.id.ll_jingyin:

                if (isSelence) {

                    iv_chat_jingyin.setImageResource(R.mipmap.chat_video_jingyin_img_normal);

                } else {

                    iv_chat_jingyin.setImageResource(R.mipmap.chat_video_jingyin_img_select);

                }
                isSelence = !isSelence;

                GQTHelper.getInstance().getCallEngine().mute();

                break;


        }

    }


    @Override
    public void onResume() {
        super.onResume();
//        textview.setText("通话中...");
    }

    @Override
    protected void onDestroy() {
        if (br != null) {
            VoiceCallInCallActivity.this.unregisterReceiver(br);
        }
        if (mElapsedTime != null) {
            mElapsedTime.stop();
        }
        super.onDestroy();
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            GQTHelper.getInstance().getCallEngine()
                    .hangupCall(CallType.CONFERENCE, "xxxx");
        }
        return super.onKeyDown(keyCode, event);
    }


    public class LocalAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private List<CallUserBean> localList ;
        private Context context;

        int[] colors = new int[]{
                getResources().getColor(R.color.chengse),
                getResources().getColor(R.color.bg_lan),
                getResources().getColor(R.color.onLine),
                getResources().getColor(R.color.heise),
                getResources().getColor(R.color.bg_lan2)
        };

        public LocalAdapter(Context context,List<CallUserBean> localList ) {
            this.context = context;
            this.localList = localList;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.local_item,parent,false);
            return new Holder(view);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            CallUserBean callUserBean = localList.get(position);


            final Holder holder1= (Holder)holder;
            String uname = callUserBean.getUname();
            if (!Utils.isEmpty(uname)) {
                String substring = uname.substring(0, 1);

                holder1.cirview.setCenterText(substring);

            }
            int i = position % 5;
            holder1.cirview.setCircleColor(colors[i]);


        }

        @Override
        public int getItemCount() {
            return localList.size();
        }




        public class Holder extends RecyclerView.ViewHolder{

            CircleView cirview;

            public Holder(View view) {
                super(view);
                cirview =  view.findViewById(R.id.cirview);
            }
        }

    }






}
