package com.example.demo.Entity;



import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Travel  implements Serializable {

    private Long id;

    private String title;

    private String content;

    //审核状态：0为未审核，1为审核中，2为审核通过，3为审核未通过
    private Integer state;

    private String timestamp;

    private String advice;

    private Long userId;

    private  String type;

    public List<Keyword> keywordList;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getAdvice() {
        return advice;
    }

    public void setAdvice(String advice) {
        this.advice = advice;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public String getType(){return type;}
    public void setType(String type){this.type=type;}

    public List<Keyword> getKeywordList(){return keywordList;}
    public void setKeywordList(List<Keyword> keywordList){this.keywordList=keywordList;}
}
