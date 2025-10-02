package io.example.coupon.service.user.impl;

import io.example.coupon.infrastructure.user.repository.UserRepository;
import io.example.coupon.model.user.User;
import io.example.coupon.model.user.UserIdentity;
import io.example.coupon.service.user.UserWriter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
@RequiredArgsConstructor
@Slf4j
public class DefaultUserWriter implements UserWriter {
    private final UserRepository userRepository;

    @Override
    public User create(User user) {
        log.info("Creating user: email={}", user.getEmail());

        // 이메일 중복 체크
        var existing = userRepository.findByEmail(user.getEmail());
        if (existing.isPresent()) {
            throw new RuntimeException("User with email " + user.getEmail() + " already exists");
        }

        // createdAt, updatedAt 자동 설정
        User newUser = new User(
                null,
                user.getEmail(),
                user.getName(),
                Instant.now(),
                Instant.now()
        );

        return userRepository.save(newUser);
    }

    @Override
    public User update(User user) {
        log.info("Updating user: userId={}", user.getUserId());

        // 존재 여부 확인
        var existing = userRepository.findById(new UserIdentity(user.getUserId()));
        if (existing.isEmpty()) {
            throw new RuntimeException("User with id " + user.getUserId() + " not found");
        }

        // updatedAt 갱신
        User updatedUser = new User(
                user.getUserId(),
                user.getEmail(),
                user.getName(),
                existing.get().getCreatedAt(),
                Instant.now()
        );

        return userRepository.save(updatedUser);
    }

    @Override
    public void deleteById(UserIdentity identity) {
        log.info("Deleting user: userId={}", identity.getUserId());

        // 존재 여부 확인
        var existing = userRepository.findById(identity);
        if (existing.isEmpty()) {
            throw new RuntimeException("User with id " + identity.getUserId() + " not found");
        }

        userRepository.deleteById(identity);
    }
}
