package com.datagenio.model;

import com.datagenio.model.exception.InvalidTransitionException;
import org.jgrapht.graph.DirectedPseudograph;

import java.util.ArrayList;
import java.util.Collection;

public class WebFlowGraph {

    private WebState root;
    private DirectedPseudograph<WebState, WebTransition> graph;
    private Collection<WebState> states;
    private Collection<WebTransition> transitions;

    public WebFlowGraph() {
        graph = new DirectedPseudograph<>(WebTransition.class);
        this.states = new ArrayList<>();
        this.transitions = new ArrayList<>();
    }

    public WebState getRoot() {
        return root;
    }


    public void setRoot(WebState root) {
        this.root = root;
    }


    public Collection<WebState> getStates() {
        return graph.vertexSet();
    }


    public Collection<WebTransition> getTransitions() {
        return graph.edgeSet();
    }


    public WebState findStateBy(WebState state) {
        return getStates().stream().filter(s -> s.equals(state)).findFirst().get();
    }


    public WebState findStateBy(String externalId) {
        return getStates().stream().filter(s -> s.getExternalIds().contains(externalId)).findFirst().get();
    }


    public WebState findStateById(String id) {
        return getStates().stream().filter(s -> s.getIdentifier().equals(id)).findFirst().get();
    }


    public boolean isNew(WebState state) {
        return !graph.containsVertex(state);
    }


    public void addState(WebState state) {
        graph.addVertex(state);
    }


    public void addTransition(WebTransition transition) throws InvalidTransitionException {
        try {
            graph.addEdge(transition.getOrigin(), transition.getDestination(), transition);
        } catch (IllegalArgumentException e) {
            throw new InvalidTransitionException(e.getMessage());
        }
    }


}
