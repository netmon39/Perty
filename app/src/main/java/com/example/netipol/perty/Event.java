package com.example.netipol.perty;

/**
 * Created by netipol on 1/2/2018 AD.
 */

public class Event {

    private String title, desc, type, image;

    public Event(){

    }

    public Event(String title, String desc, String type, String image) {
        this.title = title;
        this.desc = desc;
        this.type = type;
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
