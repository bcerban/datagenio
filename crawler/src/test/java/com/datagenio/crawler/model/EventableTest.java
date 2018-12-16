package com.datagenio.crawler.model;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class EventableTest {

    private Element source;
    private Document parent;
    private Eventable eventable;


    @Before
    public void setUp() {
        String html = "<html><head><title>Test html document</title></head>"
                + "<body><span><button id=\"button-id\">Click me!</button>"
                + "</span></body></html>";

        this.parent = Jsoup.parse(html);
        this.source = this.parent.selectFirst("button");
        this.eventable = new Eventable(this.source, Eventable.EventType.click);
    }

    @Test
    public void testGetSource() {
        assertEquals(this.source, this.eventable.getSource());
    }

    @Test
    public void testSetSource() {
        var newSource = new Element("form");
        this.eventable.setSource(newSource);
        assertEquals(newSource, this.eventable.getSource());
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
        assertEquals("#root/html/body/span/button", this.eventable.getXpath());
    }

    @Test
    public void testGetIdentifierID() {
        assertEquals("button-id", this.eventable.getSourceIdentifier());
    }

    @Test
    public void testGetIdentifierXPath() {
        this.source.attr("id", null);
        assertEquals("#root/html/body/span/button", this.eventable.getSourceIdentifier());
    }
}
