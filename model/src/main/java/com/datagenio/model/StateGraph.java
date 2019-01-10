package com.datagenio.model;

import com.datagenio.model.api.WebState;
import com.datagenio.model.api.WebTransition;

import java.util.ArrayList;
import java.util.Collection;

public class StateGraph {

    private Collection<WebState> states;
    private Collection<WebTransition> transitions;

    public StateGraph() {
        this.states = new ArrayList<>();
        this.transitions = new ArrayList<>();
    }

    public Collection<WebState> getStates() {
        return states;
    }

    public void setStates(Collection<WebState> states) {
        this.states = states;
    }

    public Collection<WebTransition> getTransitions() {
        return transitions;
    }

    public void setTransitions(Collection<WebTransition> transitions) {
        this.transitions = transitions;
    }

    public int getStateCount() {
        return this.states.size();
    }

    public void addState(WebState state) {
        if (!this.states.contains(state)) {
            this.states.add(state);
        }

        //TODO: should merge states otherwise?
    }

    public void addTransition(WebTransition transition) {
        if (canAddTransition(transition)) {
            this.transitions.add(transition);
        }
    }

    private boolean canAddTransition(WebTransition transition) {
        boolean canAdd = true;

        if (this.transitions.contains(transition)) {
            canAdd = false;
        } else if (!this.states.contains(transition.getOrigin()) || !this.states.contains(transition.getDestination())) {
            canAdd = false;
        }

        return canAdd;
    }
}
