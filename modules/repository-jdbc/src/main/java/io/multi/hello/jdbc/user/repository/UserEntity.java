package io.multi.hello.jdbc.user.repository;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;

/**
 * User Spring Data JDBC Entity
 */
@Table("USERS")
@Getter
@AllArgsConstructor
public class UserEntity {
    @Id
    private Long userId;
    private String email;
    private String name;
    private Instant createdAt;
    private Instant updatedAt;
}