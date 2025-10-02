package io.example.coupon.infrastructure.user.repository.jdbc;

import io.example.coupon.infrastructure.user.repository.UserRepository;
import io.example.coupon.model.user.User;
import io.example.coupon.model.user.UserIdentity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

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
    public List<User> findAll() {
        return StreamSupport.stream(entityRepository.findAll().spliterator(), false)
                .map(this::toModel)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return entityRepository.findByEmail(email)
                .map(this::toModel);
    }

    @Override
    public List<User> findByName(String name) {
        return entityRepository.findByName(name).stream()
                .map(this::toModel)
                .collect(Collectors.toList());
    }

    @Override
    public User save(User user) {
        UserEntity entity = toEntity(user);

        if (entity.getCreatedAt() == null) {
            entity.setCreatedAt(Instant.now());
        }
        entity.setUpdatedAt(Instant.now());

        UserEntity saved = entityRepository.save(entity);
        return toModel(saved);
    }

    @Override
    public void deleteById(UserIdentity identity) {
        entityRepository.deleteById(identity.getUserId());
    }

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
