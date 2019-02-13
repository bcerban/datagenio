package com.datagenio.generator.converter;

import com.datagenio.crawler.api.State;
import com.datagenio.crawler.api.Transitionable;
import com.datagenio.model.WebState;
import com.datagenio.model.request.AbstractRequest;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

public class StateConverter {

    private UrlAbstractor urlAbstractor;
    private HttpRequestAbstractor requestAbstractor;
    private URI rootUri;

    public StateConverter(UrlAbstractor urlAbstractor, HttpRequestAbstractor requestAbstractor, URI rootUri) {
        this.urlAbstractor = urlAbstractor;
        this.requestAbstractor = requestAbstractor;
        this.rootUri = rootUri;
    }

    public WebState convert(State eventState, Collection<Transitionable> outgoingTransitions) {
        var webState = new WebState();
        webState.addExternalId(eventState.getIdentifier());
        webState.setIsRoot(eventState.isRoot());
        webState.setUrl(urlAbstractor.process(eventState.getUri()));
        webState.setRequests(convertRequests(outgoingTransitions));

        if (eventState.hasScreenShot()) {
            webState.addScreenShot(eventState.getScreenShot());
        }

        return webState;
    }

    private Collection<AbstractRequest> convertRequests(Collection<Transitionable> transitions) {
        var requests = new ArrayList<AbstractRequest>();
        transitions.forEach(t -> {
            requests.addAll(convertRequestsFromTransition(t));
        });
        return requests;
    }

    private Collection<AbstractRequest> convertRequestsFromTransition(Transitionable transition) {
        return transition.getFilteredRequests(rootUri)
                .stream().map(r -> requestAbstractor.process(r))
                .collect(Collectors.toList());
    }
}
