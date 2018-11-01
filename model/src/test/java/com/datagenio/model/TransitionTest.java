package com.datagenio.model;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class TransitionTest {

    private Transition transition;

    @Before
    public void setUp() {
        var origin = new State(new AbstractUrl("origin_state_url"));
        var destination = new State(new AbstractUrl("destination_state_url"));

        this.transition = new Transition(
                origin,
                destination,
                new AbstractRequest("GET", destination.getContext().getContextUrl())
        );
    }

    @Test
    public void testGetOrigin() {
        assertEquals("origin_state_url", this.transition.getOrigin().getContext().getContextUrl().getBaseUrl());
    }

    @Test
    public void testSetOrigin() {
        this.transition.setOrigin(new State(new AbstractUrl("new_origin_url")));
        assertEquals("new_origin_url", this.transition.getOrigin().getContext().getContextUrl().getBaseUrl());
    }

    @Test
    public void testGetDestination() {
        assertEquals("destination_state_url", this.transition.getDestination().getContext().getContextUrl().getBaseUrl());
    }

    @Test
    public void testSetDestination() {
        this.transition.setDestination(new State(new AbstractUrl("new_destination_url")));
        assertEquals("new_destination_url", this.transition.getDestination().getContext().getContextUrl().getBaseUrl());
    }

    @Test
    public void testGetRequest() {
        assertNotNull(this.transition.getRequest());
    }

    @Test
    public void testSetRequest() {
        this.transition.setRequest(
                new AbstractRequest("POST", new AbstractUrl("new_destination_url"))
        );

        assertEquals("new_destination_url", this.transition.getRequest().getRequestUrl().getBaseUrl());
    }

    @Test
    public void testEqualsSelf() {
        assertTrue(this.transition.equals(this.transition));
    }

    @Test
    public void testEqualsIdentical() {
        Transition other = new Transition(
                this.transition.getOrigin(),
                this.transition.getDestination(),
                this.transition.getRequest()
        );
        assertTrue(this.transition.equals(other));
    }

    @Test
    public void testEqualsDiffOrigin() {
        Transition other = new Transition(
                new State(new AbstractUrl("some_new_base_url")),
                this.transition.getDestination(),
                this.transition.getRequest()
        );
        assertFalse(this.transition.equals(other));
    }

    @Test
    public void testEqualsDiffDestination() {
        Transition other = new Transition(
                this.transition.getOrigin(),
                new State(new AbstractUrl("some_new_base_url")),
                this.transition.getRequest()
        );
        assertFalse(this.transition.equals(other));
    }

    @Test
    public void testEqualsDiffRequest() {
        var request = new AbstractRequest("GET", new AbstractUrl("some_new_base_url"));
        request.getRequestUrl().addParam(new TypedParam("username", "Simpson"));

        Transition other = new Transition(
                this.transition.getOrigin(),
                this.transition.getDestination(),
                request
        );
        assertFalse(this.transition.equals(other));
    }

}
