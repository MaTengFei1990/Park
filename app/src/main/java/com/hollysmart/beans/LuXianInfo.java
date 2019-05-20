package com.hollysmart.beans;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;
import java.util.List;

@DatabaseTable(tableName = "luxian")
public class LuXianInfo implements Serializable {

	private static final long serialVersionUID = 1L;

//		 	 "fd_resmodelid":"-1",
//			 "startCoordinate":"0.000000,0.000000,0",
//			 "isUpload":false,
//			 "id":"1557020841365",
//			 "fd_restaskid":"-1",
//			 "fd_resmodelname":"路线",
//			 "fd_restaskname":"数据采集",
//			 "endCoordinate":"-122.032547,37.335275,0",
//			 "name":"路线1557020841",
//			 "lineCoordinates":"0.000000,0.000000,0,09:47 -122.030318,37.332596,0,09:47 -122.030509,37.332412,0,09:47 -122.032547,37.335275,0,09:47"

	@DatabaseField(columnName = "id",id = true)
	private String id;

	@DatabaseField(columnName = "name")
	private String name;

	private List<PointInfo> pointInfos;

	@DatabaseField(columnName = "isOpen")
	public boolean isOpen = false;   //

	private boolean isSelect;

	@DatabaseField(columnName = "fd_resmodelid")
	public String fd_resmodelid;//":"-1","

	@DatabaseField(columnName = "startCoordinate")
	public String startCoordinate;//":"0.000000,0.000000,0","

	@DatabaseField(columnName = "isUpload")
	public String isUpload;//":false,"

	@DatabaseField(columnName = "fd_restaskid")
	public String fd_restaskid;//":"-1","

	@DatabaseField(columnName = "fd_resmodelname")
	public String fd_resmodelname;//":"路线","

	@DatabaseField(columnName = "fd_restaskname")
	public String fd_restaskname;//":"数据采集","

	@DatabaseField(columnName = "endCoordinate")
	public String endCoordinate;//":"-122.032547,37.335275,0","

	@DatabaseField(columnName = "lineCoordinates")
	public String lineCoordinates;   //":"0.000000,0.000000,0,09:47

	@DatabaseField(columnName = "createtime")
	public String createtime;   //":"0.000000,0.000000,0,09:47
	private boolean upload;


	public static long getSerialVersionUID() {
		return serialVersionUID;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<PointInfo> getPointInfos() {
		return pointInfos;
	}

	public void setPointInfos(List<PointInfo> pointInfos) {
		this.pointInfos = pointInfos;
	}

	public boolean isOpen() {
		return isOpen;
	}

	public void setOpen(boolean open) {
		isOpen = open;
	}

	public boolean isSelect() {
		return isSelect;
	}

	public void setSelect(boolean select) {
		isSelect = select;
	}

	public String getFd_resmodelid() {
		return fd_resmodelid;
	}

	public void setFd_resmodelid(String fd_resmodelid) {
		this.fd_resmodelid = fd_resmodelid;
	}

	public String getStartCoordinate() {
		return startCoordinate;
	}

	public void setStartCoordinate(String startCoordinate) {
		this.startCoordinate = startCoordinate;
	}

	public String getIsUpload() {
		return isUpload;
	}

	public void setIsUpload(String isUpload) {
		this.isUpload = isUpload;
	}

	public String getFd_restaskid() {
		return fd_restaskid;
	}

	public void setFd_restaskid(String fd_restaskid) {
		this.fd_restaskid = fd_restaskid;
	}

	public String getFd_resmodelname() {
		return fd_resmodelname;
	}

	public void setFd_resmodelname(String fd_resmodelname) {
		this.fd_resmodelname = fd_resmodelname;
	}

	public String getFd_restaskname() {
		return fd_restaskname;
	}

	public void setFd_restaskname(String fd_restaskname) {
		this.fd_restaskname = fd_restaskname;
	}

	public String getEndCoordinate() {
		return endCoordinate;
	}

	public void setEndCoordinate(String endCoordinate) {
		this.endCoordinate = endCoordinate;
	}

	public String getLineCoordinates() {
		return lineCoordinates;
	}

	public void setLineCoordinates(String lineCoordinates) {
		this.lineCoordinates = lineCoordinates;
	}


	public String getCreatetime() {
		return createtime;
	}

	public void setCreatetime(String createtime) {
		this.createtime = createtime;
	}

}
