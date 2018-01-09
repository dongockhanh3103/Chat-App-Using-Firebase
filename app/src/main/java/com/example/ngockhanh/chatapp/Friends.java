package com.example.ngockhanh.chatapp;

/**
 * Created by Ngoc Khanh on 1/8/2018.
 */

public class Friends {
    public Friends(){

    }
    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String date;
    public Friends(String date){
        this.date=date;
    }
}
