package com.beam;

public enum RoomType {
    NORMAL("일반 채팅방"),
    SECRET("비밀 대화"),
    VOLATILE("휘발성 채팅");
    
    private final String displayName;
    
    RoomType(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
}