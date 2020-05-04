package com.example.demo.Entity;

import java.io.Serializable;

public class Comment implements Serializable {
    private Long id;
    private Long travelid;
    private String content;
    private String type;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTravelid() {
        return travelid;
    }

    public void setTravelId(Long travelId) {
        this.travelid = travelId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
