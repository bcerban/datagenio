package com.datagenio.crawler.model;

import com.datagenio.crawler.api.EventFlowGraph;
import com.datagenio.crawler.api.Eventable;
import com.datagenio.crawler.api.State;
import com.datagenio.crawler.api.Transitionable;
import org.jgrapht.graph.DirectedPseudograph;

import java.util.ArrayList;
import java.util.Collection;

public class EventFlowGraphImpl implements EventFlowGraph {

    private static EventFlowGraphImpl instance;
    private DirectedPseudograph<State, Transitionable> graph;
    private Collection<Eventable> events;

    public static EventFlowGraph getInstance() {
        if (instance == null) {
            instance = new EventFlowGraphImpl();
        }

        return instance;
    }

    private EventFlowGraphImpl() {
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
    public void addState(State state) {
        this.graph.addVertex(state);
    }

    @Override
    public void addTransition(Transitionable transition) {
        this.graph.addEdge(transition.getOrigin(), transition.getDestination(), transition);
    }

    @Override
    public void addEvent(Eventable event) {
        this.events.add(event);
    }
}
