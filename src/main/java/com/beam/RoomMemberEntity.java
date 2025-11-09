package com.beam;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "room_members", indexes = {
    @Index(name = "idx_room_user", columnList = "roomId,userId"),
    @Index(name = "idx_user_rooms", columnList = "userId")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomMemberEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long roomId;

    @Column(nullable = false)
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private MemberRole role = MemberRole.MEMBER;

    @Column(nullable = false)
    @Builder.Default
    private Boolean isMuted = false;

    @Column
    private LocalDateTime mutedUntil;

    @Column(nullable = false)
    @Builder.Default
    private Integer unreadCount = 0;

    @Column
    private LocalDateTime lastReadTime;

    @Column(nullable = false)
    @Builder.Default
    private LocalDateTime joinedAt = LocalDateTime.now();

    @Column
    private LocalDateTime leftAt;

    @Column(nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    public enum MemberRole {
        OWNER,
        ADMIN,
        MEMBER
    }

    @PrePersist
    public void prePersist() {
        if (this.joinedAt == null) {
            this.joinedAt = LocalDateTime.now();
        }
        if (this.role == null) {
            this.role = MemberRole.MEMBER;
        }
        if (this.isMuted == null) {
            this.isMuted = false;
        }
        if (this.unreadCount == null) {
            this.unreadCount = 0;
        }
        if (this.isActive == null) {
            this.isActive = true;
        }
    }

    public void resetUnreadCount() {
        this.unreadCount = 0;
        this.lastReadTime = LocalDateTime.now();
    }

    public void incrementUnreadCount() {
        this.unreadCount++;
    }
}