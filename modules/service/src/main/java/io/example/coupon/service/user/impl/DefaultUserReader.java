package io.example.coupon.service.user.impl;

import io.example.coupon.infrastructure.user.repository.UserRepository;
import io.example.coupon.model.user.User;
import io.example.coupon.model.user.UserIdentity;
import io.example.coupon.service.user.UserReader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class DefaultUserReader implements UserReader {
    private final UserRepository userRepository;

    @Override
    public User findById(UserIdentity identity) {
        var optional = userRepository.findById(identity);
        return optional.orElse(null);

    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public User findByEmail(String email) {
        var users = userRepository.findByEmail(email);
        return users.orElse(null);
    }

    @Override
    public User findByName(String name) {
        var users = userRepository.findByName(name);
        if (users.isEmpty()) {
            throw new RuntimeException("User with name " + name + " not found");
        }
        if (users.size() > 1) {
            throw new RuntimeException("User with name " + name + " has more than one user");
        }
        return users.getFirst();
    }
}
