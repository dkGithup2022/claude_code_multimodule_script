package io.multi.hello.service.user.impl;

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
import static org.mockito.Mockito.*;

/**
 * DefaultUserReader 단위 테스트
 *
 * Repository Mock을 사용한 비즈니스 로직 검증
 */
@ExtendWith(MockitoExtension.class)
class DefaultUserReaderTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private DefaultUserReader userReader;

    // 테스트 데이터
    private final User sampleUser = new User(
            1L,
            "test@example.com",
            "testName",
            Instant.now(),
            Instant.now()
    );

    private final UserIdentity testIdentity = new UserIdentity(1L);
    private final UserIdentity nonExistingIdentity = new UserIdentity(999L);

    // findById 테스트
    @Test
    void findById_existingId_returnsUser() {
        // given
        when(userRepository.findById(testIdentity))
                .thenReturn(Optional.of(sampleUser));

        // when
        Optional<User> result = userReader.findById(testIdentity);

        // then
        assertThat(result).isPresent();
        assertThat(result.get().getUserId()).isEqualTo(1L);
        assertThat(result.get().getName()).isEqualTo("testName");
        verify(userRepository).findById(testIdentity);
    }

    @Test
    void findById_nonExistingId_returnsEmpty() {
        // given
        when(userRepository.findById(nonExistingIdentity))
                .thenReturn(Optional.empty());

        // when
        Optional<User> result = userReader.findById(nonExistingIdentity);

        // then
        assertThat(result).isEmpty();
        verify(userRepository).findById(nonExistingIdentity);
    }

    // findByName 테스트
    @Test
    void findByName_existingName_returnsFirstUser() {
        // given
        when(userRepository.findByName("testName"))
                .thenReturn(List.of(sampleUser));

        // when
        User result = userReader.findByName("testName");

        // then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("testName");
        verify(userRepository).findByName("testName");
    }

    @Test
    void findByName_nonExistingName_returnsNull() {
        // given
        when(userRepository.findByName("nonExisting"))
                .thenReturn(List.of());

        // when
        User result = userReader.findByName("nonExisting");

        // then
        assertThat(result).isNull();
        verify(userRepository).findByName("nonExisting");
    }

    @Test
    void findByName_multipleUsers_returnsFirstUser() {
        // given
        User secondUser = new User(
                2L,
                "test2@example.com",
                "testName",
                Instant.now(),
                Instant.now()
        );
        when(userRepository.findByName("testName"))
                .thenReturn(List.of(sampleUser, secondUser));

        // when
        User result = userReader.findByName("testName");

        // then
        assertThat(result).isNotNull();
        assertThat(result.getUserId()).isEqualTo(1L); // 첫 번째 사용자
        verify(userRepository).findByName("testName");
    }

    // findByEmail 테스트
    @Test
    void findByEmail_existingEmail_returnsFirstUser() {
        // given
        when(userRepository.findByEmail("test@example.com"))
                .thenReturn(List.of(sampleUser));

        // when
        User result = userReader.findByEmail("test@example.com");

        // then
        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo("test@example.com");
        verify(userRepository).findByEmail("test@example.com");
    }

    @Test
    void findByEmail_nonExistingEmail_returnsNull() {
        // given
        when(userRepository.findByEmail("nonexisting@example.com"))
                .thenReturn(List.of());

        // when
        User result = userReader.findByEmail("nonexisting@example.com");

        // then
        assertThat(result).isNull();
        verify(userRepository).findByEmail("nonexisting@example.com");
    }

    // existsById 테스트
    @Test
    void existsById_existingId_returnsTrue() {
        // given
        when(userRepository.existsById(testIdentity))
                .thenReturn(true);

        // when
        boolean result = userReader.existsById(testIdentity);

        // then
        assertThat(result).isTrue();
        verify(userRepository).existsById(testIdentity);
    }

    @Test
    void existsById_nonExistingId_returnsFalse() {
        // given
        when(userRepository.existsById(nonExistingIdentity))
                .thenReturn(false);

        // when
        boolean result = userReader.existsById(nonExistingIdentity);

        // then
        assertThat(result).isFalse();
        verify(userRepository).existsById(nonExistingIdentity);
    }
}