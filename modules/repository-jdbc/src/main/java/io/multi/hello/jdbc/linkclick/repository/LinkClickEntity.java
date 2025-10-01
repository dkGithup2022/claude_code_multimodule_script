package io.multi.hello.jdbc.linkclick.repository;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;

/**
 * LinkClick Spring Data JDBC Entity
 */
@Table("LINK_CLICKS")
@Getter
@AllArgsConstructor
public class LinkClickEntity {
    @Id
    private Long clickId;
    private Long linkId;
    private Instant clickedAt;
    private String ipAddress;
    private String userAgent;
    private String referer;  // nullable
}