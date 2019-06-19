package com.hollysmart.groupcall;

import com.gqt.helper.GQTHelper;
import com.gqt.video.VideoManagerService;

import java.util.ArrayList;
import java.util.List;


public class VideoSizeSetting {

	private static List<VideoSizeSetting> vList=new ArrayList<VideoSizeSetting>();
	private String mCallType;
	private boolean cameraFacedFront = GQTHelper.getInstance().getSetEngine().isCameraFacedFront();
	private boolean colorCorrect = GQTHelper.getInstance().getSetEngine().isColorCorrect();
	private String resolutionStr= GQTHelper.getInstance().getSetEngine().getResolutionStr();
	private String screen_type= GQTHelper.getInstance().getSetEngine().getScreenDir();
	private String screenPro= GQTHelper.getInstance().getSetEngine().getScreenPro();
	
	public boolean isCameraFacedFront() {
		return cameraFacedFront;
	}

	public void setCameraFacedFront(boolean cameraFacedFront) {
		this.cameraFacedFront = cameraFacedFront;
	}

	public boolean isColorCorrect() {
		return colorCorrect;
	}

	public void setColorCorrect(boolean colorCorrect) {
		this.colorCorrect = colorCorrect;
	}

	public String getResolutionStr() {
		return resolutionStr;
	}

	public void setResolutionStr(String resolutionStr) {
		this.resolutionStr = resolutionStr;
	}

	public String getScreen_type() {
		return screen_type;
	}

	public void setScreen_type(String screen_type) {
		this.screen_type = screen_type;
	}

	public String getScreenPro() {
		return screenPro;
	}

	public void setScreenPro(String screenPro) {
		this.screenPro = screenPro;
	}

	private VideoSizeSetting(String callType){
		mCallType = callType;
	}
	
	public String getmCallType() {
		return mCallType;
	}

	public static VideoSizeSetting getVideoSizeSetting(String callType){
		if(vList.size()==0){
			vList.add(new VideoSizeSetting(VideoManagerService.ACTION_VIDEO_CALL));
			vList.add(new VideoSizeSetting(VideoManagerService.ACTION_VIDEO_UPLOAD));
			vList.add(new VideoSizeSetting(VideoManagerService.ACTION_VIDEO_MONITOR));
			vList.add(new VideoSizeSetting(VideoManagerService.ACTION_VIDEO_DISPATCH));
		}
		for(VideoSizeSetting vss:vList){
			
			if(vss.getmCallType().equals(callType)){
				return vss;
			}
		}
		return null;
	}
	
	public void setVideoSize(){
		GQTHelper.getInstance().getSetEngine().setScreenDir(this.getScreen_type());
		GQTHelper.getInstance().getSetEngine().setScreenPro(this.getScreenPro());
		GQTHelper.getInstance().getSetEngine().setCameraFacedFront(this.isCameraFacedFront());
		GQTHelper.getInstance().getSetEngine().setColorCorrect(this.isColorCorrect());
		GQTHelper.getInstance().getSetEngine().setResolutionStr(this.getResolutionStr());
	}
	
}
