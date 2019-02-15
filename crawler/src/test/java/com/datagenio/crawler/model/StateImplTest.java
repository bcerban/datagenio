package com.datagenio.crawler.model;

import com.datagenio.crawler.api.ExecutedEventable;
import com.datagenio.crawler.api.State;
import com.datagenio.crawler.util.ExecutableEventExtractor;
import com.datagenio.crawler.api.Eventable;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.Before;
import org.junit.Test;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

public class StateImplTest {

    private URI uri;
    private Document document;
    private ExecutableEventExtractor eventableExtractor;
    private Collection<Eventable> eventables;
    private StateImpl state;

    @Before
    public void setUp() {
        String html = "<html><head><title>Test html document</title></head>"
                + "<body><span><button id=\"button-id\">Click me!</button></span>"
                + "<span><img src=\"/avatar.jpg\" alt=\"Avatar\"></span></body></html>";

        this.uri = URI.create("http://example.com");
        this.document = Jsoup.parse(html);
        this.eventables = new ArrayList<>();

        this.eventableExtractor = mock(ExecutableEventExtractor.class);
//        when(this.eventableExtractor.extract(this.state, this.document)).thenReturn(this.eventables);

        this.state = new StateImpl(this.uri, this.document);
    }

    @Test
    public void testGetUri() {
        assertEquals(this.uri, this.state.getUri());
    }

    @Test
    public void testSetUri() {
        URI newUri = URI.create("http://test.com");
        this.state.setUri(newUri);
        assertEquals(newUri, this.state.getUri());
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

    @Test
    public void testGetUnfiredEventables() {
        assertEquals(this.eventables, this.state.getUnfiredEventables());
    }

    @Test
    public void testIsFinished() {
        assertTrue(this.state.isFinished());
    }

    @Test
    public void testMarkEventAsFired() {
        State newState = null;

        Eventable event = mock(Eventable.class);
        ExecutableEventExtractor extractor = mock(ExecutableEventExtractor.class);

        when(extractor.extractSorted(any(), any(), any())).thenReturn(List.of(event));
        newState = new StateImpl(uri, document);

        // Check preconditions for test
        assertFalse(newState.isFinished());
        assertTrue(newState.getUnfiredEventables().contains(event));

        ExecutedEventable executed = new ExecutedEvent(event);
        newState.markEventAsFired(executed);

        // Check postconditions
        assertTrue(newState.isFinished());
        assertFalse(newState.getUnfiredEventables().contains(event));
    }

    @Test
    public void testEqualsSelf() {
        assertTrue(this.state.equals(this.state));
    }

    @Test
    public void testEqualsIdentical() {
        StateImpl other = new StateImpl(this.uri, this.document);
        assertTrue(this.state.equals(other));
    }

    @Test
    public void testEqualsDifferent() {
        Document newDocument = Jsoup.parse("<html><head><title>Test html document</title></head></html>");
        StateImpl other = new StateImpl(this.uri, newDocument);
        other.setEventables(
                List.of(new ExecutableEvent(new Element("button"), Eventable.EventType.CLICK))
        );
        assertFalse(this.state.equals(other));
    }

}
