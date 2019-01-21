package com.datagenio.model.api;


import java.util.Collection;

public interface WebFlowGraph {

    WebState getRoot();
    Collection<WebState> getStates();
    Collection<WebTransition> getTransitions();

    void setRoot(WebState root);
    void addState(WebState state);
    void addTransition(WebTransition transition);

}
