package com.datagenio.crawler.api;

import java.util.Collection;

public interface EventFlowGraph {

    Collection<State> getStates();
    Collection<Transitionable> getTransitions();
    Collection<Eventable> getEvents();

    void addState(State state);
    void addTransition(Transitionable transition);
    void addEvent(Eventable event);
}
