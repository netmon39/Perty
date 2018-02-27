package com.example.netipol.perty.Model;

import android.support.annotation.NonNull;

import com.example.netipol.perty.Util.EventId;

/**
 * Created by netipol on 1/2/2018 AD.
 */

public class Event extends EventId{

    private String title, desc, type, image, timestamp, host, time, location;

    public Event(){

    }

    public Event(String title, String desc, String type, String image, String timestamp, String host, String time, String location) {
        this.title = title;
        this.desc = desc;
        this.type = type;
        this.image = image;
        this.timestamp = timestamp;
        this.host = host;
        this.time = time;
        this.location = location;
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

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;}

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
