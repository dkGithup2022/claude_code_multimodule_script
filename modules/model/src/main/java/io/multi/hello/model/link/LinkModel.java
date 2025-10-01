package io.multi.hello.model.link;

import io.multi.hello.model.AuditProps;
import java.time.Instant;

public interface LinkModel extends AuditProps {
    Long getLinkId();
    String getOriginalUrl();
    String getShortCode();
    Long getUserId();  // nullable
    Instant getExpiresAt();  // nullable
}