package com.datagenio.crawler.browser;

import com.datagenio.crawler.api.RemoteRequestBody;
import com.datagenio.crawler.api.RemoteRequestBodyPart;

import java.util.ArrayList;
import java.util.Collection;

public class RemoteHttpRequestBody implements RemoteRequestBody {
    private String mimeType;
    private Collection<RemoteRequestBodyPart> parts;

    public RemoteHttpRequestBody() {
        parts = new ArrayList<>();
    }

    @Override
    public String getMimeType() {
        return mimeType;
    }

    @Override
    public Collection<RemoteRequestBodyPart> getParts() {
        return parts;
    }

    @Override
    public void setMimeType(String type) {
        mimeType = type;
    }

    @Override
    public void setParts(Collection<RemoteRequestBodyPart> parts) {
        this.parts = parts;
    }

    @Override
    public void addPart(RemoteRequestBodyPart part) {
        parts.add(part);
    }
}
