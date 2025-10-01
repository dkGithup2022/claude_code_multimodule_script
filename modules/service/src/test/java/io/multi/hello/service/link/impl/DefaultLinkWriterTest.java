package io.multi.hello.service.link.impl;

import io.multi.hello.exception.link.LinkAlreadyExistException;
import io.multi.hello.exception.link.LinkNotFoundException;
import io.multi.hello.exception.user.UserNotFoundException;
import io.multi.hello.infrastructure.link.repository.LinkRepository;
import io.multi.hello.model.link.Link;
import io.multi.hello.model.link.LinkIdentity;
import io.multi.hello.model.user.User;
import io.multi.hello.model.user.UserIdentity;
import io.multi.hello.service.link.url.ShortCodeGenerator;
import io.multi.hello.service.link.dto.CreateLinkCommand;
import io.multi.hello.service.user.UserReader;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * DefaultLinkWriter 단위 테스트
 *
 * Repository Mock을 사용한 비즈니스 로직 검증
 * - User 존재 확인
 * - URL 중복 검증
 * - ShortCode 생성 (Mock 처리)
 * - 삭제 전 존재 확인
 */
@ExtendWith(MockitoExtension.class)
class DefaultLinkWriterTest {

    @Mock
    private LinkRepository linkRepository;

    @Mock
    private ShortCodeGenerator codeGenerator;

    @Mock
    private UserReader userReader;

    @InjectMocks
    private DefaultLinkWriter linkWriter;

    // 테스트 데이터
    private final User sampleUser = new User(
            1L,
            "test@example.com",
            "testName",
            Instant.now(),
            Instant.now()
    );

    private final CreateLinkCommand validCommand = new CreateLinkCommand(
            1L,
            "https://example.com/new-url"
    );

    private final LinkIdentity testIdentity = new LinkIdentity(1L);
    private final LinkIdentity nonExistingIdentity = new LinkIdentity(999L);

    // create 성공 테스트
    @Test
    void create_validCommand_callsRepositorySave() {
        // given
        when(userReader.findById(new UserIdentity(1L)))
                .thenReturn(Optional.of(sampleUser));
        when(linkRepository.findByOriginalUrl("https://example.com/new-url"))
                .thenReturn(Optional.empty());
        when(codeGenerator.generateWithSalt())
                .thenReturn("mockCode123");
        when(linkRepository.save(any(Link.class)))
                .thenAnswer(invocation -> {
                    Link arg = invocation.getArgument(0);
                    return new Link(
                            1L,
                            arg.getOriginalUrl(),
                            arg.getShortCode(),
                            arg.getUserId(),
                            arg.getExpiresAt(),
                            arg.getCreatedAt(),
                            arg.getUpdatedAt()
                    );
                });

        // when
        Link result = linkWriter.create(validCommand);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getLinkId()).isEqualTo(1L);
        assertThat(result.getOriginalUrl()).isEqualTo("https://example.com/new-url");
        assertThat(result.getShortCode()).isEqualTo("mockCode123");
        assertThat(result.getUserId()).isEqualTo(1L);

        verify(userReader).findById(new UserIdentity(1L));
        verify(linkRepository).findByOriginalUrl("https://example.com/new-url");
        verify(codeGenerator).generateWithSalt();
        verify(linkRepository).save(any(Link.class));
    }

    // create 실패 테스트 - User 없음
    @Test
    void create_nonExistingUser_throwsException() {
        // given
        when(userReader.findById(new UserIdentity(1L)))
                .thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> linkWriter.create(validCommand))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining("User with id '1' not found");

        verify(userReader).findById(new UserIdentity(1L));
        verify(linkRepository, never()).save(any());
    }

    // create 실패 테스트 - URL 중복
    @Test
    void create_duplicateUrl_throwsException() {
        // given
        Link existingLink = new Link(
                1L,
                "https://example.com/new-url",
                "existing123",
                1L,
                Instant.now().plusSeconds(3600),
                Instant.now(),
                Instant.now()
        );

        when(userReader.findById(new UserIdentity(1L)))
                .thenReturn(Optional.of(sampleUser));
        when(linkRepository.findByOriginalUrl("https://example.com/new-url"))
                .thenReturn(Optional.of(existingLink));

        // when & then
        assertThatThrownBy(() -> linkWriter.create(validCommand))
                .isInstanceOf(LinkAlreadyExistException.class)
                .hasMessageContaining("이미 존재하는 url 이에요");

        verify(userReader).findById(new UserIdentity(1L));
        verify(linkRepository).findByOriginalUrl("https://example.com/new-url");
        verify(linkRepository, never()).save(any());
    }

    // deleteById 성공 테스트
    @Test
    void deleteById_existingId_callsRepositoryDelete() {
        // given
        when(linkRepository.existsById(testIdentity))
                .thenReturn(true);

        // when
        linkWriter.deleteById(testIdentity);

        // then
        verify(linkRepository).existsById(testIdentity);
        verify(linkRepository).deleteById(testIdentity);
    }

    // deleteById 실패 테스트 - 존재하지 않는 ID
    @Test
    void deleteById_nonExistingId_throwsException() {
        // given
        when(linkRepository.existsById(nonExistingIdentity))
                .thenReturn(false);

        // when & then
        assertThatThrownBy(() -> linkWriter.deleteById(nonExistingIdentity))
                .isInstanceOf(LinkNotFoundException.class)
                .hasMessageContaining("Link with id '999' not found");

        verify(linkRepository).existsById(nonExistingIdentity);
        verify(linkRepository, never()).deleteById(any());
    }
}