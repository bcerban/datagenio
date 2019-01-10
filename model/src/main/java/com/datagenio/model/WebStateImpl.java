package com.datagenio.model;

import com.datagenio.model.api.AbstractHttpRequest;
import com.datagenio.model.api.AbstractUrl;
import com.datagenio.model.api.WebState;
import com.datagenio.model.request.AbstractUrlImpl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

public class WebStateImpl implements WebState {

    private AbstractUrl url;
    private Collection<AbstractHttpRequest> requests;

    public WebStateImpl() {
        this.requests = new ArrayList<>();
    }

    public WebStateImpl(AbstractUrl url) {
        this.url = url;
        this.requests = new ArrayList<>();
    }

    @Override
    public AbstractUrl getUrl() {
        return this.url;
    }

    @Override
    public void setUrl(AbstractUrl url) {
        this.url = url;
    }

    @Override
    public Collection<AbstractHttpRequest> getRequests() {
        return requests;
    }

    @Override
    public void setRequests(Collection<AbstractHttpRequest> requestSet) {
        this.requests = requestSet;
    }

    @Override
    public void addRequest(AbstractHttpRequest request) {
        this.requests.add(request);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        WebStateImpl state = (WebStateImpl) o;
        return Objects.equals(url, state.getUrl()) &&
                Objects.equals(requests, state.requests);
    }

    @Override
    public int hashCode() {
        return Objects.hash(url, requests);
    }
}
