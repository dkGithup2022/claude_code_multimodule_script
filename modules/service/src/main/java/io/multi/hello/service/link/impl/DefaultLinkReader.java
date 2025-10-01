package io.multi.hello.service.link.impl;

import io.multi.hello.exception.link.LinkNotFoundException;
import io.multi.hello.infrastructure.link.repository.LinkRepository;
import io.multi.hello.model.link.Link;
import io.multi.hello.model.link.LinkIdentity;
import io.multi.hello.service.link.LinkReader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Link 도메인 조회 서비스 구현체
 * <p>
 * CQRS 패턴의 Query 책임을 구현하며,
 * Infrastructure Repository를 활용한 조회 로직을 제공합니다.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DefaultLinkReader implements LinkReader {
    private final LinkRepository linkRepository;


    @Override
    public Link fromLongUrl(String longUrl) {
        var link = linkRepository.findByOriginalUrl(longUrl);
        if (link.isPresent()) {
            return link.get();
        }
        throw new LinkNotFoundException(String.format("Cannot find link for longUrl: %s", longUrl));
    }

    @Override
    public Link fromShortUrl(String shortCode) {
        var link = linkRepository.findByShortCode(shortCode);
        if (link.isPresent()) {
            return link.get();
        }
        throw new LinkNotFoundException(String.format("Cannot find link for shortCode: %s", shortCode));
    }

    @Override
    public List<Link> findAll() {
        return linkRepository.findAll();
    }

    @Override
    public List<Link> findByUserId(Long userId) {
        return linkRepository.findByUserId(userId);
    }
}