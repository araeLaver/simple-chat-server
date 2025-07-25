package com.chat;

public class ChatMessage {
    private String sender;
    private String content;
    private String timestamp;
    private String type;
    private String roomId;

    public ChatMessage() {}

    public ChatMessage(String sender, String content, String timestamp) {
        this.sender = sender;
        this.content = content;
        this.timestamp = timestamp;
        this.type = "message";
    }

    public ChatMessage(String sender, String content, String timestamp, String type) {
        this.sender = sender;
        this.content = content;
        this.timestamp = timestamp;
        this.type = type;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }
}