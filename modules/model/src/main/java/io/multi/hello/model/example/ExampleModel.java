package io.multi.hello.model.example;

import io.multi.hello.model.AuditProps;

public interface ExampleModel extends AuditProps {
    Long getExampleId();
    String getName();
}