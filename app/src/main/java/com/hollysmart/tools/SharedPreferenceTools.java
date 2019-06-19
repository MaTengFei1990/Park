package com.hollysmart.tools;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class SharedPreferenceTools {
	private static SharedPreferences sharedPreferences ;
	private static Context context;
	public SharedPreferenceTools(Context ctx){
		context = ctx;
	}
	
 public void putValues(String number){
	 if(sharedPreferences==null){
	sharedPreferences = context.getSharedPreferences("number", Context.MODE_PRIVATE);
	 }
	Editor editor = sharedPreferences.edit();
	editor.putString("call", number);
	
	editor.commit();
	 
 }
 
public String getValues(){
	if(sharedPreferences==null){
	 sharedPreferences = context.getSharedPreferences("number", Context.MODE_PRIVATE);
	}
	 String number = sharedPreferences.getString("call", "");
	 
	 return number;
 }

public void putBoolean(boolean change){
	 if(sharedPreferences==null){
			sharedPreferences = context.getSharedPreferences("flag", Context.MODE_PRIVATE);
			 }
	 Editor editor = sharedPreferences.edit();
	 editor.putBoolean("changedfalg", change);
}
public Boolean getFlag(){
	if(sharedPreferences==null){
		 sharedPreferences = context.getSharedPreferences("flag", Context.MODE_PRIVATE);
		}
	Boolean flag = sharedPreferences.getBoolean("changedfalg", true);
	return flag;
}
}
