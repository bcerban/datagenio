package com.datagenio.crawler.api;

import java.util.Collection;

public interface Transitionable {

    enum Status {
        TRAVERSED, SUSPECTED
    }

    State getOrigin();
    State getDestination();
    ExecutedEventable getExecutedEvent();
    Collection<RemoteRequest> getRequests();
    Status getStatus();

    void setOrigin(State origin);
    void setDestination(State destination);
    void setExecutedEvent(ExecutedEventable event);
    void setRequests(Collection<RemoteRequest> requests);
    void addRequest(RemoteRequest request);
    void setStatus(Status status);
}
