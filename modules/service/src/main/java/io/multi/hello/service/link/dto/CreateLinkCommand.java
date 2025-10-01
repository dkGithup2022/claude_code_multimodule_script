package io.multi.hello.service.link.dto;

public record CreateLinkCommand(
        long userId,
        String url
) {
}
