package com.beam.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

/**
 * Update Room Request DTO
 *
 * @since 1.2.0
 */
@Data
public class UpdateRoomRequest {

    @Size(min = 2, max = 50, message = "방 이름은 2-50자 사이여야 합니다")
    private String roomName;

    @Size(max = 200, message = "설명은 200자를 초과할 수 없습니다")
    private String description;

    @Min(value = 2, message = "최소 인원은 2명입니다")
    @Max(value = 1000, message = "최대 인원은 1000명입니다")
    private Integer maxMembers;
}
