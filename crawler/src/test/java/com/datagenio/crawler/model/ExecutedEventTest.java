package com.datagenio.crawler.model;

import com.datagenio.crawler.api.Eventable;
import org.junit.Before;
import org.junit.Test;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

public class ExecutedEventTest {

    private Eventable event;
    private ExecutedEvent executedEvent;

    @Before
    public void setUp() {
        this.event = mock(Eventable.class);
        this.executedEvent = new ExecutedEvent(this.event);
    }

    @Test
    public void testGetEvent() {
        assertEquals(this.event, this.executedEvent.getEvent());
    }

    @Test
    public void testSetEvent() {
        Eventable newEvent = mock(Eventable.class);
        this.executedEvent.setEvent(newEvent);
        assertEquals(newEvent, this.executedEvent.getEvent());
    }

    @Test
    public void testGetDataInputs() {
        assertNotNull(this.executedEvent.getDataInputs());
    }

    @Test
    public void testSetDataInputs() {
        Map<String, String> newInputs = new LinkedHashMap<>();
        this.executedEvent.setDataInputs(newInputs);
        assertEquals(newInputs, this.executedEvent.getDataInputs());
    }

    @Test
    public void testAddInput() {
        this.executedEvent.addInput("name", "testname");
        assertTrue(this.executedEvent.getDataInputs().containsKey("name"));
        assertEquals("testname", this.executedEvent.getDataInputs().get("name"));
    }
}
