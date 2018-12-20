package com.datagenio.crawler.model;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class EventableTest {

    private Element button;
    private Element img;
    private Document parent;
    private Eventable eventable;


    @Before
    public void setUp() {
        String html = "<html><head><title>Test html document</title></head>"
                + "<body><span><button id=\"button-id\">Click me!</button></span>"
                + "<span><img src=\"/avatar.jpg\" alt=\"Avatar\"></span></body></html>";

        this.parent = Jsoup.parse(html);
        this.button = this.parent.selectFirst("button");
        this.img = this.parent.selectFirst("img");
        this.eventable = new Eventable(this.button, Eventable.EventType.click);
    }

    @Test
    public void testGetSource() {
        assertEquals(this.button, this.eventable.getSource());
    }

    @Test
    public void testSetSource() {
        this.eventable.setSource(this.img);
        assertEquals(this.img, this.eventable.getSource());
    }

    @Test
    public void testGetEventType() {
        assertEquals(Eventable.EventType.click, this.eventable.getEventType());
    }

    @Test
    public void testSetEventType() {
        this.eventable.setEventType(Eventable.EventType.submit);
        assertEquals(Eventable.EventType.submit, this.eventable.getEventType());
    }

    @Test
    public void testGetHandler() {
        assertEquals("", this.eventable.getHandler());
    }

    @Test
    public void testSetHandler() {
        this.eventable.setHandler("testHandlerAction");
        assertEquals("testHandlerAction", this.eventable.getHandler());
    }

    @Test
    public void testGetParent() {
        assertEquals(this.parent, this.eventable.getParent());
    }

    @Test
    public void testSetParent() {
        var newParent = new Document("some-uri");
        this.eventable.setParent(newParent);
        assertEquals(newParent, this.eventable.getParent());
    }

    @Test
    public void testGetXpath() {
        assertEquals("/html/body/span[1]/button", this.eventable.getXpath());
    }

    @Test
    public void testGetIdentifierID() {
        assertEquals("button-id", this.eventable.getSourceIdentifier());
    }

    @Test
    public void testGetIdentifierXPath() {
        this.button.attr("id", null);
        assertEquals("/html/body/span[1]/button", this.eventable.getSourceIdentifier());
    }

    @Test
    public void testEqualsSelf() {
        assertTrue(this.eventable.equals(this.eventable));
    }

    @Test
    public void testEqualsIdentical() {
        Eventable other = new Eventable(this.button, Eventable.EventType.click);
        assertTrue(this.eventable.equals(other));
    }

    @Test
    public void testEqualsDiffEventType() {
        Eventable other = new Eventable(this.button, Eventable.EventType.submit);
        assertFalse(this.eventable.equals(other));
    }

    @Test
    public void testEqualsDiffSourceIdentifier() {
        Eventable other = new Eventable(this.img, Eventable.EventType.click);
        assertFalse(this.eventable.equals(other));
    }

    @Test
    public void testEqualsDiffHandler() {
        Eventable other = new Eventable(this.button, Eventable.EventType.click);
        other.setHandler("testHandlerAction");
        assertFalse(this.eventable.equals(other));
    }
}
