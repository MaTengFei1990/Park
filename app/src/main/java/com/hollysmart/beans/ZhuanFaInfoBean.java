package com.hollysmart.beans;

import java.io.Serializable;

/**
 * Created by Lenovo on 2018/8/7.
 */

public class ZhuanFaInfoBean implements Serializable{

//    {"id":54,"userid":2,"username":"刘静","content":"测试草稿图片","imgs":"upload/2018-08-03/1666f045-f762-4b8a-8a52-7fb624968684.jpg"}


    private String id;
    private String userid;
    private String username;
    private String content;
    private String imgs;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getImgs() {
        return imgs;
    }

    public void setImgs(String imgs) {
        this.imgs = imgs;
    }
}
