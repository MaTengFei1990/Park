package com.hollysmart.beans;

import com.hollysmart.interfaces.SelectIF;

import java.io.Serializable;

/**
 * Created by Lenovo on 2018/5/15.标签bean
 */

public class BiaoQianBean implements SelectIF,Serializable {
    private String id;
    private String title;
    private String mastBePositioned;           //":0,
    private String canToTask;           //":0

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String showInfo() {
        return title;
    }

    public String getMastBePositioned() {
        return mastBePositioned;
    }

    public void setMastBePositioned(String mastBePositioned) {
        this.mastBePositioned = mastBePositioned;
    }

    public String getCanToTask() {
        return canToTask;
    }

    public void setCanToTask(String canToTask) {
        this.canToTask = canToTask;
    }
}
