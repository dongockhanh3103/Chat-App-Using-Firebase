package com.example.ngockhanh.chatapp;

/**
 * Created by Ngoc Khanh on 1/5/2018.
 */

public class Users {
    private String name;
    private String image;
    private String status;

    public String getThumb_image() {
        return thumb_image;
    }

    public void setThumb_image(String thumb_image) {
        this.thumb_image = thumb_image;
    }

    private String thumb_image;
    public Users(){}
    public void setName(String name) {
        this.name = name;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setStatus(String status) {
        this.status = status;
    }




    public String getName() {
        return name;
    }

    public String getImage() {
        return image;
    }

    public String getStatus() {
        return status;
    }





}
