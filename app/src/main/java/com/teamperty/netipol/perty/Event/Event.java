package com.teamperty.netipol.perty.Event;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by netipol on 1/2/2018 AD.
 */

public class Event extends EventId implements Parcelable, Comparable<Event>{

    private String title, desc, date_start, date_end, type, image, timestamp, hostid, time_start, time_end, loca_desc, loca_preset, categ;

    public int compareTo(Event other) {
        return timestamp.compareTo(other.timestamp);
    }

    public Event(){

    }

    public Event(String title, String desc, String date_start, String date_end, String type, String image, String timestamp, String hostid, String time_start, String time_end, String loca_desc, String loca_preset, String categ) {
        this.title = title;
        this.desc = desc;
        this.type = type;
        this.image = image;
        this.timestamp = timestamp;
        this.hostid = hostid;
        this.date_start = date_start;
        this.date_end = date_end;
        this.time_end= time_end;
        this.time_start=time_start;
        this.loca_desc=loca_desc;
        this.loca_preset=loca_preset;
        this.categ = categ;
    }

    protected Event(Parcel in) {
        title = in.readString();
        desc = in.readString();
        type = in.readString();
        image = in.readString();
        timestamp = in.readString();
        hostid = in.readString();
        time_start = in.readString();
        time_end = in.readString();
        loca_preset = in.readString();
        loca_desc= in.readString();
        date_start = in.readString();
        date_end = in.readString();
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


    public String getHostId() {
        return hostid;
    }


    public void setHostId(String hostid) {
        this.hostid = hostid;
    }

    public String getTime_end() {
        return time_end;
    }

    public void setTime_end(String time_end) {
        this.time_end = time_end;
    }

    public String getLoca_desc() {
        return loca_desc;
    }

    public void setLoca_desc(String loca_desc) {
        this.loca_desc = loca_desc;
    }


    public String getDate_start() {
        return date_start;
    }

    public void setDate_start(String date_start) {
        this.date_start = date_start;
    }

    public String getDate_end() {
        return date_end;
    }

    public void setDate_end(String date_end) {
        this.date_end = date_end;
    }

    public String getTime_start() {
        return time_start;
    }

    public void setTime_start(String time_start) {
        this.time_start = time_start;
    }

    public String getLoca_preset() {
        return loca_preset;
    }

    public void setLoca_preset(String loca_preset) {
        this.loca_preset = loca_preset;
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
        dest.writeString(hostid);
        dest.writeString(time_start);
        dest.writeString(time_end);
        dest.writeString(loca_preset);
        dest.writeString(loca_desc);
        dest.writeString(categ);
        dest.writeString(date_start);
        dest.writeString(date_end);
    }
}
