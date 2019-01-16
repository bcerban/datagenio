package com.datagenio.crawler.exception;

public class UncrawlablePathException extends Exception {
    public UncrawlablePathException(String message) {
        super(message);
    }

    public UncrawlablePathException(Throwable e) {
        super(e);
    }

    public UncrawlablePathException(String message, Throwable e) {
        super(message, e);
    }
}
