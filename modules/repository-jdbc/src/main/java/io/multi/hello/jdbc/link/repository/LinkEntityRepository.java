package io.multi.hello.jdbc.link.repository;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * Link Spring Data JDBC Repository
 */
public interface LinkEntityRepository extends CrudRepository<LinkEntity, Long> {

    @Query("SELECT * FROM LINKS WHERE USER_ID = :userId")
    List<LinkEntity> findByUserId(@Param("userId") Long userId);

    @Query("SELECT * FROM LINKS WHERE ORIGINAL_URL = :originalUrl")
    Optional<LinkEntity> findByOriginalUrl(@Param("originalUrl") String originalUrl);

    @Query("SELECT * FROM LINKS WHERE SHORT_CODE = :shortCode")
    Optional<LinkEntity> findByShortCode(@Param("shortCode") String shortCode);

    @Query("SELECT COUNT(*) > 0 FROM LINKS WHERE SHORT_CODE = :shortCode")
    boolean existsByShortCode(@Param("shortCode") String shortCode);
}