package com.hollysmart.beans;

import java.io.Serializable;

/**
 * Created by Lenovo on 2018/8/7.
 */

public class UserInfoBean implements Serializable {

    private String UserToken;


    public String getUserToken() {
        return UserToken;
    }

    public void setUserToken(String userToken) {
        UserToken = userToken;
    }
}
