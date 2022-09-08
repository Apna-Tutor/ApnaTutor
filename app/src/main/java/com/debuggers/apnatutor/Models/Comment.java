package com.debuggers.apnatutor.Models;


import java.util.ArrayList;

import org.parceler.Parcel;

@Parcel
public class Comment {
    private String _id;
    private String userId;
    private String comment;
    private Long date;
    private ArrayList<String> likedBy;
    private ArrayList<Comment> replies;

    public Comment() {
    }

    public Comment(String userId, String comment) {
        this.userId = userId;
        this.comment = comment;
        this.date = System.currentTimeMillis();
        this.likedBy = new ArrayList<>();
        this.replies = new ArrayList<>();
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

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Long getDate() {
        return date;
    }

    public void setDate(Long date) {
        this.date = date;
    }

    public ArrayList<String> getLikedBy() {
        return likedBy;
    }

    public void setLikedBy(ArrayList<String> likedBy) {
        this.likedBy = likedBy;
    }

    public ArrayList<Comment> getReplies() {
        return replies;
    }

    public void setReplies(ArrayList<Comment> replies) {
        this.replies = replies;
    }
}
