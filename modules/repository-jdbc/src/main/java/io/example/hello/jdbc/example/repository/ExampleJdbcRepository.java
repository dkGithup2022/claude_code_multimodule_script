package io.example.hello.jdbc.example.repository;

import io.example.hello.model.example.Example;
import io.example.hello.model.example.ExampleIdentity;
import io.example.hello.infrastructure.example.repository.ExampleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Example Repository 구현체
 *
 * 헥사고날 아키텍처에서 Adapter 역할을 수행하며,
 * Infrastructure의 ExampleRepository 인터페이스를
 * Spring Data JDBC를 활용하여 구현합니다.
 */
@Repository
@RequiredArgsConstructor
public class ExampleJdbcRepository implements ExampleRepository {

    private final ExampleEntityRepository entityRepository;

    @Override
    public Optional<Example> findById(ExampleIdentity identity) {
        return entityRepository.findById(identity.getExampleId())
                .map(this::toDomain);
    }

    @Override
    public List<Example> findAll() {
        return StreamSupport.stream(entityRepository.findAll().spliterator(), false)
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Example> findByName(String name) {
        return entityRepository.findByName(name).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Example save(Example example) {
        ExampleEntity entity = toEntity(example);
        ExampleEntity saved = entityRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public void deleteById(ExampleIdentity identity) {
        entityRepository.deleteById(identity.getExampleId());
    }

    @Override
    public boolean existsById(ExampleIdentity identity) {
        return entityRepository.existsById(identity.getExampleId());
    }

    @Override
    public long count() {
        return entityRepository.count();
    }

    /**
     * Entity ↔ Domain 변환 메서드 (Model 스펙 기반 자동 생성)
     */
    private Example toDomain(ExampleEntity entity) {
        return new Example(
                entity.getExampleId(),
                entity.getName(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    private ExampleEntity toEntity(Example domain) {
        return new ExampleEntity(
                domain.getExampleId(),
                domain.getName(),
                domain.getCreatedAt(),
                domain.getUpdatedAt()
        );
    }
}
