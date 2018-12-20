package com.datagenio.crawler.model;

import com.datagenio.crawler.ConfigurableExtractor;
import com.datagenio.crawler.api.Eventable;
import com.datagenio.crawler.api.EventableExtractor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

public class StateImplTest {

    private Document document;
    private Collection<Eventable> eventables;
    private StateImpl state;

    @Before
    public void setUp() {
        String html = "<html><head><title>Test html document</title></head>"
                + "<body><span><button id=\"button-id\">Click me!</button></span>"
                + "<span><img src=\"/avatar.jpg\" alt=\"Avatar\"></span></body></html>";

        this.document = Jsoup.parse(html);
        this.eventables = new ArrayList<>();

        EventableExtractor eventableExtractor = mock(ConfigurableExtractor.class);
        when(eventableExtractor.extract(this.document)).thenReturn(this.eventables);

        this.state = new StateImpl(this.document, eventableExtractor);
    }

    @Test
    public void testGetDocument() {
        assertEquals(this.document, this.state.getDocument());
    }

    @Test
    public void testSetDocument() {
        Document newDocument = Jsoup.parse("<html><head><title>Test html document</title></head></html>");
        this.state.setDocument(newDocument);
        assertEquals(newDocument, this.state.getDocument());
    }

    @Test
    public void testGetEventables() {
        assertEquals(this.eventables, this.state.getEventables());
    }

    @Test
    public void testSetEventables() {
        Collection<Eventable> otherEventables = new ArrayList<>();
        this.state.setEventables(otherEventables);
        assertEquals(otherEventables, this.state.getEventables());
    }
}
