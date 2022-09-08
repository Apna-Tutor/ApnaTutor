package com.debuggers.apnatutor.Models;

import org.parceler.Parcel;

@Parcel
public class Note {
    private String _id;
    private String userId;
    private String title;
    private String description;
    private int timeStamp;

    public Note() {
    }

    public Note(String userId, String title, String description, int timeStamp) {
        this.userId = userId;
        this.title = title;
        this.description = description;
        this.timeStamp = timeStamp;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(int timeStamp) {
        this.timeStamp = timeStamp;
    }
}
