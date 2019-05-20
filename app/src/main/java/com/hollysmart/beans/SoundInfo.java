package com.hollysmart.beans;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

@DatabaseTable(tableName = "auto")
public class SoundInfo implements Serializable {


	private static final long serialVersionUID = 1L;

	@DatabaseField(columnName = "id", generatedId = true)
	private int id;
	@DatabaseField(columnName = "jqId")
	private String jqId;
	@DatabaseField(columnName = "jdId")
	private String jdId;
	@DatabaseField(columnName = "createtime")
	private String createtime;
	@DatabaseField(columnName = "src")
	private String src;
	@DatabaseField(columnName = "filename")
	private String filename;
	@DatabaseField(columnName = "status")
	private boolean status;
	@DatabaseField(columnName = "filePath")
	private String filePath;
	@DatabaseField(columnName = "audioUrl")
	private String audioUrl;
	@DatabaseField(columnName = "isDownLoad")
	private String isDownLoad;
	@DatabaseField(columnName = "isAddFlag")
	private int isAddFlag = 1;//


	public static long getSerialVersionUID() {
		return serialVersionUID;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getJqId() {
		return jqId;
	}

	public void setJqId(String jqId) {
		this.jqId = jqId;
	}

	public String getJdId() {
		return jdId;
	}

	public void setJdId(String jdId) {
		this.jdId = jdId;
	}

	public String getCreatetime() {
		return createtime;
	}

	public void setCreatetime(String createtime) {
		this.createtime = createtime;
	}

	public String getSrc() {
		return src;
	}

	public void setSrc(String src) {
		this.src = src;
	}


	public boolean isStatus() {
		return status;
	}

	public void setStatus(boolean status) {
		this.status = status;
	}


	public String getIsDownLoad() {
		return isDownLoad;
	}

	public void setIsDownLoad(String isDownLoad) {
		this.isDownLoad = isDownLoad;
	}

	public int getIsAddFlag() {
		return isAddFlag;
	}

	public void setIsAddFlag(int isAddFlag) {
		this.isAddFlag = isAddFlag;
	}


	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public String getAudioUrl() {
		return audioUrl;
	}

	public void setAudioUrl(String audioUrl) {
		this.audioUrl = audioUrl;
	}
}
