package com.hollysmart.beans;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Lenovo on 2019/4/12.
 */

public class DongTaiFormBean implements Serializable {

    private String isShow;

    private String content;

    private String propertyLabel;

    private String fieldName;

    private String entityName;

    private String pointLength;


    private String javaField;

    private int orderNum;

    private String iSShowRequired;

    private String dictText;

    private String isShowList;

    /**
     * 显示类型 text：文本框
     * list：下拉框
     * datetime：时间选择
     * radio：单选框
     * checkbox：多选框
     * tree：树控件
     * file：上传
     * drag：拖拽上传
     * rate：评分
     * switch：开关
     * range：滑动输入条
     * textarea：多行文本
     * popup：弹出框
     * editor">编辑器
     * password">密码
     * marker">地图定位
     */

    private String showType;

    private String isQuery;

    private boolean fieldMustInput;

    private String dictField;

    private String dictTable;

    private String fieldDefault;


    private String dictFieldValue;


    private String isEdit;


    private boolean showTiShi=false;

    /**
     校验规则
     */
    private List<cgformRuleBean> cgformRuleList;

    private List<DongTaiFormBean> cgformFieldList;


    private int Groupindex;
    private int childindex;

    private int position;


    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    public String getEntityName() {
        return entityName;
    }

    public void setPointLength(String pointLength) {
        this.pointLength = pointLength;
    }

    public String getPointLength() {
        return pointLength;
    }



    public void setOrderNum(int orderNum) {
        this.orderNum = orderNum;
    }

    public int getOrderNum() {
        return orderNum;
    }

    public void setIsShow(String isShow) {
        this.isShow = isShow;
    }

    public String getIsShow() {
        return isShow;
    }

    public void setIsShowList(String isShowList) {
        this.isShowList = isShowList;
    }

    public String getIsShowList() {
        return isShowList;
    }

    public void setShowType(String showType) {
        this.showType = showType;
    }

    public String getShowType() {
        return showType;
    }

    public void setIsQuery(String isQuery) {
        this.isQuery = isQuery;
    }

    public String getIsQuery() {
        return isQuery;
    }

    public void setFieldMustInput(boolean fieldMustInput) {
        this.fieldMustInput = fieldMustInput;
    }

    public boolean getFieldMustInput() {
        return fieldMustInput;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public void setDictField(String dictField) {
        this.dictField = dictField;
    }

    public String getDictField() {
        return dictField;
    }

    public void setDictTable(String dictTable) {
        this.dictTable = dictTable;
    }

    public String getDictTable() {
        return dictTable;
    }

    public void setDictText(String dictText) {
        this.dictText = dictText;
    }

    public String getDictText() {
        return dictText;
    }

    public void setFieldDefault(String fieldDefault) {
        this.fieldDefault = fieldDefault;
    }

    public String getFieldDefault() {
        return fieldDefault;
    }



    public void setDictFieldValue(String dictFieldValue) {
        this.dictFieldValue = dictFieldValue;
    }

    public String getDictFieldValue() {
        return dictFieldValue;
    }

    public void setJavaField(String javaField) {
        this.javaField = javaField;
    }

    public String getJavaField() {
        return javaField;
    }


    public void setIsEdit(String isEdit) {
        this.isEdit = isEdit;
    }

    public String getIsEdit() {
        return isEdit;
    }


    public List<cgformRuleBean> getCgformRuleList() {
        return cgformRuleList;
    }

    public void setCgformRuleList(List<cgformRuleBean> cgformRuleList) {
        this.cgformRuleList = cgformRuleList;
    }


    public String getPropertyLabel() {
        return propertyLabel;
    }

    public void setPropertyLabel(String propertyLabel) {
        this.propertyLabel = propertyLabel;
    }


    public boolean isShowTiShi() {
        return showTiShi;
    }

    public void setShowTiShi(boolean showTiShi) {
        this.showTiShi = showTiShi;
    }

    public List<DongTaiFormBean> getCgformFieldList() {
        return cgformFieldList;
    }

    public void setCgformFieldList(List<DongTaiFormBean> cgformFieldList) {
        this.cgformFieldList = cgformFieldList;
    }


    public int getGroupindex() {
        return Groupindex;
    }

    public void setGroupindex(int groupindex) {
        Groupindex = groupindex;
    }

    public int getChildindex() {
        return childindex;
    }

    public void setChildindex(int childindex) {
        this.childindex = childindex;
    }


    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
