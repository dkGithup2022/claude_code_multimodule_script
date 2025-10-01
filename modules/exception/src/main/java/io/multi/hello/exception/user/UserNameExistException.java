package io.multi.hello.exception.user;

import io.multi.hello.exception.ClientException;

public class UserNameExistException extends ClientException {
    protected UserNameExistException(String status, String message) {
        super(status, message);
    }


    public UserNameExistException(String message) {
        this("400", message);
    }
}
