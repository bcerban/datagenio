package com.datagenio.crawler.exception;

public class UncrawlableStateException extends Exception {
    public UncrawlableStateException(String message) {
        super(message);
    }

    public UncrawlableStateException(Throwable e) {
        super(e);
    }
}
