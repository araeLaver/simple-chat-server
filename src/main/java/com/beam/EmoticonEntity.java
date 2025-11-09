package com.beam;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "emoticons", indexes = {
    @Index(name = "idx_category", columnList = "category"),
    @Index(name = "idx_korean_name", columnList = "koreanName")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmoticonEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 100)
    private String koreanName;

    @Column(nullable = false, length = 10)
    private String emoji;

    @Column(length = 50)
    private String category;

    @Column(length = 200)
    private String keywords;

    @Column(nullable = false)
    @Builder.Default
    private Boolean isPopular = false;

    @Column(nullable = false)
    @Builder.Default
    private Integer usageCount = 0;

    @Column(nullable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @PrePersist
    public void prePersist() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
        if (this.isPopular == null) {
            this.isPopular = false;
        }
        if (this.usageCount == null) {
            this.usageCount = 0;
        }
    }

    public void incrementUsage() {
        this.usageCount++;
    }
}