package io.example.hello.service.example.impl;

import io.example.hello.exception.ExampleNotFoundException;
import io.example.hello.infrastructure.example.repository.ExampleRepository;
import io.example.hello.model.example.Example;
import io.example.hello.model.example.ExampleIdentity;
import io.example.hello.service.example.ExampleWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class DefaultExampleWriter implements ExampleWriter {

    private final ExampleRepository exampleRepository;

    @Override
    public Example create(String name) {
        Example newExample = Example.newOne(name);
        return exampleRepository.save(newExample);
    }

    @Override
    public Example update(ExampleIdentity identity, String name) {
        Example existing = exampleRepository.findById(identity)
                .orElseThrow(() -> new ExampleNotFoundException(identity.getExampleId()));

        Example updated = new Example(
                existing.getExampleId(),
                name,
                existing.getCreatedAt(),
                existing.getUpdatedAt()
        );

        return exampleRepository.save(updated);
    }

    @Override
    public void delete(ExampleIdentity identity) {
        if (!exampleRepository.existsById(identity)) {
            throw new ExampleNotFoundException(identity.getExampleId());
        }
        exampleRepository.deleteById(identity);
    }
}
