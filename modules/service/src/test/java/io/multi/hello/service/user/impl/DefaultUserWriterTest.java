package io.multi.hello.service.user.impl;

import io.multi.hello.exception.user.CantCreateUserException;
import io.multi.hello.exception.user.UserNotFoundException;
import io.multi.hello.infrastructure.user.repository.UserRepository;
import io.multi.hello.model.user.User;
import io.multi.hello.model.user.UserIdentity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

/**
 * DefaultUserWriter 단위 테스트
 *
 * Repository Mock을 사용한 비즈니스 로직 검증
 * - 중복 검증 로직
 * - 이름 길이 검증 로직
 * - 삭제 전 존재 확인 로직
 */
@ExtendWith(MockitoExtension.class)
class DefaultUserWriterTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private DefaultUserWriter userWriter;

    // 테스트 데이터
    private final User sampleUser = new User(
            null,
            "test@example.com",
            "testName",
            Instant.now(),
            Instant.now()
    );

    private final UserIdentity testIdentity = new UserIdentity(1L);
    private final UserIdentity nonExistingIdentity = new UserIdentity(999L);

    // upsert 성공 테스트
    @Test
    void upsert_validNewUser_callsRepositorySave() {
        // given
        when(userRepository.findByName("testName"))
                .thenReturn(List.of());
        when(userRepository.findByEmail("test@example.com"))
                .thenReturn(List.of());
        when(userRepository.save(sampleUser))
                .thenReturn(new User(1L, "test@example.com", "testName", Instant.now(), Instant.now()));

        // when
        User result = userWriter.upsert(sampleUser);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getUserId()).isEqualTo(1L);
        verify(userRepository).findByName("testName");
        verify(userRepository).findByEmail("test@example.com");
        verify(userRepository).save(sampleUser);
    }

    // upsert 실패 테스트 - 이름 중복
    @Test
    void upsert_duplicateName_throwsException() {
        // given
        User existingUser = new User(1L, "other@example.com", "testName", Instant.now(), Instant.now());
        when(userRepository.findByName("testName"))
                .thenReturn(List.of(existingUser));

        // when & then
        assertThatThrownBy(() -> userWriter.upsert(sampleUser))
                .isInstanceOf(CantCreateUserException.class)
                .hasMessageContaining("User with name 'testName' already exists");

        verify(userRepository).findByName("testName");
        verify(userRepository, never()).save(any());
    }

    // upsert 실패 테스트 - 이메일 중복
    @Test
    void upsert_duplicateEmail_throwsException() {
        // given
        User existingUser = new User(1L, "test@example.com", "otherName", Instant.now(), Instant.now());
        when(userRepository.findByName("testName"))
                .thenReturn(List.of());
        when(userRepository.findByEmail("test@example.com"))
                .thenReturn(List.of(existingUser));

        // when & then
        assertThatThrownBy(() -> userWriter.upsert(sampleUser))
                .isInstanceOf(CantCreateUserException.class)
                .hasMessageContaining("User with email 'test@example.com' already exists");

        verify(userRepository).findByName("testName");
        verify(userRepository).findByEmail("test@example.com");
        verify(userRepository, never()).save(any());
    }

    // upsert 실패 테스트 - 이름 null
    @Test
    void upsert_nullName_throwsException() {
        // given
        User userWithNullName = new User(null, "test@example.com", null, Instant.now(), Instant.now());

        // when & then
        assertThatThrownBy(() -> userWriter.upsert(userWithNullName))
                .isInstanceOf(CantCreateUserException.class)
                .hasMessageContaining("User name cannot be empty");

        verify(userRepository, never()).save(any());
    }

    // upsert 실패 테스트 - 이름 빈 문자열
    @Test
    void upsert_blankName_throwsException() {
        // given
        User userWithBlankName = new User(null, "test@example.com", "   ", Instant.now(), Instant.now());

        // when & then
        assertThatThrownBy(() -> userWriter.upsert(userWithBlankName))
                .isInstanceOf(CantCreateUserException.class)
                .hasMessageContaining("User name cannot be empty");

        verify(userRepository, never()).save(any());
    }

    // upsert 실패 테스트 - 이름 길이 초과
    @Test
    void upsert_nameTooLong_throwsException() {
        // given
        User userWithLongName = new User(
                null,
                "test@example.com",
                "a".repeat(21), // 21자 (20자 초과)
                Instant.now(),
                Instant.now()
        );

        // when & then
        assertThatThrownBy(() -> userWriter.upsert(userWithLongName))
                .isInstanceOf(CantCreateUserException.class)
                .hasMessageContaining("User name cannot be longer than 20 characters");

        verify(userRepository, never()).save(any());
    }

    // deleteById 성공 테스트
    @Test
    void deleteById_existingId_callsRepositoryDelete() {
        // given
        User existingUser = new User(1L, "test@example.com", "testName", Instant.now(), Instant.now());
        when(userRepository.findById(testIdentity))
                .thenReturn(Optional.of(existingUser));

        // when
        userWriter.deleteById(testIdentity);

        // then
        verify(userRepository).findById(testIdentity);
        verify(userRepository).deleteById(testIdentity);
    }

    // deleteById 실패 테스트 - 존재하지 않는 ID
    @Test
    void deleteById_nonExistingId_throwsException() {
        // given
        when(userRepository.findById(nonExistingIdentity))
                .thenReturn(null); // 구현체가 null 체크를 하므로 null 반환

        // when & then
        assertThatThrownBy(() -> userWriter.deleteById(nonExistingIdentity))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining("User with id '999' not found");

        verify(userRepository).findById(nonExistingIdentity);
        verify(userRepository, never()).deleteById(any());
    }
}