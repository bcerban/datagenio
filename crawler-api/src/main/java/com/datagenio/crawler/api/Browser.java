package com.datagenio.crawler.api;

import com.datagenio.crawler.exception.BrowserException;
import com.datagenio.crawler.exception.EventTriggerException;
import com.datagenio.crawler.exception.UnsupportedEventTypeException;
import org.apache.http.HttpRequest;
import org.jsoup.nodes.Document;

import java.io.File;
import java.net.URI;
import java.util.Collection;
import java.util.Map;

public interface Browser {

    int DEFAULT_WAIT_AFTER_LOAD = 500;

    void navigateTo(URI uri) throws BrowserException;
    void back() throws BrowserException;
    void backOrClose() throws BrowserException;
    void close() throws BrowserException;
    void quit() throws BrowserException;
    void pause() throws BrowserException;
    void triggerEvent(Eventable event, Map<String, String> inputs) throws UnsupportedEventTypeException, EventTriggerException;

    State getCurrentBrowserState();
    Document getDOM();
    Object executeJavaScript(String code) throws BrowserException;
    File getScreenShotFile();
    byte[] getScreenShotBytes();
    Collection<RemoteRequest> getCapturedRequests(URI domain);
    Collection<RemoteRequest> getCapturedRequests(URI domain, String fileName, String saveToDirectory);
}
