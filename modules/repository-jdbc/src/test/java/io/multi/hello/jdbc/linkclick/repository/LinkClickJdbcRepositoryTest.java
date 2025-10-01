package io.multi.hello.jdbc.linkclick.repository;

import io.multi.hello.jdbc.link.repository.LinkJdbcRepository;
import io.multi.hello.jdbc.user.repository.UserJdbcRepository;
import io.multi.hello.model.link.Link;
import io.multi.hello.model.linkclick.LinkClick;
import io.multi.hello.model.linkclick.LinkClickIdentity;
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
 * LinkClickJdbcRepository 통합 테스트
 *
 * Entity↔Domain 변환 로직 검증
 */
@DataJdbcTest
@ComponentScan({
        "io.multi.hello.jdbc.linkclick.repository",
        "io.multi.hello.jdbc.link.repository",
        "io.multi.hello.jdbc.user.repository"
})
class LinkClickJdbcRepositoryTest {

    @Autowired
    private LinkClickJdbcRepository linkClickRepository;

    @Autowired
    private LinkJdbcRepository linkRepository;

    @Autowired
    private UserJdbcRepository userRepository;

    private Long testLinkId;
    private LinkClick sampleLinkClick;

    private final LinkClickIdentity nonExistingIdentity = new LinkClickIdentity(999L);

    @BeforeEach
    void setUp() {
        // 외래키 제약을 위해 User -> Link 먼저 생성
        User testUser = new User(
                null,
                "test@example.com",
                "testUser",
                Instant.now(),
                Instant.now()
        );
        User savedUser = userRepository.save(testUser);

        Link testLink = new Link(
                null,
                "https://example.com",
                "abc123",
                savedUser.getUserId(),
                null,
                Instant.now(),
                Instant.now()
        );
        Link savedLink = linkRepository.save(testLink);
        testLinkId = savedLink.getLinkId();

        // 테스트 데이터
        sampleLinkClick = new LinkClick(
                null,
                testLinkId,
                Instant.now(),
                "192.168.1.1",
                "Mozilla/5.0",
                "https://google.com"
        );
    }

    @Test
    void save_withValidDomain_returnsConvertedDomain() {
        // when
        LinkClick saved = linkClickRepository.save(sampleLinkClick);

        // then
        assertThat(saved).isNotNull();
        assertThat(saved.getClickId()).isNotNull();
        assertThat(saved.getLinkId()).isEqualTo(testLinkId);
        assertThat(saved.getIpAddress()).isEqualTo("192.168.1.1");
        assertThat(saved.getUserAgent()).isEqualTo("Mozilla/5.0");
        assertThat(saved.getReferer()).isEqualTo("https://google.com");
    }

    @Test
    void save_withNullId_generatesIdAndReturns() {
        // given
        LinkClick clickWithNullId = new LinkClick(
                null,
                testLinkId,
                Instant.now(),
                "10.0.0.1",
                "Chrome/1.0",
                null
        );

        // when
        LinkClick saved = linkClickRepository.save(clickWithNullId);

        // then
        assertThat(saved.getClickId()).isNotNull();
        assertThat(saved.getClickId()).isGreaterThan(0L);
    }

    @Test
    void findById_existingId_returnsConvertedDomain() {
        // given
        LinkClick saved = linkClickRepository.save(sampleLinkClick);
        LinkClickIdentity identity = new LinkClickIdentity(saved.getClickId());

        // when
        Optional<LinkClick> found = linkClickRepository.findById(identity);

        // then
        assertThat(found).isPresent();
        assertThat(found.get().getClickId()).isEqualTo(saved.getClickId());
        assertThat(found.get().getLinkId()).isEqualTo(testLinkId);
    }

    @Test
    void findById_nonExistingId_returnsEmpty() {
        // when
        Optional<LinkClick> found = linkClickRepository.findById(nonExistingIdentity);

        // then
        assertThat(found).isEmpty();
    }

    @Test
    void findByLinkId_existingLinkId_returnsConvertedDomainList() {
        // given
        linkClickRepository.save(sampleLinkClick);
        LinkClick anotherClick = new LinkClick(
                null,
                testLinkId,
                Instant.now(),
                "192.168.1.2",
                "Safari/1.0",
                null
        );
        linkClickRepository.save(anotherClick);

        // when
        List<LinkClick> found = linkClickRepository.findByLinkId(testLinkId);

        // then
        assertThat(found).hasSize(2);
        assertThat(found).allMatch(click -> click.getLinkId().equals(testLinkId));
    }

    @Test
    void findByLinkId_nonExistingLinkId_returnsEmptyList() {
        // when
        List<LinkClick> found = linkClickRepository.findByLinkId(999L);

        // then
        assertThat(found).isEmpty();
    }

    @Test
    void countByLinkId_existingLinkId_returnsCorrectCount() {
        // given
        linkClickRepository.save(sampleLinkClick);
        LinkClick secondClick = new LinkClick(
                null,
                testLinkId,
                Instant.now(),
                "192.168.1.3",
                "Firefox/1.0",
                null
        );
        linkClickRepository.save(secondClick);

        LinkClick thirdClick = new LinkClick(
                null,
                testLinkId,
                Instant.now(),
                "192.168.1.4",
                "Edge/1.0",
                null
        );
        linkClickRepository.save(thirdClick);

        // when
        long count = linkClickRepository.countByLinkId(testLinkId);

        // then
        assertThat(count).isEqualTo(3);
    }

    @Test
    void countByLinkId_nonExistingLinkId_returnsZero() {
        // when
        long count = linkClickRepository.countByLinkId(999L);

        // then
        assertThat(count).isEqualTo(0);
    }
}