package com.hollysmart.beans;


/**
 * Created by Lenovo on 2019/4/4.
 */

public class StateBean {



    private String title;
    private int State;

    private boolean isSelect;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getState() {
        return State;
    }

    public void setState(int state) {
        State = state;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }
}
