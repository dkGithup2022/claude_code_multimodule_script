package io.multi.hello.exception.user;

import io.multi.hello.exception.ClientException;

public class CantCreateUserException extends ClientException {

    protected CantCreateUserException(String status, String message) {
        super(status, message);
    }

    public CantCreateUserException(String message) {
        super("400", message);
    }
}
