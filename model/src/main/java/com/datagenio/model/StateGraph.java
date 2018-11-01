package com.datagenio.model;

import java.util.ArrayList;
import java.util.Collection;

public class StateGraph {

    private Collection<State> states;
    private Collection<Transition> transitions;

    public StateGraph() {
        this.states = new ArrayList<>();
        this.transitions = new ArrayList<>();
    }

    public Collection<State> getStates() {
        return states;
    }

    public void setStates(Collection<State> states) {
        this.states = states;
    }

    public Collection<Transition> getTransitions() {
        return transitions;
    }

    public void setTransitions(Collection<Transition> transitions) {
        this.transitions = transitions;
    }

    public int getStateCount() {
        return this.states.size();
    }

    public void addState(State state) {
        if (!this.states.contains(state)) {
            this.states.add(state);
        }

        //TODO: should merge states otherwise?
    }

    public void addTransition(Transition transition) {
        if (canAddTransition(transition)) {
            this.transitions.add(transition);
        }
    }

    private boolean canAddTransition(Transition transition) {
        boolean canAdd = true;

        if (this.transitions.contains(transition)) {
            canAdd = false;
        } else if (!this.states.contains(transition.getOrigin()) || !this.states.contains(transition.getDestination())) {
            canAdd = false;
        }

        return canAdd;
    }
}
