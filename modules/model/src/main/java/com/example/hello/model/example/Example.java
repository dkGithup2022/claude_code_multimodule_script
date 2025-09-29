package com.example.hello.model.example;

import lombok.Value;
import java.time.Instant;

@Value
public class Example implements ExampleModel {
    Long exampleId;
    String name;
    Instant createdAt;
    Instant updatedAt;
}