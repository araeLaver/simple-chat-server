package com.beam;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "conversations", indexes = {
    @Index(name = "idx_user1_user2", columnList = "user1Id,user2Id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConversationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String conversationId;

    @Column(nullable = false)
    private Long user1Id;

    @Column(nullable = false)
    private Long user2Id;

    @Column(columnDefinition = "TEXT")
    private String lastMessage;

    @Column
    private LocalDateTime lastMessageTime;

    @Column
    private Long lastMessageSenderId;

    @Column(nullable = false)
    @Builder.Default
    private Integer unreadCountUser1 = 0;

    @Column(nullable = false)
    @Builder.Default
    private Integer unreadCountUser2 = 0;

    @Column(nullable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
        if (this.unreadCountUser1 == null) {
            this.unreadCountUser1 = 0;
        }
        if (this.unreadCountUser2 == null) {
            this.unreadCountUser2 = 0;
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public void incrementUnreadCount(Long userId) {
        if (userId.equals(user1Id)) {
            this.unreadCountUser1++;
        } else if (userId.equals(user2Id)) {
            this.unreadCountUser2++;
        }
    }

    public void resetUnreadCount(Long userId) {
        if (userId.equals(user1Id)) {
            this.unreadCountUser1 = 0;
        } else if (userId.equals(user2Id)) {
            this.unreadCountUser2 = 0;
        }
    }

    public Integer getUnreadCount(Long userId) {
        if (userId.equals(user1Id)) {
            return unreadCountUser1;
        } else if (userId.equals(user2Id)) {
            return unreadCountUser2;
        }
        return 0;
    }
}