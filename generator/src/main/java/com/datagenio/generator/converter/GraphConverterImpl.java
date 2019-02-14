package com.datagenio.generator.converter;

import com.datagenio.context.Context;
import com.datagenio.crawler.api.EventFlowGraph;
import com.datagenio.crawler.api.State;
import com.datagenio.crawler.api.Transitionable;
import com.datagenio.generator.api.GraphConverter;
import com.datagenio.model.WebFlowGraph;
import com.datagenio.model.WebState;
import com.datagenio.model.WebTransition;
import com.datagenio.model.exception.InvalidTransitionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.NoSuchElementException;

public class GraphConverterImpl implements GraphConverter {

    private static Logger logger = LoggerFactory.getLogger(GraphConverterImpl.class);

    private Context context;
    private StateConverter stateConverter;
    private HttpRequestAbstractor requestAbstractor;

    public GraphConverterImpl(Context context, StateConverter stateConverter, HttpRequestAbstractor requestAbstractor) {
        this.context = context;
        this.stateConverter = stateConverter;
        this.requestAbstractor = requestAbstractor;
    }

    @Override
    public WebFlowGraph convert(EventFlowGraph eventFlowGraph, WebFlowGraph webFlowGraph) {
        eventFlowGraph.getStates().forEach(state -> {
            convertAndAdd(state, webFlowGraph, eventFlowGraph);
        });

        eventFlowGraph.getTransitions().forEach(transition -> {
            if (transition.hasRemoteRequest(context.getRootUri())) {
                convertAndAdd(transition, webFlowGraph);
            }
        });

        return webFlowGraph;
    }

    private void convertAndAdd(Transitionable transition, WebFlowGraph webGraph) {
        try {
            WebState origin = webGraph.findStateBy(transition.getOrigin().getIdentifier());
            WebState destination = webGraph.findStateBy(transition.getDestination().getIdentifier());

            WebTransition webTransition = new WebTransition(origin, destination);
            transition.getFilteredRequests(context.getRootUri()).forEach(
                    request -> webTransition.addRequest(requestAbstractor.process(request))
            );

            webGraph.addTransition(webTransition);
        } catch (InvalidTransitionException| NoSuchElementException e) {
            logger.info(e.getMessage(), e);
        }
    }

    private WebState convertAndAdd(State state, WebFlowGraph webGraph, EventFlowGraph eventFlowGraph) {
        WebState webState;
        try {
            webState = webGraph.findStateBy(state.getIdentifier());
            webState.addExternalId(state.getIdentifier());

            if (state.hasScreenShot()) {
                webState.addScreenShot(state.getScreenShot());
            }
        } catch (NoSuchElementException e) {
            webState = stateConverter.convert(state, eventFlowGraph.getOutgoingTransitions(state));

            if (webGraph.containsState(webState)) {
                try {
                    var same = webGraph.findStateBy(webState);
                    same.addExternalId(state.getIdentifier());
                    if (state.hasScreenShot()) same.addScreenShot(state.getScreenShot());
                } catch (NoSuchElementException n) {
                    logger.info("Failed to convert state {}. Looks like a duplicate but can't be found.", state.getIdentifier(), n);
                }
            } else {
                webGraph.addState(webState);
            }
        }

        if (state.isRoot()) webGraph.setRoot(webState);
        return webState;
    }
}
