package com.datagenio.model.api;

import java.util.Collection;

public interface WebTransition {

    WebState getOrigin();
    WebState getDestination();
    Collection<AbstractHttpRequest> getAbstractRequests();

    void setOrigin(WebState origin);
    void setDestination(WebState destination);
    void addRequest(AbstractHttpRequest request);
}
