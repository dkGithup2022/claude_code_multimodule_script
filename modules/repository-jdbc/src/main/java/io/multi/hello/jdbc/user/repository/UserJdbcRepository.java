package io.multi.hello.jdbc.user.repository;

import io.multi.hello.infrastructure.user.repository.UserRepository;
import io.multi.hello.model.user.User;
import io.multi.hello.model.user.UserIdentity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * User JDBC Repository 구현체
 */
@Repository
@RequiredArgsConstructor
public class UserJdbcRepository implements UserRepository {

    private final UserEntityRepository entityRepository;

    @Override
    public Optional<User> findById(UserIdentity identity) {
        return entityRepository.findById(identity.getUserId())
                .map(this::toModel);
    }

    @Override
    public List<User> findByName(String name) {
        return entityRepository.findByName(name).stream()
                .map(this::toModel)
                .collect(Collectors.toList());
    }

    @Override
    public List<User> findByEmail(String email) {
        return entityRepository.findByEmail(email).stream()
                .map(this::toModel)
                .collect(Collectors.toList());
    }

    @Override
    public User save(User user) {
        UserEntity entity = toEntity(user);
        UserEntity saved = entityRepository.save(entity);
        return toModel(saved);
    }

    @Override
    public void deleteById(UserIdentity identity) {
        entityRepository.deleteById(identity.getUserId());
    }

    @Override
    public boolean existsById(UserIdentity identity) {
        return entityRepository.existsById(identity.getUserId());
    }

    // Entity <-> Model 변환
    private User toModel(UserEntity entity) {
        return new User(
                entity.getUserId(),
                entity.getEmail(),
                entity.getName(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    private UserEntity toEntity(User model) {
        return new UserEntity(
                model.getUserId(),
                model.getEmail(),
                model.getName(),
                model.getCreatedAt(),
                model.getUpdatedAt()
        );
    }
}