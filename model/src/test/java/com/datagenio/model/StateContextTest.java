package com.datagenio.model;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class StateContextTest {
    private StateContext context;

    @Before
    public void setUp() {
        this.context = new StateContext(new AbstractUrl("test_url"));
    }

    @Test
    public void testGetContextUrl() {
        assertNotNull(this.context.getContextUrl().getBaseUrl());
        assertEquals("test_url", this.context.getContextUrl().getBaseUrl());
    }

    @Test
    public void testSetContextUrl() {
        this.context.setContextUrl(new AbstractUrl("new_url"));
        assertEquals("new_url", this.context.getContextUrl().getBaseUrl());
    }

    @Test
    public void testGetSession() {
        assertNotNull(this.context.getSession());
    }

    @Test
    public void testSetSession() {
        Session session = new Session();
        this.context.setSession(session);
        assertEquals(session, this.context.getSession());
    }
}
