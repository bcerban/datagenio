package com.datagenio.generator.converter;

import com.datagenio.crawler.api.State;
import com.datagenio.crawler.api.Transitionable;
import com.datagenio.model.WebStateImpl;
import com.datagenio.model.api.AbstractHttpRequest;
import com.datagenio.model.api.WebState;

import java.util.ArrayList;
import java.util.Collection;

public class StateConverter {

    private UrlAbstractor urlAbstractor;
    private HttpRequestAbstractor requestAbstractor;

    public StateConverter(UrlAbstractor urlAbstractor, HttpRequestAbstractor requestAbstractor) {
        this.urlAbstractor = urlAbstractor;
        this.requestAbstractor = requestAbstractor;
    }

    public WebState convert(State eventState, Collection<Transitionable> outgoingTransitions) {
        WebState webState = new WebStateImpl();
        webState.setUrl(urlAbstractor.process(eventState.getUri()));
        webState.setRequests(convertRequests(outgoingTransitions));

        return webState;
    }

    private Collection<AbstractHttpRequest> convertRequests(Collection<Transitionable> transitions) {
        return new ArrayList<>();
    }
}
