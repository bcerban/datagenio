package com.datagenio.model;

import com.datagenio.model.exception.InvalidTransitionException;
import com.datagenio.model.request.AbstractUrlImpl;
import org.junit.Before;
import org.junit.Test;


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
    public void testGetTransitions() {
        assertNotNull(this.graph.getTransitions());
    }

    @Test
    public void testAddStateNew() {
        var state = new WebStateImpl(new AbstractUrlImpl("added_state_url"));
        this.graph.addState(state);
        assertTrue(this.graph.getStates().contains(state));
    }

    @Test
    public void testAddStateAlreadyExists() {
        var state = new WebStateImpl(new AbstractUrlImpl("added_state_url"));
        var newState = new WebStateImpl(state.getUrl());

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
        this.graph.addTransition(new WebTransitionImpl(transition.getOrigin(), transition.getDestination()));

        assertEquals(1, this.graph.getTransitions().size());
    }

    private WebTransitionImpl getTestTransition() {
        var origin = new WebStateImpl(new AbstractUrlImpl("origin"));
        var destination = new WebStateImpl(new AbstractUrlImpl("destination"));
        return new WebTransitionImpl(origin, destination);
    }
}
