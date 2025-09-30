package io.multi.hello.jdbc.example.repository;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;

/**
 * Example Spring Data JDBC Entity
 *
 * Model 클래스 스펙을 기반으로 생성된 데이터베이스 매핑용 엔티티
 */
@Table("EXAMPLES")
@Getter
@AllArgsConstructor
public class ExampleEntity {
    @Id
    private Long exampleId;
    private String name;
    private Instant createdAt;
    private Instant updatedAt;
}