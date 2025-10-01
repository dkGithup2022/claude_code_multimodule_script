package io.multi.hello.exception;
public abstract class ClientException extends ApplicationException {
    protected ClientException(String status, String message) {
        super(status, message);
    }
}
