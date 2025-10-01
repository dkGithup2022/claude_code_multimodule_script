package io.multi.hello.service.linkclick.impl;

import io.multi.hello.infrastructure.linkclick.repository.LinkClickRepository;
import io.multi.hello.model.linkclick.LinkClick;
import io.multi.hello.model.linkclick.LinkClickIdentity;
import io.multi.hello.service.linkclick.LinkClickReader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * LinkClick 도메인 조회 서비스 구현체
 *
 * 단순 CRUD 조회 기능을 제공합니다.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DefaultLinkClickReader implements LinkClickReader {

    private final LinkClickRepository linkClickRepository;

    @Override
    public Optional<LinkClick> findById(LinkClickIdentity identity) {
        log.debug("Find click by id: {}", identity.getClickId());
        return linkClickRepository.findById(identity);
    }

    @Override
    public List<LinkClick> findByLinkId(Long linkId) {
        log.debug("Find clicks by linkId: {}", linkId);
        return linkClickRepository.findByLinkId(linkId);
    }

    @Override
    public long countByLinkId(Long linkId) {
        log.debug("Count clicks by linkId: {}", linkId);
        return linkClickRepository.countByLinkId(linkId);
    }
}