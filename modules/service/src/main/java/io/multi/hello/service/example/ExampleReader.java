package io.multi.hello.service.example;

import io.multi.hello.model.example.Example;
import io.multi.hello.model.example.ExampleIdentity;

import java.util.List;

/**
 * Example 도메인 조회 서비스 인터페이스
 *
 * CQRS 패턴의 Query 책임을 담당합니다.
 */
public interface ExampleReader {

    /**
     * ID로 Example 조회
     *
     * @param identity Example 식별자
     * @return Example 엔티티 (없으면 null)
     */
    Example findByIdentity(ExampleIdentity identity);

    /**
     * 모든 Example 조회
     *
     * @return Example 목록
     */
    List<Example> findAll();
}