package com.example.chirp.chats;

public class MessageModel {
    private String message;
    private String messageFrom;
    private long messageTime;

    public MessageModel() {
    }

    public MessageModel(String message, String messageFrom, long messageTime) {
        this.message = message;
        this.messageFrom = messageFrom;
        this.messageTime = messageTime;
    }

    public String getMessage() {
        return message;
    }

    public String getMessageFrom() {
        return messageFrom;
    }

    public long getMessageTime() {
        return messageTime;
    }

}
