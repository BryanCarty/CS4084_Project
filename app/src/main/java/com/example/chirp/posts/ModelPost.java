package com.example.chirp.posts;

import com.google.firebase.Timestamp;

public class ModelPost {

    public String title;
    public String content;
    public Long timeSent;
    public String userID;
    public String userName;
    public String userImage;

    public ModelPost() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getTimeSent() {
        return timeSent;
    }

    public void setTimeSent(Long timeSent) {
        this.timeSent = timeSent;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserImage() {
        return userImage;
    }

    public void setUserImage(String userImage) {
        this.userImage = userImage;
    }

    public ModelPost(String title, String content, Long timeSent, String userID, String userName, String userImage) {
        this.title = title;
        this.content = content;
        this.timeSent = timeSent;
        this.userID = userID;
        this.userName = userName;
        this.userImage = userImage;
    }

}
