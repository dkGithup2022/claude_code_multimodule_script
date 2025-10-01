package io.multi.hello.jdbc.linkclick.repository;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * LinkClick Spring Data JDBC Repository
 */
public interface LinkClickEntityRepository extends CrudRepository<LinkClickEntity, Long> {

    @Query("SELECT * FROM LINK_CLICKS WHERE LINK_ID = :linkId ORDER BY CLICKED_AT DESC")
    List<LinkClickEntity> findByLinkId(@Param("linkId") Long linkId);

    @Query("SELECT COUNT(*) FROM LINK_CLICKS WHERE LINK_ID = :linkId")
    long countByLinkId(@Param("linkId") Long linkId);
}