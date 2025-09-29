package com.example.hello.service.example.impl;

import com.example.hello.model.example.Example;
import com.example.hello.model.example.ExampleIdentity;
import com.example.hello.service.example.ExampleReader;
import com.example.hello.infrastructure.example.repository.ExampleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Example 도메인 조회 서비스 구현체
 *
 * CQRS 패턴의 Query 책임을 구현합니다.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DefaultExampleReader implements ExampleReader {

    private final ExampleRepository exampleRepository;

    @Override
    public Example findByIdentity(ExampleIdentity identity) {
        var example = exampleRepository.findById(identity).orElse(null);
        log.info("Find Example by Identity {} , Found Example {}", identity, example);
        return example;
    }

    @Override
    public List<Example> findAll() {
        var examples = exampleRepository.findAll();
        log.info("FindAll examples - {} found", examples.size());
        return examples;
    }
}