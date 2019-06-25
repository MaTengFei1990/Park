package com.hollysmart.beans;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Lenovo on 2019/4/4.
 */
@DatabaseTable(tableName = "projectdb")
public class ProjectBean implements Serializable {

    @DatabaseField(columnName = "id",id = true)
    private String id;

    @DatabaseField(columnName = "userinfoid")
    private String userinfoid;

    @DatabaseField(columnName = "remarks")
    private String remarks;

    @DatabaseField(columnName = "createDate")
    private String createDate;

    @DatabaseField(columnName = "updateDate")
    private String updateDate;

    @DatabaseField(columnName = "fOfficeId")
    private String fOfficeId;

    @DatabaseField(columnName = "fTaskname")
    private String fTaskname;

    @DatabaseField(columnName = "fTaskmodel")
    private String fTaskmodel;

    @DatabaseField(columnName = "fBegindate")
    private String fBegindate;

    @DatabaseField(columnName = "fEnddate")
    private String fEnddate;


    @DatabaseField(columnName = "fRange")  //范围；
    private String fRange;

    @DatabaseField(columnName = "fTaskmodelnames")
    private String fTaskmodelnames;


    @DatabaseField(columnName = "fState")
    private String fState;    // 代办1  进行中2  已完成 3



    @DatabaseField(columnName = "isOpen")
    public boolean isOpen=false;   //

    @DatabaseField(columnName = "fVersion")
    public String  fVersion;

    @DatabaseField(columnName = "fDescription")
    public String  fDescription;


    private  boolean isSelect;


    private int allConunt;

    private int syncCount;

    @DatabaseField(columnName = "netCount")
    private int netCount;


    private List<ResDataBean> unitList; //资源列表





    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getfTaskname() {
        return fTaskname;
    }

    public void setfTaskname(String fTaskname) {
        this.fTaskname = fTaskname;
    }

    public String getfBegindate() {
        return fBegindate;
    }

    public void setfBegindate(String fBegindate) {
        this.fBegindate = fBegindate;
    }

    public String getfEnddate() {
        return fEnddate;
    }

    public void setfEnddate(String fEnddate) {
        this.fEnddate = fEnddate;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }


    public String getfState() {
        return fState;
    }

    public void setfState(String fState) {
        this.fState = fState;
    }


    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }


    public String getfTaskmodel() {
        return fTaskmodel;
    }

    public void setfTaskmodel(String fTaskmodel) {
        this.fTaskmodel = fTaskmodel;
    }


    public String getfRange() {
        return fRange;
    }

    public void setfRange(String fRange) {
        this.fRange = fRange;
    }


    public String getfTaskmodelnames() {
        return fTaskmodelnames;
    }

    public void setfTaskmodelnames(String fTaskmodelnames) {
        this.fTaskmodelnames = fTaskmodelnames;
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


    public String getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(String updateDate) {
        this.updateDate = updateDate;
    }

    public String getfOfficeId() {
        return fOfficeId;
    }

    public void setfOfficeId(String fOfficeId) {
        this.fOfficeId = fOfficeId;
    }


    public List<ResDataBean> getUnitList() {
        return unitList;
    }

    public void setUnitList(List<ResDataBean> unitList) {
        this.unitList = unitList;
    }


    public String getfVersion() {
        return fVersion;
    }

    public void setfVersion(String fVersion) {
        this.fVersion = fVersion;
    }


    public String getfDescription() {
        return fDescription;
    }

    public void setfDescription(String fDescription) {
        this.fDescription = fDescription;
    }


    public int getAllConunt() {
        return allConunt;
    }

    public void setAllConunt(int allConunt) {
        this.allConunt = allConunt;
    }

    public int getSyncCount() {
        return syncCount;
    }

    public void setSyncCount(int syncCount) {
        this.syncCount = syncCount;
    }


    public String getUserinfoid() {
        return userinfoid;
    }

    public void setUserinfoid(String userinfoid) {
        this.userinfoid = userinfoid;
    }

    public int getNetCount() {
        return netCount;
    }

    public void setNetCount(int netCount) {
        this.netCount = netCount;
    }
}