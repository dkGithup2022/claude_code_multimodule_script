package com.example.hello.exception;

public class ExampleNotFoundException extends RuntimeException {

    public ExampleNotFoundException(Long exampleId) {
        super("Example not found with id: " + exampleId);
    }

    public ExampleNotFoundException(String message) {
        super(message);
    }

    public ExampleNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}