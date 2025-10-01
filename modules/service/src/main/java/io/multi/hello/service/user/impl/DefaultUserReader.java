package io.multi.hello.service.user.impl;

import io.multi.hello.infrastructure.user.repository.UserRepository;
import io.multi.hello.model.user.User;
import io.multi.hello.model.user.UserIdentity;
import io.multi.hello.service.user.UserReader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * User 도메인 조회 서비스 구현체
 * <p>
 * CQRS 패턴의 Query 책임을 구현하며,
 * Infrastructure Repository를 활용한 조회 로직을 제공합니다.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DefaultUserReader implements UserReader {

    private final UserRepository userRepository;

    @Override
    public Optional<User> findById(UserIdentity identity) {
        log.debug("Finding user by id: {}", identity.getUserId());
        return userRepository.findById(identity);
    }

    @Override
    public User findByName(String name) {
        log.debug("Finding users by name: {}", name);
        var users = userRepository.findByName(name);
        return users.size() != 0 ? users.getFirst() : null;
    }

    @Override
    public User findByEmail(String email) {
        log.debug("Finding user by email: {}", email);
        var users = userRepository.findByEmail(email);
        return users.size() != 0 ? users.getFirst() : null;
    }

    @Override
    public boolean existsById(UserIdentity identity) {
        log.debug("Checking user exists by id: {}", identity.getUserId());
        return userRepository.existsById(identity);
    }
}