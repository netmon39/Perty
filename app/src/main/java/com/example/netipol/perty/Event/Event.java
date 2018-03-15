package com.example.netipol.perty.Event;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by netipol on 1/2/2018 AD.
 */

public class Event extends EventId implements Parcelable {

    private String title, desc, type, image, timestamp, host, time, location, categ;

    public Event(){

    }

    public Event(String title, String desc, String type, String image, String timestamp, String host, String time, String location, String categ) {
        this.title = title;
        this.desc = desc;
        this.type = type;
        this.image = image;
        this.timestamp = timestamp;
        this.host = host;
        this.time = time;
        this.location = location;
        this.categ = categ;
    }

    protected Event(Parcel in) {
        title = in.readString();
        desc = in.readString();
        type = in.readString();
        image = in.readString();
        timestamp = in.readString();
        host = in.readString();
        time = in.readString();
        location = in.readString();
        categ = in.readString();
    }

    public static final Creator<Event> CREATOR = new Creator<Event>() {
        @Override
        public Event createFromParcel(Parcel in) {
            return new Event(in);
        }

        @Override
        public Event[] newArray(int size) {
            return new Event[size];
        }
    };

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

    public String getCateg() {
        return categ;
    }

    public void setCateg(String categ) {
        this.categ = categ;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(desc);
        dest.writeString(type);
        dest.writeString(image);
        dest.writeString(timestamp);
        dest.writeString(host);
        dest.writeString(time);
        dest.writeString(location);
        dest.writeString(categ);
    }
}
