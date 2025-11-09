<<<<<<< HEAD:src/main/java/com/beam/FriendEntity.java
package com.beam;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "friends", indexes = {
    @Index(name = "idx_user_friend", columnList = "userId,friendId"),
    @Index(name = "idx_status", columnList = "status")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
=======
package com.chat;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "friends", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"user_id", "friend_id"})
})
>>>>>>> 9106cb986b257fdd9ab7197fea5c599fbc536571:src/main/java/com/chat/FriendEntity.java
public class FriendEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

<<<<<<< HEAD:src/main/java/com/beam/FriendEntity.java
    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Long friendId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private FriendStatus status = FriendStatus.PENDING;

    @Column
    private LocalDateTime requestedAt;

    @Column
    private LocalDateTime acceptedAt;

    @Column
    private LocalDateTime blockedAt;

    @Column(nullable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column
    private LocalDateTime updatedAt;

    public enum FriendStatus {
        PENDING,
        ACCEPTED,
        REJECTED,
        BLOCKED
    }

    @PrePersist
    public void prePersist() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
        if (this.status == null) {
            this.status = FriendStatus.PENDING;
        }
        if (this.requestedAt == null && this.status == FriendStatus.PENDING) {
            this.requestedAt = LocalDateTime.now();
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
        if (this.status == FriendStatus.ACCEPTED && this.acceptedAt == null) {
            this.acceptedAt = LocalDateTime.now();
        }
        if (this.status == FriendStatus.BLOCKED && this.blockedAt == null) {
            this.blockedAt = LocalDateTime.now();
        }
    }
}
=======
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "friend_id", nullable = false)
    private Long friendId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private FriendStatus status = FriendStatus.PENDING;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "accepted_at")
    private LocalDateTime acceptedAt;

    public FriendEntity() {
        this.createdAt = LocalDateTime.now();
    }

    public FriendEntity(Long userId, Long friendId) {
        this.userId = userId;
        this.friendId = friendId;
        this.createdAt = LocalDateTime.now();
        this.status = FriendStatus.PENDING;
    }

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

    public Long getFriendId() {
        return friendId;
    }

    public void setFriendId(Long friendId) {
        this.friendId = friendId;
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

    public LocalDateTime getAcceptedAt() {
        return acceptedAt;
    }

    public void setAcceptedAt(LocalDateTime acceptedAt) {
        this.acceptedAt = acceptedAt;
    }

    public void accept() {
        this.status = FriendStatus.ACCEPTED;
        this.acceptedAt = LocalDateTime.now();
    }

    public void block() {
        this.status = FriendStatus.BLOCKED;
    }
}
>>>>>>> 9106cb986b257fdd9ab7197fea5c599fbc536571:src/main/java/com/chat/FriendEntity.java
