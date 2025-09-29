package com.example.hello.model.example;

import com.example.hello.model.AuditProps;

public interface ExampleModel extends AuditProps {
    Long getExampleId();
    String getName();
}