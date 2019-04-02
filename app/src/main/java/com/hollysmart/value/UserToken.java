package com.hollysmart.value;

/**
 * Created by cai on 2018/3/29.
 */

public class UserToken {

    private static UserToken userToken;
    private UserToken(){
    }

    public static UserToken getUserToken(){
        if (userToken == null)
            userToken = new UserToken();
        return userToken;
    }

    private String token;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
