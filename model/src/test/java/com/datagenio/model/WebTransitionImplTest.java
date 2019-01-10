package com.datagenio.model;

import com.datagenio.model.api.AbstractHttpRequest;
import com.datagenio.model.api.WebState;
import com.datagenio.model.request.AbstractUrlImpl;
import org.apache.http.ProtocolVersion;
import org.apache.http.message.BasicHttpRequest;
import org.apache.http.message.BasicRequestLine;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class WebTransitionImplTest {

    private AbstractHttpRequest request;
    private WebState origin;
    private WebState destination;
    private WebTransitionImpl transition;

    @Before
    public void setUp() {
        this.origin = new WebStateImpl(new AbstractUrlImpl("origin_state_url"));
        this.destination = new WebStateImpl(new AbstractUrlImpl("destination_state_url"));
//        this.request = new AbstractRequest("GET", destination.getContext().getContextUrl())
        this.transition = new WebTransitionImpl(
                this.origin,
                this.destination
        );
    }

    @Test
    public void testGetOrigin() {
        assertEquals(this.origin, this.transition.getOrigin());
    }

    @Test
    public void testSetOrigin() {
        var newOrigin = new WebStateImpl(new AbstractUrlImpl("new_origin_url"));
        this.transition.setOrigin(newOrigin);
        assertEquals(newOrigin, this.transition.getOrigin());
    }

    @Test
    public void testGetDestination() {
        assertEquals(this.destination, this.transition.getDestination());
    }

    @Test
    public void testSetDestination() {
        var newDestination = new WebStateImpl(new AbstractUrlImpl("new_destination_url"));
        this.transition.setDestination(newDestination);
        assertEquals(newDestination, this.transition.getDestination());
    }

    @Test
    public void testEqualsSelf() {
        assertTrue(this.transition.equals(this.transition));
    }

    @Test
    public void testEqualsIdentical() {
        WebTransitionImpl other = new WebTransitionImpl(
                this.transition.getOrigin(),
                this.transition.getDestination()
        );
        assertTrue(this.transition.equals(other));
    }

    @Test
    public void testEqualsDiffOrigin() {
        WebTransitionImpl other = new WebTransitionImpl(
                new WebStateImpl(new AbstractUrlImpl("some_new_base_url")),
                this.transition.getDestination()
        );
        assertFalse(this.transition.equals(other));
    }

    @Test
    public void testEqualsDiffDestination() {
        WebTransitionImpl other = new WebTransitionImpl(
                this.transition.getOrigin(),
                new WebStateImpl(new AbstractUrlImpl("some_new_base_url"))
        );
        assertFalse(this.transition.equals(other));
    }

    @Test
    public void testEqualsDiffRequest() {
        var requestLine = new BasicRequestLine(
                "GET",
                "http://test.com",
                new ProtocolVersion("HTTP", 1, 1)
        );

        WebTransitionImpl other = new WebTransitionImpl(
                this.transition.getOrigin(),
                this.transition.getDestination()
        );
        other.addRequest(new BasicHttpRequest(requestLine));
        assertFalse(this.transition.equals(other));
    }

}
