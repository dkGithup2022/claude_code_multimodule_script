package io.example.coupon.model;

import java.time.Instant;

public interface AuditProps {
    Instant getCreatedAt();
    Instant getUpdatedAt();
}
