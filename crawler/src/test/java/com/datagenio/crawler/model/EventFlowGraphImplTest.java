package com.datagenio.crawler.model;

import com.datagenio.crawler.api.EventFlowGraph;
import com.datagenio.crawler.api.Eventable;
import com.datagenio.crawler.api.State;
import com.datagenio.crawler.api.Transitionable;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class EventFlowGraphImplTest {

    @Test
    public void testGetInstance() {
        assertTrue(EventFlowGraphImpl.getInstance() instanceof EventFlowGraph);
    }

    @Test
    public void testGetStates() {
        assertTrue(EventFlowGraphImpl.getInstance().getStates().isEmpty());
    }

    @Test
    public void testGetTransitions() {
        assertTrue(EventFlowGraphImpl.getInstance().getTransitions().isEmpty());
    }

    @Test
    public void testGetEvents() {
        assertTrue(EventFlowGraphImpl.getInstance().getEvents().isEmpty());
    }

    @Test
    public void testAddState() {
        State state = mock(State.class);
        EventFlowGraphImpl.getInstance().addState(state);
        assertTrue(EventFlowGraphImpl.getInstance().getStates().contains(state));
    }

    @Test
    public void testAddTransition() {
        State origin = mock(State.class);
        State destination = mock(State.class);
        EventFlowGraphImpl.getInstance().addState(origin);
        EventFlowGraphImpl.getInstance().addState(destination);

        Transitionable transition = mock(Transitionable.class);
        when(transition.getOrigin()).thenReturn(origin);
        when(transition.getDestination()).thenReturn(destination);

        EventFlowGraphImpl.getInstance().addTransition(transition);
        assertTrue(EventFlowGraphImpl.getInstance().getTransitions().contains(transition));
    }

    @Test
    public void testAddEvent() {
        Eventable event = mock(Eventable.class);
        EventFlowGraphImpl.getInstance().addEvent(event);
        assertTrue(EventFlowGraphImpl.getInstance().getEvents().contains(event));
    }
}
