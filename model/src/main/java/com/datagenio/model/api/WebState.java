package com.datagenio.model.api;

import com.datagenio.model.request.AbstractUrlImpl;

import java.util.Collection;

public interface WebState {

    AbstractUrlImpl getUrl();
    Collection<AbstractHTTPRequest> getRequests();

    void setUrl(AbstractUrlImpl url);
    void setRequests(Collection<AbstractHTTPRequest> requests);
    void addRequest(AbstractHTTPRequest request);
}
