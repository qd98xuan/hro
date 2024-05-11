package com.linzen.exception;

public class ConnectDatabaseException extends RuntimeException {

    public ConnectDatabaseException(String reason, Throwable cause) {
        super(reason, cause);
    }
}
