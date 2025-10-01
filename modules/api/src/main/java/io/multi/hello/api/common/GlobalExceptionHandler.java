package io.multi.hello.api.common;

import io.multi.hello.exception.link.LinkAlreadyExistException;
import io.multi.hello.exception.link.LinkNotFoundException;
import io.multi.hello.exception.user.CantCreateUserException;
import io.multi.hello.exception.user.UserNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

/**
 * 전역 예외 핸들러
 *
 * API에서 발생하는 예외를 일관된 형식으로 응답합니다.
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 사용자를 찾을 수 없을 때
     */
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFound(UserNotFoundException ex) {
        log.warn("User not found: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ErrorResponse.of("USER_NOT_FOUND", ex.getMessage()));
    }

    /**
     * 사용자 생성/수정 불가능할 때
     */
    @ExceptionHandler(CantCreateUserException.class)
    public ResponseEntity<ErrorResponse> handleCantCreateUser(CantCreateUserException ex) {
        log.warn("Can't create user: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.of("INVALID_USER", ex.getMessage()));
    }

    /**
     * 요청 validation 실패
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationError(MethodArgumentNotValidException ex) {
        String errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));

        log.warn("Validation error: {}", errors);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.of("VALIDATION_ERROR", errors));
    }

    /**
     * 링크를 찾을 수 없을 때
     */
    @ExceptionHandler(LinkNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleLinkNotFound(LinkNotFoundException ex) {
        log.warn("Link not found: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ErrorResponse.of("LINK_NOT_FOUND", ex.getMessage()));
    }

    /**
     * 링크가 이미 존재할 때
     */
    @ExceptionHandler(LinkAlreadyExistException.class)
    public ResponseEntity<ErrorResponse> handleLinkAlreadyExist(LinkAlreadyExistException ex) {
        log.warn("Link already exists: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(ErrorResponse.of("LINK_ALREADY_EXISTS", ex.getMessage()));
    }

    /**
     * 기타 예외
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericError(Exception ex) {
        log.error("Unexpected error", ex);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ErrorResponse.of("INTERNAL_ERROR", "An unexpected error occurred"));
    }
}