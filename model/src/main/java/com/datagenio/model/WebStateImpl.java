package com.datagenio.model;

import com.datagenio.model.api.AbstractHTTPRequest;
import com.datagenio.model.api.WebState;
import com.datagenio.model.request.AbstractUrlImpl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

public class WebStateImpl implements WebState {

    private StateContext context;
    private Collection<AbstractHTTPRequest> requests;

    public WebStateImpl(AbstractUrlImpl url) {
        this.context = new StateContext(url);
        this.requests = new ArrayList<>();
    }

    public StateContext getContext() {
        return context;
    }

    public void setContext(StateContext context) {
        this.context = context;
    }

    @Override
    public AbstractUrlImpl getUrl() {
        return this.context.getContextUrl();
    }

    @Override
    public void setUrl(AbstractUrlImpl url) {
        this.context.setContextUrl(url);
    }

    @Override
    public Collection<AbstractHTTPRequest> getRequests() {
        return requests;
    }

    @Override
    public void setRequests(Collection<AbstractHTTPRequest> requestSet) {
        this.requests = requestSet;
    }

    @Override
    public void addRequest(AbstractHTTPRequest request) {
        this.requests.add(request);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        WebStateImpl state = (WebStateImpl) o;
        return Objects.equals(context.getContextUrl(), state.context.getContextUrl()) &&
                Objects.equals(requests, state.requests);
    }

    @Override
    public int hashCode() {
        return Objects.hash(context, requests);
    }
}
