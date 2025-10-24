package com.chat;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * 친구 요청 엔티티
 * 카카오톡의 친구 추가 요청 기능
 */
@Entity
@Table(name = "friend_requests",
    indexes = {
        @Index(name = "idx_to_user", columnList = "to_user_id, status"),
        @Index(name = "idx_from_user", columnList = "from_user_id")
    },
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_from_to_pending",
            columnNames = {"from_user_id", "to_user_id", "status"}
        )
    }
)
public class FriendRequestEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 요청을 보낸 사용자
     */
    @Column(name = "from_user_id", nullable = false)
    private Long fromUserId;

    /**
     * 요청을 받을 사용자
     */
    @Column(name = "to_user_id", nullable = false)
    private Long toUserId;

    /**
     * 요청 메시지
     */
    @Column(name = "message", length = 200)
    private String message;

    /**
     * 요청 상태
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private FriendStatus status = FriendStatus.PENDING;

    /**
     * 요청 생성 일시
     */
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    /**
     * 처리 일시 (수락/거절)
     */
    @Column(name = "processed_at")
    private LocalDateTime processedAt;

    public FriendRequestEntity() {
        this.createdAt = LocalDateTime.now();
    }

    public FriendRequestEntity(Long fromUserId, Long toUserId, String message) {
        this.fromUserId = fromUserId;
        this.toUserId = toUserId;
        this.message = message;
        this.status = FriendStatus.PENDING;
        this.createdAt = LocalDateTime.now();
    }

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getFromUserId() {
        return fromUserId;
    }

    public void setFromUserId(Long fromUserId) {
        this.fromUserId = fromUserId;
    }

    public Long getToUserId() {
        return toUserId;
    }

    public void setToUserId(Long toUserId) {
        this.toUserId = toUserId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public FriendStatus getStatus() {
        return status;
    }

    public void setStatus(FriendStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getProcessedAt() {
        return processedAt;
    }

    public void setProcessedAt(LocalDateTime processedAt) {
        this.processedAt = processedAt;
    }

    public void accept() {
        this.status = FriendStatus.ACCEPTED;
        this.processedAt = LocalDateTime.now();
    }

    public void reject() {
        this.status = FriendStatus.DELETED;
        this.processedAt = LocalDateTime.now();
    }
}
