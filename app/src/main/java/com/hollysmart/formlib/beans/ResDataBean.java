package com.hollysmart.formlib.beans;

import com.hollysmart.beans.JDPicInfo;
import com.hollysmart.beans.SoundInfo;
import com.hollysmart.formlib.beans.FormModelBean;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;
import java.util.List;


@DatabaseTable(tableName = "jingdian")
public class ResDataBean implements Serializable{

	private static final long serialVersionUID = 1L;


	@DatabaseField(columnName = "id", id = true)
	private String  id;
	@DatabaseField(columnName = "fdTaskId")
	private String fdTaskId;
	@DatabaseField(columnName = "fd_restaskname")
	private String fd_restaskname;
	@DatabaseField(columnName = "fd_resmodelid")
	private String fd_resmodelid;
	@DatabaseField(columnName = "fd_resmodelname")
	private String fd_resmodelname;
	@DatabaseField(columnName = "fd_resdate")
	private String fd_resdate;
	@DatabaseField(columnName = "fdResData")
	private String fdResData;
	@DatabaseField(columnName = "rescode")
	private String rescode;//资源编号
	@DatabaseField(columnName = "fd_resname")
	private String fd_resname;
	@DatabaseField(columnName = "note")
	private String note;
	@DatabaseField(columnName = "createdAt")
	private String createdAt;
	@DatabaseField(columnName = "fd_resposition")
	private String fd_resposition;
	@DatabaseField(columnName = "scope")
	private int scope;
	@DatabaseField(columnName = "isUpload")
	private boolean isUpload;
	@DatabaseField(columnName = "latitude")
	private String latitude;
	@DatabaseField(columnName = "longitude")
	private String longitude;

	@DatabaseField(columnName = "type")
	private String type;

	@DatabaseField(columnName = "fromdata")
	private String FormData;

	@DatabaseField(columnName = "categoryId")
	private String categoryId;

	@DatabaseField(columnName = "number")
	private String number;

	private List<JDPicInfo> pic;

	private List<SoundInfo> audio;


	private FormModelBean formModel;



	public static long getSerialVersionUID() {
		return serialVersionUID;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getFdTaskId() {
		return fdTaskId;
	}

	public void setFdTaskId(String fdTaskId) {
		this.fdTaskId = fdTaskId;
	}

	public String getFd_restaskname() {
		return fd_restaskname;
	}

	public void setFd_restaskname(String fd_restaskname) {
		this.fd_restaskname = fd_restaskname;
	}

	public String getFd_resmodelid() {
		return fd_resmodelid;
	}

	public void setFd_resmodelid(String fd_resmodelid) {
		this.fd_resmodelid = fd_resmodelid;
	}

	public String getFd_resmodelname() {
		return fd_resmodelname;
	}

	public void setFd_resmodelname(String fd_resmodelname) {
		this.fd_resmodelname = fd_resmodelname;
	}

	public String getFdResData() {
		return fdResData;
	}

	public void setFdResData(String fdResData) {
		this.fdResData = fdResData;
	}

	public String getRescode() {
		return rescode;
	}

	public void setRescode(String rescode) {
		this.rescode = rescode;
	}

	public String getFd_resname() {
		return fd_resname;
	}

	public void setFd_resname(String fd_resname) {
		this.fd_resname = fd_resname;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public String getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(String createdAt) {
		this.createdAt = createdAt;
	}

	public String getFd_resposition() {
		return fd_resposition;
	}

	public void setFd_resposition(String fd_resposition) {
		this.fd_resposition = fd_resposition;
	}


	public int getScope() {
		return scope;
	}

	public void setScope(int scope) {
		this.scope = scope;
	}

	public List<JDPicInfo> getJdPicInfos() {
		return pic;
	}

	public void setJdPicInfos(List<JDPicInfo> pic) {
		this.pic = pic;
	}


	public boolean isUpload() {
		return isUpload;
	}

	public void setUpload(boolean upload) {
		isUpload = upload;
	}


	public FormModelBean getFormModel() {
		return formModel;
	}

	public void setFormModel(FormModelBean formModel) {
		this.formModel = formModel;
	}

	public String getFd_resdate() {
		return fd_resdate;
	}

	public void setFd_resdate(String fd_resdate) {
		this.fd_resdate = fd_resdate;
	}


	public String getLatitude() {
		return latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	public String getLongitude() {
		return longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

	public List<JDPicInfo> getPic() {
		return pic;
	}

	public void setPic(List<JDPicInfo> pic) {
		this.pic = pic;
	}

	public List<SoundInfo> getAudio() {
		return audio;
	}

	public void setAudio(List<SoundInfo> audio) {
		this.audio = audio;
	}


	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}


	public String getFormData() {
		return FormData;
	}

	public void setFormData(String formData) {
		FormData = formData;
	}


	public String getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(String categoryId) {
		this.categoryId = categoryId;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}
}
