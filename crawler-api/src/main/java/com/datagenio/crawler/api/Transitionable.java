package com.datagenio.crawler.api;

import org.apache.http.HttpRequest;

import java.util.Collection;

public interface Transitionable {

    State getOrigin();
    State getDestination();
    ExecutedEventable getExecutedEvent();
    Collection<RemoteRequest> getRequests();

    void setOrigin(State origin);
    void setDestination(State destination);
    void setExecutedEvent(ExecutedEventable event);
    void setRequests(Collection<RemoteRequest> requests);

    void addRequest(RemoteRequest request);
}
