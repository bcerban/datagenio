package com.datagenio.crawler.exception;

public class BrowserException extends Exception {

    public BrowserException(String message) {
        super(message);
    }

    public BrowserException(Throwable e) {
        super(e);
    }

    public BrowserException(String message, Throwable e) {
        super(message, e);
    }
}
