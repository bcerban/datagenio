package com.datagenio.crawler.exception;

public class EventTriggerException extends Exception {
    public EventTriggerException(String message) {
        super(message);
    }

    public EventTriggerException(String message, Throwable e) {
        super(message, e);
    }

    public EventTriggerException(Throwable e) {
        super(e);
    }
}
