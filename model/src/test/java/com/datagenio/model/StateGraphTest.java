package com.datagenio.model;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

public class StateGraphTest {

    private StateGraph graph;

    @Before
    public void setUp() {
        this.graph = new StateGraph();
    }

    @Test
    public void testGetStates() {
        assertNotNull(this.graph.getStates());
    }

    @Test
    public void testSetStates() {
        var states = new ArrayList<State>();
        states.add(new State(new AbstractUrl("state_url")));
        this.graph.setStates(states);

        assertEquals(states, this.graph.getStates());
    }

    @Test
    public void testGetTransitions() {
        assertNotNull(this.graph.getTransitions());
    }

    @Test
    public void testSetTransitions() {
        var transitions = new ArrayList<Transition>();

        var origin = new State(new AbstractUrl("origin"));
        var destination = new State(new AbstractUrl("destination"));
        var request = new AbstractRequest("GET", destination.getContext().getContextUrl());

        transitions.add(new Transition(origin, destination, request));
        this.graph.setTransitions(transitions);

        assertEquals(transitions, this.graph.getTransitions());
    }

    @Test
    public void testAddStateNew() {
        var state = new State(new AbstractUrl("added_state_url"));
        this.graph.addState(state);

        assertEquals(1, this.graph.getStateCount());
        assertTrue(this.graph.getStates().contains(state));
    }

    @Test
    public void testAddStateAlreadyExists() {
        var states = new ArrayList<State>();
        var state = new State(new AbstractUrl("added_state_url"));
        var newState = new State(state.getContext().getContextUrl());
        states.add(state);

        this.graph.setStates(states);
        this.graph.addState(newState);

        assertEquals(1, this.graph.getStateCount());
        assertTrue(this.graph.getStates().contains(newState));
    }

    @Test
    public void testAddTransitionMissingOrigin() {
        var transition = getTestTransition();

        this.graph.addState(transition.getDestination());
        this.graph.addTransition(transition);

        assertEquals(0, this.graph.getTransitions().size());
    }

    @Test
    public void testAddTransitionMissingDestination() {
        var transition = getTestTransition();

        this.graph.addState(transition.getOrigin());
        this.graph.addTransition(transition);

        assertEquals(0, this.graph.getTransitions().size());
    }

    @Test
    public void testAddTransitionDuplicate() {
        var transition = getTestTransition();

        this.graph.addState(transition.getOrigin());
        this.graph.addState(transition.getDestination());
        this.graph.addTransition(transition);
        this.graph.addTransition(new Transition(transition.getOrigin(), transition.getDestination(), transition.getRequest()));

        assertEquals(1, this.graph.getTransitions().size());
    }

    private Transition getTestTransition() {
        var origin = new State(new AbstractUrl("origin"));
        var destination = new State(new AbstractUrl("destination"));
        var request = new AbstractRequest("GET", destination.getContext().getContextUrl());
        return new Transition(origin, destination, request);
    }
}
