package com.datagenio.crawler.model;

import com.datagenio.crawler.api.Eventable;
import com.datagenio.crawler.api.State;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class ExecutableEventTest {

    private State origin;
    private State destination;
    private Element button;
    private Element img;
    private Document parent;
    private ExecutableEvent executableEvent;

    @Before
    public void setUp() {
        String html = "<html><head><title>Test html document</title></head>"
                + "<body><span><button id=\"button-id\">Click me!</button></span>"
                + "<span><img src=\"/avatar.jpg\" alt=\"Avatar\"></span></body></html>";

        this.origin = mock(State.class);
        this.destination = mock(State.class);
        this.parent = Jsoup.parse(html);
        this.button = this.parent.selectFirst("button");
        this.img = this.parent.selectFirst("img");
        this.executableEvent = new ExecutableEvent(this.origin, this.destination, this.button, ExecutableEvent.EventType.click);
    }

    @Test
    public void testGetOrigin() {
        assertEquals(this.origin, this.executableEvent.getOrigin());
    }

    @Test
    public void testSetOrigin() {
        State newOrigin = mock(State.class);
        this.executableEvent.setOrigin(newOrigin);
        assertEquals(newOrigin, this.executableEvent.getOrigin());
    }

    @Test
    public void testGetDestination() {
        assertEquals(this.destination, this.executableEvent.getDestination());
    }

    @Test
    public void testSetDestination() {
        State newDestination = mock(State.class);
        this.executableEvent.setDestination(newDestination);
        assertEquals(newDestination, this.executableEvent.getDestination());
    }

    @Test
    public void testGetSource() {
        assertEquals(this.button, this.executableEvent.getSource());
    }

    @Test
    public void testSetSource() {
        this.executableEvent.setSource(this.img);
        assertEquals(this.img, this.executableEvent.getSource());
    }

    @Test
    public void testGetEventType() {
        assertEquals(ExecutableEvent.EventType.click, this.executableEvent.getEventType());
    }

    @Test
    public void testSetEventType() {
        this.executableEvent.setEventType(ExecutableEvent.EventType.submit);
        assertEquals(ExecutableEvent.EventType.submit, this.executableEvent.getEventType());
    }

    @Test
    public void testGetHandler() {
        assertEquals("", this.executableEvent.getHandler());
    }

    @Test
    public void testSetHandler() {
        this.executableEvent.setHandler("testHandlerAction");
        assertEquals("testHandlerAction", this.executableEvent.getHandler());
    }

    @Test
    public void testGetParent() {
        assertEquals(this.parent, this.executableEvent.getParent());
    }

    @Test
    public void testSetParent() {
        var newParent = new Document("some-uri");
        this.executableEvent.setParent(newParent);
        assertEquals(newParent, this.executableEvent.getParent());
    }

    @Test
    public void testGetXpath() {
        assertEquals("/html/body/span[1]/button", this.executableEvent.getXpath());
    }

    @Test
    public void testGetIdentifierID() {
        assertEquals("button-id", this.executableEvent.getIdentifier());
    }

    @Test
    public void testGetIdentifierXPath() {
        this.button.attr("id", null);
        assertEquals("/html/body/span[1]/button", this.executableEvent.getIdentifier());
    }

    @Test
    public void testEqualsSelf() {
        assertTrue(this.executableEvent.equals(this.executableEvent));
    }

    @Test
    public void testEqualsIdentical() {
        var other = new ExecutableEvent(this.origin, this.destination, this.button, Eventable.EventType.click);
        assertTrue(this.executableEvent.equals(other));
    }

    @Test
    public void testEqualsDiffEventType() {
        ExecutableEvent other = new ExecutableEvent(this.origin, this.destination, this.button, Eventable.EventType.submit);
        assertFalse(this.executableEvent.equals(other));
    }

    @Test
    public void testEqualsDiffSourceIdentifier() {
        ExecutableEvent other = new ExecutableEvent(this.origin, this.destination, this.img, Eventable.EventType.click);
        assertFalse(this.executableEvent.equals(other));
    }

    @Test
    public void testEqualsDiffHandler() {
        ExecutableEvent other = new ExecutableEvent(this.origin, this.destination, this.button, Eventable.EventType.click);
        other.setHandler("testHandlerAction");
        assertFalse(this.executableEvent.equals(other));
    }
}
