package io.multi.hello.model.linkclick;

import java.time.Instant;

public interface LinkClickModel {
    Long getClickId();
    Long getLinkId();
    Instant getClickedAt();
    String getIpAddress();
    String getUserAgent();
    String getReferer();  // nullable
}