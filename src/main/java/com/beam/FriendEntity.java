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
public class FriendEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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
