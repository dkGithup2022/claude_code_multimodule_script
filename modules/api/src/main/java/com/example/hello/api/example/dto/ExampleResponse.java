package com.example.hello.api.example.dto;

import com.example.hello.model.example.Example;
import lombok.Value;

import java.time.Instant;

@Value
public class ExampleResponse {
    Long exampleId;
    String name;
    Instant createdAt;
    Instant updatedAt;

    public static ExampleResponse from(Example example) {
        return new ExampleResponse(
                example.getExampleId(),
                example.getName(),
                example.getCreatedAt(),
                example.getUpdatedAt()
        );
    }
}