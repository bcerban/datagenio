package com.datagenio.crawler.model;

import com.datagenio.crawler.api.ExecutedEventable;
import com.datagenio.crawler.api.RemoteRequest;
import com.datagenio.crawler.api.State;
import com.datagenio.crawler.api.Transitionable;
import com.datagenio.crawler.util.SiteBoundChecker;
import org.apache.http.HttpRequest;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

public class Transition implements Transitionable {

    private State origin;
    private State destination;
    private ExecutedEventable executedEvent;
    private Collection<RemoteRequest> requests;
    private Status status;

    public Transition(State origin, State destination, ExecutedEventable event) {
        this.origin = origin;
        this.destination = destination;
        this.executedEvent = event;
        this.requests = new ArrayList<>();
    }

    @Override
    public State getOrigin() {
        return origin;
    }

    @Override
    public State getDestination() {
        return destination;
    }

    @Override
    public ExecutedEventable getExecutedEvent() {
        return executedEvent;
    }

    @Override
    public Collection<RemoteRequest> getRequests() {
        return requests;
    }

    @Override
    public Collection<RemoteRequest> getFilteredRequests(URI uri) {
        return getRequests().stream()
                .filter(r -> !SiteBoundChecker.isOutOfBounds(URI.create(r.getUrl()), uri))
                .collect(Collectors.toList());
    }

    @Override
    public void setOrigin(State origin) {
        this.origin = origin;
    }

    @Override
    public void setDestination(State destination) {
        this.destination = destination;
    }

    @Override
    public void setExecutedEvent(ExecutedEventable event) {
        this.executedEvent = event;
    }

    @Override
    public void setRequests(Collection<RemoteRequest> requests) {
        this.requests = requests;
    }

    @Override
    public void addRequest(RemoteRequest request) {
        requests.add(request);
    }

    @Override
    public Status getStatus() {
        return status;
    }

    @Override
    public boolean hasRemoteRequest(URI uri) {
        return getRequests().stream()
                .filter(r -> !SiteBoundChecker.isOutOfBounds(URI.create(r.getUrl()), uri))
                .count() > 0;
    }

    @Override
    public void setStatus(Status status) {
        this.status = status;
    }
}
