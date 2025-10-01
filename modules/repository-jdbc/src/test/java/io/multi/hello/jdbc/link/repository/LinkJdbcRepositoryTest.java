package io.multi.hello.jdbc.link.repository;

import io.multi.hello.jdbc.user.repository.UserJdbcRepository;
import io.multi.hello.model.link.Link;
import io.multi.hello.model.link.LinkIdentity;
import io.multi.hello.model.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.context.annotation.ComponentScan;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * LinkJdbcRepository 통합 테스트
 *
 * Entity↔Domain 변환 로직 검증
 */
@DataJdbcTest
@ComponentScan({"io.multi.hello.jdbc.link.repository", "io.multi.hello.jdbc.user.repository"})
class LinkJdbcRepositoryTest {

    @Autowired
    private LinkJdbcRepository linkRepository;

    @Autowired
    private UserJdbcRepository userRepository;

    private Long testUserId;
    private Link sampleLink;

    private final LinkIdentity nonExistingIdentity = new LinkIdentity(999L);

    @BeforeEach
    void setUp() {
        // 외래키 제약을 위해 User 먼저 생성
        User testUser = new User(
                null,
                "test@example.com",
                "testUser",
                Instant.now(),
                Instant.now()
        );
        User savedUser = userRepository.save(testUser);
        testUserId = savedUser.getUserId();

        // 테스트 데이터
        sampleLink = new Link(
                null,
                "https://example.com/very-long-url",
                "abc123",
                testUserId,
                Instant.now().plusSeconds(3600),
                Instant.now(),
                Instant.now()
        );
    }

    @Test
    void save_withValidDomain_returnsConvertedDomain() {
        // when
        Link saved = linkRepository.save(sampleLink);

        // then
        assertThat(saved).isNotNull();
        assertThat(saved.getLinkId()).isNotNull();
        assertThat(saved.getOriginalUrl()).isEqualTo("https://example.com/very-long-url");
        assertThat(saved.getShortCode()).isEqualTo("abc123");
        assertThat(saved.getUserId()).isEqualTo(testUserId);
    }

    @Test
    void save_withNullId_generatesIdAndReturns() {
        // given
        Link linkWithNullId = new Link(
                null,
                "https://test.com",
                "test123",
                testUserId,
                null,
                Instant.now(),
                Instant.now()
        );

        // when
        Link saved = linkRepository.save(linkWithNullId);

        // then
        assertThat(saved.getLinkId()).isNotNull();
        assertThat(saved.getLinkId()).isGreaterThan(0L);
    }

    @Test
    void findById_existingId_returnsConvertedDomain() {
        // given
        Link saved = linkRepository.save(sampleLink);
        LinkIdentity identity = new LinkIdentity(saved.getLinkId());

        // when
        Optional<Link> found = linkRepository.findById(identity);

        // then
        assertThat(found).isPresent();
        assertThat(found.get().getLinkId()).isEqualTo(saved.getLinkId());
        assertThat(found.get().getOriginalUrl()).isEqualTo("https://example.com/very-long-url");
    }

    @Test
    void findById_nonExistingId_returnsEmpty() {
        // when
        Optional<Link> found = linkRepository.findById(nonExistingIdentity);

        // then
        assertThat(found).isEmpty();
    }

    @Test
    void findByUserId_existingUserId_returnsConvertedDomainList() {
        // given
        linkRepository.save(sampleLink);
        Link anotherLink = new Link(
                null,
                "https://another.com",
                "xyz789",
                testUserId,
                null,
                Instant.now(),
                Instant.now()
        );
        linkRepository.save(anotherLink);

        // when
        List<Link> found = linkRepository.findByUserId(testUserId);

        // then
        assertThat(found).hasSize(2);
        assertThat(found).allMatch(link -> link.getUserId().equals(testUserId));
    }

    @Test
    void findByUserId_nonExistingUserId_returnsEmptyList() {
        // when
        List<Link> found = linkRepository.findByUserId(999L);

        // then
        assertThat(found).isEmpty();
    }

    @Test
    void findByOriginalUrl_existingUrl_returnsConvertedDomain() {
        // given
        linkRepository.save(sampleLink);

        // when
        Optional<Link> found = linkRepository.findByOriginalUrl("https://example.com/very-long-url");

        // then
        assertThat(found).isPresent();
        assertThat(found.get().getOriginalUrl()).isEqualTo("https://example.com/very-long-url");
    }

    @Test
    void findByOriginalUrl_nonExistingUrl_returnsEmpty() {
        // when
        Optional<Link> found = linkRepository.findByOriginalUrl("https://nonexisting.com");

        // then
        assertThat(found).isEmpty();
    }

    @Test
    void findByShortCode_existingCode_returnsConvertedDomain() {
        // given
        linkRepository.save(sampleLink);

        // when
        Optional<Link> found = linkRepository.findByShortCode("abc123");

        // then
        assertThat(found).isPresent();
        assertThat(found.get().getShortCode()).isEqualTo("abc123");
    }

    @Test
    void findByShortCode_nonExistingCode_returnsEmpty() {
        // when
        Optional<Link> found = linkRepository.findByShortCode("nonexisting");

        // then
        assertThat(found).isEmpty();
    }

    @Test
    void deleteById_existingId_removesLink() {
        // given
        Link saved = linkRepository.save(sampleLink);
        LinkIdentity identity = new LinkIdentity(saved.getLinkId());

        // when
        linkRepository.deleteById(identity);

        // then
        Optional<Link> found = linkRepository.findById(identity);
        assertThat(found).isEmpty();
    }

    @Test
    void existsById_existingId_returnsTrue() {
        // given
        Link saved = linkRepository.save(sampleLink);
        LinkIdentity identity = new LinkIdentity(saved.getLinkId());

        // when
        boolean exists = linkRepository.existsById(identity);

        // then
        assertThat(exists).isTrue();
    }

    @Test
    void existsById_nonExistingId_returnsFalse() {
        // when
        boolean exists = linkRepository.existsById(nonExistingIdentity);

        // then
        assertThat(exists).isFalse();
    }

    @Test
    void existsByShortCode_existingCode_returnsTrue() {
        // given
        linkRepository.save(sampleLink);

        // when
        boolean exists = linkRepository.existsByShortCode("abc123");

        // then
        assertThat(exists).isTrue();
    }

    @Test
    void existsByShortCode_nonExistingCode_returnsFalse() {
        // when
        boolean exists = linkRepository.existsByShortCode("nonexisting");

        // then
        assertThat(exists).isFalse();
    }
}