package com.datagenio.model;

import com.datagenio.model.request.AbstractRequest;
import com.datagenio.model.request.AbstractUrl;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class WebTransitionTest {
    private WebState origin;
    private WebState destination;
    private WebTransition transition;

    @Before
    public void setUp() {
        origin = new WebState(new AbstractUrl("origin_state_url"));
        destination = new WebState(new AbstractUrl("destination_state_url"));
        transition = new WebTransition(origin, destination);
    }

    @Test
    public void testGetOrigin() {
        assertEquals(origin, transition.getOrigin());
    }

    @Test
    public void testSetOrigin() {
        var newOrigin = new WebState(new AbstractUrl("new_origin_url"));
        transition.setOrigin(newOrigin);
        assertEquals(newOrigin, transition.getOrigin());
    }

    @Test
    public void testGetDestination() {
        assertEquals(destination, transition.getDestination());
    }

    @Test
    public void testSetDestination() {
        var newDestination = new WebState(new AbstractUrl("new_destination_url"));
        transition.setDestination(newDestination);
        assertEquals(newDestination, transition.getDestination());
    }

    @Test
    public void testEqualsSelf() {
        assertTrue(transition.equals(transition));
    }

    @Test
    public void testEqualsIdentical() {
        WebTransition other = new WebTransition(transition.getOrigin(), transition.getDestination());
        assertTrue(transition.equals(other));
    }

    @Test
    public void testEqualsDiffOrigin() {
        WebTransition other = new WebTransition(
                new WebState(new AbstractUrl("some_new_base_url")),
                transition.getDestination()
        );
        assertFalse(transition.equals(other));
    }

    @Test
    public void testEqualsDiffDestination() {
        WebTransition other = new WebTransition(
                transition.getOrigin(),
                new WebState(new AbstractUrl("some_new_base_url"))
        );
        assertFalse(transition.equals(other));
    }

    @Test
    public void testEqualsDiffRequest() {
        WebTransition other = new WebTransition(transition.getOrigin(), transition.getDestination());
        other.addRequest(new AbstractRequest("GET", new AbstractUrl("http://test.com")));
        assertFalse(transition.equals(other));
    }

}
