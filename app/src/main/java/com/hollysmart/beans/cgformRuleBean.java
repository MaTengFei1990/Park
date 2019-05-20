package com.hollysmart.beans;

import java.io.Serializable;

/**
 * Created by Lenovo on 2019/4/12.
 */

public class cgformRuleBean implements Serializable {



    private String remarks;

    private String createDate;

    private String updateDate;

    private String pageNo;

    private String pageSize;

    private String modified;

    private String mykey;


    /***
     *  验证type 1：数字 2：英文和数字 3：邮箱 4：电话号码和固话 5：身份证号 6：ip地址 7：邮政编码 8：数字和小数点
     */

    private String type;

    private String pattern;

    private String error;



    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(String updateDate) {
        this.updateDate = updateDate;
    }

    public String getPageNo() {
        return pageNo;
    }

    public void setPageNo(String pageNo) {
        this.pageNo = pageNo;
    }

    public String getPageSize() {
        return pageSize;
    }

    public void setPageSize(String pageSize) {
        this.pageSize = pageSize;
    }

    public String getModified() {
        return modified;
    }

    public void setModified(String modified) {
        this.modified = modified;
    }

    public String getMykey() {
        return mykey;
    }

    public void setMykey(String mykey) {
        this.mykey = mykey;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
