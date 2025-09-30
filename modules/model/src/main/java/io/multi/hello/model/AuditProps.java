package io.multi.hello.model;

import java.time.Instant;

public interface AuditProps {
    Instant getCreatedAt();
    Instant getUpdatedAt();
}