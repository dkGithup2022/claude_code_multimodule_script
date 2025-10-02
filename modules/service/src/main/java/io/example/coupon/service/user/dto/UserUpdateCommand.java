package io.example.coupon.service.user.dto;

public record UserUpdateCommand(
        Long userId,
        String email,
        String name
) {
}
