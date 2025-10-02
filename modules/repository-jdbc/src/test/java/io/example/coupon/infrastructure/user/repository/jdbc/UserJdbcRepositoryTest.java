package io.example.coupon.infrastructure.user.repository.jdbc;

import io.example.coupon.model.user.User;
import io.example.coupon.model.user.UserIdentity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.context.annotation.ComponentScan;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJdbcTest
@ComponentScan("io.example.coupon.infrastructure.user.repository.jdbc")
class UserJdbcRepositoryTest {

    @Autowired
    private UserJdbcRepository userRepository;

    // Test data based on User model spec
    private final User sampleUser = new User(
            null,                    // userId: null (auto-generated)
            "test@example.com",      // email
            "testName",              // name
            Instant.now(),           // createdAt
            Instant.now()            // updatedAt
    );

    private final UserIdentity testIdentity = new UserIdentity(1L);
    private final UserIdentity nonExistingIdentity = new UserIdentity(999L);

    // Entity↔Domain conversion tests (Required)

    @Test
    void save_withValidDomain_returnsConvertedDomain() {
        // When
        User savedUser = userRepository.save(sampleUser);

        // Then
        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getUserId()).isNotNull();
        assertThat(savedUser.getEmail()).isEqualTo(sampleUser.getEmail());
        assertThat(savedUser.getName()).isEqualTo(sampleUser.getName());
    }

    @Test
    void save_withNullId_generatesIdAndReturns() {
        // When
        User savedUser = userRepository.save(sampleUser);

        // Then
        assertThat(savedUser.getUserId()).isNotNull();
        assertThat(savedUser.getUserId()).isGreaterThan(0L);
    }

    @Test
    void findById_existingId_returnsConvertedDomain() {
        // Given
        User savedUser = userRepository.save(sampleUser);
        UserIdentity savedIdentity = new UserIdentity(savedUser.getUserId());

        // When
        Optional<User> foundUser = userRepository.findById(savedIdentity);

        // Then
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getEmail()).isEqualTo(sampleUser.getEmail());
        assertThat(foundUser.get().getName()).isEqualTo(sampleUser.getName());
    }

    @Test
    void findById_nonExistingId_returnsEmpty() {
        // When
        Optional<User> foundUser = userRepository.findById(nonExistingIdentity);

        // Then
        assertThat(foundUser).isEmpty();
    }

    @Test
    void findAll_withData_returnsConvertedList() {
        // Given
        userRepository.save(sampleUser);
        User anotherUser = new User(null, "another@example.com", "anotherName", Instant.now(), Instant.now());
        userRepository.save(anotherUser);

        // When
        List<User> users = userRepository.findAll();

        // Then
        assertThat(users).hasSize(2);
        assertThat(users).extracting(User::getEmail)
                .contains(sampleUser.getEmail(), anotherUser.getEmail());
    }

    @Test
    void findAll_emptyRepository_returnsEmptyList() {
        // When
        List<User> users = userRepository.findAll();

        // Then
        assertThat(users).isEmpty();
    }

    // Custom Named Query tests (if discovered)

    @Test
    void findByEmail_existingEmail_returnsConvertedOptional() {
        // Given
        userRepository.save(sampleUser);

        // When
        Optional<User> foundUser = userRepository.findByEmail(sampleUser.getEmail());

        // Then
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getEmail()).isEqualTo(sampleUser.getEmail());
    }

    @Test
    void findByEmail_nonExistingEmail_returnsEmpty() {
        // When
        Optional<User> foundUser = userRepository.findByEmail("nonexisting@example.com");

        // Then
        assertThat(foundUser).isEmpty();
    }

    @Test
    void findByName_existingName_returnsConvertedList() {
        // Given
        userRepository.save(sampleUser);

        // When
        List<User> users = userRepository.findByName(sampleUser.getName());

        // Then
        assertThat(users).hasSize(1);
        assertThat(users.get(0).getName()).isEqualTo(sampleUser.getName());
    }

    @Test
    void findByName_nonExistingName_returnsEmptyList() {
        // When
        List<User> users = userRepository.findByName("nonExistingName");

        // Then
        assertThat(users).isEmpty();
    }

    @Test
    void deleteById_existingId_removesUser() {
        // Given
        User savedUser = userRepository.save(sampleUser);
        UserIdentity savedIdentity = new UserIdentity(savedUser.getUserId());

        // When
        userRepository.deleteById(savedIdentity);

        // Then
        Optional<User> foundUser = userRepository.findById(savedIdentity);
        assertThat(foundUser).isEmpty();
    }
}
