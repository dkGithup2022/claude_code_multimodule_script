package com.example.hello.jdbc.example.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Example Entity CRUD API 인터페이스
 *
 * Spring Data JDBC를 활용한 ExampleEntity 데이터 접근 계층
 * Infrastructure Repository 인터페이스 기반으로 필요한 메서드만 생성
 */
@Repository
public interface ExampleEntityRepository extends CrudRepository<ExampleEntity, Long> {
    List<ExampleEntity> findByName(String name);
}