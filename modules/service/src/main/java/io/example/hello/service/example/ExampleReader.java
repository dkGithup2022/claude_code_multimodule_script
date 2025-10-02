package io.example.hello.service.example;

import io.example.hello.model.example.Example;
import io.example.hello.model.example.ExampleIdentity;

import java.util.List;

public interface ExampleReader {
    Example findById(ExampleIdentity identity);
    List<Example> findAll();
    List<Example> findByName(String name);
}
