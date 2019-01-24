package com.datagenio.crawler.api;

import com.datagenio.crawler.exception.InvalidTransitionException;
import com.datagenio.crawler.exception.UncrawlableStateException;
import org.jgrapht.GraphPath;

import java.util.Collection;

public interface EventFlowGraph {

    Collection<State> getStates();
    Collection<Transitionable> getTransitions();
    Collection<Transitionable> getOutgoingTransitions(State state);
    Collection<Eventable> getEvents();
    State getRoot();
    State getCurrentState();
    State find(State state);
    State findNearestUnfinishedStateFrom(State state) throws UncrawlableStateException;
    GraphPath<State, Transitionable> findPath(State from, State to);
    Transitionable findTransition(Eventable eventable) throws InvalidTransitionException;
    boolean isNewState(State state);
    boolean isRegistered(Eventable eventable);
    int getGraphDiameter();

    void addState(State state);
    void addStateAsCurrent(State state) throws UncrawlableStateException;
    void addTransition(Transitionable transition);
    void addEvent(Eventable event);
    void setCurrentState(State state) throws UncrawlableStateException;
    void setRoot(State state);
}
