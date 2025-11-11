package com.beam.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

/**
 * Send Message Request DTO
 *
 * @since 1.2.0
 */
@Data
public class SendMessageRequest {

    @NotBlank(message = "메시지 내용은 필수입니다")
    @Size(max = 5000, message = "메시지는 5000자를 초과할 수 없습니다")
    private String content;

    @Pattern(regexp = "TEXT|IMAGE|FILE|LINK", message = "메시지 타입은 TEXT, IMAGE, FILE, LINK만 가능합니다")
    private String messageType = "TEXT";
}
