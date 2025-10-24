package com.chat;

public class ChatMessage {
    private String sender;
    private String content;
    private String timestamp;
    private String type;
    private String roomId;
    private MessageSecurityType securityType = MessageSecurityType.NORMAL;
    private Integer volatileDuration;
    private String encryptionKey;
    private Boolean isEncrypted = false;
    
    // 방 생성용 필드들
    private String roomName;
    private String roomType;
    private String creator;
    private String description;
    private String password;

    // 1:1 채팅용 필드들
    private Long friendUserId;

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

    public MessageSecurityType getSecurityType() {
        return securityType;
    }

    public void setSecurityType(MessageSecurityType securityType) {
        this.securityType = securityType;
    }

    public Integer getVolatileDuration() {
        return volatileDuration;
    }

    public void setVolatileDuration(Integer volatileDuration) {
        this.volatileDuration = volatileDuration;
    }

    public String getEncryptionKey() {
        return encryptionKey;
    }

    public void setEncryptionKey(String encryptionKey) {
        this.encryptionKey = encryptionKey;
    }

    public Boolean getIsEncrypted() {
        return isEncrypted;
    }

    public void setIsEncrypted(Boolean isEncrypted) {
        this.isEncrypted = isEncrypted;
    }

    // 방 생성용 getter/setter
    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public String getRoomType() {
        return roomType;
    }

    public void setRoomType(String roomType) {
        this.roomType = roomType;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    // 1:1 채팅용 getter/setter
    public Long getFriendUserId() {
        return friendUserId;
    }

    public void setFriendUserId(Long friendUserId) {
        this.friendUserId = friendUserId;
    }
}