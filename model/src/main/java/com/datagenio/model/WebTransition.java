package com.datagenio.model;

import com.datagenio.model.request.AbstractRequest;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.UUID;

public class WebTransition {

    private String identifier;
    private WebState origin;
    private WebState destination;
    private Collection<AbstractRequest> abstractRequests;

    public WebTransition() {
        identifier = UUID.randomUUID().toString();
        abstractRequests = new ArrayList<>();
    }

    public WebTransition(WebState origin, WebState destination) {
        this();
        this.origin = origin;
        this.destination = destination;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public WebState getOrigin() {
        return origin;
    }


    public void setOrigin(WebState origin) {
        this.origin = origin;
    }


    public WebState getDestination() {
        return destination;
    }


    public void setDestination(WebState destination) {
        this.destination = destination;
    }


    public void addRequest(AbstractRequest request) {
        abstractRequests.add(request);
    }


    public Collection<AbstractRequest> getAbstractRequests() {
        return abstractRequests;
    }


    public void setRequests(Collection<AbstractRequest> requests) {
        abstractRequests = requests;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        WebTransition that = (WebTransition) o;
        return Objects.equals(origin, that.getOrigin()) &&
                Objects.equals(destination, that.getDestination()) &&
                Objects.equals(abstractRequests, that.getAbstractRequests());
    }

    @Override
    public int hashCode() {
        return Objects.hash(origin, destination);
    }
}
