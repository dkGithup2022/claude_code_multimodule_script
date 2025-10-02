package io.example.hello.service.example.impl;

import io.example.hello.exception.ExampleNotFoundException;
import io.example.hello.infrastructure.example.repository.ExampleRepository;
import io.example.hello.model.example.Example;
import io.example.hello.model.example.ExampleIdentity;
import io.example.hello.service.example.ExampleReader;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DefaultExampleReader implements ExampleReader {

    private final ExampleRepository exampleRepository;

    @Override
    public Example findById(ExampleIdentity identity) {
        return exampleRepository.findById(identity)
                .orElseThrow(() -> new ExampleNotFoundException(identity.getExampleId()));
    }

    @Override
    public List<Example> findAll() {
        return exampleRepository.findAll();
    }

    @Override
    public List<Example> findByName(String name) {
        return exampleRepository.findByName(name);
    }
}
