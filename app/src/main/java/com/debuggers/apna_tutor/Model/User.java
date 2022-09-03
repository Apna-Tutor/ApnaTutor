package com.debuggers.apna_tutor.Model;

public class User {
    private String name;
    private String email;
    private String avatar;
    private String password;
    private String type;

    public User(String name, String email, String avatar, String password, String type) {
        this.name = name;
        this.email = email;
        this.avatar = avatar;
        this.password = password;
        this.type = type;
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
