package com.datagenio.model;

import com.datagenio.model.request.AbstractRequest;
import com.datagenio.model.request.AbstractUrl;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.UUID;

public class WebState {

    private String identifier;
    private AbstractUrl url;
    private Collection<AbstractRequest> requests;
    private Collection<String> externalIds;
    private Collection<File> screenShots;
    private boolean isRoot = false;

    public WebState() {
        identifier = UUID.randomUUID().toString();
        requests = new ArrayList<>();
        externalIds = new ArrayList<>();
        screenShots = new ArrayList<>();
    }

    public WebState(AbstractUrl url) {
        this();
        this.url = url;
    }


    public String getIdentifier() {
        return identifier;
    }


    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }


    public AbstractUrl getUrl() {
        return url;
    }


    public void setUrl(AbstractUrl url) {
        this.url = url;
    }


    public Collection<AbstractRequest> getRequests() {
        return requests;
    }


    public Collection<String> getExternalIds() {
        return externalIds;
    }


    public void setExternalIds(Collection<String> ids) {
        externalIds = ids;
    }


    public void setRequests(Collection<AbstractRequest> requestSet) {
        this.requests = requestSet;
    }


    public void addRequest(AbstractRequest request) {
        requests.add(request);
    }


    public void addExternalId(String id) {
        if (!externalIds.contains(id)) externalIds.add(id);
    }


    public boolean isRoot() {
        return isRoot;
    }


    public void setIsRoot(boolean isRoot) {
        this.isRoot = isRoot;
    }


    public Collection<File> getScreenShots() {
        return screenShots;
    }


    public void setScreenShots(Collection<File> screenShots) {
        this.screenShots = screenShots;
    }


    public void addScreenShot(File screenShot) {
        if (!screenShots.contains(screenShot)) screenShots.add(screenShot);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        WebState state = (WebState) o;
        return Objects.equals(url, state.getUrl()) &&
                Objects.equals(requests, state.requests);
    }

    @Override
    public int hashCode() {
        return Objects.hash(url, requests);
    }
}
