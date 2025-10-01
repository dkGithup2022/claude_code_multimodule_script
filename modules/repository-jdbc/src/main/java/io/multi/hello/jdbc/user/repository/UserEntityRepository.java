package io.multi.hello.jdbc.user.repository;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * User Spring Data JDBC Repository
 */
public interface UserEntityRepository extends CrudRepository<UserEntity, Long> {

    @Query("SELECT * FROM USERS WHERE NAME = :name")
    List<UserEntity> findByName(@Param("name") String name);

    @Query("SELECT * FROM USERS WHERE EMAIL = :email")
    List<UserEntity>  findByEmail(@Param("email") String email);
}