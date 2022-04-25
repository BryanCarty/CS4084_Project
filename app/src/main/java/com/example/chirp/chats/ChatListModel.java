package com.example.chirp.chats;

/* Used to hold the data/properties required for the chat list */

public class ChatListModel {

    private String userId;
    private final String userName;
    private final String photoName;
    private final String unreadCount;
    private final String lastMessageTime;

    public ChatListModel(String userId, String userName, String photoName, String unreadCount, String lastMessageTime) {
        this.userId = userId;
        this.userName = userName;
        this.photoName = photoName;
        this.unreadCount = unreadCount;
        this.lastMessageTime = lastMessageTime;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public String getPhotoName() {
        return photoName;
    }

    public String getUnreadCount() {
        return unreadCount;
    }

    public String getLastMessageTime() {
        return lastMessageTime;
    }

}
