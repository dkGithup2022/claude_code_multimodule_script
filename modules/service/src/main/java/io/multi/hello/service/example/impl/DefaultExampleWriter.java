package io.multi.hello.service.example.impl;

import io.multi.hello.model.example.Example;
import io.multi.hello.model.example.ExampleIdentity;
import io.multi.hello.service.example.ExampleWriter;
import io.multi.hello.infrastructure.example.repository.ExampleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Example 도메인 변경 서비스 구현체
 *
 * CQRS 패턴의 Command 책임을 구현합니다.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DefaultExampleWriter implements ExampleWriter {

    private final ExampleRepository exampleRepository;

    @Override
    public Example upsert(Example example) {
        log.info("Upserting example: {}", example);
        return exampleRepository.save(example);
    }

    @Override
    public void delete(ExampleIdentity identity) {
        log.info("Deleting example: {}", identity);
        exampleRepository.deleteById(identity);
    }
}