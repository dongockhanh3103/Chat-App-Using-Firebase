package com.example.ngockhanh.chatapp;

/**
 * Created by Ngoc Khanh on 1/9/2018.
 */

public class Messages {
    private String message;
    private String seen;
    private String time;
    private String type;
    public  Messages(String message,String seen,String time,String type){
        this.message=message;
        this.seen=seen;
        this.time=time;
        this.type=type;

    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSeen() {
        return seen;
    }

    public void setSeen(String seen) {
        this.seen = seen;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }




}
