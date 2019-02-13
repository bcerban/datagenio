package com.datagenio.generator.converter;

import com.datagenio.crawler.api.RemoteRequest;
import com.datagenio.model.request.AbstractRequest;

import java.net.URI;

public class HttpRequestAbstractor {

    private UrlAbstractor urlAbstractor;
    private BodyConverter bodyConverter;

    public HttpRequestAbstractor(UrlAbstractor urlAbstractor, BodyConverter bodyConverter) {
        this.urlAbstractor = urlAbstractor;
        this.bodyConverter = bodyConverter;
    }

    public AbstractRequest process(RemoteRequest remoteRequest) {
        var remoteUri = URI.create(remoteRequest.getUrl());
        var request = new AbstractRequest(remoteRequest.getMethod(), urlAbstractor.process(remoteUri));
        request.setSortOrder(remoteRequest.getSortOrder());
        remoteRequest.getHeaders().forEach((name, value) -> request.addHeader(name, value));

        if (remoteRequest.hasBody()) {
            request.setBody(bodyConverter.process(remoteRequest));
        }

        return request;
    }
}
