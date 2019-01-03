package com.datagenio.crawler.api;

import org.apache.http.HttpRequest;

import java.util.Collection;

public interface Transitionable {

    State getOrigin();
    State getDestination();
    ExecutedEventable getExecutedEvent();
    Collection<HttpRequest> getRequests();

    void setOrigin(State origin);
    void setDestination(State destination);
    void setExecutedEvent(ExecutedEventable event);
    void setRequests(Collection<HttpRequest> requests);

    void addRequest(HttpRequest request);
}
