package io.multi.hello.exception.link;

import io.multi.hello.exception.ClientException;

public class LinkAlreadyExistException extends ClientException {

    protected LinkAlreadyExistException(String status, String message) {
        super(status, message);
    }

    public LinkAlreadyExistException(String message) {
        super("400", message);
    }
}