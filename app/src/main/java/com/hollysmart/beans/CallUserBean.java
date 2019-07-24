package com.hollysmart.beans;

import java.io.Serializable;

public class CallUserBean implements Serializable {

    private String Uname;
    private String Uid;


    public String getUname() {
        return Uname;
    }

    public void setUname(String uname) {
        Uname = uname;
    }

    public String getUid() {
        return Uid;
    }

    public void setUid(String uid) {
        Uid = uid;
    }
}
