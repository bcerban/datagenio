package com.datagenio.generator.converter;

import com.datagenio.crawler.api.RemoteRequest;
import com.datagenio.model.api.AbstractHttpRequest;
import com.datagenio.model.request.AbstractRequest;

import java.net.URI;

public class HttpRequestAbstractor {

    private UrlAbstractor urlAbstractor;

    public HttpRequestAbstractor(UrlAbstractor urlAbstractor) {
        this.urlAbstractor = urlAbstractor;
    }

    public AbstractHttpRequest process(RemoteRequest remoteRequest) {
        var remoteUri = URI.create(remoteRequest.getUrl());
        AbstractHttpRequest request = new AbstractRequest(remoteRequest.getMethod(), urlAbstractor.process(remoteUri));

        remoteRequest.getHeaders().forEach((name, value) -> request.addHeader(name, value));

        // TODO: add abstracted body
        return request;
    }
}
