package com.hollysmart.park;


import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.media.SoundPool;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.widget.Toast;

import com.gqt.sipua.SipUAApp;
import com.gqt.utils.LogUtil;

import java.io.InputStream;
import java.util.HashMap;

/**
 * tip sound playing,such as mp3,wav file.
 * @author oumogang
 */
public class TipSoundPlayer {

	private static boolean mIsPlaying;
	protected static String tag = "TipSoundPlayer";
	private AsyncTipSoundHandleThread sTipSoundHandleThread = new AsyncTipSoundHandleThread();
	private AudioTrack mAudioTrack;
	private Object mLock = new Object();
	private HashMap<Integer, Integer> mSoundLoadIDMap = new HashMap<Integer, Integer>();
	private HashMap<Integer, Integer> mSoundLoadIDMap4NormalMode = new HashMap<Integer, Integer>();
	private SoundPool mSoundPool;
	private SoundPool mSoundPool4NormalMode;
	/**
	 * �Ƿ�ʹ�ö��в���
	 * {@link TipSoundPlayer#playWAVTipSound(int)}
	 */
	private boolean mUseSoundQueue = false;
	private AudioManager mAudioManager;
	private TipSoundPlayer() {
		mAudioManager = (AudioManager) SipUAApp.mContext
				.getSystemService(Context.AUDIO_SERVICE);
	}
	/**
	 * init sound pool added by wei.deng  2014-05-21
	 */
	public void init(Context context) {
		Sound[] sounds = Sound.values();
		mSoundPool = new SoundPool(sounds.length, AudioManager.STREAM_VOICE_CALL, 0);
		mSoundPool4NormalMode = new SoundPool(sounds.length, AudioManager.STREAM_MUSIC, 0);
		for (Sound sound : sounds) {
			int id = sound.getResId();
			mSoundLoadIDMap.put(id, mSoundPool.load(context,id,1));
			mSoundLoadIDMap4NormalMode.put(id, mSoundPool4NormalMode.load(context,id,1));
		}
	}
	/**
	 * exit and release SoundPools 
	 */
	public void exit(){
		if(mSoundPool!=null)
			mSoundPool.release();
		if(mSoundPool4NormalMode!=null)
			mSoundPool4NormalMode.release();
	}
	/**
	 * enum Sounds for tipsounds
	 * @author oumogang
	 *
	 */
	public enum Sound {
		PTT_DOWN(R.raw.on8k16bit),
		PTT_UP(R.raw.off8k16bit),
		PTT_ACCEPT(R.raw.pttaccept8k16bit),
		PTT_RELEASE(R.raw.pttrelease8k16bit),
		MESSAGE_ACCEPT(R.raw.imreceive);
		
		private int mSoundId = -1;
		
		private Sound(){
		}
		
		private Sound(int soundId){
			this.mSoundId = soundId;
		}
		
		public int getResId(){
			return mSoundId;
		}
		
		public boolean existSoundResId() {
			return (this.mSoundId!=-1);
		}
	}
	public void play(Sound state){
		if(state.existSoundResId()) {
			playWAVTipSound(state.getResId());
		}else {
			Toast.makeText(SipUAApp.mContext,"sound not found",0).show();
		}
	}

	public static TipSoundPlayer getInstance() {
		return InnerTipSoundPlayer.sDefault;
	}
	
	private static final class InnerTipSoundPlayer {
		public static TipSoundPlayer sDefault = new TipSoundPlayer();
	}
	/**
	 * ��������ʾ��������У����β��š� added by wei.deng  2014-05-21
	 */
	private class AsyncTipSoundHandleThread extends HandlerThread {

		private InnerHanler mHanler;

		public AsyncTipSoundHandleThread() {
			super("BackgroundThread",
					android.os.Process.THREAD_PRIORITY_DEFAULT);
			ensureThreadLocked();
		}
		
		private final class InnerHanler extends Handler {
			public InnerHanler(Looper looper) {
				super(looper);
			}

			@Override
			public void handleMessage(Message msg) {
				if (msg != null) {
					int id = msg.arg1;
					TipSoundPlayer.this.play(id);
				}
			}
		}
		
		private void ensureThreadLocked() {
			start();
			mHanler = new InnerHanler(getLooper());
		}

		public void post(Message msg) {
			synchronized (AsyncTipSoundHandleThread.class) {
				mHanler.sendMessage(msg);
			}
		}
	}
	public static long sPlayBeginTime ;
	public static final int sPlayNeedTime = 400;
	
	/**
	 * wav Tip Sound enqueue
	 */
	private void playWAVTipSound(final int id) {
		mIsPlaying = true;
		if(mUseSoundQueue) {
			synchronized (mLock) {
				
				if(!sTipSoundHandleThread.isAlive() || sTipSoundHandleThread.getState()== Thread.State.TERMINATED) {
					sTipSoundHandleThread = new AsyncTipSoundHandleThread();
				}
				
				Message msg = Message.obtain();
				msg.arg1 = id;
				sTipSoundHandleThread.post(msg);
			}
		} else {
			
			try {
//				if (isSppConnect) {
//					//�Ͱ汾��֧��pa���ƣ���Ҫ��PA_ON �� add by mou 2014-07-30
//					//�Ͱ汾�ɶԽ�״̬������PA���أ���GroupCallStateReceiver.java ����
//					LogUtil.sendSPPMessage(SppMessage.PTT_PA_ON,"TipSoundPlayer#playWAVTipSound()");
//				}
				//use sound pool play  added by wei.deng 2014.05.21
//				int mode = MyAudioManager.getInstance().getMode();
				if (/*mode == AudioManager.MODE_NORMAL*/!mAudioManager.isBluetoothScoOn()) {
					if (!mAudioManager.isBluetoothA2dpOn() && mAudioManager.getMode() != AudioManager.MODE_NORMAL) {
						LogUtil.makeLog(tag, "playWAVTipSound()  mSoundPool.play()");
						mSoundPool.play(mSoundLoadIDMap.get(id),1.0f, 1.0f, 0, 0, 1);
					}else {
						LogUtil.makeLog(tag, "playWAVTipSound()  mSoundPool4NormalMode.play()");
						mSoundPool4NormalMode.play(mSoundLoadIDMap4NormalMode.get(id),1.0f, 1.0f, 0, 0, 1);
					}
				}else {
					LogUtil.makeLog(tag, "playWAVTipSound()  mSoundPool.play()");
					mSoundPool.play(mSoundLoadIDMap.get(id),1.0f, 1.0f, 0, 0, 1);
				}
				sPlayBeginTime = System.currentTimeMillis();
			} finally{	
//				if (isSppConnect) {
//					LogUtil.getInstance().addDelayTask(DelayTaskTypes.TYPE_DELAY_TASK_SEND_SPP_MSG_PA_OFF, 5000,true);
//					sTipSoundHandleThread.mHanler.postDelayed(new Runnable() {
//						
//						@Override
//						public void run() {
//							mIsPlaying = false;
//							LogUtil.getInstance().sendSPPMessage(SppMessage.PTT_PA_OFF,"TipSoundPlayer#playWAVTipSound()");
//						}
//					}, /*2*/10*1000);
//				}
			}
		}
		
	}
	
	private void play(final int id) {

		int m_out_buf_size;
		int channel = AudioFormat.ENCODING_PCM_16BIT;
		int sampleRateInHz = 8000;
		m_out_buf_size = AudioTrack.getMinBufferSize(sampleRateInHz,
				AudioFormat.CHANNEL_OUT_MONO,
				channel);
		int streamType = AudioManager.STREAM_VOICE_CALL;

		InputStream is = SipUAApp.mContext.getResources().openRawResource(id);

		try {
			if(mAudioTrack==null){
				mAudioTrack = new AudioTrack(streamType, sampleRateInHz,
						AudioFormat.CHANNEL_OUT_MONO,
						channel, m_out_buf_size, AudioTrack.MODE_STREAM);
			}
			
			mAudioTrack.play();

			int len;
			byte[] speechBuffer = new byte[320];
			while (true) {
				len = is.read(speechBuffer, 0, 320);
				if (len == -1) {
					mAudioTrack.write(speechBuffer, 0, speechBuffer.length);
					break;
				}
				mAudioTrack.write(speechBuffer, 0, len);
			}

			is.close();

			mAudioTrack.stop();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {

		}
	}

	public void quit() {
		sTipSoundHandleThread.quit();
		if(mAudioTrack!=null){
			mAudioTrack.stop();
			mAudioTrack.release();
			mAudioTrack = null;
		}
	}
	public boolean isPlaying() {
		return mIsPlaying;
	}
}
