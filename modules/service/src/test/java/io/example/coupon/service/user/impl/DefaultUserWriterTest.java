package io.example.coupon.service.user.impl;

import io.example.coupon.infrastructure.user.repository.UserRepository;
import io.example.coupon.model.user.User;
import io.example.coupon.model.user.UserIdentity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DefaultUserWriterTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private DefaultUserWriter userWriter;

    // Test data based on User model spec
    private final User sampleUser = new User(
            1L,                     // userId
            "test@example.com",     // email
            "testName",             // name
            Instant.now(),          // createdAt
            Instant.now()           // updatedAt
    );

    private final UserIdentity testIdentity = new UserIdentity(1L);

    // create tests

    @Test
    void create_newUser_callsRepositorySaveAndReturnsUser() {
        // given
        User newUser = new User(null, "new@example.com", "newName", null, null);
        User savedUser = new User(1L, "new@example.com", "newName", Instant.now(), Instant.now());

        when(userRepository.findByEmail("new@example.com"))
                .thenReturn(Optional.empty());
        when(userRepository.save(any(User.class)))
                .thenReturn(savedUser);

        // when
        User result = userWriter.create(newUser);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo("new@example.com");
        assertThat(result.getName()).isEqualTo("newName");
        assertThat(result.getUserId()).isEqualTo(1L);

        verify(userRepository).findByEmail("new@example.com");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void create_duplicateEmail_throwsException() {
        // given
        User newUser = new User(null, "test@example.com", "testName", null, null);

        when(userRepository.findByEmail("test@example.com"))
                .thenReturn(Optional.of(sampleUser));

        // when & then
        assertThatThrownBy(() -> userWriter.create(newUser))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("already exists");

        verify(userRepository).findByEmail("test@example.com");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void create_setsCreatedAtAndUpdatedAt() {
        // given
        User newUser = new User(null, "new@example.com", "newName", null, null);
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);

        when(userRepository.findByEmail("new@example.com"))
                .thenReturn(Optional.empty());
        when(userRepository.save(any(User.class)))
                .thenReturn(sampleUser);

        // when
        userWriter.create(newUser);

        // then
        verify(userRepository).save(userCaptor.capture());
        User capturedUser = userCaptor.getValue();
        assertThat(capturedUser.getCreatedAt()).isNotNull();
        assertThat(capturedUser.getUpdatedAt()).isNotNull();
    }

    // update tests

    @Test
    void update_existingUser_callsRepositorySaveAndReturnsUser() {
        // given
        User updateUser = new User(1L, "updated@example.com", "updatedName", Instant.now(), Instant.now());

        when(userRepository.findById(testIdentity))
                .thenReturn(Optional.of(sampleUser));
        when(userRepository.save(any(User.class)))
                .thenReturn(updateUser);

        // when
        User result = userWriter.update(updateUser);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo("updated@example.com");
        assertThat(result.getName()).isEqualTo("updatedName");

        verify(userRepository).findById(testIdentity);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void update_nonExistingUser_throwsException() {
        // given
        User updateUser = new User(999L, "updated@example.com", "updatedName", Instant.now(), Instant.now());
        UserIdentity nonExistingIdentity = new UserIdentity(999L);

        when(userRepository.findById(nonExistingIdentity))
                .thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> userWriter.update(updateUser))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("not found");

        verify(userRepository).findById(nonExistingIdentity);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void update_preservesCreatedAtAndUpdatesUpdatedAt() {
        // given
        Instant originalCreatedAt = Instant.now().minusSeconds(3600);
        User existingUser = new User(1L, "test@example.com", "testName", originalCreatedAt, Instant.now());
        User updateUser = new User(1L, "updated@example.com", "updatedName", Instant.now(), Instant.now());
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);

        when(userRepository.findById(testIdentity))
                .thenReturn(Optional.of(existingUser));
        when(userRepository.save(any(User.class)))
                .thenReturn(updateUser);

        // when
        userWriter.update(updateUser);

        // then
        verify(userRepository).save(userCaptor.capture());
        User capturedUser = userCaptor.getValue();
        assertThat(capturedUser.getCreatedAt()).isEqualTo(originalCreatedAt);
        assertThat(capturedUser.getUpdatedAt()).isNotNull();
    }

    // deleteById tests

    @Test
    void deleteById_existingId_callsRepositoryDelete() {
        // given
        when(userRepository.findById(testIdentity))
                .thenReturn(Optional.of(sampleUser));

        // when
        userWriter.deleteById(testIdentity);

        // then
        verify(userRepository).findById(testIdentity);
        verify(userRepository).deleteById(testIdentity);
    }

    @Test
    void deleteById_nonExistingId_throwsException() {
        // given
        UserIdentity nonExistingIdentity = new UserIdentity(999L);

        when(userRepository.findById(nonExistingIdentity))
                .thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> userWriter.deleteById(nonExistingIdentity))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("not found");

        verify(userRepository).findById(nonExistingIdentity);
        verify(userRepository, never()).deleteById(any(UserIdentity.class));
    }
}
