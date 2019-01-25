package com.datagenio.crawler.model;

import com.datagenio.crawler.api.Eventable;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class ExecutableEventTest {

    private Element button;
    private Element img;
    private Document parent;
    private ExecutableEvent executableEvent;

    @Before
    public void setUp() {
        String html = "<html><head><title>Test html document</title></head>"
                + "<body><span><button id=\"button-id\">Click me!</button></span>"
                + "<span><img src=\"/avatar.jpg\" alt=\"Avatar\"></span></body></html>";

        this.parent = Jsoup.parse(html);
        this.button = this.parent.selectFirst("button");
        this.img = this.parent.selectFirst("img");
        this.executableEvent = new ExecutableEvent(this.button, ExecutableEvent.EventType.CLICK);
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
        assertEquals(ExecutableEvent.EventType.CLICK, this.executableEvent.getEventType());
    }

    @Test
    public void testSetEventType() {
        this.executableEvent.setEventType(ExecutableEvent.EventType.SUBMIT);
        assertEquals(ExecutableEvent.EventType.SUBMIT, this.executableEvent.getEventType());
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
        var other = new ExecutableEvent(this.button, Eventable.EventType.CLICK);
        assertTrue(this.executableEvent.equals(other));
    }

    @Test
    public void testEqualsDiffEventType() {
        ExecutableEvent other = new ExecutableEvent(this.button, Eventable.EventType.SUBMIT);
        assertFalse(this.executableEvent.equals(other));
    }

    @Test
    public void testEqualsDiffSourceIdentifier() {
        ExecutableEvent other = new ExecutableEvent(this.img, Eventable.EventType.CLICK);
        assertFalse(this.executableEvent.equals(other));
    }

    @Test
    public void testEqualsDiffHandler() {
        ExecutableEvent other = new ExecutableEvent(this.button, Eventable.EventType.CLICK);
        other.setHandler("testHandlerAction");
        assertFalse(this.executableEvent.equals(other));
    }
}
