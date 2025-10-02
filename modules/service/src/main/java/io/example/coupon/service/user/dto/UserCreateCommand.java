package io.example.coupon.service.user.dto;

public record UserCreateCommand(
        String email,
        String name
) {
}
