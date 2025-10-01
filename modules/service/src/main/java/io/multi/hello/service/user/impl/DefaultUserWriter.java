package io.multi.hello.service.user.impl;

import io.multi.hello.exception.user.CantCreateUserException;
import io.multi.hello.exception.user.UserNotFoundException;
import io.multi.hello.infrastructure.user.repository.UserRepository;
import io.multi.hello.model.user.User;
import io.multi.hello.model.user.UserIdentity;
import io.multi.hello.service.user.UserWriter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * User 도메인 변경 서비스 구현체
 *
 * CQRS 패턴의 Command 책임을 구현하며,
 * Infrastructure Repository를 활용한 변경 로직을 제공합니다.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DefaultUserWriter implements UserWriter {

    private final UserRepository userRepository;

    @Override
    public User upsert(User user) {
        validateCreateUser(user);
        log.info("Upserting user: userId={}, email={}", user.getUserId(), user.getEmail());
        return userRepository.save(user);
    }


    private void validateCreateUser(User user) {
        validateUserName(user.getName());
        var users = userRepository.findByName(user.getName());
        var usersByEmail = userRepository.findByEmail(user.getEmail());

        if(!users.isEmpty()) {
            throw new CantCreateUserException(String.format("User with name '%s' already exists", user.getName()));
        }
        if(!usersByEmail.isEmpty()) {
            throw new CantCreateUserException(String.format("User with email '%s' already exists", user.getEmail()));
        }
    }

    private void validateUserName(String name) {
        if(name == null || name.isBlank()) {
            throw new CantCreateUserException(String.format("User name cannot be empty"));
        }
        if(name.length() > 20) {
            throw new CantCreateUserException(String.format("User name cannot be longer than 20 characters"));
        }
    }

    @Override
    public void deleteById(UserIdentity identity) {
        log.info("Deleting user by id: {}", identity.getUserId());
        var user = userRepository.findById(identity);
        if(user == null) {
            throw new UserNotFoundException(String.format("User with id '%s' not found", identity.getUserId()));
        }
        userRepository.deleteById(identity);
    }


}