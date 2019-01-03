package com.datagenio.crawler.model;

import com.datagenio.crawler.api.ExecutedEventable;
import com.datagenio.crawler.api.State;
import com.datagenio.crawler.api.Transitionable;
import org.apache.http.HttpRequest;

import java.util.ArrayList;
import java.util.Collection;

public class Transition implements Transitionable {

    private State origin;
    private State destination;
    private ExecutedEventable executedEvent;
    private Collection<HttpRequest> requests;

    public Transition(State origin, State destination, ExecutedEventable event) {
        this.origin = origin;
        this.destination = destination;
        this.executedEvent = event;
        this.requests = new ArrayList<>();
    }

    @Override
    public State getOrigin() {
        return this.origin;
    }

    @Override
    public State getDestination() {
        return this.destination;
    }

    @Override
    public ExecutedEventable getExecutedEvent() {
        return this.executedEvent;
    }

    @Override
    public Collection<HttpRequest> getRequests() {
        return this.requests;
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
    public void setRequests(Collection<HttpRequest> requests) {
        this.requests = requests;
    }

    @Override
    public void addRequest(HttpRequest request) {
        this.requests.add(request);
    }
}
