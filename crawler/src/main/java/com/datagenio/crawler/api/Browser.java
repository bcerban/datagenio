package com.datagenio.crawler.api;

import com.datagenio.crawler.exception.BrowserException;
import com.datagenio.crawler.exception.UnsupportedEventTypeException;
import org.jsoup.nodes.Document;
import org.openqa.selenium.InvalidArgumentException;

import java.io.File;
import java.net.URI;

public interface Browser {

    int DEFAULT_WAIT_AFTER_LOAD = 500;

    void back() throws BrowserException;
    void close() throws BrowserException;
    void pause() throws BrowserException;
    File takeScreenShot();
    void navigateTo(URI uri) throws BrowserException;
    Document triggerEvent(Eventable event) throws UnsupportedEventTypeException, InvalidArgumentException;
    Document getDOM();
    Object executeJavaScript(String code) throws BrowserException;
}
