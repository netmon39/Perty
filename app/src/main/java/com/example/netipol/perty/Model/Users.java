package com.example.netipol.perty.Model;

/**
 * Created by Administrator on 8/2/2561.
 */
public class Users {

    public String name, image, status;

    public Users(){

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Users(String name, String image, String status) {
        this.name = name;
        this.image = image;
        this.status = status;
    }
}
