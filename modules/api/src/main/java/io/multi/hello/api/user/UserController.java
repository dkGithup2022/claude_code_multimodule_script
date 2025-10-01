package io.multi.hello.api.user;

import io.multi.hello.api.user.dto.CreateUserRequest;
import io.multi.hello.api.user.dto.UpdateUserRequest;
import io.multi.hello.api.user.dto.UserResponse;
import io.multi.hello.model.user.User;
import io.multi.hello.model.user.UserIdentity;
import io.multi.hello.service.user.UserReader;
import io.multi.hello.service.user.UserWriter;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;

/**
 * User API 컨트롤러
 *
 * 사용자 생성, 조회, 수정, 삭제 기능을 제공합니다.
 */
@Tag(name = "User", description = "사용자 관리 API")
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserWriter userWriter;
    private final UserReader userReader;

    /**
     * 사용자 생성
     *
     * @param request 사용자 생성 요청
     * @return 생성된 사용자 정보
     */
    @Operation(summary = "사용자 생성", description = "새로운 사용자를 생성합니다.")
    @PostMapping
    public ResponseEntity<UserResponse> createUser(
            @Valid @RequestBody CreateUserRequest request) {

        log.info("Creating user: email={}, name={}", request.email(), request.name());

        Instant now = Instant.now();
        User user = new User(
                null,
                request.email(),
                request.name(),
                now,
                now
        );

        User created = userWriter.upsert(user);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(UserResponse.from(created));
    }

    /**
     * 사용자 ID로 조회
     *
     * @param userId 사용자 ID
     * @return 사용자 정보
     */
    @GetMapping("/{userId}")
    public ResponseEntity<UserResponse> getUser(@PathVariable Long userId) {
        log.info("Getting user by id: {}", userId);

        User user = userReader.findById(new UserIdentity(userId))
                .orElseThrow(() -> new RuntimeException("User not found"));

        return ResponseEntity.ok(UserResponse.from(user));
    }

    /**
     * 이메일로 사용자 조회
     *
     * @param email 이메일
     * @return 사용자 정보
     */
    @GetMapping("/email/{email}")
    public ResponseEntity<UserResponse> getUserByEmail(@PathVariable String email) {
        log.info("Getting user by email: {}", email);

        User user = userReader.findByEmail(email);

        return ResponseEntity.ok(UserResponse.from(user));
    }

    /**
     * 이름으로 사용자 조회
     *
     * @param name 이름
     * @return 사용자 정보
     */
    @GetMapping("/name/{name}")
    public ResponseEntity<UserResponse> getUserByName(@PathVariable String name) {
        log.info("Getting user by name: {}", name);

        User user = userReader.findByName(name);

        return ResponseEntity.ok(UserResponse.from(user));
    }

    /**
     * 사용자 수정
     *
     * @param userId 사용자 ID
     * @param request 수정 요청
     * @return 수정된 사용자 정보
     */
    @PutMapping("/{userId}")
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable Long userId,
            @Valid @RequestBody UpdateUserRequest request) {

        log.info("Updating user: userId={}, name={}", userId, request.name());

        User existing = userReader.findById(new UserIdentity(userId))
                .orElseThrow(() -> new RuntimeException("User not found"));

        User updated = new User(
                existing.getUserId(),
                existing.getEmail(),
                request.name(),
                existing.getCreatedAt(),
                Instant.now()
        );

        User saved = userWriter.upsert(updated);

        return ResponseEntity.ok(UserResponse.from(saved));
    }

    /**
     * 사용자 삭제
     *
     * @param userId 사용자 ID
     * @return 204 No Content
     */
    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long userId) {
        log.info("Deleting user: userId={}", userId);

        userWriter.deleteById(new UserIdentity(userId));

        return ResponseEntity.noContent().build();
    }
}