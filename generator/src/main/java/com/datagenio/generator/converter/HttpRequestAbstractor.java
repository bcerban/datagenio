package com.datagenio.generator.converter;

import com.datagenio.context.EventInput;
import com.datagenio.crawler.api.Eventable;
import com.datagenio.crawler.api.RemoteRequest;
import com.datagenio.model.request.AbstractRequest;

import java.net.URI;
import java.util.List;

public class HttpRequestAbstractor {

    private UrlAbstractor urlAbstractor;
    private BodyConverter bodyConverter;

    public HttpRequestAbstractor(UrlAbstractor urlAbstractor, BodyConverter bodyConverter) {
        this.urlAbstractor = urlAbstractor;
        this.bodyConverter = bodyConverter;
    }

    public AbstractRequest process(RemoteRequest remoteRequest, Eventable event, List<EventInput> inputs) {
        var remoteUri = URI.create(remoteRequest.getUrl());
        var request = new AbstractRequest(remoteRequest.getMethod(), urlAbstractor.process(remoteUri, event, inputs));
        request.setSortOrder(remoteRequest.getSortOrder());
        remoteRequest.getHeaders().forEach((name, value) -> request.addHeader(name, value));

        if (remoteRequest.hasBody()) {
            request.setBody(bodyConverter.process(remoteRequest.getBody(), event, inputs));
        }

        return request;
    }
}
