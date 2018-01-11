package com.example.ngockhanh.chatapp;

/**
 * Created by Ngoc Khanh on 1/9/2018.
 */

public class Messages {
    private String message;
    private Boolean seen;
    private Long time;
    private String type;

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    private String from;

    public  Messages(String message,Boolean seen,Long time,String type,String from){
        this.message=message;
        this.seen=seen;
        this.time=time;
        this.type=type;
        this.from=from;

    }
    public Messages(){

    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Boolean getSeen() {
        return seen;
    }

    public void setSeen(Boolean seen) {
        this.seen = seen;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }




}
