package com.example.chirp.chats;


public class MessageModel {
    private String message;
    private String messageFrom;
    private long messageTime;

    /*
     Required empty constructor
     If not here, there will be a runtime exception
     for datasnapshot.getValue in onChildAdded within chat activity
    */
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
