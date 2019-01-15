package com.datagenio.crawler.exception;

import org.openqa.selenium.WebDriverException;

public class BrowserException extends Exception {

    public BrowserException(WebDriverException e) {
        super(e);
    }

    public BrowserException(Throwable e) {
        super(e);
    }
}
