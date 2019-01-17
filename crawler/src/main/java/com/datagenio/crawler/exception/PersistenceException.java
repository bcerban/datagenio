package com.datagenio.crawler.exception;

public class PersistenceException extends Exception {
    public PersistenceException(String message) {
        super(message);
    }

    public PersistenceException(String message, Throwable e) {
        super(message, e);
    }

    public PersistenceException(Throwable e) {
        super(e);
    }
}
