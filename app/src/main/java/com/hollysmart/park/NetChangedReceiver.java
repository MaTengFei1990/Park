package com.hollysmart.park;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;

import com.gqt.helper.GQTHelper;
import com.gqt.log.MyLog;
import com.gqt.sipua.SipUAApp;

public class NetChangedReceiver extends BroadcastReceiver {
	
	public static String lastGrpID = "";
	private static boolean networkdown = false;
	private static String networkTypeName = "";
	private static String mobileSubTypeName = "";
	private final String TAG = "NetChangedReceiver";
	static {
		ConnectivityManager connMgr = (ConnectivityManager) SipUAApp.mContext
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeInfo = connMgr.getActiveNetworkInfo();
			if (activeInfo != null) {
				networkTypeName = activeInfo.getTypeName();
				if("mobile".equalsIgnoreCase(networkTypeName)){
					mobileSubTypeName = activeInfo.getSubtypeName();
					MyLog.e("zzhan-3-29", "init subTypeName is :" + mobileSubTypeName);
				}
				MyLog.e("zzhan-3-29", "Initialization networkTypeName is :" + networkTypeName);
			}
	}
	@Override
	public void onReceive(Context context, Intent intent) {
        StringBuilder builder = new StringBuilder("NetChangedReceiver#onReceive");
        String action = intent.getAction();
		if(action.equals("android.net.conn.CONNECTIVITY_CHANGE")){
			builder.append(" android.net.conn.CONNECTIVITY_CHANGE");
			ConnectivityManager connMgr = (ConnectivityManager) SipUAApp.mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
			
			State mobileState = null;
			boolean isMobileAvalilable = false;  
	        boolean isMobileConn = false;
	        boolean isMobile = false; //�ֻ�����
	        NetworkInfo mobileInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
	        if (mobileInfo != null) {
		        mobileState = mobileInfo.getState();  
		        isMobileAvalilable = mobileInfo.isAvailable();  
		        isMobileConn = mobileInfo.isConnected();  
		        isMobile = mobileInfo.isConnectedOrConnecting();  
		        MyLog.e("Receiver", "mobile state is : " + mobileState.toString());
	       	}
	        //isWifiAvalilable = true, isWifiConn = true, isWifi = true, when wifi connected but can not go on the web
		    State wifiState = null;
	        boolean isWifiAvalilable = false;  
	        boolean isWifiConn = false;
	        boolean isWifi = false;//wifi����
	        NetworkInfo wifiInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
	        if (wifiInfo != null) {
		        wifiState = wifiInfo.getState();  
		        isWifiAvalilable = wifiInfo.isAvailable();  
		        isWifiConn = wifiInfo.isConnected();  
		        isWifi = wifiInfo.isConnectedOrConnecting();  
		        MyLog.e("Receiver","wifi state is : " + wifiState.toString());
	        }
	        //active network info  
	        NetworkInfo activeInfo = connMgr.getActiveNetworkInfo();
	        builder.append(" NetWorkInfo"+"activeInfo:"+(activeInfo == null? "null":activeInfo.toString()));
	        MyLog.e("zzhan-3-29", "NetWorkInfo"+"activeInfo:"+(activeInfo == null? "null":activeInfo.toString()));
	        if (activeInfo != null) {
	        	MyLog.e("Receiver", "activeInfo is not null.");
		        String typeName = activeInfo.getTypeName();
		        State activeState = activeInfo.getState();
		        MyLog.e("Receiver", "active network is : " + typeName);
		        MyLog.e("Receiver", "active network state is " + activeState.toString());
		        if (!networkTypeName.equals(typeName)) {
		        	if (!networkdown) {
		        		GQTHelper.getInstance().getRegisterEngine().halt();
		        		MyLog.e("Receiver", "engine halt.");
		        	}
		        	GQTHelper.getInstance().getRegisterEngine().startEngine();
		        	MyLog.e("Receiver", "engine start.");
		        } else {
		        	if("mobile".equalsIgnoreCase(networkTypeName)){
		        		if(!mobileSubTypeName.equalsIgnoreCase(activeInfo.getSubtypeName())){
		        			if (!networkdown) {
		        				GQTHelper.getInstance().getRegisterEngine().halt();
				        		MyLog.e("Receiver", "engine halt.");
				        		networkdown = true;
				        		MyLog.e("zzhan-3-29", "�л��㲥 : halt()"+"last subNet:"+mobileSubTypeName+","+"this subNet:"+activeInfo.getSubtypeName());
				        		mobileSubTypeName = activeInfo.getSubtypeName();
				        	}
		        		}
		        	}
		        	if (networkdown){
		        		if("mobile".equalsIgnoreCase(networkTypeName)){
		        			mobileSubTypeName = activeInfo.getSubtypeName();
		        		}
		        		MyLog.e("zzhan-3-29", "�л��ɹ��㲥: StartEngine()"+"this subNet:"+mobileSubTypeName);
		        		GQTHelper.getInstance().getRegisterEngine().startEngine();
		        		MyLog.e("Receiver", "engine start.");
		        	}
	        	}
		        networkdown = false;
		        networkTypeName = typeName;
	        } else {
	        	MyLog.e("Receiver", "activeInfo is null.");
	        	if (!networkdown) {
	        		GQTHelper.getInstance().getRegisterEngine().halt();
		        	MyLog.e("Receiver", "engine halt.");
		        	 MyLog.e("zzhan-3-29", "�л��ɹ��㲥:activeInfo = null, halt()");
	        	}
	        	networkdown = true;
	        }
		}
		
	}
	
	private static NetChangedReceiver sReceiver;
	
	public synchronized static void registerSelf(){
		
		if(sReceiver==null){
			// --------------------------
			IntentFilter infilter = new IntentFilter();
			infilter.addAction("android.net.wifi.WIFI_STATE_CHANGED");
			infilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
			//���� ��������
			infilter.addAction(Intent.ACTION_SCREEN_ON);
			infilter.addAction(Intent.ACTION_SCREEN_OFF);
			
			NetChangedReceiver receiver = new NetChangedReceiver();
			SipUAApp.getAppContext().registerReceiver(receiver, infilter);
			sReceiver = receiver;
		}
		
	}
	
	public synchronized static void unregisterSelf() {
		if(sReceiver!=null){
			try {
				SipUAApp.getAppContext().unregisterReceiver(sReceiver);
			}catch(Exception e){
			}
			sReceiver = null;
		}
	}
	
}
