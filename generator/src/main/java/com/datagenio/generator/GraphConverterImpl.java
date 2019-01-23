package com.datagenio.generator;

import com.datagenio.crawler.api.Context;
import com.datagenio.crawler.api.EventFlowGraph;
import com.datagenio.crawler.api.State;
import com.datagenio.crawler.api.Transitionable;
import com.datagenio.generator.api.GraphConverter;
import com.datagenio.generator.converter.HttpRequestAbstractor;
import com.datagenio.generator.converter.StateConverter;
import com.datagenio.model.WebFlowGraphImpl;
import com.datagenio.model.WebTransitionImpl;
import com.datagenio.model.api.WebFlowGraph;
import com.datagenio.model.api.WebState;
import com.datagenio.model.api.WebTransition;
import com.datagenio.model.exception.InvalidTransitionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;

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
    public WebFlowGraph convert(EventFlowGraph eventFlowGraph) {
        WebFlowGraph webGraph = new WebFlowGraphImpl();

//        eventFlowGraph.getStates().forEach(state -> {
//            var converted = stateConverter.convert(state, eventFlowGraph.getOutgoingTransitions(state));
//            webGraph.addState(converted);
//
//            if (state.isRoot()) webGraph.setRoot(converted);
//        });

        eventFlowGraph.getTransitions().forEach(transition -> {
            if (transition.hasRemoteRequest(context.getRootUri())) {
                convertAndAdd(transition, webGraph, eventFlowGraph);
            }
        });

        return webGraph;
    }

    private void convertAndAdd(Transitionable transition, WebFlowGraph webGraph, EventFlowGraph eventFlowGraph) {
        try {
            WebState origin = convertAndAdd(transition.getOrigin(), webGraph, eventFlowGraph);
            WebState destination = convertAndAdd(transition.getDestination(), webGraph, eventFlowGraph);

            WebTransition webTransition = new WebTransitionImpl(origin, destination);
            transition.getFilteredRequests(context.getRootUri()).forEach(request -> {
                webTransition.addRequest(requestAbstractor.process(request));
            });

            webGraph.addTransition(webTransition);
        } catch (InvalidTransitionException e) {
            logger.info(e.getMessage(), e);
        }
    }

    private WebState convertAndAdd(State state, WebFlowGraph webGraph, EventFlowGraph eventFlowGraph) {
        WebState webState = convertWebState(state, eventFlowGraph.getOutgoingTransitions(state));

        webGraph.addState(webState);
        if (state.isRoot()) webGraph.setRoot(webState);

        return webState;
    }

    private WebState convertWebState(State state, Collection<Transitionable> transitions) {
        return stateConverter.convert(state, transitions);
    }
}
