package com.datagenio.model;

import com.datagenio.model.api.WebState;
import com.datagenio.model.api.WebTransition;
import com.datagenio.model.request.AbstractRequest;
import com.datagenio.model.request.AbstractUrlImpl;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

public class WebFlowGraphTest {

    private WebFlowGraphImpl graph;

    @Before
    public void setUp() {
        this.graph = new WebFlowGraphImpl();
    }

    @Test
    public void testGetStates() {
        assertNotNull(this.graph.getStates());
    }

    @Test
    public void testSetStates() {
        var states = new ArrayList<WebState>();
        states.add(new WebStateImpl(new AbstractUrlImpl("state_url")));
        this.graph.setStates(states);

        assertEquals(states, this.graph.getStates());
    }

    @Test
    public void testGetTransitions() {
        assertNotNull(this.graph.getTransitions());
    }

    @Test
    public void testSetTransitions() {
        var transitions = new ArrayList<WebTransition>();

        var origin = new WebStateImpl(new AbstractUrlImpl("origin"));
        var destination = new WebStateImpl(new AbstractUrlImpl("destination"));
        var request = new AbstractRequest("GET", destination.getUrl());

        transitions.add(new WebTransitionImpl(origin, destination));
        this.graph.setTransitions(transitions);

        assertEquals(transitions, this.graph.getTransitions());
    }

    @Test
    public void testAddStateNew() {
        var state = new WebStateImpl(new AbstractUrlImpl("added_state_url"));
        this.graph.addState(state);

        assertEquals(1, this.graph.getStateCount());
        assertTrue(this.graph.getStates().contains(state));
    }

    @Test
    public void testAddStateAlreadyExists() {
        var states = new ArrayList<WebState>();
        var state = new WebStateImpl(new AbstractUrlImpl("added_state_url"));
        var newState = new WebStateImpl(state.getUrl());
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
        this.graph.addTransition(new WebTransitionImpl(transition.getOrigin(), transition.getDestination()));

        assertEquals(1, this.graph.getTransitions().size());
    }

    private WebTransitionImpl getTestTransition() {
        var origin = new WebStateImpl(new AbstractUrlImpl("origin"));
        var destination = new WebStateImpl(new AbstractUrlImpl("destination"));
        var request = new AbstractRequest("GET", destination.getUrl());
        return new WebTransitionImpl(origin, destination);
    }
}
