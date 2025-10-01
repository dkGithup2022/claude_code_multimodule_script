package io.multi.hello.exception.user;

import io.multi.hello.exception.ClientException;

public class UserNotFoundException extends ClientException {
    protected UserNotFoundException(String status, String message) {
        super(status, message);
    }

    public UserNotFoundException(String message) {
        super("404", message);
    }
}
