package io.example.hello.exception;

public class ExampleNotFoundException extends RuntimeException {
    public ExampleNotFoundException(Long exampleId) {
        super("Example not found: id=" + exampleId);
    }
}
