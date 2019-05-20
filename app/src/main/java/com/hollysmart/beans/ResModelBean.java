package com.hollysmart.beans;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

/**
 * Created by Lenovo on 2018/12/11.
 */
@DatabaseTable(tableName = "reslistdb")
public class ResModelBean implements Serializable {


//            "id":"04492deb83d04fda98388bc2e38a3ffa",
//            "remarks":"",
//            "createDate":"2018-11-21 14:09:14",
//            "updateDate":"2018-11-21 14:09:14",
//            "fModelName":"古树",
//            "fTaskid":"",
//            "fJsonData":"[{"fieldName":"enter_city","entityName":"","pointLength":"","isNull":"","orderNum":2,"isShow":"1","isShowList":"1","showType":"list","isQuery":"1","fieldMustInput":"true","content":"所属区县","dictField":"","dictTable":"","dictText":"","fieldDefault":"","extendJson":"","dictFieldValue":"","javaField":"enterCity","isInsert":"1","isEdit":"1"},{"fieldName":"enter_company","entityName":"","pointLength":"","isNull":"","orderNum":3,"isShow":"1","isShowList":"1","showType":"text","isQuery":"1","fieldMustInput":"true","content":"填报单位","dictField":"","dictTable":"","dictText":"","fieldDefault":"","extendJson":"","dictFieldValue":"","javaField":"enterCompany","isInsert":"1","isEdit":"1"},{"fieldName":"enterer","entityName":"","pointLength":"","isNull":"","orderNum":4,"isShow":"1","isShowList":"1","showType":"text","isQuery":"1","fieldMustInput":"true","content":"填报人","dictField":"","dictTable":"","dictText":"","fieldDefault":"","extendJson":"","dictFieldValue":"","javaField":"enterer","isInsert":"1","isEdit":"1"},{"fieldName":"enter_phone","entityName":"","pointLength":"","isNull":"","orderNum":5,"isShow":"1","isShowList":"1","showType":"text","isQuery":"1","fieldMustInput":"true","content":"填报人电话","dictField":"","dictTable":"","dictText":"","fieldDefault":"","extendJson":"","dictFieldValue":"","javaField":"enterPhone","isInsert":"1","isEdit":"1","cgformRuleList":[{"remarks":"","createDate":"2018-05-3116:01:18","updateDate":"2018-05-3116:01:18","pageNo":"","pageSize":"","modified":false,"cgformField":{"remarks":"","createDate":"","updateDate":"","pageNo":"","pageSize":"","modified":false,"cgformHead":"","fieldName":"","entityName":"","length":"","pointLength":"","type":"","isNull":"","orderNum":"","isShow":"","isShowList":"","showType":"","isQuery":"","fieldLength":"","fieldHref":"","fieldValidType":"","fieldMustInput":"","queryMode":"","content":"","createName":"","updateName":"","dictField":"","dictTable":"","dictText":"","mainTable":"","mainField":"","fieldDefault":"","extendJson":"","dictFieldValue":"","javaType":"","javaField":"","isInsert":"","isEdit":"","metype":"","nameAndComments":"null","simpleJavaType":"","simpleJavaField":"","javaFieldId":"","javaFieldName":"","javaFieldAttrs":"","annotationList":["javax.validation.constraints.NotNull(message='null不能为空')"],"isNotBaseField":true},"mykey":"","type":"4","pattern":"^(0[0-9]{2,3}-?)?([2-9][0-9]{6,7})+(-[0-9]{1,4})?$|(^(1[0-9][0-9])\\d{8}$)","error":"手机号或固号输入错误"}]},{"fieldName":"enter_time","entityName":"","pointLength":"","isNull":"","orderNum":6,"isShow":"1","isShowList":"1","showType":"datetime","isQuery":"1","fieldMustInput":"true","content":"填报时间","dictField":"","dictTable":"","dictText":"","fieldDefault":"","extendJson":"","dictFieldValue":"","javaField":"enterTime","isInsert":"1","isEdit":"1"},{"fieldName":"objCode","entityName":"","pointLength":"","isNull":"","orderNum":7,"isShow":"1","isShowList":"1","showType":"text","isQuery":"1","fieldMustInput":"true","content":"保护区编号","dictField":"","dictTable":"","dictText":"","fieldDefault":"","extendJson":"","dictFieldValue":"","javaField":"objCode","isInsert":"1","isEdit":"1"},{"fieldName":"objName","entityName":"","pointLength":"","isNull":"","orderNum":8,"isShow":"1","isShowList":"1","showType":"text","isQuery":"1","fieldMustInput":"true","content":"保护区名称","dictField":"","dictTable":"","dictText":"","fieldDefault":"","extendJson":"","dictFieldValue":"","javaField":"objName","isInsert":"1","isEdit":"1"},{"fieldName":"province","entityName":"","pointLength":"","isNull":"","orderNum":9,"isShow":"0","isShowList":"0","showType":"text","isQuery":"0","fieldMustInput":"true","content":"位置","dictField":"","dictTable":"","dictText":"","fieldDefault":"","extendJson":"","dictFieldValue":"","javaField":"province","isInsert":"1","isEdit":"1"},{"fieldName":"city","entityName":"","pointLength":"","isNull":"","orderNum":10,"isShow":"0","isShowList":"0","showType":"text","isQuery":"0","fieldMustInput":"true","content":"区","dictField":"","dictTable":"","dictText":"","fieldDefault":"","extendJson":"","dictFieldValue":"","javaField":"city","isInsert":"1","isEdit":"1"},{"fieldName":"objqxcode","entityName":"","pointLength":"","isNull":"","orderNum":11,"isShow":"1","isShowList":"1","showType":"text","isQuery":"1","fieldMustInput":"true","content":"区县","dictField":"","dictTable":"","dictText":"","fieldDefault":"","extendJson":"","dictFieldValue":"","javaField":"objqxcode","isInsert":"1","isEdit":"1"},{"fieldName":"town","entityName":"","pointLength":"","isNull":"","orderNum":12,"isShow":"1","isShowList":"1","showType":"text","isQuery":"1","fieldMustInput":"true","content":"镇(乡)","dictField":"","dictTable":"","dictText":"","fieldDefault":"","extendJson":"","dictFieldValue":"","javaField":"town","isInsert":"1","isEdit":"1"},{"fieldName":"village","entityName":"","pointLength":"","isNull":"","orderNum":13,"isShow":"1","isShowList":"1","showType":"text","isQuery":"1","fieldMustInput":"true","content":"村","dictField":"","dictTable":"","dictText":"","fieldDefault":"","extendJson":"","dictFieldValue":"","javaField":"village","isInsert":"1","isEdit":"1"},{"fieldName":"small_place_name","entityName":"","pointLength":"","isNull":"","orderNum":14,"isShow":"1","isShowList":"1","showType":"text","isQuery":"1","fieldMustInput":"true","content":"小地名","dictField":"","dictTable":"","dictText":"","fieldDefault":"","extendJson":"","dictFieldValue":"","javaField":"smallPlaceName","isInsert":"1","isEdit":"1"},{"fieldName":"level","entityName":"","pointLength":"","isNull":"","orderNum":15,"isShow":"1","isShowList":"1","showType":"text","isQuery":"1","fieldMustInput":"true","content":"保护区级别","dictField":"","dictTable":"","dictText":"","fieldDefault":"","extendJson":"","dictFieldValue":"","javaField":"level","isInsert":"1","isEdit":"1"},{"fieldName":"category","entityName":"","pointLength":"","isNull":"","orderNum":16,"isShow":"1","isShowList":"1","showType":"text","isQuery":"1","fieldMustInput":"true","content":"保护区类型","dictField":"","dictTable":"","dictText":"","fieldDefault":"","extendJson":"","dictFieldValue":"","javaField":"category","isInsert":"1","isEdit":"1"},{"fieldName":"altitude","entityName":"","pointLength":"","isNull":"","orderNum":17,"isShow":"1","isShowList":"1","showType":"text","isQuery":"1","fieldMustInput":"true","content":"海拔(米)","dictField":"","dictTable":"","dictText":"","fieldDefault":"","extendJson":"","dictFieldValue":"","javaField":"altitude","isInsert":"1","isEdit":"1","cgformRuleList":[{"remarks":"","createDate":"2018-05-3116:01:18","updateDate":"2018-05-3116:01:18","pageNo":"","pageSize":"","modified":false,"cgformField":{"remarks":"","createDate":"","updateDate":"","pageNo":"","pageSize":"","modified":false,"cgformHead":"","fieldName":"","entityName":"","length":"","pointLength":"","type":"","isNull":"","orderNum":"","isShow":"","isShowList":"","showType":"","isQuery":"","fieldLength":"","fieldHref":"","fieldValidType":"","fieldMustInput":"","queryMode":"","content":"","createName":"","updateName":"","dictField":"","dictTable":"","dictText":"","mainTable":"","mainField":"","fieldDefault":"","extendJson":"","dictFieldValue":"","javaType":"","javaField":"","isInsert":"","isEdit":"","metype":"","nameAndComments":"null","simpleJavaType":"","simpleJavaField":"","javaFieldId":"","javaFieldName":"","javaFieldAttrs":"","annotationList":["javax.validation.constraints.NotNull(message='null不能为空')"],"isNotBaseField":true},"mykey":"","type":"8","pattern":"^[0-9]+([.]{1}[0-9]+){0,1}$","error":"请输入整数或小数"}]},{"fieldName":"ranges","entityName":"","pointLength":"","isNull":"","orderNum":18,"isShow":"1","isShowList":"1","showType":"text","isQuery":"1","fieldMustInput":"true","content":"经纬度范围","dictField":"","dictTable":"","dictText":"","fieldDefault":"","extendJson":"","dictFieldValue":"","javaField":"ranges","isInsert":"1","isEdit":"1"},{"fieldName":"four_range","entityName":"","pointLength":"","isNull":"","orderNum":19,"isShow":"1","isShowList":"1","showType":"text","isQuery":"1","fieldMustInput":"true","content":"四至范围","dictField":"","dictTable":"","dictText":"","fieldDefault":"","extendJson":"","dictFieldValue":"","javaField":"fourRange","isInsert":"1","isEdit":"1"},{"fieldName":"total_area","entityName":"","pointLength":"2","isNull":"","orderNum":20,"isShow":"1","isShowList":"1","showType":"text","isQuery":"1","fieldMustInput":"true","content":"总面积","dictField":"","dictTable":"","dictText":"","fieldDefault":"","extendJson":"","dictFieldValue":"","javaField":"totalArea","isInsert":"1","isEdit":"1","cgformRuleList":[{"remarks":"","createDate":"2018-05-3116:01:18","updateDate":"2018-05-3116:01:18","pageNo":"","pageSize":"","modified":false,"cgformField":{"remarks":"","createDate":"","updateDate":"","pageNo":"","pageSize":"","modified":false,"cgformHead":"","fieldName":"","entityName":"","length":"","pointLength":"","type":"","isNull":"","orderNum":"","isShow":"","isShowList":"","showType":"","isQuery":"","fieldLength":"","fieldHref":"","fieldValidType":"","fieldMustInput":"","queryMode":"","content":"","createName":"","updateName":"","dictField":"","dictTable":"","dictText":"","mainTable":"","mainField":"","fieldDefault":"","extendJson":"","dictFieldValue":"","javaType":"","javaField":"","isInsert":"","isEdit":"","metype":"","nameAndComments":"null","simpleJavaType":"","simpleJavaField":"","javaFieldId":"","javaFieldName":"","javaFieldAttrs":"","annotationList":["javax.validation.constraints.NotNull(message='null不能为空')"],"isNotBaseField":true},"mykey":"","type":"8","pattern":"^[0-9]+([.]{1}[0-9]+){0,1}$","error":"请输入整数或小数"}]},{"fieldName":"core_area","entityName":"","pointLength":"2","isNull":"","orderNum":21,"isShow":"1","isShowList":"1","showType":"text","isQuery":"1","fieldMustInput":"true","content":"核心区","dictField":"","dictTable":"","dictText":"","fieldDefault":"","extendJson":"","dictFieldValue":"","javaField":"coreArea","isInsert":"1","isEdit":"1"},{"fieldName":"buffer_area","entityName":"","pointLength":"2","isNull":"","orderNum":22,"isShow":"1","isShowList":"1","showType":"text","isQuery":"1","fieldMustInput":"true","content":"缓冲区","dictField":"","dictTable":"","dictText":"","fieldDefault":"","extendJson":"","dictFieldValue":"","javaField":"bufferArea","isInsert":"1","isEdit":"1"},{"fieldName":"test_area","entityName":"","pointLength":"2","isNull":"","orderNum":23,"isShow":"1","isShowList":"1","showType":"text","isQuery":"1","fieldMustInput":"true","content":"实验区","dictField":"","dictTable":"","dictText":"","fieldDefault":"","extendJson":"","dictFieldValue":"","javaField":"testArea","isInsert":"1","isEdit":"1"},{"fieldName":"approval_time","entityName":"","pointLength":"","isNull":"","orderNum":24,"isShow":"1","isShowList":"1","showType":"text","isQuery":"1","fieldMustInput":"true","content":"批准时间","dictField":"","dictTable":"","dictText":"","fieldDefault":"","extendJson":"","dictFieldValue":"","javaField":"approvalTime","isInsert":"1","isEdit":"1"},{"fieldName":"approval_num","entityName":"","pointLength":"","isNull":"","orderNum":25,"isShow":"1","isShowList":"1","showType":"text","isQuery":"1","fieldMustInput":"true","content":"批准文号","dictField":"","dictTable":"","dictText":"","fieldDefault":"","extendJson":"","dictFieldValue":"","javaField":"approvalNum","isInsert":"1","isEdit":"1"},{"fieldName":"tocity_time","entityName":"","pointLength":"","isNull":"","orderNum":26,"isShow":"1","isShowList":"1","showType":"datetime","isQuery":"1","fieldMustInput":"true","content":"晋升市级时间","dictField":"","dictTable":"","dictText":"","fieldDefault":"","extendJson":"","dictFieldValue":"","javaField":"tocityTime","isInsert":"1","isEdit":"1"},{"fieldName":"toprovince_num","entityName":"","pointLength":"","isNull":"","orderNum":27,"isShow":"1","isShowList":"1","showType":"text","isQuery":"1","fieldMustInput":"true","content":"晋升市级文号","dictField":"","dictTable":"","dictText":"","fieldDefault":"","extendJson":"","dictFieldValue":"","javaField":"toprovinceNum","isInsert":"1","isEdit":"1"},{"fieldName":"tonation_time","entityName":"","pointLength":"","isNull":"","orderNum":28,"isShow":"1","isShowList":"1","showType":"datetime","isQuery":"1","fieldMustInput":"true","content":"晋升国家级时间","dictField":"","dictTable":"","dictText":"","fieldDefault":"","extendJson":"","dictFieldValue":"","javaField":"tonationTime","isInsert":"1","isEdit":"1"},{"fieldName":"tonation_num","entityName":"","pointLength":"","isNull":"","orderNum":29,"isShow":"1","isShowList":"1","showType":"text","isQuery":"1","fieldMustInput":"true","content":"晋升国家级文号","dictField":"","dictTable":"","dictText":"","fieldDefault":"","extendJson":"","dictFieldValue":"","javaField":"tonationNum","isInsert":"1","isEdit":"1"},{"fieldName":"agency_name","entityName":"","pointLength":"","isNull":"","orderNum":30,"isShow":"1","isShowList":"1","showType":"text","isQuery":"1","fieldMustInput":"true","content":"管理机构名称","dictField":"","dictTable":"","dictText":"","fieldDefault":"","extendJson":"","dictFieldValue":"","javaField":"agencyName","isInsert":"1","isEdit":"1"},{"fieldName":"agency_nature","entityName":"","pointLength":"","isNull":"","orderNum":31,"isShow":"1","isShowList":"1","showType":"text","isQuery":"1","fieldMustInput":"true","content":"管理机构性质","dictField":"","dictTable":"","dictText":"","fieldDefault":"","extendJson":"","dictFieldValue":"","javaField":"agencyNature","isInsert":"1","isEdit":"1"},{"fieldName":"agency_level","entityName":"","pointLength":"","isNull":"","orderNum":32,"isShow":"1","isShowList":"1","showType":"text","isQuery":"1","fieldMustInput":"true","content":"管理机构级别","dictField":"","dictTable":"","dictText":"","fieldDefault":"","extendJson":"","dictFieldValue":"","javaField":"agencyLevel","isInsert":"1","isEdit":"1"},{"fieldName":"agency_phone","entityName":"","pointLength":"","isNull":"","orderNum":33,"isShow":"1","isShowList":"1","showType":"text","isQuery":"1","fieldMustInput":"true","content":"管理机构电话","dictField":"","dictTable":"","dictText":"","fieldDefault":"","extendJson":"","dictFieldValue":"","javaField":"agencyPhone","isInsert":"1","isEdit":"1","cgformRuleList":[{"remarks":"","createDate":"2018-05-3116:01:18","updateDate":"2018-05-3116:01:18","pageNo":"","pageSize":"","modified":false,"cgformField":{"remarks":"","createDate":"","updateDate":"","pageNo":"","pageSize":"","modified":false,"cgformHead":"","fieldName":"","entityName":"","length":"","pointLength":"","type":"","isNull":"","orderNum":"","isShow":"","isShowList":"","showType":"","isQuery":"","fieldLength":"","fieldHref":"","fieldValidType":"","fieldMustInput":"","queryMode":"","content":"","createName":"","updateName":"","dictField":"","dictTable":"","dictText":"","mainTable":"","mainField":"","fieldDefault":"","extendJson":"","dictFieldValue":"","javaType":"","javaField":"","isInsert":"","isEdit":"","metype":"","nameAndComments":"null","simpleJavaType":"","simpleJavaField":"","javaFieldId":"","javaFieldName":"","javaFieldAttrs":"","annotationList":["javax.validation.constraints.NotNull(message='null不能为空')"],"isNotBaseField":true},"mykey":"","type":"4","pattern":"^(0[0-9]{2,3}-?)?([2-9][0-9]{6,7})+(-[0-9]{1,4})?$|(^(1[0-9][0-9])\\d{8}$)","error":"手机号或固号输入错误"}]},{"fieldName":"principal","entityName":"","pointLength":"","isNull":"","orderNum":34,"isShow":"1","isShowList":"1","showType":"text","isQuery":"1","fieldMustInput":"true","content":"负责人","dictField":"","dictTable":"","dictText":"","fieldDefault":"","extendJson":"","dictFieldValue":"","javaField":"principal","isInsert":"1","isEdit":"1"},{"fieldName":"principal_name","entityName":"","pointLength":"","isNull":"","orderNum":35,"isShow":"1","isShowList":"1","showType":"text","isQuery":"1","fieldMustInput":"true","content":"负责人电话","dictField":"","dictTable":"","dictText":"","fieldDefault":"","extendJson":"","dictFieldValue":"","javaField":"principalName","isInsert":"1","isEdit":"1","cgformRuleList":[{"remarks":"","createDate":"2018-05-3116:01:18","updateDate":"2018-05-3116:01:18","pageNo":"","pageSize":"","modified":false,"cgformField":{"remarks":"","createDate":"","updateDate":"","pageNo":"","pageSize":"","modified":false,"cgformHead":"","fieldName":"","entityName":"","length":"","pointLength":"","type":"","isNull":"","orderNum":"","isShow":"","isShowList":"","showType":"","isQuery":"","fieldLength":"","fieldHref":"","fieldValidType":"","fieldMustInput":"","queryMode":"","content":"","createName":"","updateName":"","dictField":"","dictTable":"","dictText":"","mainTable":"","mainField":"","fieldDefault":"","extendJson":"","dictFieldValue":"","javaType":"","javaField":"","isInsert":"","isEdit":"","metype":"","nameAndComments":"null","simpleJavaType":"","simpleJavaField":"","javaFieldId":"","javaFieldName":"","javaFieldAttrs":"","annotationList":["javax.validation.constraints.NotNull(message='null不能为空')"],"isNotBaseField":true},"mykey":"","type":"4","pattern":"^(0[0-9]{2,3}-?)?([2-9][0-9]{6,7})+(-[0-9]{1,4})?$|(^(1[0-9][0-9])\\d{8}$)","error":"手机号或固号输入错误"}]},{"fieldName":"agency_wechat","entityName":"","pointLength":"","isNull":"","orderNum":36,"isShow":"1","isShowList":"1","showType":"text","isQuery":"1","fieldMustInput":"true","content":"对外微信","dictField":"","dictTable":"","dictText":"","fieldDefault":"","extendJson":"","dictFieldValue":"","javaField":"agencyWechat","isInsert":"1","isEdit":"1"},{"fieldName":"agency_website","entityName":"","pointLength":"","isNull":"","orderNum":37,"isShow":"1","isShowList":"1","showType":"text","isQuery":"1","fieldMustInput":"true","content":"对外网站","dictField":"","dictTable":"","dictText":"","fieldDefault":"","extendJson":"","dictFieldValue":"","javaField":"agencyWebsite","isInsert":"1","isEdit":"1"},{"fieldName":"data_state","entityName":"","pointLength":"","isNull":"","orderNum":29,"isShow":"1","isShowList":"1","showType":"list","isQuery":"1","fieldMustInput":"true","content":"上报状态","dictField":"type","dictTable":"","dictText":"label","fieldDefault":"","extendJson":"","dictFieldValue":"data_state","javaField":"dataState","isInsert":"1","isEdit":"1"}]",
//            "fVersion":"0",
//            "fOfficeId":"1"
//


    public static String  HINT = "1";
    public static String SHOW = "2";

    @DatabaseField(columnName = "id", id = true)
    private String  id;

    @DatabaseField(columnName = "remarks")
    private String remarks;

    @DatabaseField(columnName = "createDate")
    private String createDate;

    @DatabaseField(columnName = "updateDate")
    private String updateDate;

    @DatabaseField(columnName = "fModelName")
    private String fModelName;


    @DatabaseField(columnName = "fTaskid")
    private String fTaskid;

    @DatabaseField(columnName = "fJsonData")
    private String fJsonData;

    @DatabaseField(columnName = "fVersion")
    private int fVersion;

    @DatabaseField(columnName = "fOfficeId")
    private String fOfficeId;






    @DatabaseField(columnName = "isSelect")
    private boolean isSelect;

    @DatabaseField(columnName = "Hint")
    private String Hint="2";// 1 是隐藏， 2是不隐藏




    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return fModelName;
    }

    public void setName(String name) {
        this.fModelName = name;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }

    public String getHint() {
        return Hint;
    }

    public void setHint(String hint) {
        Hint = hint;
    }


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

    public String getfModelName() {
        return fModelName;
    }

    public void setfModelName(String fModelName) {
        this.fModelName = fModelName;
    }

    public String getfTaskid() {
        return fTaskid;
    }

    public void setfTaskid(String fTaskid) {
        this.fTaskid = fTaskid;
    }

    public String getfJsonData() {
        return fJsonData;
    }

    public void setfJsonData(String fJsonData) {
        this.fJsonData = fJsonData;
    }

    public int getfVersion() {
        return fVersion;
    }

    public void setfVersion(int fVersion) {
        this.fVersion = fVersion;
    }

    public String getfOfficeId() {
        return fOfficeId;
    }

    public void setfOfficeId(String fOfficeId) {
        this.fOfficeId = fOfficeId;
    }
}
