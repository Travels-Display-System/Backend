package com.example.demo.Entity;


public class User {

    private Long id;

    private String username;

    private String password;

    private Integer enabled;

    private Integer iseditor;

    private String type;

    public User(){

    }
    User(String username,String password){
        this.username=username;
        this.password=password;
    }
    User(String username, String password, Integer enabled, Integer iseditor){
        this.username=username;
        this.password=password;
        this.enabled=enabled;
        this.iseditor=iseditor;
    }
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getEnabled() {
        return enabled;
    }

    public void setEnabled(Integer enabled) {
        this.enabled = enabled;
    }

    public Integer getIseditor() {
        return iseditor;
    }

    public void setIseditor(Integer iseditor) {
        this.iseditor = iseditor;
    }


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
