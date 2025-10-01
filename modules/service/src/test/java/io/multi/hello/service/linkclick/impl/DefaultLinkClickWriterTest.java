package io.multi.hello.service.linkclick.impl;

import io.multi.hello.infrastructure.linkclick.repository.LinkClickRepository;
import io.multi.hello.model.linkclick.LinkClick;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * DefaultLinkClickWriter 단위 테스트
 *
 * Repository Mock을 사용한 단순 저장 로직 검증
 */
@ExtendWith(MockitoExtension.class)
class DefaultLinkClickWriterTest {

    @Mock
    private LinkClickRepository linkClickRepository;

    @InjectMocks
    private DefaultLinkClickWriter linkClickWriter;

    // 테스트 데이터
    private final LinkClick sampleLinkClick = new LinkClick(
            null,
            100L,
            Instant.now(),
            "192.168.1.1",
            "Mozilla/5.0",
            "https://google.com"
    );

    // record 성공 테스트
    @Test
    void record_validLinkClick_callsRepositorySave() {
        // given
        when(linkClickRepository.save(sampleLinkClick))
                .thenAnswer(invocation -> {
                    LinkClick arg = invocation.getArgument(0);
                    return new LinkClick(
                            1L,
                            arg.getLinkId(),
                            arg.getClickedAt(),
                            arg.getIpAddress(),
                            arg.getUserAgent(),
                            arg.getReferer()
                    );
                });

        // when
        LinkClick result = linkClickWriter.record(sampleLinkClick);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getClickId()).isEqualTo(1L);
        assertThat(result.getLinkId()).isEqualTo(100L);
        assertThat(result.getIpAddress()).isEqualTo("192.168.1.1");
        verify(linkClickRepository).save(sampleLinkClick);
    }

    // record 성공 테스트 - null referer
    @Test
    void record_nullReferer_callsRepositorySave() {
        // given
        LinkClick clickWithNullReferer = new LinkClick(
                null,
                100L,
                Instant.now(),
                "192.168.1.2",
                "Chrome/1.0",
                null
        );

        when(linkClickRepository.save(clickWithNullReferer))
                .thenAnswer(invocation -> {
                    LinkClick arg = invocation.getArgument(0);
                    return new LinkClick(
                            2L,
                            arg.getLinkId(),
                            arg.getClickedAt(),
                            arg.getIpAddress(),
                            arg.getUserAgent(),
                            arg.getReferer()
                    );
                });

        // when
        LinkClick result = linkClickWriter.record(clickWithNullReferer);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getClickId()).isEqualTo(2L);
        assertThat(result.getReferer()).isNull();
        verify(linkClickRepository).save(clickWithNullReferer);
    }
}