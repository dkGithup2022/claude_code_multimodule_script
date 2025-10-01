package io.multi.hello.jdbc.user.repository;

import io.multi.hello.model.user.User;
import io.multi.hello.model.user.UserIdentity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.context.annotation.ComponentScan;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * UserJdbcRepository 통합 테스트
 *
 * Entity↔Domain 변환 로직 검증
 */
@DataJdbcTest
@ComponentScan("io.multi.hello.jdbc.user.repository")
class UserJdbcRepositoryTest {

    @Autowired
    private UserJdbcRepository userRepository;

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

    @Test
    void save_withValidDomain_returnsConvertedDomain() {
        // when
        User saved = userRepository.save(sampleUser);

        // then
        assertThat(saved).isNotNull();
        assertThat(saved.getUserId()).isNotNull();
        assertThat(saved.getEmail()).isEqualTo("test@example.com");
        assertThat(saved.getName()).isEqualTo("testName");
    }

    @Test
    void save_withNullId_generatesIdAndReturns() {
        // given
        User userWithNullId = new User(
                null,
                "nullid@example.com",
                "NullIdUser",
                Instant.now(),
                Instant.now()
        );

        // when
        User saved = userRepository.save(userWithNullId);

        // then
        assertThat(saved.getUserId()).isNotNull();
        assertThat(saved.getUserId()).isGreaterThan(0L);
    }

    @Test
    void findById_existingId_returnsConvertedDomain() {
        // given
        User saved = userRepository.save(sampleUser);
        UserIdentity identity = new UserIdentity(saved.getUserId());

        // when
        Optional<User> found = userRepository.findById(identity);

        // then
        assertThat(found).isPresent();
        assertThat(found.get().getUserId()).isEqualTo(saved.getUserId());
        assertThat(found.get().getEmail()).isEqualTo("test@example.com");
    }

    @Test
    void findById_nonExistingId_returnsEmpty() {
        // when
        Optional<User> found = userRepository.findById(nonExistingIdentity);

        // then
        assertThat(found).isEmpty();
    }

    @Test
    void findByName_existingName_returnsConvertedDomain() {
        // given
        userRepository.save(sampleUser);

        // when
        List<User> found = userRepository.findByName("testName");

        // then
        assertThat(found).isNotEmpty();
        assertThat(found.get(0).getName()).isEqualTo("testName");
    }

    @Test
    void findByName_nonExistingName_returnsEmptyList() {
        // when
        List<User> found = userRepository.findByName("nonExisting");

        // then
        assertThat(found).isEmpty();
    }

    @Test
    void findByEmail_existingEmail_returnsConvertedDomain() {
        // given
        userRepository.save(sampleUser);

        // when
        List<User> found = userRepository.findByEmail("test@example.com");

        // then
        assertThat(found).isNotEmpty();
        assertThat(found.get(0).getEmail()).isEqualTo("test@example.com");
    }

    @Test
    void findByEmail_nonExistingEmail_returnsEmptyList() {
        // when
        List<User> found = userRepository.findByEmail("nonexisting@example.com");

        // then
        assertThat(found).isEmpty();
    }

    @Test
    void deleteById_existingId_removesUser() {
        // given
        User saved = userRepository.save(sampleUser);
        UserIdentity identity = new UserIdentity(saved.getUserId());

        // when
        userRepository.deleteById(identity);

        // then
        Optional<User> found = userRepository.findById(identity);
        assertThat(found).isEmpty();
    }

    @Test
    void existsById_existingId_returnsTrue() {
        // given
        User saved = userRepository.save(sampleUser);
        UserIdentity identity = new UserIdentity(saved.getUserId());

        // when
        boolean exists = userRepository.existsById(identity);

        // then
        assertThat(exists).isTrue();
    }

    @Test
    void existsById_nonExistingId_returnsFalse() {
        // when
        boolean exists = userRepository.existsById(nonExistingIdentity);

        // then
        assertThat(exists).isFalse();
    }
}