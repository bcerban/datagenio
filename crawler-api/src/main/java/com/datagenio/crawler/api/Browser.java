package com.datagenio.crawler.api;

import com.datagenio.context.EventInput;
import com.datagenio.crawler.exception.BrowserException;
import com.datagenio.crawler.exception.EventTriggerException;
import com.datagenio.crawler.exception.UnsupportedEventTypeException;
import org.apache.http.HttpRequest;
import org.jsoup.nodes.Document;

import java.io.File;
import java.net.URI;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface Browser {

    int DEFAULT_WAIT_AFTER_LOAD = 120;
    int DEFAULT_WAIT_AFTER_SUBMIT = 500;

    void navigateTo(URI uri) throws BrowserException;
    void navigateTo(URI uri, boolean saveProxyData) throws BrowserException;
    void back() throws BrowserException;
    void backOrClose() throws BrowserException;
    void close() throws BrowserException;
    void quit() throws BrowserException;
    void pause() throws BrowserException;
    void triggerEvent(Eventable event, List<EventInput> inputs) throws UnsupportedEventTypeException, EventTriggerException;
    void triggerEvent(Eventable event, List<EventInput> inputs, boolean saveProxyData) throws UnsupportedEventTypeException, EventTriggerException;

    State getCurrentBrowserState() throws BrowserException;
    Document getDOM() throws BrowserException;
    Object executeJavaScript(String code) throws BrowserException;
    File getScreenShotFile();
    byte[] getScreenShotBytes();
    Collection<RemoteRequest> getCapturedRequests(URI domain);
    Collection<RemoteRequest> getCapturedRequests(URI domain, String fileName, String saveToDirectory);
}
