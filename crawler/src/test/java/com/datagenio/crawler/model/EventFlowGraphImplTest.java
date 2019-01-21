package com.datagenio.crawler.model;

import com.datagenio.crawler.api.EventFlowGraph;
import com.datagenio.crawler.api.Eventable;
import com.datagenio.crawler.api.State;
import com.datagenio.crawler.api.Transitionable;
import com.datagenio.crawler.exception.UncrawlableStateException;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.InvalidArgumentException;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class EventFlowGraphImplTest {

    private EventFlowGraph graph;

    @Before
    public void setUp() {
        this.graph = new EventFlowGraphImpl();
    }

    @Test
    public void testGetStates() {
        assertTrue(this.graph.getStates().isEmpty());
    }

    @Test
    public void testGetTransitions() {
        assertTrue(this.graph.getTransitions().isEmpty());
    }

    @Test
    public void testGetEvents() {
        assertTrue(this.graph.getEvents().isEmpty());
    }

    @Test
    public void testAddState() {
        State state = mock(State.class);
        this.graph.addState(state);
        assertTrue(this.graph.getStates().contains(state));
    }

    @Test
    public void testAddTransition() {
        State origin = mock(State.class);
        State destination = mock(State.class);
        ExecutedEvent executedEvent = mock(ExecutedEvent.class);
        this.graph.addState(origin);
        this.graph.addState(destination);

        Transitionable transition = mock(Transitionable.class);
        when(transition.getOrigin()).thenReturn(origin);
        when(transition.getDestination()).thenReturn(destination);
        when(transition.getExecutedEvent()).thenReturn(executedEvent);

        this.graph.addTransition(transition);
        assertTrue(this.graph.getTransitions().contains(transition));
    }

    @Test
    public void testAddEvent() {
        Eventable event = mock(Eventable.class);
        this.graph.addEvent(event);
        assertTrue(this.graph.getEvents().contains(event));
    }

    @Test
    public void testGetCurrentState() {
        assertNull(this.graph.getCurrentState());
    }

    @Test(expected = UncrawlableStateException.class)
    public void testSetCurrentStateNotInGraph() throws UncrawlableStateException {
        State state = mock(State.class);
        this.graph.setCurrentState(state);
        assertEquals(state, this.graph.getCurrentState());
    }

    @Test
    public void testSetCurrentState() throws UncrawlableStateException {
        State state = mock(State.class);
        this.graph.addState(state);
        this.graph.setCurrentState(state);
        assertEquals(state, this.graph.getCurrentState());
    }

    @Test
    public void testAddStateAsCurrent() throws UncrawlableStateException {
        State state = mock(State.class);
        this.graph.addStateAsCurrent(state);
        assertEquals(state, this.graph.getCurrentState());
        assertTrue(this.graph.getStates().contains(state));
    }
}
