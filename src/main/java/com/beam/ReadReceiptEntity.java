package com.beam;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "read_receipts", indexes = {
    @Index(name = "idx_message_user", columnList = "messageId,userId"),
    @Index(name = "idx_group_message_user", columnList = "groupMessageId,userId")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReadReceiptEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private Long messageId;

    @Column
    private Long groupMessageId;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    @Builder.Default
    private LocalDateTime readAt = LocalDateTime.now();

    @PrePersist
    public void prePersist() {
        if (this.readAt == null) {
            this.readAt = LocalDateTime.now();
        }
    }
}