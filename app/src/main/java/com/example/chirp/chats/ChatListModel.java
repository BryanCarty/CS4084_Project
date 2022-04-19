package com.example.chirp.chats;

public class ChatListModel {

    private String userId;
    private final String userName;
    private final String photoName;
    private final String unreadCount;
    private final String lastMessage;
    private final String lastMessageTime;

    public ChatListModel(String userId, String userName, String photoName, String unreadCount, String lastMessage, String lastMessageTime) {
        this.userId = userId;
        this.userName = userName;
        this.photoName = photoName;
        this.unreadCount = unreadCount;
        this.lastMessage = lastMessage;
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

    public String getLastMessage() {
        return lastMessage;
    }

    public String getLastMessageTime() {
        return lastMessageTime;
    }

}
