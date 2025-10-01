package io.multi.hello.jdbc.link.repository;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;

/**
 * Link Spring Data JDBC Entity
 */
@Table("LINKS")
@Getter
@AllArgsConstructor
public class LinkEntity {
    @Id
    private Long linkId;
    private String originalUrl;
    private String shortCode;
    private Long userId;  // nullable
    private Instant expiresAt;  // nullable
    private Instant createdAt;
    private Instant updatedAt;
}