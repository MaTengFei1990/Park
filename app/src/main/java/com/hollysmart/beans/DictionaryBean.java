package com.hollysmart.beans;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

/**
 * Created by Lenovo on 2019/4/24.
 */
@DatabaseTable(tableName = "dictionary")
public class DictionaryBean implements Serializable {


//            "value":"1",
//            "label":"未上报",
//            "type":"label",
//            "description":"上报状态",
//            "sort":10,
//            "childList":[
//
    @DatabaseField(columnName = "id", generatedId = true)
    private int id;

    @DatabaseField(columnName = "value")
    private String value;      //":"1","

    @DatabaseField(columnName = "label")
    private String label;     //":" 未上报","

    @DatabaseField(columnName = "type")
    private String type;      //":" label","

    @DatabaseField(columnName = "description")
    private String description; //":" 上报状态","

    @DatabaseField(columnName = "sort")
    private String sort;       //":10,"


    @DatabaseField(columnName = "modelid")
    private String modelid;       //":10,"


    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public String getModelid() {
        return modelid;
    }

    public void setModelid(String modelid) {
        this.modelid = modelid;
    }
}
