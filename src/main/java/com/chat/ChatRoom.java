package com.chat;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ChatRoom {
    private String roomId;
    private String roomName;
    private RoomType roomType = RoomType.NORMAL;
    private String encryptionKey;
    private String password;
    private String creator;
    private String description;
    private Map<String, User> users;
    private long createdTime;

    public ChatRoom() {
        this.users = new ConcurrentHashMap<>();
    }

    public ChatRoom(String roomId, String roomName) {
        this.roomId = roomId;
        this.roomName = roomName;
        this.users = new ConcurrentHashMap<>();
        this.createdTime = System.currentTimeMillis();
    }
    
    public ChatRoom(String roomId, String roomName, RoomType roomType, String password) {
        this.roomId = roomId;
        this.roomName = roomName;
        this.roomType = roomType;
        // SECURITY: 비밀번호 BCrypt 해싱 (카카오톡 수준)
        this.password = (password != null && !password.isEmpty()) ? PasswordUtil.hashPassword(password) : null;
        this.users = new ConcurrentHashMap<>();
        this.createdTime = System.currentTimeMillis();

        if (roomType == RoomType.SECRET) {
            // 암호화 키는 원본 비밀번호로 생성 (해시 전)
            this.encryptionKey = EncryptionUtil.generateRoomKey(roomId, password != null ? password : "");
        }
    }

    public ChatRoom(String roomId, String roomName, RoomType roomType, String password, String creator, String description) {
        this.roomId = roomId;
        this.roomName = roomName;
        this.roomType = roomType;
        // SECURITY: 비밀번호 BCrypt 해싱 (카카오톡 수준)
        String originalPassword = password;  // 암호화 키 생성용
        this.password = (password != null && !password.isEmpty()) ? PasswordUtil.hashPassword(password) : null;
        this.creator = creator;
        this.description = description;
        this.users = new ConcurrentHashMap<>();
        this.createdTime = System.currentTimeMillis();

        if (roomType == RoomType.SECRET) {
            // 암호화 키는 원본 비밀번호로 생성 (해시 전)
            this.encryptionKey = EncryptionUtil.generateRoomKey(roomId, originalPassword != null ? originalPassword : "");
        }
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public Map<String, User> getUsers() {
        return users;
    }

    public void setUsers(Map<String, User> users) {
        this.users = users;
    }

    public long getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(long createdTime) {
        this.createdTime = createdTime;
    }

    public void addUser(User user) {
        users.put(user.getSessionId(), user);
    }

    public User removeUser(String sessionId) {
        return users.remove(sessionId);
    }

    public int getUserCount() {
        return users.size();
    }

    public RoomType getRoomType() {
        return roomType;
    }

    public void setRoomType(RoomType roomType) {
        this.roomType = roomType;
    }

    public String getEncryptionKey() {
        return encryptionKey;
    }

    public void setEncryptionKey(String encryptionKey) {
        this.encryptionKey = encryptionKey;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    
    public boolean isSecureRoom() {
        return roomType == RoomType.SECRET || roomType == RoomType.VOLATILE;
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

    /**
     * 비밀번호 검증 (BCrypt 사용)
     * @param inputPassword 사용자가 입력한 평문 비밀번호
     * @return 일치 여부
     */
    public boolean verifyPassword(String inputPassword) {
        if (password == null) return true;
        // SECURITY: BCrypt로 안전하게 비교 (카카오톡 수준)
        return PasswordUtil.verifyPassword(inputPassword, password);
    }
}