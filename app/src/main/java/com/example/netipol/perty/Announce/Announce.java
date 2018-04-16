package com.example.netipol.perty.Announce;

/**
 * Created by netipol on 15/4/2018 AD.
 */

public class Announce extends AnnounceId implements Comparable<Announce>{

    private String uid;
    private String eid;
    private String message;
    private String timestamp;

    public Announce(){

    }

    public Announce(String uid, String eid, String message, String timestamp) {
        this.uid = uid;
        this.eid = eid;
        this.message = message;
        this.timestamp = timestamp;

    }

public int compareTo(Announce other) {
        return timestamp.compareTo(other.timestamp);
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getEid() {
        return eid;
    }

    public void setEid(String eid) {
        this.eid = eid;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;}
}
