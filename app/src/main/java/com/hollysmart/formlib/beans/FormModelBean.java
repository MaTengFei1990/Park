package com.hollysmart.formlib.beans;

import com.hollysmart.formlib.beans.DongTaiFormBean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Lenovo on 2019/4/25.
 */

public class FormModelBean implements Serializable{

    private List<DongTaiFormBean> cgformFieldList;


    public List<DongTaiFormBean> getCgformFieldList() {
        return cgformFieldList;
    }

    public void setCgformFieldList(List<DongTaiFormBean> cgformFieldList) {
        this.cgformFieldList = cgformFieldList;
    }
}
