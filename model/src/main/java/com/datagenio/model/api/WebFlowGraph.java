package com.datagenio.model.api;


import java.util.Collection;

public interface WebFlowGraph {

    Collection<WebState> getStates();
    Collection<WebTransition> getTransitions();

    void addState(WebState state);
    void addTransition(WebTransition transition);

}
