package com.debuggers.apna_tutor.Models;

import java.util.ArrayList;
import java.util.Date;

public class Comment {
    private String _id;
    private String userId;
    private String comment;
    private Date date;
    private ArrayList<String> likedBy;
    private ArrayList<Comment> replies;

    public Comment(String _id, String userId, String comment, Date date, ArrayList<String> likedBy, ArrayList<Comment> replies) {
        this.userId = userId;
        this.comment = comment;
        this.date = date;
        this.likedBy = likedBy;
        this.replies = replies;
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

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
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
