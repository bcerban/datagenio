package com.datagenio.crawler.exception;

import org.openqa.selenium.WebDriverException;

public class BrowserException extends Exception {

    public BrowserException(String message) {
        super(message);
    }

    public BrowserException(WebDriverException e) {
        super(e);
    }

    public BrowserException(Throwable e) {
        super(e);
    }

    public BrowserException(String message, Throwable e) {
        super(message, e);
    }
}
