package com.datagenio.crawler.exception;

public class OutOfBoundsException extends Exception {
    public OutOfBoundsException(String message) {
        super(message);
    }

    public OutOfBoundsException(String message, Throwable e) {
        super(message, e);
    }

    public OutOfBoundsException(Throwable e) {
        super(e);
    }
}
