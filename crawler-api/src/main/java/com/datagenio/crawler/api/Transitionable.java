package com.datagenio.crawler.api;

import java.net.URI;
import java.util.Collection;

public interface Transitionable {

    enum Status {
        TRAVERSED, SUSPECTED
    }

    State getOrigin();
    State getDestination();
    ExecutedEventable getExecutedEvent();
    Collection<RemoteRequest> getRequests();
    Collection<RemoteRequest> getFilteredRequests(URI uri);
    Status getStatus();
    boolean hasRemoteRequest(URI uri);

    void setOrigin(State origin);
    void setDestination(State destination);
    void setExecutedEvent(ExecutedEventable event);
    void setRequests(Collection<RemoteRequest> requests);
    void addRequest(RemoteRequest request);
    void setStatus(Status status);
}
