package com.datagenio.crawler.api;

import com.datagenio.crawler.exception.InvalidTransitionException;
import com.datagenio.crawler.exception.UncrawlableStateException;
import org.jgrapht.GraphPath;

import java.util.Collection;
import java.util.List;

public interface EventFlowGraph {

    Collection<State> getStates();
    Collection<Transitionable> getTransitions();
    Collection<Transitionable> getOutgoingTransitions(State state);
    Collection<Eventable> getEvents();
    State getRoot();
    State getCurrentState();
    State find(State state);
    State findById(String id);
    State findNearestUnfinishedStateFrom(State state) throws UncrawlableStateException;
    List<State> getStates(Eventable event);
    Eventable findEvent(String id);
    Eventable getEvent(Eventable e);
    GraphPath<State, Transitionable> findPath(State from, State to);
    Transitionable findTransitions(Eventable eventable) throws InvalidTransitionException;
    List<Transitionable> findTransitions(Eventable eventable, State state);
    boolean isNewState(State state);
    boolean isRegistered(Eventable eventable);
    int getGraphDiameter();

    void addState(State state);
    void addStateAsCurrent(State state) throws UncrawlableStateException;
    void addTransition(Transitionable transition);
    void addEvent(Eventable event);
    void addEvents(Collection<Eventable> events);
    void setCurrentState(State state) throws UncrawlableStateException;
    void setRoot(State state);
    void removeTransition(Transitionable transition);
}
