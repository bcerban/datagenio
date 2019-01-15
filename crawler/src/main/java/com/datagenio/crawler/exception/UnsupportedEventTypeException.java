package com.datagenio.crawler.exception;

public class UnsupportedEventTypeException extends Exception {
    public UnsupportedEventTypeException(String message) {
        super(message);
    }
    public UnsupportedEventTypeException(Throwable e) {
        super(e);
    }
}
