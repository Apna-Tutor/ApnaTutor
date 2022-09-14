package com.debuggers.apnatutor.Models;

import static com.debuggers.apnatutor.App.ME;

import org.parceler.Parcel;

import java.util.Objects;

@Parcel
public class Note {
    private String _id;
    private String userId;
    private String title;
    private String description;
    private long timeStamp;

    public Note() {
    }

    public Note(String title, String description, long timeStamp) {
        this.userId = ME.get_id();
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

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Note note = (Note) o;
        return _id.equals(note._id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(_id);
    }
}
