package com.beam.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

/**
 * Create Room Request DTO
 *
 * @since 1.2.0
 */
@Data
public class CreateRoomRequest {

    @NotBlank(message = "방 이름은 필수입니다")
    @Size(min = 2, max = 50, message = "방 이름은 2-50자 사이여야 합니다")
    private String roomName;

    @Size(max = 200, message = "설명은 200자를 초과할 수 없습니다")
    private String description;

    @Pattern(regexp = "PUBLIC|PRIVATE", message = "방 타입은 PUBLIC 또는 PRIVATE만 가능합니다")
    private String roomType = "PUBLIC";

    @Min(value = 2, message = "최소 인원은 2명입니다")
    @Max(value = 1000, message = "최대 인원은 1000명입니다")
    private Integer maxMembers = 100;
}
