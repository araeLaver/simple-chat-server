package com.beam;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "file_metadata", indexes = {
    @Index(name = "idx_uploader", columnList = "uploaderId"),
    @Index(name = "idx_file_conversation", columnList = "conversationId"),
    @Index(name = "idx_room", columnList = "roomId")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FileMetadataEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 500)
    private String fileName;

    @Column(nullable = false, length = 500)
    private String filePath;

    @Column(nullable = false, length = 100)
    private String fileType;

    @Column(nullable = false)
    private Long fileSize;

    @Column(nullable = false)
    private Long uploaderId;

    @Column(length = 100)
    private String conversationId;

    @Column
    private Long roomId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private FileCategory category = FileCategory.OTHER;

    @Column(length = 500)
    private String thumbnailPath;

    @Column(nullable = false)
    @Builder.Default
    private Integer downloadCount = 0;

    @Column
    private LocalDateTime expiresAt;

    @Column(nullable = false)
    @Builder.Default
    private Boolean isDeleted = false;

    @Column
    private LocalDateTime deletedAt;

    @Column(nullable = false)
    @Builder.Default
    private LocalDateTime uploadedAt = LocalDateTime.now();

    public enum FileCategory {
        IMAGE,
        VIDEO,
        AUDIO,
        DOCUMENT,
        OTHER
    }

    @PrePersist
    public void prePersist() {
        if (this.uploadedAt == null) {
            this.uploadedAt = LocalDateTime.now();
        }
        if (this.category == null) {
            this.category = FileCategory.OTHER;
        }
        if (this.downloadCount == null) {
            this.downloadCount = 0;
        }
        if (this.isDeleted == null) {
            this.isDeleted = false;
        }
    }

    public void incrementDownloadCount() {
        this.downloadCount++;
    }

    public static FileCategory getCategoryFromMimeType(String mimeType) {
        if (mimeType == null) return FileCategory.OTHER;

        if (mimeType.startsWith("image/")) return FileCategory.IMAGE;
        if (mimeType.startsWith("video/")) return FileCategory.VIDEO;
        if (mimeType.startsWith("audio/")) return FileCategory.AUDIO;
        if (mimeType.startsWith("application/pdf") ||
            mimeType.startsWith("application/msword") ||
            mimeType.startsWith("application/vnd.")) return FileCategory.DOCUMENT;

        return FileCategory.OTHER;
    }
}