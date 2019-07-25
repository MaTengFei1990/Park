package com.hollysmart.beans;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

@DatabaseTable(tableName = "voiceinfo")
public class VoiceInfoBean implements Serializable {

    @DatabaseField(columnName = "id", generatedId = true)
    private int id;

    @DatabaseField(columnName = "userName")
    private String userName;

    @DatabaseField(columnName = "userPasWord")
    private String userPasWord;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPasWord() {
        return userPasWord;
    }

    public void setUserPasWord(String userPasWord) {
        this.userPasWord = userPasWord;
    }
}
