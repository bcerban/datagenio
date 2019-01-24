package com.datagenio.model.api;

import java.io.File;
import java.util.Collection;

public interface WebState {

    String getIdentifier();
    AbstractUrl getUrl();
    Collection<AbstractHttpRequest> getRequests();
    Collection<String> getExternalIds();
    Collection<File> getScreenShots();
    boolean isRoot();

    void setUrl(AbstractUrl url);
    void setRequests(Collection<AbstractHttpRequest> requests);
    void addRequest(AbstractHttpRequest request);
    void addExternalId(String id);
    void setIsRoot(boolean isRoot);
    void addScreenShot(File screenShot);
}
