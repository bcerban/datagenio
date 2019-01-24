package com.datagenio.model;

import com.datagenio.model.api.AbstractHttpRequest;
import com.datagenio.model.api.AbstractUrl;
import com.datagenio.model.api.WebState;
import com.datagenio.model.request.AbstractUrlImpl;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.UUID;

public class WebStateImpl implements WebState {

    private final String identifier;
    private AbstractUrl url;
    private Collection<AbstractHttpRequest> requests;
    private Collection<String> externalIds;
    private Collection<File> screenShots;
    private boolean isRoot = false;

    public WebStateImpl() {
        identifier = UUID.randomUUID().toString();
        requests = new ArrayList<>();
        externalIds = new ArrayList<>();
        screenShots = new ArrayList<>();
    }

    public WebStateImpl(AbstractUrl url) {
        this();
        this.url = url;
    }

    @Override
    public String getIdentifier() {
        return identifier;
    }

    @Override
    public AbstractUrl getUrl() {
        return url;
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
    public Collection<String> getExternalIds() {
        return externalIds;
    }

    @Override
    public void setRequests(Collection<AbstractHttpRequest> requestSet) {
        this.requests = requestSet;
    }

    @Override
    public void addRequest(AbstractHttpRequest request) {
        requests.add(request);
    }

    @Override
    public void addExternalId(String id) {
        externalIds.add(id);
    }

    @Override
    public boolean isRoot() {
        return isRoot;
    }

    @Override
    public void setIsRoot(boolean isRoot) {
        this.isRoot = isRoot;
    }

    @Override
    public Collection<File> getScreenShots() {
        return screenShots;
    }

    @Override
    public void addScreenShot(File screenShot) {
        screenShots.add(screenShot);
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
