package com.example.demo.Entity;

public class Report {
    private Long id;
    private String content;
    private Long userid;
    private Long travelid;
    private String type;


    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getUserid() {
        return userid;
    }

    public void setUserid(Long userid) {
        this.userid = userid;
    }

    public Long getTravelid() {
        return travelid;
    }

    public void setTravelid(Long travelid) {
        this.travelid = travelid;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
