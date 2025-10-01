package io.multi.hello.api.link.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * 링크 생성 요청 DTO
 */
public record CreateLinkRequest(
        @NotBlank(message = "URL is required")
        String url,

        @NotNull(message = "User ID is required")
        Long userId
) {
}