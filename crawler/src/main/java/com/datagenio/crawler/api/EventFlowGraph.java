package com.datagenio.crawler.api;

import com.datagenio.crawler.exception.UncrawlableStateException;
import org.jgrapht.GraphPath;

import java.util.Collection;

public interface EventFlowGraph {

    Collection<State> getStates();
    Collection<Transitionable> getTransitions();
    Collection<Eventable> getEvents();
    State getRoot();
    State getCurrentState();
    State findNearestUnfinishedStateFrom(State state) throws UncrawlableStateException;
    GraphPath<State, Transitionable> findPath(State from, State to);
    boolean isNewState(State state);

    void addState(State state);
    void addStateAsCurrent(State state);
    void addTransition(Transitionable transition);
    void addEvent(Eventable event);
    void setCurrentState(State state);
    void setRoot(State state);
}
