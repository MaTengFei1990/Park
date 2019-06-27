package com.hollysmart.park;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.hollysmart.adapter.RecordListAdapter;
import com.hollysmart.beans.ProjectBean;
import com.hollysmart.beans.SoundInfo;
import com.hollysmart.park.R;
import com.hollysmart.record.RecordAudioButton;
import com.hollysmart.record.RecordVoicePopWindow;
import com.hollysmart.style.StyleAnimActivity;
import com.hollysmart.utils.Utils;
import com.hollysmart.value.Values;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import hollysmart.com.audiolib.AudioPlayManager;
import hollysmart.com.audiolib.AudioRecordManager;
import hollysmart.com.audiolib.IAudioPlayListener;
import hollysmart.com.audiolib.IAudioRecordListener;
import kr.co.namee.permissiongen.PermissionGen;


public class RecordListActivity extends StyleAnimActivity {

    private static final int MAX_VOICE_TIME = 20;
    private  String AUDIO_DIR_NAME;

    @Override
    public int layoutResID() {
        return R.layout.activity_record_list;
    }


    private RecyclerView recycler_record;
    private LinearLayout mRoot;

    private RecordAudioButton btn_record;

    private File mAudioDir;

    private RecordVoicePopWindow mRecordVoicePopWindow;
    private List<SoundInfo> soundInfoList = new ArrayList<>();
    private List<SoundInfo> netaudios = new ArrayList<>();

    private RecordListAdapter mAdapter;

    private List<SoundInfo> deletlist;

    private String TempSoundfile; //临时文件的路径


    @Override
    public void findView() {

        recycler_record = findViewById(R.id.recycler_record);

        btn_record = findViewById(R.id.btn_record);
        mRoot = findViewById(R.id.ll_content);
        btn_record.setOnClickListener(this);

        findViewById(R.id.ib_back).setOnClickListener(this);
        findViewById(R.id.iv_shure).setOnClickListener(this);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this );
        recycler_record.setLayoutManager(layoutManager);
        deletlist = new ArrayList<>();

    }


    private void initVoice() {
        mAudioDir = new File(AUDIO_DIR_NAME);
        if (!mAudioDir.exists()) {
            mAudioDir.mkdirs();
        }
        AudioRecordManager.getInstance(mContext).setAudioSavePath(mAudioDir.getAbsolutePath());
        AudioRecordManager.getInstance(this).setMaxVoiceDuration(MAX_VOICE_TIME);
        btn_record.setOnVoiceButtonCallBack(new RecordAudioButton.OnVoiceButtonCallBack() {
            @Override
            public void onStartRecord() {
                AudioRecordManager.getInstance(mContext).startRecord();
            }

            @Override
            public void onStopRecord() {
                AudioRecordManager.getInstance(mContext).stopRecord();
            }

            @Override
            public void onWillCancelRecord() {
                AudioRecordManager.getInstance(mContext).willCancelRecord();
            }

            @Override
            public void onContinueRecord() {
                AudioRecordManager.getInstance(mContext).continueRecord();
            }
        });
        AudioRecordManager.getInstance(this).setAudioRecordListener(new IAudioRecordListener() {
            @Override
            public void initTipView() {
                if (mRecordVoicePopWindow == null) {
                    mRecordVoicePopWindow = new RecordVoicePopWindow(mContext);
                }
                mRecordVoicePopWindow.showAsDropDown(mRoot);
            }

            @Override
            public void setTimeoutTipView(int counter) {
                if (mRecordVoicePopWindow != null) {
                    mRecordVoicePopWindow.showTimeOutTipView(counter);
                }
            }

            @Override
            public void setRecordingTipView() {
                if (mRecordVoicePopWindow != null) {
                    mRecordVoicePopWindow.showRecordingTipView();
                }
            }

            @Override
            public void setAudioShortTipView() {
                if (mRecordVoicePopWindow != null) {
                    mRecordVoicePopWindow.showRecordTooShortTipView();
                }
            }

            @Override
            public void setCancelTipView() {
                if (mRecordVoicePopWindow != null) {
                    mRecordVoicePopWindow.showCancelTipView();
                }
            }

            @Override
            public void destroyTipView() {
                if (mRecordVoicePopWindow != null) {
                    mRecordVoicePopWindow.dismiss();
                }
            }

            @Override
            public void onStartRecord() {

            }

            @Override
            public void onFinish(Uri audioPath, int duration) {
                File file = new File(audioPath.getPath());
                if (file.exists()) {
                    Toast.makeText(getApplicationContext(), "录制成功", Toast.LENGTH_SHORT).show();
                    loadData();
                }
            }

            @Override
            public void onAudioDBChanged(int db) {
                if (mRecordVoicePopWindow != null) {
                    mRecordVoicePopWindow.updateCurrentVolume(db);
                }
            }
        });
    }



    private void initData() {
        loadData();
        setAdapter();
    }


    private void loadData() {
        if (mAudioDir.exists()) {
            soundInfoList.clear();

            //获取网络的音频文件；

            soundInfoList.addAll(netaudios);

            File[] files = mAudioDir.listFiles();
            for (File file : files) {
                if (file.getAbsolutePath().endsWith(".mp3")) {

                    SoundInfo soundInfo = new SoundInfo();
                    soundInfo.setFilePath(file.getAbsolutePath());
                    soundInfo.setFilename(file.getName());
                    soundInfoList.add(soundInfo);
                }
            }

            setAdapter();
        }
    }

    private void setAdapter() {
        if (mAdapter == null) {
            mAdapter = new RecordListAdapter(this, soundInfoList,deletlist);

//            mAdapter.setMyOnItemClickListener(new RecordListAdapter.MyOnItemClickListener() {
//                @Override
//                public void myClick(int positon) {
//                    AudioPlayManager.getInstance().stopPlay();
//                    SoundInfo item = soundInfoList.get(positon);
//
//                    if (!Utils.isEmpty(item.getAudioUrl())) {
//                        AudioPlayManager.getInstance().startPlayNetData(mContext, Values.SERVICE_URL_ADMIN+ item.getAudioUrl(), new IAudioPlayListener() {
//                            @Override
//                            public void onStart(Uri var1) {
//                            }
//
//                            @Override
//                            public void onStop(Uri var1) {
//                            }
//
//                            @Override
//                            public void onComplete(Uri var1) {
//                            }
//                        });
//
//
//                    } else if (!Utils.isEmpty(item.getFilePath())) {
//
//                        Uri audioUri = Uri.fromFile(new File(item.getFilePath()));
//
//                        Log.e("LQR", audioUri.toString());
//                        AudioPlayManager.getInstance().startPlay(mContext, audioUri, new IAudioPlayListener() {
//                            @Override
//                            public void onStart(Uri var1) {
//                            }
//
//                            @Override
//                            public void onStop(Uri var1) {
//                            }
//
//                            @Override
//                            public void onComplete(Uri var1) {
//                            }
//                        });
//
//                    }
//
//                }
//            });

            recycler_record.setAdapter(mAdapter);
        } else

        {
            mAdapter.notifyDataSetChanged();
        }
    }












    private List<String> soundpathList = new ArrayList<>();

    @Override
    public void init() {
        requestPermisson();
        TempSoundfile =  getIntent().getStringExtra("TempSoundfile");
        netaudios = (List<SoundInfo>) getIntent().getSerializableExtra("netaudios");

        AUDIO_DIR_NAME = TempSoundfile;

        initVoice();
        initData();

    }



    private void requestPermisson() {
        PermissionGen.with(this)
                .addRequestCode(100)
                .permissions(Manifest.permission.RECORD_AUDIO
                        , Manifest.permission.WRITE_EXTERNAL_STORAGE
                        , Manifest.permission.WAKE_LOCK
                        , Manifest.permission.READ_EXTERNAL_STORAGE)
                .request();
    }


    private final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1;
    private final int MY_PERMISSIONS_REQUEST_READ_AUTORECORD = 2;


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // 权限请求成功的操作
                    CreateDir();
                } else {
                    // 权限请求失败的操作
                    Utils.showToast(RecordListActivity.this, "请在权限管理中设置存储权限，不然会影响正常使用");
                }
                break;


            case MY_PERMISSIONS_REQUEST_READ_AUTORECORD:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                } else {
                    Toast.makeText(this, "请授权使用录音权限", Toast.LENGTH_SHORT)
                            .show();
                }
                break;
        }
    }


    private void CreateDir() {
        CreateDir(Values.SDCARD_DIR);
    }

    private File CreateDir(String folder) {
        File dir = new File(folder);
        dir.mkdirs();
        return dir;
    }





    //递归查找的所有音频文件
    private void soundFile(String strPath,String name) {

        soundpathList =new ArrayList<String>();
        String filename;//文件名
        String suf;//文件后缀
        File dir = new File(strPath);//文件夹dir
        File[] files = dir.listFiles();//文件夹下的所有文件或文件夹

        if (files == null)
            return;

        for (int i = 0; i < files.length; i++) {

            if (files[i].isDirectory())
            {
                soundFile(files[i].getAbsolutePath(),name);//递归文件夹！！！

            }
            else {
                filename = files[i].getName();
                int j = filename.lastIndexOf(".");
                int k=filename.lastIndexOf("-");
                suf = filename.substring(k+1,j);//得到文件后缀

                soundpathList.add(files[i].getName());//对于文件才把它加到list中
            }

        }
    }



    private MediaPlayer mMediaPlayer01 = null;
    private AudioTrack aAudioTrack01 = null;



//    // iChannel = 0 means left channel test, iChannel = 1 means right channel test.
//    private void playSound(String strPath, int iChannel) {
//        // If now is playing...
//        if (aAudioTrack01 != null) {
//            aAudioTrack01.release();
//            aAudioTrack01 = null;
//        }
//        // Get the AudioTrack minimum buffer size
//        int iMinBufSize = AudioTrack.getMinBufferSize(44100,
//                AudioFormat.CHANNEL_CONFIGURATION_STEREO,
//                AudioFormat.ENCODING_PCM_16BIT);
//        if (iMinBufSize == AudioTrack.ERROR_BAD_VALUE || iMinBufSize == AudioTrack.ERROR) {
//            return;
//        }
//        // Constructor a AudioTrack object
//        try {
//            aAudioTrack01 = new AudioTrack(AudioManager.STREAM_MUSIC, 44100,
//                    AudioFormat.CHANNEL_CONFIGURATION_STEREO,
//                    AudioFormat.ENCODING_PCM_16BIT,
//                    iMinBufSize,
//                    AudioTrack.MODE_STREAM);
//        } catch (IllegalArgumentException iae) {
//            iae.printStackTrace();
//        }
//        // Write data to buffer
//        byte data[] = new byte[iMinBufSize];
//        aAudioTrack01.write(data, 0, data.length);
//        aAudioTrack01.write(data, 0, data.length);
//        float lValue = 0;
//        float rValue = 0;
//
//        if (iChannel == 0) {
//            lValue = 1.0f;
//            rValue = 0.0f;
//        } else if (iChannel == 1) {
//            lValue = 0.0f;
//            rValue = 1.0f;
//        }
//
//        aAudioTrack01.play();
//        if (aAudioTrack01.setStereoVolume(lValue, rValue) == AudioTrack.SUCCESS) {
//        }
//        aAudioTrack01.stop();
//        if (aAudioTrack01.setStereoVolume(midVol, midVol) == AudioTrack.SUCCESS) {
//        }
//        aAudioTrack01.release();
//        aAudioTrack01 = null;
//
//    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.ib_back:
                Intent intent1 = new Intent();
                intent1.putExtra("recordlist", (Serializable) soundInfoList);
                intent1.putExtra("deletList", (Serializable) deletlist);
                setResult(5,intent1);
                finish();

                break;
            case R.id.iv_shure:

                Intent intent = new Intent();
                intent.putExtra("recordlist", (Serializable) soundInfoList);
                intent.putExtra("deletList", (Serializable) deletlist);
                setResult(5,intent);
                finish();

                break;
        }


    }

}
