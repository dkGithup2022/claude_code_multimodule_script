package io.multi.hello.service.link.impl;

import io.multi.hello.exception.link.LinkAlreadyExistException;
import io.multi.hello.exception.link.LinkNotFoundException;
import io.multi.hello.exception.user.UserNotFoundException;
import io.multi.hello.infrastructure.link.repository.LinkRepository;
import io.multi.hello.model.link.Link;
import io.multi.hello.model.link.LinkIdentity;
import io.multi.hello.model.user.UserIdentity;
import io.multi.hello.service.link.LinkWriter;
import io.multi.hello.service.link.dto.CreateLinkCommand;
import io.multi.hello.service.link.url.ShortCodeGenerator;
import io.multi.hello.service.user.UserReader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;

/**
 * Link 도메인 변경 서비스 구현체
 * <p>
 * CQRS 패턴의 Command 책임을 구현하며,
 * Infrastructure Repository를 활용한 변경 로직을 제공합니다.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DefaultLinkWriter implements LinkWriter {

    private final LinkRepository linkRepository;
    private final ShortCodeGenerator codeGenerator;
    private final UserReader userReader;


    @Override
    public Link create(CreateLinkCommand command) {
        log.info("Create link: {}", command);

        validateUser(command.userId());
        validateLink(command.url());

        var shorten = codeGenerator.generateWithSalt();
        var now = Instant.now();

        var toSave = new Link(
                null,
                command.url(),
                shorten,
                command.userId(),
                now.plus(Duration.ofDays(365L)), now, now
        );
        return linkRepository.save(toSave);
    }

    @Override
    public void deleteById(LinkIdentity identity) {
        log.info("Deleting link by id: {}", identity.getLinkId());

        var exists = linkRepository.existsById(identity);
        if (!exists) {
            throw new LinkNotFoundException(String.format("Link with id '%s' not found", identity.getLinkId()));
        }

        linkRepository.deleteById(identity);
    }

    private void validateLink(String url) {

        var link = linkRepository.findByOriginalUrl(url);
        if (link.isPresent()) {
            throw new LinkAlreadyExistException("이미 존재하는 url 이에요 : " + url);
        }
    }

    private void validateUser(long id) {
        var user = userReader.findById(new UserIdentity(id));
        if (user.isEmpty()) {
            throw new UserNotFoundException(String.format("User with id '%d' not found", id));
        }
    }


}