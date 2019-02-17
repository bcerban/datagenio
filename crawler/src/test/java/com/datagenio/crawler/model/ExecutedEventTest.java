package com.datagenio.crawler.model;

import com.datagenio.context.EventInput;
import com.datagenio.crawler.api.Eventable;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

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
        List<EventInput> newInputs = new ArrayList<>();
        this.executedEvent.setDataInputs(newInputs);
        assertEquals(newInputs, this.executedEvent.getDataInputs());
    }
}
