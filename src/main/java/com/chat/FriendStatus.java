package com.chat;

/**
 * 친구 관계 상태
 * 카카오톡 수준의 친구 관리
 */
public enum FriendStatus {
    /**
     * 친구 요청 대기 중
     */
    PENDING,

    /**
     * 친구 수락 완료 (활성 상태)
     */
    ACCEPTED,

    /**
     * 차단된 상태
     */
    BLOCKED,

    /**
     * 삭제된 상태 (soft delete)
     */
    DELETED
}
