package com.datagenio.model;

import com.datagenio.model.api.AbstractHttpRequest;
import com.datagenio.model.api.RequestAbstractor;
import com.datagenio.model.api.WebState;
import com.datagenio.model.api.WebTransition;
import com.datagenio.model.util.SimpleRequestAbstractor;
import org.apache.http.HttpRequest;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

public class WebTransitionImpl implements WebTransition {

    private RequestAbstractor abstractor;
    private WebState origin;
    private WebState destination;
    private Collection<HttpRequest> concreteRequests;
    private Collection<AbstractHttpRequest> abstractRequests;

    public WebTransitionImpl(WebState origin, WebState destination) {
        // TODO: inject dependency
        this.abstractor = new SimpleRequestAbstractor();

        this.origin = origin;
        this.destination = destination;
        this.concreteRequests = new ArrayList<>();
        this.abstractRequests = new ArrayList<>();
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
    public Collection<HttpRequest> getConcreteRequests() {
        return concreteRequests;
    }

    @Override
    public void setConcreteRequests(Collection<HttpRequest> requests) {
        this.concreteRequests = requests;
    }

    @Override
    public Collection<AbstractHttpRequest> getAbstractRequests() {
        return abstractRequests;
    }

    @Override
    public void addRequest(HttpRequest request) {
        this.concreteRequests.add(request);
        this.abstractRequests.add(this.abstractor.process(request));
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
