package io.multi.hello.exception.link;

import io.multi.hello.exception.ClientException;

public class LinkNotFoundException  extends ClientException {

    protected LinkNotFoundException(String status, String message) {
        super(status, message);
    }

    public LinkNotFoundException(String message) {
        super("404", message);
    }
}