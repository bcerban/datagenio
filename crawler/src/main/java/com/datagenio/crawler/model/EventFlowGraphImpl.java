package com.datagenio.crawler.model;

import com.datagenio.crawler.StateTraversalManager;
import com.datagenio.crawler.api.EventFlowGraph;
import com.datagenio.crawler.api.Eventable;
import com.datagenio.crawler.api.State;
import com.datagenio.crawler.api.Transitionable;
import com.datagenio.crawler.exception.InvalidTransitionException;
import com.datagenio.crawler.exception.UncrawlableStateException;
import org.jgrapht.GraphMetrics;
import org.jgrapht.GraphPath;
import org.jgrapht.graph.DirectedPseudograph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
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
    public State findById(String id) {
        return getStates().stream().filter(s -> s.getIdentifier().equals(id)).findFirst().get();
    }

    @Override
    public Eventable findEvent(String id) {
        return getEvents().stream().filter(e -> e.getId().equals(id)).findFirst().get();
    }

    @Override
    public State findNearestUnfinishedStateFrom(State state) throws UncrawlableStateException {
        if (state == null || !getStates().contains(state)) {
            throw new UncrawlableStateException("Trying to navigate from nonexistent state.");
        }

        return StateTraversalManager.findNearestUnfinishedState(graph, state);
    }

    @Override
    public List<State> getStates(Eventable event) {
        return getStates().stream().filter(s -> s.getEventables().contains(event)).collect(Collectors.toList());
    }

    @Override
    public GraphPath<State, Transitionable> findPath(State from, State to) {
        return StateTraversalManager.findPath(graph, from, to);
    }

    @Override
    public Transitionable findTransitions(Eventable eventable) throws InvalidTransitionException {
        List<Transitionable> transitions = getTransitions().stream()
                .filter(transition -> transition.getExecutedEvent().getEvent().equals(eventable))
                .collect(Collectors.toList());

        if (transitions.size() != 1) {
            throw new InvalidTransitionException("Too many or too few transitions found for event.");
        }

        return transitions.get(0);
    }

    @Override
    public List<Transitionable> findTransitions(Eventable eventable, State origin) {
        return getTransitions().stream()
                .filter(transition -> transition.getExecutedEvent().getEvent().equals(eventable) && transition.getOrigin().equals(origin))
                .collect(Collectors.toList());
    }

    @Override
    public boolean isNewState(State state) {
        return !getStates().contains(state);
    }

    @Override
    public boolean isRegistered(Eventable eventable) {
        return getTransitions().stream()
                .filter(transition -> transition.getExecutedEvent().getEvent().equals(eventable))
                .count() > 0;
    }

    @Override
    public int getGraphDiameter() {
        return (int) GraphMetrics.getDiameter(graph);
    }

    @Override
    public void addState(State state) {
        logger.debug("Adding state {} to graph.", state.getIdentifier());
        graph.addVertex(state);
        processNewStateEvents(state);
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
        if (!events.contains(event)) events.add(event);
    }

    @Override
    public void addEvents(Collection<Eventable> events) {
        this.events.addAll(events);
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

    @Override
    public void removeTransition(Transitionable transition) {
        graph.removeEdge(transition);

        if (getTransitions()
                .stream()
                .filter(t -> t.getExecutedEvent().getEvent().equals(transition.getExecutedEvent().getEvent()))
                .count() == 0
        ) {
            events.remove(transition.getExecutedEvent().getEvent());
        }
    }

    private void processNewStateEvents(State state) {
        var stateEvents = new ArrayList<Eventable>();
        state.getEventables().forEach(e -> {
            if (events.contains(e)) {
                stateEvents.add(getEvent(e));
            } else {
                stateEvents.add(e);
                addEvent(e);
            }
        });
        state.setEventables(stateEvents);
        state.setUnfiredEventables(stateEvents);
    }

    private Eventable getEvent(Eventable e) {
        return events.stream().filter(event -> event.equals(e)).findFirst().get();
    }
}
