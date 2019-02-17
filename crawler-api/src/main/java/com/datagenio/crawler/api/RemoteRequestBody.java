package com.datagenio.crawler.api;

import java.util.Collection;

public interface RemoteRequestBody {

    String getMimeType();
    String getBoundary();
    Collection<RemoteRequestBodyPart> getParts();

    void setMimeType(String type);
    void setBoundary(String boundary);
    void setParts(Collection<RemoteRequestBodyPart> parts);
    void addPart(RemoteRequestBodyPart part);
}
