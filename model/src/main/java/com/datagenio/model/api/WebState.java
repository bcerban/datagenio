package com.datagenio.model.api;

import java.util.Collection;

public interface WebState {

    AbstractUrl getUrl();
    Collection<AbstractHttpRequest> getRequests();

    void setUrl(AbstractUrl url);
    void setRequests(Collection<AbstractHttpRequest> requests);
    void addRequest(AbstractHttpRequest request);
}
