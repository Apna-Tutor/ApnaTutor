package com.debuggers.apna_tutor.Models;

import java.util.ArrayList;
import java.util.Date;

public class Video {
    private String _id;
    private String title;
    private String description;
    private String thumbnail;
    private String videoUrl;
    private Date date;
    private ArrayList<String> likedBy;
    private ArrayList<String> viewedBy;
    private ArrayList<Comment> comments;
    private ArrayList<Note> notes;
    private ArrayList<Quiz> quiz;

    public Video(String _id, String title, String description, String thumbnail, String videoUrl, Date date, ArrayList<String> likedBy, ArrayList<String> viewedBy, ArrayList<Comment> comments, ArrayList<Note> notes, ArrayList<Quiz> quiz) {
        this.title = title;
        this.description = description;
        this.thumbnail = thumbnail;
        this.videoUrl = videoUrl;
        this.date = date;
        this.likedBy = likedBy;
        this.viewedBy = viewedBy;
        this.comments = comments;
        this.notes = notes;
        this.quiz = quiz;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
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

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
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

    public ArrayList<String> getViewedBy() {
        return viewedBy;
    }

    public void setViewedBy(ArrayList<String> viewedBy) {
        this.viewedBy = viewedBy;
    }

    public ArrayList<Comment> getComments() {
        return comments;
    }

    public void setComments(ArrayList<Comment> comments) {
        this.comments = comments;
    }

    public ArrayList<Note> getNotes() {
        return notes;
    }

    public void setNotes(ArrayList<Note> notes) {
        this.notes = notes;
    }

    public ArrayList<Quiz> getQuiz() {
        return quiz;
    }

    public void setQuiz(ArrayList<Quiz> quiz) {
        this.quiz = quiz;
    }
}
