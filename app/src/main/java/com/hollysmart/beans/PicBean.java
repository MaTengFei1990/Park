package com.hollysmart.beans;

import java.io.Serializable;

/**
 * Created by Lenovo on 2018/5/18.
 */

public class PicBean implements Serializable {
    private String id;
    private String path;
    private String urlpath;
    private String isDownLoad;
    private int isAddFlag = 1;//

    public PicBean(String id, String path,String urlpath, int isAddFlag,String isDownLoad) {
        this.id = id;
        this.path = path;
        this.urlpath = urlpath;
        this.isAddFlag = isAddFlag;
        this.isDownLoad = isDownLoad;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getIsAddFlag() {
        return isAddFlag;
    }

    public void setIsAddFlag(int isAddFlag) {
        this.isAddFlag = isAddFlag;
    }

    public String getUrlpath() {
        return urlpath;
    }

    public void setUrlpath(String urlpath) {
        this.urlpath = urlpath;
    }

    public String getIsDownLoad() {
        return isDownLoad;
    }

    public void setIsDownLoad(String isDownLoad) {
        this.isDownLoad = isDownLoad;
    }
}
