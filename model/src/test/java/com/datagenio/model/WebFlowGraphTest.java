package com.datagenio.model;

import com.datagenio.model.exception.InvalidTransitionException;
import com.datagenio.model.request.AbstractUrl;
import org.junit.Before;
import org.junit.Test;


import static org.junit.Assert.*;

public class WebFlowGraphTest {

    private WebFlowGraph graph;

    @Before
    public void setUp() {
        this.graph = new WebFlowGraph();
    }

    @Test
    public void testGetStates() {
        assertNotNull(this.graph.getStates());
    }

    @Test
    public void testGetTransitions() {
        assertNotNull(this.graph.getTransitions());
    }

    @Test
    public void testAddStateNew() {
        var state = new WebState(new AbstractUrl("added_state_url"));
        this.graph.addState(state);
        assertTrue(this.graph.getStates().contains(state));
    }

    @Test
    public void testAddStateAlreadyExists() {
        var state = new WebState(new AbstractUrl("added_state_url"));
        var newState = new WebState(state.getUrl());

        this.graph.addState(state);
        this.graph.addState(newState);
        assertTrue(this.graph.getStates().contains(newState));
    }

    @Test(expected = InvalidTransitionException.class)
    public void testAddTransitionMissingOrigin() throws InvalidTransitionException {
        var transition = getTestTransition();

        this.graph.addState(transition.getDestination());
        this.graph.addTransition(transition);

        assertEquals(0, this.graph.getTransitions().size());
    }

    @Test(expected = InvalidTransitionException.class)
    public void testAddTransitionMissingDestination() throws InvalidTransitionException {
        var transition = getTestTransition();

        this.graph.addState(transition.getOrigin());
        this.graph.addTransition(transition);

        assertEquals(0, this.graph.getTransitions().size());
    }

    @Test
    public void testAddTransitionDuplicate() throws InvalidTransitionException {
        var transition = getTestTransition();

        this.graph.addState(transition.getOrigin());
        this.graph.addState(transition.getDestination());
        this.graph.addTransition(transition);
        this.graph.addTransition(new WebTransition(transition.getOrigin(), transition.getDestination()));

        assertEquals(1, this.graph.getTransitions().size());
    }

    private WebTransition getTestTransition() {
        var origin = new WebState(new AbstractUrl("origin"));
        var destination = new WebState(new AbstractUrl("destination"));
        return new WebTransition(origin, destination);
    }
}
