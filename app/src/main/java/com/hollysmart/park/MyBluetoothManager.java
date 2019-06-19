package com.hollysmart.park;


import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;

import com.gqt.bean.AudioMode;
import com.gqt.bean.GroupState;
import com.gqt.helper.GQTHelper;


/*
 * ����������
 */

@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH) public class MyBluetoothManager {

	public static BluetoothAdapter mBluetoothAdapter = BluetoothAdapter
			.getDefaultAdapter();

		public static MyBluetoothManager sInstance = null;

	public static MyBluetoothManager getInstance(Context context) {
		// TODO Auto-generated method stub
		
		if(sInstance == null){
			sInstance = new MyBluetoothManager();
			context.registerReceiver(bReceiver, makeFilter());
		}
		return sInstance;
	}


	private static IntentFilter makeFilter() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        filter.addAction(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED);
        return filter;
    }
	
	private  static BroadcastReceiver bReceiver = new BroadcastReceiver(){

		@Override
		public void onReceive(Context arg0, Intent arg1) {
			// TODO Auto-generated method stub
			if (arg1.getAction().equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
				receiveBluetoothAdapterState(arg0,arg1);
			}
			else if (arg1.getAction().equals(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED)) {
				receiveConnectionStateAudioState(arg0,arg1);
			}
		}
		
	};
	
	static void receiveConnectionStateAudioState(Context context, Intent intent) {
		int state = intent.getIntExtra(BluetoothAdapter.EXTRA_CONNECTION_STATE,
				-1);

		BluetoothDevice device = intent
				.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
		switch (state) {
		case BluetoothAdapter.STATE_CONNECTING:
			break;
		case BluetoothAdapter.STATE_CONNECTED:
			android.util.Log.e("jiangkai", "���ӳɹ�");
			 GQTHelper.getInstance().getCallEngine().setAudioConnectMode(AudioMode.BLUETOOTH);
			 GQTHelper.getInstance().getCallEngine().setNeedBlueTooth(true);
			break;
		case BluetoothAdapter.STATE_DISCONNECTING:
			break;
		case BluetoothAdapter.STATE_DISCONNECTED:
			android.util.Log.e("jiangkai", "�Ͽ�����");
			 GQTHelper.getInstance().getCallEngine().setNeedBlueTooth(false);
			if(GQTHelper.getInstance().getGroupEngine().getCurGrp().getCurState()== GroupState.CLOSED){
				GQTHelper.getInstance().getCallEngine().setAudioConnectMode(AudioMode.HOOK);
			}else{
				GQTHelper.getInstance().getCallEngine().setAudioConnectMode(AudioMode.SPEAKER);
			}
			break;

		default:
			break;
		}
	}
	
	static void receiveBluetoothAdapterState(Context context, Intent intent) {
		int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1);
		switch (state) {
		case BluetoothAdapter.STATE_TURNING_ON:
			break;
		case BluetoothAdapter.STATE_ON:
			break;
		case BluetoothAdapter.STATE_TURNING_OFF:
			break;
		case BluetoothAdapter.STATE_OFF:
			GQTHelper.getInstance().getCallEngine().setNeedBlueTooth(false);
			if(GQTHelper.getInstance().getGroupEngine().getCurGrp().getCurState()== GroupState.CLOSED){
				GQTHelper.getInstance().getCallEngine().setAudioConnectMode(AudioMode.HOOK);
			}else{
				GQTHelper.getInstance().getCallEngine().setAudioConnectMode(AudioMode.SPEAKER);
			}
			break;
		default:
			break;
		}
	}


	public boolean isconnect() {
		if (mBluetoothAdapter == null) {
			return false; // error
		} else if (mBluetoothAdapter.isEnabled()) {
			int a2dp = mBluetoothAdapter
					.getProfileConnectionState(BluetoothProfile.A2DP); // �ɲٿ������豸�����������ͣ���ܵ���������
			int headset = mBluetoothAdapter
					.getProfileConnectionState(BluetoothProfile.HEADSET); // ����ͷ��ʽ������֧�������������
			int health = mBluetoothAdapter
					.getProfileConnectionState(BluetoothProfile.HEALTH); // ��������ʽ�豸

			// �鿴�Ƿ������Ƿ����ӵ������豸��һ�֣��Դ����ж��Ƿ�������״̬���Ǵ򿪲�û�����ӵ�״̬
			int flag = -1;
			if (a2dp == BluetoothProfile.STATE_CONNECTED) {
				flag = a2dp;
			} else if (headset == BluetoothProfile.STATE_CONNECTED) {
				flag = headset;
			} else if (health == BluetoothProfile.STATE_CONNECTED) {
				flag = health;
			}
			// ˵���������������豸��һ��
			if (flag != -1) {
				return true; // discontinued
			}
		}
		return false;
	}
}
