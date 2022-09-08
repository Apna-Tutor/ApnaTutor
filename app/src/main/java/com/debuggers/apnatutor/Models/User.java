package com.debuggers.apnatutor.Models;

import org.parceler.Parcel;

@Parcel
public class User {
    public static final String USER = "USER";
    public static final String TEACHER = "TEACHER";

    private String _id;
    private String name;
    private String email;
    private String avatar;
    private String password;
    private String type;

    public User() {
    }

    public User(String name, String email, String avatar, String password) {
        this.name = name;
        this.email = email;
        this.avatar = avatar;
        this.password = password;
        this.type = USER;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
