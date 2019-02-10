package com.datagenio.model;

import com.datagenio.model.api.AbstractHttpRequest;
import com.datagenio.model.api.RequestAbstractor;
import com.datagenio.model.api.WebState;
import com.datagenio.model.api.WebTransition;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

public class WebTransitionImpl implements WebTransition {

    private WebState origin;
    private WebState destination;
    private Collection<AbstractHttpRequest> abstractRequests;

    public WebTransitionImpl() {
        abstractRequests = new ArrayList<>();
    }

    public WebTransitionImpl(WebState origin, WebState destination) {
        this();
        this.origin = origin;
        this.destination = destination;
    }

    @Override
    public WebState getOrigin() {
        return origin;
    }

    @Override
    public void setOrigin(WebState origin) {
        this.origin = origin;
    }

    @Override
    public WebState getDestination() {
        return destination;
    }

    @Override
    public void setDestination(WebState destination) {
        this.destination = destination;
    }

    @Override
    public void addRequest(AbstractHttpRequest request) {
        abstractRequests.add(request);
    }

    @Override
    public Collection<AbstractHttpRequest> getAbstractRequests() {
        return abstractRequests;
    }

    @Override
    public void setRequests(Collection<AbstractHttpRequest> requests) {
        abstractRequests = requests;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        WebTransitionImpl that = (WebTransitionImpl) o;
        return Objects.equals(origin, that.getOrigin()) &&
                Objects.equals(destination, that.getDestination()) &&
                Objects.equals(abstractRequests, that.getAbstractRequests());
    }

    @Override
    public int hashCode() {
        return Objects.hash(origin, destination);
    }
}
