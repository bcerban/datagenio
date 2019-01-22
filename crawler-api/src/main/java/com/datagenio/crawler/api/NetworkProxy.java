package com.datagenio.crawler.api;

import org.openqa.selenium.Proxy;

import java.net.URI;
import java.util.Collection;

public interface NetworkProxy {

    Proxy getDriverProxy();
    Collection<RemoteRequest> getLoggedRequests();
    Collection<RemoteRequest> getLoggedRequestsForDomain(URI domain);
    Collection<RemoteRequest> getLoggedRequestsForDomain(URI domain, String fileName, String saveTo);

    void saveFor(String site);
    void stop();
}
