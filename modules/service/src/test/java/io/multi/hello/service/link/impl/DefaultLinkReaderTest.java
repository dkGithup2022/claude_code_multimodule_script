package io.multi.hello.service.link.impl;

import io.multi.hello.exception.link.LinkNotFoundException;
import io.multi.hello.infrastructure.link.repository.LinkRepository;
import io.multi.hello.model.link.Link;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

/**
 * DefaultLinkReader 단위 테스트
 *
 * Repository Mock을 사용한 비즈니스 로직 검증
 */
@ExtendWith(MockitoExtension.class)
class DefaultLinkReaderTest {

    @Mock
    private LinkRepository linkRepository;

    @InjectMocks
    private DefaultLinkReader linkReader;

    // 테스트 데이터
    private final Link sampleLink = new Link(
            1L,
            "https://example.com/very-long-url",
            "abc123",
            1L,
            Instant.now().plusSeconds(3600),
            Instant.now(),
            Instant.now()
    );

    // fromLongUrl 테스트
    @Test
    void fromLongUrl_existingUrl_returnsLink() {
        // given
        when(linkRepository.findByOriginalUrl("https://example.com/very-long-url"))
                .thenReturn(Optional.of(sampleLink));

        // when
        Link result = linkReader.fromLongUrl("https://example.com/very-long-url");

        // then
        assertThat(result).isNotNull();
        assertThat(result.getOriginalUrl()).isEqualTo("https://example.com/very-long-url");
        assertThat(result.getShortCode()).isEqualTo("abc123");
        verify(linkRepository).findByOriginalUrl("https://example.com/very-long-url");
    }

    @Test
    void fromLongUrl_nonExistingUrl_throwsException() {
        // given
        when(linkRepository.findByOriginalUrl("https://nonexisting.com"))
                .thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> linkReader.fromLongUrl("https://nonexisting.com"))
                .isInstanceOf(LinkNotFoundException.class)
                .hasMessageContaining("Cannot find link for longUrl: https://nonexisting.com");

        verify(linkRepository).findByOriginalUrl("https://nonexisting.com");
    }

    // fromShortUrl 테스트
    @Test
    void fromShortUrl_existingCode_returnsLink() {
        // given
        when(linkRepository.findByShortCode("abc123"))
                .thenReturn(Optional.of(sampleLink));

        // when
        Link result = linkReader.fromShortUrl("abc123");

        // then
        assertThat(result).isNotNull();
        assertThat(result.getShortCode()).isEqualTo("abc123");
        assertThat(result.getOriginalUrl()).isEqualTo("https://example.com/very-long-url");
        verify(linkRepository).findByShortCode("abc123");
    }

    @Test
    void fromShortUrl_nonExistingCode_throwsException() {
        // given
        when(linkRepository.findByShortCode("nonexisting"))
                .thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> linkReader.fromShortUrl("nonexisting"))
                .isInstanceOf(LinkNotFoundException.class)
                .hasMessageContaining("Cannot find link for shortCode: nonexisting");

        verify(linkRepository).findByShortCode("nonexisting");
    }
}