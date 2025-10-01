package io.multi.hello.service.link;

import io.multi.hello.model.link.Link;
import io.multi.hello.model.link.LinkIdentity;
import io.multi.hello.service.link.dto.CreateLinkCommand;

/**
 * Link 도메인 변경 서비스 인터페이스
 *
 * CQRS 패턴의 Command 책임을 담당하며,
 * Infrastructure Repository 기반으로 변경 로직을 제공합니다.
 */
public interface LinkWriter {

    /**
     * Link 저장 (생성/수정)
     *
     * @param command 링크 저장에 필요한 요소 ( url & userId) ;
     * @return 저장된 Link
     */
    Link create(CreateLinkCommand command);

    /**
     * ID로 Link 삭제
     *
     * @param identity Link 식별자
     */
    void deleteById(LinkIdentity identity);
}