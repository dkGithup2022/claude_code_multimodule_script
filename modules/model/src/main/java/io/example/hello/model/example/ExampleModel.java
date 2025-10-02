package io.example.hello.model.example;

import io.example.hello.model.AuditProps;

public interface ExampleModel extends AuditProps {
    Long getExampleId();
    String getName();
}
