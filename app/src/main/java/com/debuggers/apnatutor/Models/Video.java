package com.debuggers.apnatutor.Models;

import java.util.ArrayList;
import java.util.List;
import java.util.List;
import java.util.Objects;

import org.parceler.Parcel;

@Parcel
public class Video {
    private String _id;
    private String title;
    private String description;
    private String thumbnail;
    private String videoUrl;
    private Long date;
    private List<String> likedBy;
    private List<String> viewedBy;
    private List<Comment> comments;
    private List<Note> notes;
    private List<Quiz> quiz;

    public Video() {
    }

    public Video(String title, String description, String thumbnail, String videoUrl, List<Quiz> quizzes) {
        this.title = title;
        this.description = description;
        this.thumbnail = thumbnail;
        this.videoUrl = videoUrl;
        this.date = System.currentTimeMillis();
        this.quiz = quizzes;
        this.likedBy = new ArrayList<>();
        this.viewedBy = new ArrayList<>();
        this.comments = new ArrayList<>();
        this.notes = new ArrayList<>();
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

    public Long getDate() {
        return date;
    }

    public void setDate(Long date) {
        this.date = date;
    }

    public List<String> getLikedBy() {
        return likedBy;
    }

    public void setLikedBy(List<String> likedBy) {
        this.likedBy = likedBy;
    }

    public List<String> getViewedBy() {
        return viewedBy;
    }

    public void setViewedBy(List<String> viewedBy) {
        this.viewedBy = viewedBy;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public List<Note> getNotes() {
        return notes;
    }

    public void setNotes(List<Note> notes) {
        this.notes = notes;
    }

    public List<Quiz> getQuiz() {
        return quiz;
    }

    public void setQuiz(List<Quiz> quiz) {
        this.quiz = quiz;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Video video = (Video) o;
        return _id.equals(video._id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(_id);
    }
}
