package io.multi.hello.service.linkclick.impl;

import io.multi.hello.infrastructure.linkclick.repository.LinkClickRepository;
import io.multi.hello.model.linkclick.LinkClick;
import io.multi.hello.model.linkclick.LinkClickIdentity;
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
 * DefaultLinkClickReader 단위 테스트
 *
 * Repository Mock을 사용한 단순 위임 로직 검증
 */
@ExtendWith(MockitoExtension.class)
class DefaultLinkClickReaderTest {

    @Mock
    private LinkClickRepository linkClickRepository;

    @InjectMocks
    private DefaultLinkClickReader linkClickReader;

    // 테스트 데이터
    private final LinkClick sampleLinkClick = new LinkClick(
            1L,
            100L,
            Instant.now(),
            "192.168.1.1",
            "Mozilla/5.0",
            "https://google.com"
    );

    private final LinkClickIdentity testIdentity = new LinkClickIdentity(1L);
    private final LinkClickIdentity nonExistingIdentity = new LinkClickIdentity(999L);

    // findById 테스트
    @Test
    void findById_existingId_returnsLinkClick() {
        // given
        when(linkClickRepository.findById(testIdentity))
                .thenReturn(Optional.of(sampleLinkClick));

        // when
        Optional<LinkClick> result = linkClickReader.findById(testIdentity);

        // then
        assertThat(result).isPresent();
        assertThat(result.get().getClickId()).isEqualTo(1L);
        assertThat(result.get().getLinkId()).isEqualTo(100L);
        verify(linkClickRepository).findById(testIdentity);
    }

    @Test
    void findById_nonExistingId_returnsEmpty() {
        // given
        when(linkClickRepository.findById(nonExistingIdentity))
                .thenReturn(Optional.empty());

        // when
        Optional<LinkClick> result = linkClickReader.findById(nonExistingIdentity);

        // then
        assertThat(result).isEmpty();
        verify(linkClickRepository).findById(nonExistingIdentity);
    }

    // findByLinkId 테스트
    @Test
    void findByLinkId_existingLinkId_returnsList() {
        // given
        LinkClick secondClick = new LinkClick(
                2L,
                100L,
                Instant.now(),
                "192.168.1.2",
                "Chrome/1.0",
                null
        );
        when(linkClickRepository.findByLinkId(100L))
                .thenReturn(List.of(sampleLinkClick, secondClick));

        // when
        List<LinkClick> result = linkClickReader.findByLinkId(100L);

        // then
        assertThat(result).hasSize(2);
        assertThat(result).allMatch(click -> click.getLinkId().equals(100L));
        verify(linkClickRepository).findByLinkId(100L);
    }

    @Test
    void findByLinkId_nonExistingLinkId_returnsEmptyList() {
        // given
        when(linkClickRepository.findByLinkId(999L))
                .thenReturn(List.of());

        // when
        List<LinkClick> result = linkClickReader.findByLinkId(999L);

        // then
        assertThat(result).isEmpty();
        verify(linkClickRepository).findByLinkId(999L);
    }

    // countByLinkId 테스트
    @Test
    void countByLinkId_existingLinkId_returnsCount() {
        // given
        when(linkClickRepository.countByLinkId(100L))
                .thenReturn(5L);

        // when
        long result = linkClickReader.countByLinkId(100L);

        // then
        assertThat(result).isEqualTo(5L);
        verify(linkClickRepository).countByLinkId(100L);
    }

    @Test
    void countByLinkId_nonExistingLinkId_returnsZero() {
        // given
        when(linkClickRepository.countByLinkId(999L))
                .thenReturn(0L);

        // when
        long result = linkClickReader.countByLinkId(999L);

        // then
        assertThat(result).isEqualTo(0L);
        verify(linkClickRepository).countByLinkId(999L);
    }
}