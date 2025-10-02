package io.example.hello.service.example;

import io.example.hello.model.example.Example;
import io.example.hello.model.example.ExampleIdentity;

public interface ExampleWriter {
    Example create(String name);
    Example update(ExampleIdentity identity, String name);
    void delete(ExampleIdentity identity);
}
