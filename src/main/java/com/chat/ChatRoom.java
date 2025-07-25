package com.chat;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ChatRoom {
    private String roomId;
    private String roomName;
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
}