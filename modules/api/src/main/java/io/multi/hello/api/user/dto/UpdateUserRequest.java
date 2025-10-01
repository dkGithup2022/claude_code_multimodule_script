package io.multi.hello.api.user.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * 사용자 수정 요청 DTO
 */
public record UpdateUserRequest(
        @NotBlank(message = "Name is required")
        String name
) {
}