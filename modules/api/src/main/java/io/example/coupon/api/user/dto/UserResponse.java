package io.example.coupon.api.user.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.example.coupon.model.user.User;
import lombok.Getter;

import java.time.Instant;

/**
 * User 응답 DTO
 */
@Getter
public class UserResponse {

    private final Long userId;
    private final String email;
    private final String name;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
    private final Instant createdAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
    private final Instant updatedAt;

    public UserResponse(Long userId, String email, String name, Instant createdAt, Instant updatedAt) {
        this.userId = userId;
        this.email = email;
        this.name = name;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

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
