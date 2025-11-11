package com.beam.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

/**
 * Friend Request DTO
 *
 * @since 1.2.0
 */
@Data
public class FriendRequestDto {

    @NotNull(message = "친구 ID는 필수입니다")
    @Positive(message = "유효한 사용자 ID를 입력하세요")
    private Long friendId;
}
