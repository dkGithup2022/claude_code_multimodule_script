package io.multi.hello.service.linkclick.impl;

import io.multi.hello.infrastructure.linkclick.repository.LinkClickRepository;
import io.multi.hello.model.linkclick.LinkClick;
import io.multi.hello.service.linkclick.LinkClickWriter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * LinkClick 도메인 변경 서비스 구현체
 *
 * 클릭 이벤트를 단순히 저장합니다.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DefaultLinkClickWriter implements LinkClickWriter {

    private final LinkClickRepository linkClickRepository;

    @Override
    public LinkClick record(LinkClick linkClick) {
        log.info("Recording click: linkId={}, ip={}", linkClick.getLinkId(), linkClick.getIpAddress());
        return linkClickRepository.save(linkClick);
    }
}