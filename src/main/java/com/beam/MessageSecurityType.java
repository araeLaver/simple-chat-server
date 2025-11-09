package com.beam;

public enum MessageSecurityType {
    NORMAL("일반"),
    SECRET("비밀 대화"),
    VOLATILE("임시 메시지");
    
    private final String displayName;
    
    MessageSecurityType(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
}