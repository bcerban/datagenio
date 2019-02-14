package com.datagenio.model;

import com.datagenio.model.exception.InvalidTransitionException;
import org.jgrapht.graph.DirectedPseudograph;
import java.util.Collection;

public class WebFlowGraph {

    private WebState root;
    private DirectedPseudograph<WebState, WebTransition> graph;

    public WebFlowGraph() {
        graph = new DirectedPseudograph<>(WebTransition.class);
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

    public boolean containsState(WebState state) {
        return graph.containsVertex(state);
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
            if (!graph.containsEdge(transition)) {
                graph.addEdge(transition.getOrigin(), transition.getDestination(), transition);
            }
        } catch (IllegalArgumentException e) {
            throw new InvalidTransitionException(e.getMessage());
        }
    }
}
