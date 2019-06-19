package com.hollysmart.voicecall;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Selection;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Chronometer;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.gqt.bean.AudioMode;
import com.gqt.bean.CallType;
import com.gqt.helper.GQTHelper;
import com.hollysmart.park.MyBluetoothManager;
import com.hollysmart.park.R;
import com.hollysmart.tools.SharedPreferenceTools;

import java.util.HashMap;

public class VoiceCallInCallActivity extends Activity implements OnClickListener {
	private TextView nummber;
	LinearLayout hangupLine;
	Chronometer mElapsedTime;
	ToggleButton silence;
	ImageView keyboardSet;
	private int flag = 1;
	RadioGroup mAudioModeGroup;

	private EditText numTxt = null;
	private ImageButton btnone = null;
	private ImageButton btntwo = null;
	private ImageButton btnthree = null;
	private ImageButton btnfour = null;
	private ImageButton btnfive = null;
	private ImageButton btnsix = null;
	private ImageButton btnseven = null;
	private ImageButton btnenight = null;
	private ImageButton btnnine = null;
	private ImageButton btn0 = null;
	private ImageButton btnmi = null;
	private ImageButton btnjing = null;
	private ImageButton btndel = null;

	private BroadcastReceiver br = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			if ("com.gqt.hangup".equals(intent.getAction())) {
				VoiceCallInCallActivity.this.finish();
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.callaccept);
		init();
		String callnumber = new SharedPreferenceTools(this).getValues();
		Log.v("huangfujian", "callnumber=" + callnumber);
		nummber.setText(callnumber);
		registerReceiver(br, new IntentFilter("com.gqt.hangup"));
		if (mElapsedTime != null) {
			mElapsedTime.setBase(SystemClock.elapsedRealtime());
			mElapsedTime.start();
		}
		silence.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
				GQTHelper.getInstance().getCallEngine().mute();
			}
		});

	}

	Thread t;
	boolean running = false;

	protected void onResume() {
		if (t == null) {
			numTxt.setText("");
			numTxt.setInputType(InputType.TYPE_NULL);
			running = true;
			(t = new Thread() {
				public void run() {
					int len = 0;
					long time;
					for (;;) {
						if (!running) {
							t = null;
							break;
						}
						if (len != numTxt.getText().length()) {
							time = SystemClock.elapsedRealtime();

							if (numTxt.getText().length() > (len)) {
								GQTHelper
										.getInstance()
										.getCallEngine()
										.sendDTMFINFO(
												numTxt.getText().charAt(len++),
												250);
							}

							time = 250 - (SystemClock.elapsedRealtime() - time);

							try {
								if (time > 0)
									sleep(time);
							} catch (InterruptedException e) {
							}

							try {
								if (running)
									sleep(250);
							} catch (InterruptedException e) {
							}
							continue;
						}

						try {
							sleep(1000);
						} catch (InterruptedException e) {
						}
					}

				}
			}).start();
		}
		super.onResume();
	};

	OnClickListener keyboardsetListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			if (findViewById(R.id.keyboard_layout).getVisibility() == View.VISIBLE) {
				findViewById(R.id.keyboard_layout).setVisibility(View.GONE);
				keyboardSet
						.setImageResource(R.mipmap.ic_dialpad_holo_dark_hide);
			} else {
				findViewById(R.id.keyboard_layout).setVisibility(View.VISIBLE);
				keyboardSet
						.setImageResource(R.mipmap.ic_dialpad_holo_dark_show);
			}
		}
	};

	private void initKeyBoard() {

		numTxt = (EditText) findViewById(R.id.p_digits);
		numTxt.setText("");
		numTxt.setCursorVisible(false);
		// 设置文本框输入字数上限；
		numTxt.setFilters(new InputFilter[] { new InputFilter.LengthFilter(1000) });
		numTxt.setDrawingCacheEnabled(true);
		//
		btnjing = (ImageButton) findViewById(R.id.pjing);
		btnjing.setOnClickListener(this);
		btnone = (ImageButton) findViewById(R.id.pone);
		btnone.setOnClickListener(this);
		//
		btntwo = (ImageButton) findViewById(R.id.ptwo);
		btntwo.setOnClickListener(this);
		//
		btnthree = (ImageButton) findViewById(R.id.pthree);
		btnthree.setOnClickListener(this);
		//
		btnfour = (ImageButton) findViewById(R.id.pfour);
		btnfour.setOnClickListener(this);
		//
		btnfive = (ImageButton) findViewById(R.id.pfive);
		btnfive.setOnClickListener(this);
		//
		btnsix = (ImageButton) findViewById(R.id.psix);
		btnsix.setOnClickListener(this);
		//
		btnseven = (ImageButton) findViewById(R.id.pseven);
		btnseven.setOnClickListener(this);
		//
		btnenight = (ImageButton) findViewById(R.id.penight);
		btnenight.setOnClickListener(this);
		//
		btnnine = (ImageButton) findViewById(R.id.pnine);
		btnnine.setOnClickListener(this);
		//
		btn0 = (ImageButton) findViewById(R.id.p0);
		btn0.setOnClickListener(this);
		//
		btnmi = (ImageButton) findViewById(R.id.pmi);
		btnmi.setOnClickListener(this);

		// 删除
		btndel = (ImageButton) findViewById(R.id.pdel);
		btndel.setOnClickListener(this);

		InitTones();
	}

	private void init() {
		initKeyBoard();
		nummber = (TextView) this.findViewById(R.id.callnum);
		hangupLine = (LinearLayout) findViewById(R.id.hangupline);
		mElapsedTime = (Chronometer) findViewById(R.id.elapsedTime);
		mAudioModeGroup = (RadioGroup)findViewById(R.id.audiomode);
		switch (GQTHelper.getInstance().getCallEngine().getAudioMode()) {
		case HOOK:
			mAudioModeGroup.check(R.id.hook);
			break;
		case SPEAKER:
			mAudioModeGroup.check(R.id.speaker);
			break;
		case BLUETOOTH:
			mAudioModeGroup.check(R.id.bluetooth);
			break;

		default:
			break;
		}
		silence = (ToggleButton) this.findViewById(R.id.jingyin);
		keyboardSet = (ImageView) findViewById(R.id.keyboard_show);
		keyboardSet.setOnClickListener(keyboardsetListener);
		findViewById(R.id.pphone).setVisibility(View.GONE);
		findViewById(R.id.video_call).setVisibility(View.GONE);
		hangupLine.setOnClickListener(btnoutendlistener);
		mAudioModeGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(RadioGroup arg0, int arg1) {
				// TODO Auto-generated method stub
				switch (arg1) {
				case R.id.hook:
					GQTHelper.getInstance().getCallEngine().setAudioConnectMode(AudioMode.HOOK);
					break;
				case R.id.speaker:
					new Thread(){

						@Override
						public void run() {
							// TODO Auto-generated method stub
							super.run();
							GQTHelper.getInstance().getCallEngine().setAudioConnectMode(AudioMode.SPEAKER);
						}
					}.start();
					break;
				case R.id.bluetooth:
					if(MyBluetoothManager.getInstance(VoiceCallInCallActivity.this).isconnect())
					GQTHelper.getInstance().getCallEngine().setAudioConnectMode(AudioMode.BLUETOOTH);
					break;
				default:
					break;
				}
			}
		});
	}

	OnClickListener btnoutendlistener = new OnClickListener() {

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			GQTHelper.getInstance().getCallEngine()
					.hangupCall(CallType.VIDEOCALL, "xxxx");
			
			VoiceCallInCallActivity.this.finish();
		}
	};

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			GQTHelper.getInstance().getCallEngine()
					.hangupCall(CallType.VOICECALL, "xxxx");
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onDestroy() {
		running=false;
		if (br != null) {
			unregisterReceiver(br);
		} else if (mElapsedTime != null) {
			mElapsedTime.stop();
		}
		super.onDestroy();
	}

	public static final HashMap<Character, Integer> mToneMap = new HashMap<Character, Integer>();


	private void InitTones() {
		mToneMap.put('1', ToneGenerator.TONE_DTMF_1);
		mToneMap.put('2', ToneGenerator.TONE_DTMF_2);
		mToneMap.put('3', ToneGenerator.TONE_DTMF_3);
		mToneMap.put('4', ToneGenerator.TONE_DTMF_4);
		mToneMap.put('5', ToneGenerator.TONE_DTMF_5);
		mToneMap.put('6', ToneGenerator.TONE_DTMF_6);
		mToneMap.put('7', ToneGenerator.TONE_DTMF_7);
		mToneMap.put('8', ToneGenerator.TONE_DTMF_8);
		mToneMap.put('9', ToneGenerator.TONE_DTMF_9);
		mToneMap.put('0', ToneGenerator.TONE_DTMF_0);
		mToneMap.put('#', ToneGenerator.TONE_DTMF_P);
		mToneMap.put('*', ToneGenerator.TONE_DTMF_S);
		mToneMap.put('d', ToneGenerator.TONE_DTMF_A);


	}


	// 按钮事件触发手动调用此方法
	public void downKey(String key) {
		numTxt.setGravity(Gravity.CENTER);
		numTxt.setText(numTxt.getText().toString().trim() + key);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.pone:
			downKey("1");
			break;
		case R.id.ptwo:
			downKey("2");
			break;
		case R.id.pthree:
			downKey("3");
			break;
		case R.id.pfour:
			downKey("4");
			break;
		case R.id.pfive:
			downKey("5");
			break;
		case R.id.psix:
			downKey("6");
			break;
		case R.id.pseven:
			downKey("7");
			break;
		case R.id.penight:
			downKey("8");
			break;
		case R.id.pnine:
			downKey("9");
			break;

		case R.id.p0:
			downKey("0");
			break;

		case R.id.pmi:
			downKey("*");
			break;

		case R.id.pjing:
			downKey("#");
			break;
		case R.id.pdel:
			delete();
			break;
		}
	}

	private void delete() {

		StringBuffer sb = new StringBuffer(numTxt.getText().toString().trim());
		int index = 0;
		{
			index = numTxt.length();
			if (index > 0) {
				sb = sb.delete(index - 1, index);
			}
		}
		numTxt.setText(sb.toString());
		if (index > 0) {
			Selection.setSelection(numTxt.getText(), index - 1);
		}
		if (numTxt.getText().toString().trim().length() <= 0) {
			numTxt.setCursorVisible(false);
			numTxt.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);// GQT英文版
																		// 2014-8-28
		}
	}

}
