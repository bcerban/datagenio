package com.datagenio.model;

import com.datagenio.model.api.WebFlowGraph;
import com.datagenio.model.api.WebState;
import com.datagenio.model.api.WebTransition;
import com.datagenio.model.exception.InvalidTransitionException;
import org.jgrapht.graph.DirectedPseudograph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.NoSuchElementException;
import java.util.Optional;

public class WebFlowGraphImpl implements WebFlowGraph {

    private WebState root;
    private DirectedPseudograph<WebState, WebTransition> graph;
    private Collection<WebState> states;
    private Collection<WebTransition> transitions;

    public WebFlowGraphImpl() {
        graph = new DirectedPseudograph<>(WebTransition.class);
        this.states = new ArrayList<>();
        this.transitions = new ArrayList<>();
    }

    @Override
    public WebState getRoot() {
        return root;
    }

    @Override
    public void setRoot(WebState root) {
        this.root = root;
    }

    @Override
    public Collection<WebState> getStates() {
        return graph.vertexSet();
    }

    @Override
    public Collection<WebTransition> getTransitions() {
        return graph.edgeSet();
    }

    @Override
    public WebState findStateBy(WebState state) {
        return getStates().stream().filter(s -> s.equals(state)).findFirst().get();
    }

    @Override
    public WebState findStateBy(String externalId) {
        return getStates().stream().filter(s -> s.getExternalIds().contains(externalId)).findFirst().get();
    }

    @Override
    public boolean isNew(WebState state) {
        return !graph.containsVertex(state);
    }

    @Override
    public void addState(WebState state) {
        graph.addVertex(state);
    }

    @Override
    public void addTransition(WebTransition transition) throws InvalidTransitionException {
        try {
            graph.addEdge(transition.getOrigin(), transition.getDestination(), transition);
        } catch (IllegalArgumentException e) {
            throw new InvalidTransitionException(e.getMessage());
        }
    }


}
