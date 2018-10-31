package com.datagenio.model;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

public class StateTest {

    private AbstractUrl url;
    private State state;

    @Before
    public void setUp() {
        this.url = new AbstractUrl("test_url");
        this.state = new State(this.url);
    }

    @Test
    public void testGetContext() {
        assertNotNull(this.state.getContext());
        assertEquals("test_url", this.state.getContext().getContextUrl().getBaseUrl());
    }

    @Test
    public void testSetContext() {
        StateContext context = new StateContext(new AbstractUrl("other_url"));
        this.state.setContext(context);

        assertNotNull(this.state.getContext());
        assertEquals("other_url", this.state.getContext().getContextUrl().getBaseUrl());
    }

    @Test
    public void testGetRequestSet() {
        assertNotNull(this.state.getRequestSet());
        assertEquals(0, this.state.getRequestSet().size());
    }

    @Test
    public void testSetRequestSet() {
        ArrayList<AbstractRequest> set = new ArrayList<>();
        this.state.setRequestSet(set);
        assertEquals(set, this.state.getRequestSet());
    }

    @Test
    public void testAddRequest() {
        AbstractRequest req = new AbstractRequest("GET", new AbstractUrl("test_url"));
        this.state.addRequest(req);
        assertEquals(1, this.state.getRequestSet().size());
        assertTrue(this.state.getRequestSet().contains(req));
    }
}
