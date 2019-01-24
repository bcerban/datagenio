package com.datagenio.model;

import com.datagenio.model.api.WebState;
import com.datagenio.model.request.AbstractRequest;
import com.datagenio.model.request.AbstractUrlImpl;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class WebTransitionImplTest {
    private WebState origin;
    private WebState destination;
    private WebTransitionImpl transition;

    @Before
    public void setUp() {
        origin = new WebStateImpl(new AbstractUrlImpl("origin_state_url"));
        destination = new WebStateImpl(new AbstractUrlImpl("destination_state_url"));
        transition = new WebTransitionImpl(origin, destination);
    }

    @Test
    public void testGetOrigin() {
        assertEquals(origin, transition.getOrigin());
    }

    @Test
    public void testSetOrigin() {
        var newOrigin = new WebStateImpl(new AbstractUrlImpl("new_origin_url"));
        transition.setOrigin(newOrigin);
        assertEquals(newOrigin, transition.getOrigin());
    }

    @Test
    public void testGetDestination() {
        assertEquals(destination, transition.getDestination());
    }

    @Test
    public void testSetDestination() {
        var newDestination = new WebStateImpl(new AbstractUrlImpl("new_destination_url"));
        transition.setDestination(newDestination);
        assertEquals(newDestination, transition.getDestination());
    }

    @Test
    public void testEqualsSelf() {
        assertTrue(transition.equals(transition));
    }

    @Test
    public void testEqualsIdentical() {
        WebTransitionImpl other = new WebTransitionImpl(transition.getOrigin(), transition.getDestination());
        assertTrue(transition.equals(other));
    }

    @Test
    public void testEqualsDiffOrigin() {
        WebTransitionImpl other = new WebTransitionImpl(
                new WebStateImpl(new AbstractUrlImpl("some_new_base_url")),
                transition.getDestination()
        );
        assertFalse(transition.equals(other));
    }

    @Test
    public void testEqualsDiffDestination() {
        WebTransitionImpl other = new WebTransitionImpl(
                transition.getOrigin(),
                new WebStateImpl(new AbstractUrlImpl("some_new_base_url"))
        );
        assertFalse(transition.equals(other));
    }

    @Test
    public void testEqualsDiffRequest() {
        WebTransitionImpl other = new WebTransitionImpl(transition.getOrigin(), transition.getDestination());
        other.addRequest(new AbstractRequest("GET", new AbstractUrlImpl("http://test.com")));
        assertFalse(transition.equals(other));
    }

}
