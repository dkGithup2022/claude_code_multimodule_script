package io.multi.hello.api.user.dto;

import io.multi.hello.model.user.User;

import java.time.Instant;

/**
 * 사용자 응답 DTO
 */
public record UserResponse(
        Long userId,
        String email,
        String name,
        Instant createdAt,
        Instant updatedAt
) {
    /**
     * User 도메인 모델로부터 DTO 생성
     */
    public static UserResponse from(User user) {
        return new UserResponse(
                user.getUserId(),
                user.getEmail(),
                user.getName(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }
}