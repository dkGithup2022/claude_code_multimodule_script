package io.multi.hello.exception;

public abstract class CommonException extends RuntimeException {
    private final String message;

    @Override
    public String getMessage() {
        return message;
    }

    public CommonException(String message) {
        this.message = message;
    }
}
