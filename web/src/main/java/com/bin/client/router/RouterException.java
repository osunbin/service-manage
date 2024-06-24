package com.bin.client.router;

public class RouterException extends RuntimeException{

    public RouterException() {
    }

    public RouterException(String message) {
        super(message);
    }

    public RouterException(String message, Throwable cause) {
        super(message, cause);
    }

    public RouterException(Throwable cause) {
        super(cause);
    }
}
