package io.example.hello.model.example;

import lombok.Value;
import java.time.Instant;

@Value
public class Example implements ExampleModel {
    Long exampleId;
    String name;
    Instant createdAt;
    Instant updatedAt;

    public static Example newOne(String name) {
        return new Example(null, name, Instant.now(), Instant.now());
    }
}
