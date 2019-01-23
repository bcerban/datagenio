package com.datagenio.model.api;


import com.datagenio.model.exception.InvalidTransitionException;

import java.util.Collection;

public interface WebFlowGraph {

    WebState getRoot();
    Collection<WebState> getStates();
    Collection<WebTransition> getTransitions();
    WebState find(WebState state);
    boolean isNew(WebState state);

    void setRoot(WebState root);
    void addState(WebState state);
    void addTransition(WebTransition transition) throws InvalidTransitionException;

}
