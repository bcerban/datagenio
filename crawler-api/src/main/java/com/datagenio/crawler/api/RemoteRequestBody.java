package com.datagenio.crawler.api;

import java.util.Collection;

public interface RemoteRequestBody {

    String getMimeType();
    Collection<RemoteRequestBodyPart> getParts();

    void setMimeType(String type);
    void setParts(Collection<RemoteRequestBodyPart> parts);
    void addPart(RemoteRequestBodyPart part);
}
