package io.multi.hello.jdbc.link.repository;

import io.multi.hello.infrastructure.link.repository.LinkRepository;
import io.multi.hello.model.link.Link;
import io.multi.hello.model.link.LinkIdentity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Link JDBC Repository 구현체
 *
 * Infrastructure의 LinkRepository 인터페이스를 구현하여
 * Spring Data JDBC와 도메인 모델을 연결합니다.
 */
@Repository
@RequiredArgsConstructor
public class LinkJdbcRepository implements LinkRepository {

    private final LinkEntityRepository entityRepository;

    @Override
    public Optional<Link> findById(LinkIdentity identity) {
        return entityRepository.findById(identity.getLinkId())
                .map(this::toModel);
    }

    @Override
    public List<Link> findByUserId(Long userId) {
        return entityRepository.findByUserId(userId).stream()
                .map(this::toModel)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Link> findByOriginalUrl(String originalUrl) {
        return entityRepository.findByOriginalUrl(originalUrl)
                .map(this::toModel);
    }

    @Override
    public Optional<Link> findByShortCode(String shortCode) {
        return entityRepository.findByShortCode(shortCode)
                .map(this::toModel);
    }

    @Override
    public Link save(Link link) {
        LinkEntity entity = toEntity(link);
        LinkEntity saved = entityRepository.save(entity);
        return toModel(saved);
    }

    @Override
    public void deleteById(LinkIdentity identity) {
        entityRepository.deleteById(identity.getLinkId());
    }

    @Override
    public boolean existsById(LinkIdentity identity) {
        return entityRepository.existsById(identity.getLinkId());
    }

    @Override
    public boolean existsByShortCode(String shortCode) {
        return entityRepository.existsByShortCode(shortCode);
    }

    @Override
    public List<Link> findAll() {
        return StreamSupport.stream(entityRepository.findAll().spliterator(), false)
                .map(this::toModel)
                .collect(Collectors.toList());
    }

    // Entity <-> Model 변환
    private Link toModel(LinkEntity entity) {
        return new Link(
                entity.getLinkId(),
                entity.getOriginalUrl(),
                entity.getShortCode(),
                entity.getUserId(),
                entity.getExpiresAt(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    private LinkEntity toEntity(Link model) {
        return new LinkEntity(
                model.getLinkId(),
                model.getOriginalUrl(),
                model.getShortCode(),
                model.getUserId(),
                model.getExpiresAt(),
                model.getCreatedAt(),
                model.getUpdatedAt()
        );
    }
}