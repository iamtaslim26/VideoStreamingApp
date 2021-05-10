package com.kgec.videostreamingapp.model;

public class Videos {

    private String uid,url,date,time,title,fullname;

    public Videos() {
    }

    public Videos(String uid, String url, String date, String time, String title,String fullname) {
        this.uid = uid;
        this.url = url;
        this.date = date;
        this.time = time;
        this.title = title;
        this.fullname=fullname;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }
}
