package io.example.coupon.infrastructure.user.repository.jdbc;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;

@Table("USERS")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserEntity {

    @Id
    private Long userId;

    private String email;

    private String name;

    private Instant createdAt;

    private Instant updatedAt;
}
