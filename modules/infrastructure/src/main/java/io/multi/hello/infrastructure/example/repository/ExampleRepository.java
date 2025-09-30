package io.multi.hello.infrastructure.example.repository;

import io.multi.hello.model.example.Example;
import io.multi.hello.model.example.ExampleIdentity;
import java.util.List;
import java.util.Optional;

/**
 * Example Repository 인터페이스
 *
 * 헥사고날 아키텍처에서 Port 역할을 수행하며,
 * 비즈니스 로직에서 데이터 접근을 위한 인터페이스를 정의합니다.
 */
public interface ExampleRepository {

    /**
     * ID로 Example 조회
     *
     * @param identity Example 식별자
     * @return Example 엔티티 (존재하지 않으면 Optional.empty())
     */
    Optional<Example> findById(ExampleIdentity identity);

    /**
     * 모든 Example 조회
     *
     * @return Example 목록
     */
    List<Example> findAll();

    /**
     * 이름으로 Example 조회
     *
     * @param name 이름
     * @return Example 목록
     */
    List<Example> findByName(String name);

    /**
     * Example 저장 (생성/수정)
     *
     * @param example 저장할 Example
     * @return 저장된 Example
     */
    Example save(Example example);

    /**
     * ID로 Example 삭제
     *
     * @param identity Example 식별자
     */
    void deleteById(ExampleIdentity identity);

    /**
     * Example 존재 여부 확인
     *
     * @param identity Example 식별자
     * @return 존재하면 true, 없으면 false
     */
    boolean existsById(ExampleIdentity identity);

    /**
     * 총 Example 개수 조회
     *
     * @return Example 개수
     */
    long count();
}