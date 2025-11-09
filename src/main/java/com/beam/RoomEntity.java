package com.beam;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "rooms", indexes = {
    @Index(name = "idx_room_type", columnList = "roomType"),
    @Index(name = "idx_created_by", columnList = "createdBy")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String roomName;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private RoomType roomType = RoomType.PUBLIC;

    @Column(nullable = false)
    private Long createdBy;

    @Column(nullable = false)
    @Builder.Default
    private Integer maxMembers = 100;

    @Column(nullable = false)
    @Builder.Default
    private Integer currentMembers = 0;

    @Column(length = 500)
    private String roomImageUrl;

    @Column(nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    @Column(columnDefinition = "TEXT")
    private String lastMessage;

    @Column
    private LocalDateTime lastMessageTime;

    @Column
    private Long lastMessageSenderId;

    @Column(nullable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column
    private LocalDateTime updatedAt;

    public enum RoomType {
        PUBLIC,
        PRIVATE,
        SECRET
    }

    @PrePersist
    public void prePersist() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
        if (this.isActive == null) {
            this.isActive = true;
        }
        if (this.roomType == null) {
            this.roomType = RoomType.PUBLIC;
        }
        if (this.maxMembers == null) {
            this.maxMembers = 100;
        }
        if (this.currentMembers == null) {
            this.currentMembers = 0;
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public void incrementMemberCount() {
        this.currentMembers++;
    }

    public void decrementMemberCount() {
        if (this.currentMembers > 0) {
            this.currentMembers--;
        }
    }
}