package io.example.hello.infrastructure.example.repository;

import io.example.hello.model.example.Example;
import io.example.hello.model.example.ExampleIdentity;

import java.util.List;
import java.util.Optional;

public interface ExampleRepository {
    Optional<Example> findById(ExampleIdentity identity);
    List<Example> findAll();
    List<Example> findByName(String name);
    Example save(Example example);
    void deleteById(ExampleIdentity identity);
    boolean existsById(ExampleIdentity identity);
    long count();
}
