package io.multi.hello.model.linkclick;

import lombok.Value;
import java.time.Instant;

@Value
public class LinkClick implements LinkClickModel {
    Long clickId;
    Long linkId;
    Instant clickedAt;
    String ipAddress;
    String userAgent;
    String referer;  // nullable
}