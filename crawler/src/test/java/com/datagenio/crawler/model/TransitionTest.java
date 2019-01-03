package com.datagenio.crawler.model;

import com.datagenio.crawler.api.ExecutedEventable;
import com.datagenio.crawler.api.State;
import org.apache.http.*;
import org.apache.http.message.BasicHttpRequest;
import org.junit.Before;
import org.junit.Test;

import java.util.Collection;
import java.util.LinkedList;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

public class TransitionTest {

    private State origin;
    private State destination;
    private ExecutedEventable executedEventable;
    private Transition transition;

    @Before
    public void setUp() {
        this.origin = mock(State.class);
        this.destination = mock(State.class);
        this.executedEventable = mock(ExecutedEventable.class);
        this.transition = new Transition(this.origin, this.destination, this.executedEventable);
    }

    @Test
    public void testGetOrigin()
    {
        assertEquals(this.origin, this.transition.getOrigin());
    }

    @Test
    public void testGetDestination()
    {
        assertEquals(this.destination, this.transition.getDestination());
    }

    @Test
    public void testGetExecutedEvent()
    {
        assertEquals(this.executedEventable, this.transition.getExecutedEvent());
    }

    @Test
    public void testGetRequests()
    {
        assertNotNull(this.transition.getRequests());
    }

    @Test
    public void testSetOrigin()
    {
        State newOrigin = mock(State.class);
        this.transition.setOrigin(newOrigin);
        assertEquals(newOrigin, this.transition.getOrigin());
    }

    @Test
    public void testSetDestination()
    {
        State newDestination = mock(State.class);
        this.transition.setDestination(newDestination);
        assertEquals(newDestination, this.transition.getDestination());
    }

    @Test
    public void testSetExecutedEvent()
    {
        ExecutedEventable newEvent = mock(ExecutedEventable.class);
        this.transition.setExecutedEvent(newEvent);
        assertEquals(newEvent, this.transition.getExecutedEvent());
    }

    @Test
    public void testSetRequests()
    {
        Collection<HttpRequest> newRequestCollection = new LinkedList<>();
        this.transition.setRequests(newRequestCollection);
        assertEquals(newRequestCollection, this.transition.getRequests());
    }

    @Test
    public void testAddRequest()
    {
        HttpRequest request = new BasicHttpRequest("GET", "http://example.com");
        this.transition.addRequest(request);
        assertTrue(this.transition.getRequests().contains(request));
    }
}
