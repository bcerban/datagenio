package com.datagenio.crawler.model;

import com.datagenio.crawler.StateTraversalManager;
import com.datagenio.crawler.api.EventFlowGraph;
import com.datagenio.crawler.api.Eventable;
import com.datagenio.crawler.api.State;
import com.datagenio.crawler.api.Transitionable;
import com.datagenio.crawler.exception.UncrawlableStateException;
import org.jgrapht.GraphPath;
import org.jgrapht.graph.DirectedPseudograph;
import org.openqa.selenium.InvalidArgumentException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;

public class EventFlowGraphImpl implements EventFlowGraph {

    private static Logger logger = LoggerFactory.getLogger(EventFlowGraphImpl.class);

    private DirectedPseudograph<State, Transitionable> graph;
    private Collection<Eventable> events;
    private State root;
    private State current;

    public EventFlowGraphImpl() {
        this.graph = new DirectedPseudograph<>(Transitionable.class);
        this.events = new ArrayList<>();
    }

    @Override
    public Collection<State> getStates() {
        return this.graph.vertexSet();
    }

    @Override
    public Collection<Transitionable> getTransitions() {
        return this.graph.edgeSet();
    }

    @Override
    public Collection<Eventable> getEvents() {
        return this.events;
    }

    @Override
    public State getRoot() {
        return root;
    }

    @Override
    public State getCurrentState() {
        return this.current;
    }

    @Override
    public State findNearestUnfinishedStateFrom(State state) throws UncrawlableStateException {
        if (state == null || !this.getStates().contains(state)) {
            throw new UncrawlableStateException("Trying to navigate from nonexistent state.");
        }

        return StateTraversalManager.findNearestUnfinishedState(this.graph, state);
    }

    @Override
    public GraphPath<State, Transitionable> findPath(State from, State to) {
        return StateTraversalManager.findPath(this.graph, from, to);
    }

    @Override
    public boolean isNewState(State state) {
        return !this.getStates().contains(state);
    }

    @Override
    public void addState(State state) {
        this.graph.addVertex(state);
        logger.debug("Adding state {} to graph.", state.getIdentifier());
    }

    @Override
    public void addStateAsCurrent(State state) {
        this.addState(state);
        this.setCurrentState(state);
    }

    @Override
    public void addTransition(Transitionable transition) {
        this.graph.addEdge(transition.getOrigin(), transition.getDestination(), transition);
        this.addEvent(transition.getExecutedEvent().getEvent());
    }

    @Override
    public void addEvent(Eventable event) {
        this.events.add(event);
    }

    @Override
    public void setCurrentState(State state) {
        if (!this.graph.containsVertex(state)) {
            throw new InvalidArgumentException("Selected state does not belong to graph!");
        }
        this.current = state;
    }

    @Override
    public void setRoot(State state) {
        this.root = state;
    }
}
