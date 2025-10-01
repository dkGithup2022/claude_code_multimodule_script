package io.multi.hello.jdbc.linkclick.repository;

import io.multi.hello.infrastructure.linkclick.repository.LinkClickRepository;
import io.multi.hello.model.linkclick.LinkClick;
import io.multi.hello.model.linkclick.LinkClickIdentity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * LinkClick JDBC Repository 구현체
 */
@Repository
@RequiredArgsConstructor
public class LinkClickJdbcRepository implements LinkClickRepository {

    private final LinkClickEntityRepository entityRepository;

    @Override
    public Optional<LinkClick> findById(LinkClickIdentity identity) {
        return entityRepository.findById(identity.getClickId())
                .map(this::toModel);
    }

    @Override
    public List<LinkClick> findByLinkId(Long linkId) {
        return entityRepository.findByLinkId(linkId).stream()
                .map(this::toModel)
                .collect(Collectors.toList());
    }

    @Override
    public long countByLinkId(Long linkId) {
        return entityRepository.countByLinkId(linkId);
    }

    @Override
    public LinkClick save(LinkClick linkClick) {
        LinkClickEntity entity = toEntity(linkClick);
        LinkClickEntity saved = entityRepository.save(entity);
        return toModel(saved);
    }

    // Entity <-> Model 변환
    private LinkClick toModel(LinkClickEntity entity) {
        return new LinkClick(
                entity.getClickId(),
                entity.getLinkId(),
                entity.getClickedAt(),
                entity.getIpAddress(),
                entity.getUserAgent(),
                entity.getReferer()
        );
    }

    private LinkClickEntity toEntity(LinkClick model) {
        return new LinkClickEntity(
                model.getClickId(),
                model.getLinkId(),
                model.getClickedAt(),
                model.getIpAddress(),
                model.getUserAgent(),
                model.getReferer()
        );
    }
}