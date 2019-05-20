package com.hollysmart.beans;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

public class PointInfo implements Serializable {
	
	private static final long serialVersionUID = 1L;

	private double latitude;

	private double longitude;

	private String time;

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	
	public JSONObject getJsonObject(){
		try {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("latitude", latitude);
			jsonObject.put("longitude", longitude);
			jsonObject.put("time", time);
			return jsonObject;
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return null;
	}
}











