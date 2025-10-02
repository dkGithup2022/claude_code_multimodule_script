package io.example.coupon.service.user.impl;

import io.example.coupon.infrastructure.user.repository.UserRepository;
import io.example.coupon.model.user.User;
import io.example.coupon.model.user.UserIdentity;
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

@ExtendWith(MockitoExtension.class)
class DefaultUserReaderTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private DefaultUserReader userReader;

    // Test data based on User model spec
    private final User sampleUser = new User(
            1L,                     // userId
            "test@example.com",     // email
            "testName",             // name
            Instant.now(),          // createdAt
            Instant.now()           // updatedAt
    );

    private final UserIdentity testIdentity = new UserIdentity(1L);

    // findById tests

    @Test
    void findById_existingId_returnsUser() {
        // given
        when(userRepository.findById(testIdentity))
                .thenReturn(Optional.of(sampleUser));

        // when
        User result = userReader.findById(testIdentity);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo(sampleUser.getName());
        assertThat(result.getEmail()).isEqualTo(sampleUser.getEmail());
        verify(userRepository).findById(testIdentity);
    }

    @Test
    void findById_nonExistingId_returnsNull() {
        // given
        when(userRepository.findById(testIdentity))
                .thenReturn(Optional.empty());

        // when
        User result = userReader.findById(testIdentity);

        // then
        assertThat(result).isNull();
        verify(userRepository).findById(testIdentity);
    }

    // findAll tests

    @Test
    void findAll_withData_returnsList() {
        // given
        User anotherUser = new User(2L, "another@example.com", "anotherName", Instant.now(), Instant.now());
        when(userRepository.findAll())
                .thenReturn(List.of(sampleUser, anotherUser));

        // when
        List<User> result = userReader.findAll();

        // then
        assertThat(result).hasSize(2);
        assertThat(result).contains(sampleUser, anotherUser);
        verify(userRepository).findAll();
    }

    @Test
    void findAll_emptyData_returnsEmptyList() {
        // given
        when(userRepository.findAll())
                .thenReturn(List.of());

        // when
        List<User> result = userReader.findAll();

        // then
        assertThat(result).isEmpty();
        verify(userRepository).findAll();
    }

    // findByEmail tests

    @Test
    void findByEmail_existingEmail_returnsUser() {
        // given
        when(userRepository.findByEmail("test@example.com"))
                .thenReturn(Optional.of(sampleUser));

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
        when(userRepository.findByEmail("nonexistent@example.com"))
                .thenReturn(Optional.empty());

        // when
        User result = userReader.findByEmail("nonexistent@example.com");

        // then
        assertThat(result).isNull();
        verify(userRepository).findByEmail("nonexistent@example.com");
    }

    // findByName tests

    @Test
    void findByName_existingName_returnsUser() {
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
    void findByName_nonExistingName_throwsException() {
        // given
        when(userRepository.findByName("nonExistentName"))
                .thenReturn(List.of());

        // when & then
        assertThatThrownBy(() -> userReader.findByName("nonExistentName"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("not found");
        verify(userRepository).findByName("nonExistentName");
    }

    @Test
    void findByName_multipleResults_throwsException() {
        // given
        User anotherUser = new User(2L, "another@example.com", "testName", Instant.now(), Instant.now());
        when(userRepository.findByName("testName"))
                .thenReturn(List.of(sampleUser, anotherUser));

        // when & then
        assertThatThrownBy(() -> userReader.findByName("testName"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("more than one user");
        verify(userRepository).findByName("testName");
    }
}
