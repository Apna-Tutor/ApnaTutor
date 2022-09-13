package com.debuggers.apnatutor.Models;

import static com.debuggers.apnatutor.App.ME;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import org.parceler.Parcel;

@Parcel
public class Course {
    private String _id;
    private String author;
    private String title;
    private String description;
    private String thumbnail;
    private ArrayList<String> videos;
    private ArrayList<String> followedBy;
    private HashMap<String, String> leaderBoard;

    public Course() {
    }

    public Course(String title, String description, String thumbnail) {
        this.author = ME.get_id();
        this.title = title;
        this.description = description;
        this.thumbnail = thumbnail;
        this.videos = new ArrayList<>();
        this.followedBy = new ArrayList<>();
        this.leaderBoard = new HashMap<>();
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
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

    public ArrayList<String> getVideos() {
        return videos;
    }

    public void setVideos(ArrayList<String> videos) {
        this.videos = videos;
    }

    public ArrayList<String> getFollowedBy() {
        return followedBy;
    }

    public void setFollowedBy(ArrayList<String> followedBy) {
        this.followedBy = followedBy;
    }

    public HashMap<String, String> getLeaderBoard() {
        return leaderBoard;
    }

    public void setLeaderBoard(HashMap<String, String> leaderBoard) {
        this.leaderBoard = leaderBoard;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Course course = (Course) o;
        return _id.equals(course._id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(_id);
    }
}
