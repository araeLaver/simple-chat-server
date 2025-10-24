package com.chat;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * 친구 관계 엔티티
 * 카카오톡의 친구 목록 기능
 */
@Entity
@Table(name = "friends", indexes = {
    @Index(name = "idx_user_friend", columnList = "user_id, friend_user_id"),
    @Index(name = "idx_user_status", columnList = "user_id, status")
})
public class FriendEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 친구를 추가한 사용자
     */
    @Column(name = "user_id", nullable = false)
    private Long userId;

    /**
     * 친구 사용자
     */
    @Column(name = "friend_user_id", nullable = false)
    private Long friendUserId;

    /**
     * 친구 관계 상태
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private FriendStatus status = FriendStatus.PENDING;

    /**
     * 친구 별명 (선택)
     */
    @Column(name = "nickname", length = 50)
    private String nickname;

    /**
     * 즐겨찾기 여부
     */
    @Column(name = "is_favorite", nullable = false)
    private Boolean isFavorite = false;

    /**
     * 생성 일시
     */
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    /**
     * 수정 일시
     */
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public FriendEntity() {
        this.createdAt = LocalDateTime.now();
    }

    public FriendEntity(Long userId, Long friendUserId, FriendStatus status) {
        this.userId = userId;
        this.friendUserId = friendUserId;
        this.status = status;
        this.createdAt = LocalDateTime.now();
    }

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getFriendUserId() {
        return friendUserId;
    }

    public void setFriendUserId(Long friendUserId) {
        this.friendUserId = friendUserId;
    }

    public FriendStatus getStatus() {
        return status;
    }

    public void setStatus(FriendStatus status) {
        this.status = status;
        this.updatedAt = LocalDateTime.now();
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
        this.updatedAt = LocalDateTime.now();
    }

    public Boolean getIsFavorite() {
        return isFavorite;
    }

    public void setIsFavorite(Boolean isFavorite) {
        this.isFavorite = isFavorite;
        this.updatedAt = LocalDateTime.now();
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
