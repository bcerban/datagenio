package com.datagenio.crawler.api;

import java.util.Collection;

public interface EventFlowGraph {

    Collection<State> getStates();
    Collection<Transitionable> getTransitions();
    Collection<Eventable> getEvents();
    State getCurrentState();

    void addState(State state);
    void addStateAsCurrent(State state);
    void addTransition(Transitionable transition);
    void addEvent(Eventable event);
    void setCurrentState(State state);
}
