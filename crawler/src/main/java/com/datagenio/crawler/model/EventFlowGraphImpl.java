package com.datagenio.crawler.model;

import com.datagenio.crawler.StateTraversalManager;
import com.datagenio.crawler.api.EventFlowGraph;
import com.datagenio.crawler.api.Eventable;
import com.datagenio.crawler.api.State;
import com.datagenio.crawler.api.Transitionable;
import com.datagenio.crawler.exception.InvalidTransitionException;
import com.datagenio.crawler.exception.UncrawlableStateException;
import org.jgrapht.GraphPath;
import org.jgrapht.graph.DirectedPseudograph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class EventFlowGraphImpl implements EventFlowGraph {

    private static Logger logger = LoggerFactory.getLogger(EventFlowGraphImpl.class);

    private DirectedPseudograph<State, Transitionable> graph;
    private Collection<Eventable> events;
    private State root;
    private State current;

    public EventFlowGraphImpl() {
        graph = new DirectedPseudograph<>(Transitionable.class);
        events = new ArrayList<>();
    }

    @Override
    public Collection<State> getStates() {
        return graph.vertexSet();
    }

    @Override
    public Collection<Transitionable> getTransitions() {
        return graph.edgeSet();
    }

    @Override
    public Collection<Transitionable> getOutgoingTransitions(State state) {
        return graph.outgoingEdgesOf(state);
    }

    @Override
    public Collection<Eventable> getEvents() {
        return events;
    }

    @Override
    public State getRoot() {
        return root;
    }

    @Override
    public State getCurrentState() {
        return current;
    }

    @Override
    public State find(State state) {
        return getStates().stream().filter(s -> s.equals(state)).findFirst().get();
    }

    @Override
    public State findNearestUnfinishedStateFrom(State state) throws UncrawlableStateException {
        if (state == null || !getStates().contains(state)) {
            throw new UncrawlableStateException("Trying to navigate from nonexistent state.");
        }

        return StateTraversalManager.findNearestUnfinishedState(graph, state);
    }

    @Override
    public GraphPath<State, Transitionable> findPath(State from, State to) {
        return StateTraversalManager.findPath(graph, from, to);
    }

    @Override
    public Transitionable findTransition(Eventable eventable) throws InvalidTransitionException {
        List<Transitionable> transitions = getTransitions().stream()
                .filter(transition -> transition.getExecutedEvent().getEvent().equals(eventable))
                .collect(Collectors.toList());

        if (transitions.size() != 1) {
            throw new InvalidTransitionException("Too many or too few transitions found for event.");
        }

        return transitions.get(0);
    }

    @Override
    public boolean isNewState(State state) {
        return !getStates().contains(state);
    }

    @Override
    public boolean isRegistered(Eventable eventable) {
        return getEvents().contains(eventable);
    }

    @Override
    public void addState(State state) {
        graph.addVertex(state);
        logger.debug("Adding state {} to graph.", state.getIdentifier());
    }

    @Override
    public void addStateAsCurrent(State state) throws UncrawlableStateException {
        addState(state);
        setCurrentState(state);
    }

    @Override
    public void addTransition(Transitionable transition) {
        graph.addEdge(transition.getOrigin(), transition.getDestination(), transition);
        addEvent(transition.getExecutedEvent().getEvent());
    }

    @Override
    public void addEvent(Eventable event) {
        events.add(event);
    }

    @Override
    public void setCurrentState(State state) throws UncrawlableStateException {
        if (!graph.containsVertex(state)) {
            throw new UncrawlableStateException("Selected state does not belong to graph!");
        }
        current = state;
    }

    @Override
    public void setRoot(State state) {
        state.setIsRoot(true);
        root = state;
    }
}
