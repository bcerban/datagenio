package com.datagenio.model.api;

import org.apache.http.HttpRequest;

import java.util.Collection;

public interface WebTransition {

    WebState getOrigin();
    WebState getDestination();
    Collection<HttpRequest> getConcreteRequests();
    Collection<AbstractHTTPRequest> getAbstractRequests();

    void setOrigin(WebState origin);
    void setDestination(WebState destination);
    void setConcreteRequests(Collection<HttpRequest> requests);
    void addRequest(HttpRequest request);
}
