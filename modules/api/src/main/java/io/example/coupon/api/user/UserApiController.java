package io.example.coupon.api.user;

import io.example.coupon.api.user.dto.UserCreateRequest;
import io.example.coupon.api.user.dto.UserResponse;
import io.example.coupon.api.user.dto.UserUpdateRequest;
import io.example.coupon.model.user.User;
import io.example.coupon.model.user.UserIdentity;
import io.example.coupon.service.user.UserReader;
import io.example.coupon.service.user.UserWriter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

/**
 * User REST API 컨트롤러
 */
@Tag(name = "User", description = "사용자 관리 API")
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
public class UserApiController {

    private final UserReader userReader;
    private final UserWriter userWriter;

    @Operation(summary = "사용자 생성", description = "새로운 사용자를 생성합니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "사용자 생성 성공",
                    content = @Content(schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content)
    })
    @PostMapping
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody UserCreateRequest request) {
        log.info("Creating user: email={}", request.getEmail());

        User user = new User(null, request.getEmail(), request.getName(), null, null);
        User created = userWriter.create(user);

        return ResponseEntity.status(HttpStatus.CREATED).body(UserResponse.from(created));
    }

    @Operation(summary = "사용자 조회", description = "사용자 ID로 사용자 정보를 조회합니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음", content = @Content)
    })
    @GetMapping("/{userId}")
    public ResponseEntity<UserResponse> getUser(
            @Parameter(description = "사용자 ID", required = true) @PathVariable("userId") Long userId) {
        log.info("Getting user: userId={}", userId);

        User user = userReader.findById(new UserIdentity(userId));
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(UserResponse.from(user));
    }

    @Operation(summary = "전체 사용자 목록 조회", description = "모든 사용자 목록을 조회합니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = UserResponse.class)))
    })
    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        log.info("Getting all users");

        List<UserResponse> users = userReader.findAll().stream()
                .map(UserResponse::from)
                .collect(Collectors.toList());

        return ResponseEntity.ok(users);
    }



    @Operation(summary = "사용자 정보 수정", description = "사용자 정보를 수정합니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "수정 성공",
                    content = @Content(schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음", content = @Content),
            @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content)
    })
    @PutMapping("/{userId}")
    public ResponseEntity<UserResponse> updateUser(
            @Parameter(description = "사용자 ID", required = true) @PathVariable("userId") Long userId,
            @Valid @RequestBody UserUpdateRequest request) {

        log.info("Updating user: userId={}", userId);

        User existing = userReader.findById(new UserIdentity(userId));
        if (existing == null) {
            return ResponseEntity.notFound().build();
        }

        User updated = new User(
                userId,
                request.getEmail(),
                request.getName(),
                existing.getCreatedAt(),
                Instant.now()
        );
        User result = userWriter.update(updated);
        return ResponseEntity.ok(UserResponse.from(result));
    }

    @Operation(summary = "사용자 삭제", description = "사용자를 삭제합니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "삭제 성공"),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음", content = @Content)
    })
    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(
            @Parameter(description = "사용자 ID", required = true) @PathVariable("userId") Long userId) {
        log.info("Deleting user: userId={}", userId);

        userWriter.deleteById(new UserIdentity(userId));
        return ResponseEntity.noContent().build();
    }
}
