package com.kgec.videostreamingapp.model;

public class Comments {

    private String uid,time,date,comment,fullname;

    public Comments() {
    }

    public Comments(String uid, String time, String date, String comment, String fullname) {
        this.uid = uid;
        this.time = time;
        this.date = date;
        this.comment = comment;
        this.fullname = fullname;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }
}
