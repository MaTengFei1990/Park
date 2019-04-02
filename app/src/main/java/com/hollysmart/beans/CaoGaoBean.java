package com.hollysmart.beans;

/**
 * Created by Lenovo on 2018/5/25.
 */

public class CaoGaoBean {
//    {
//        "id":32,
//            "type_id":1,
//            "content":"测试新增马",
//            "tagid":354,
//            "tagname":"便民新举措",
//            "ispublic":0,
//            "imgs":""
//    }


    private String id;
    private String type_id;
    private String content;
    private String tagid;
    private String tagname;
    private String ispublic;
    private String imgs;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType_id() {
        return type_id;
    }

    public void setType_id(String type_id) {
        this.type_id = type_id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTagid() {
        return tagid;
    }

    public void setTagid(String tagid) {
        this.tagid = tagid;
    }

    public String getTagname() {
        return tagname;
    }

    public void setTagname(String tagname) {
        this.tagname = tagname;
    }

    public String getIspublic() {
        return ispublic;
    }

    public void setIspublic(String ispublic) {
        this.ispublic = ispublic;
    }

    public String getImgs() {
        return imgs;
    }

    public void setImgs(String imgs) {
        this.imgs = imgs;
    }
}
