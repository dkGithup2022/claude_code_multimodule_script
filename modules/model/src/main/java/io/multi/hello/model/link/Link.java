package io.multi.hello.model.link;

import lombok.Value;
import java.time.Instant;

@Value
public class Link implements LinkModel {
    Long linkId;
    String originalUrl;
    String shortCode;
    Long userId;  // nullable
    Instant expiresAt;  // nullable
    Instant createdAt;
    Instant updatedAt;
}