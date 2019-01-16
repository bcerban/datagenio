package com.datagenio.crawler;

import com.datagenio.crawler.api.*;
import com.datagenio.crawler.exception.BrowserException;
import com.datagenio.crawler.exception.UncrawlablePathException;
import com.datagenio.crawler.exception.UncrawlableStateException;
import com.datagenio.crawler.exception.UnsupportedEventTypeException;
import com.datagenio.databank.api.InputBuilder;
import org.jgrapht.GraphPath;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Before;
import org.junit.Test;

import java.net.URI;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class CrawlerTest {

    private static String OUTPUT_DIR = "/tmp/test";
    private static String ROOT_URL = "http://test.com";

    private Browser browser;
    private CrawlContext context;
    private Crawler crawler;
    private InputBuilder inputBuilder;


    @Before
    public void setUp() {
        this.browser = mock(Browser.class);
        this.inputBuilder = mock(InputBuilder.class);
        this.context = new CrawlContext(OUTPUT_DIR);
        this.crawler = new Crawler(this.context, this.browser, this.inputBuilder);
    }

    @Test
    public void testGetContext() {
        assertEquals(this.context, this.crawler.getContext());
    }

    @Test
    public void testGetBrowser() {
        assertEquals(this.browser, this.crawler.getBrowser());
    }

    @Test
    public void testSetBrowser() {
        var newBrowser = mock(Browser.class);
        this.crawler.setBrowser(newBrowser);
        assertEquals(newBrowser, this.crawler.getBrowser());
    }

    @Test
    public void testGetLogger() {
        assertNotNull(Crawler.getLogger());
    }

    @Test
    public void testCrawl() {
        String html = "<html><head><title>Test html document</title></head>"
                + "<body><span><button id=\"button-id\">Click me!</button></span>"
                + "<span><img src=\"/avatar.jpg\" alt=\"Avatar\"></span></body></html>";
        Document document = Jsoup.parse(html);
        when(this.browser.getDOM()).thenReturn(document);

        var graph = this.crawler.crawl(ROOT_URL);
        assertTrue(graph instanceof EventFlowGraph);
    }

    @Test
    public void testRelocateNoNearestState() throws UncrawlableStateException {
        State state = mock(State.class);
        EventFlowGraph graph = mock(EventFlowGraph.class);
        Crawler mockCrawler = mock(Crawler.class);

        doCallRealMethod().when(mockCrawler).relocateFrom(state);
        doReturn(graph).when(mockCrawler).getGraph();
        doReturn(null).when(graph).findNearestUnfinishedStateFrom(state);

        assertFalse(mockCrawler.relocateFrom(state));
    }

    @Test
    public void testRelocateUncrawlablePath() throws UncrawlableStateException, UncrawlablePathException {
        State state = mock(State.class);
        EventFlowGraph graph = mock(EventFlowGraph.class);
        GraphPath path = mock(GraphPath.class);
        Crawler mockCrawler = mock(Crawler.class);

        doCallRealMethod().when(mockCrawler).relocateFrom(state);
        doReturn(graph).when(mockCrawler).getGraph();
        doReturn(state).when(graph).findNearestUnfinishedStateFrom(state);
        doReturn(path).when(graph).findPath(any(), any());
        doThrow(new UncrawlablePathException("")).when(mockCrawler).walk(path);

        assertFalse(mockCrawler.relocateFrom(state));
    }

    @Test
    public void testRelocate() throws UncrawlableStateException, UncrawlablePathException {
        State state = mock(State.class);
        EventFlowGraph graph = mock(EventFlowGraph.class);
        GraphPath path = mock(GraphPath.class);
        Crawler mockCrawler = mock(Crawler.class);

        doCallRealMethod().when(mockCrawler).relocateFrom(state);
        doReturn(graph).when(mockCrawler).getGraph();
        doReturn(state).when(graph).findNearestUnfinishedStateFrom(state);
        doReturn(path).when(graph).findPath(any(), any());

        assertTrue(mockCrawler.relocateFrom(state));
        verify(mockCrawler, times(1)).walk(path);
        verify(graph, times(1)).setCurrentState(state);
    }

    @Test(expected = UncrawlablePathException.class)
    public void testWalkBrowserException() throws UncrawlablePathException, BrowserException {
        URI uri = URI.create(ROOT_URL);
        State first = mock(State.class);
        GraphPath path = mock(GraphPath.class);

        doReturn(uri).when(first).getUri();
        doReturn(first).when(path).getStartVertex();
        doThrow(new BrowserException("")).when(this.browser).navigateTo(uri);

        this.crawler.walk(path);
    }

    @Test(expected = UncrawlablePathException.class)
    public void testWalkUnsupportedType() throws UncrawlablePathException, BrowserException, UnsupportedEventTypeException {
        URI uri = URI.create(ROOT_URL);
        State first = mock(State.class);
        Transitionable firstToSecond = mock(Transitionable.class);
        Eventable event = mock(Eventable.class);
        ExecutedEventable executedEvent = mock(ExecutedEventable.class);
        GraphPath path = mock(GraphPath.class);

        this.crawler.getGraph().addState(first);

        doReturn(uri).when(first).getUri();
        doReturn(first).when(path).getStartVertex();
        doReturn(first).when(firstToSecond).getOrigin();
        doReturn(List.of(firstToSecond)).when(path).getEdgeList();
        doReturn(executedEvent).when(firstToSecond).getExecutedEvent();
        doReturn(event).when(executedEvent).getEvent();
        doThrow(new UnsupportedEventTypeException("")).when(this.browser).triggerEvent(any(), any());

        this.crawler.walk(path);

        verify(this.browser, times(1)).navigateTo(uri);
    }

    @Test(expected = UncrawlablePathException.class)
    public void testWalkSameState() throws UncrawlablePathException, BrowserException, UnsupportedEventTypeException {
        URI uri = URI.create(ROOT_URL);
        State first = mock(State.class);
        Transitionable firstToSecond = mock(Transitionable.class);
        Eventable event = mock(Eventable.class);
        ExecutedEventable executedEvent = mock(ExecutedEventable.class);
        GraphPath path = mock(GraphPath.class);

        this.crawler.getGraph().addState(first);

        doReturn(uri).when(first).getUri();
        doReturn(first).when(path).getStartVertex();
        doReturn(first).when(firstToSecond).getOrigin();
        doReturn(List.of(firstToSecond)).when(path).getEdgeList();
        doReturn(executedEvent).when(firstToSecond).getExecutedEvent();
        doReturn(event).when(executedEvent).getEvent();
        doReturn(first).when(this.browser).getCurrentBrowserState();

        this.crawler.walk(path);

        verify(this.browser, times(1)).navigateTo(uri);
    }

    @Test
    public void testWalk() throws UncrawlablePathException, BrowserException, UnsupportedEventTypeException {
        URI uri = URI.create(ROOT_URL);
        State first = mock(State.class);
        State second = mock(State.class);
        Transitionable firstToSecond = mock(Transitionable.class);
        Eventable event = mock(Eventable.class);
        ExecutedEventable executedEvent = mock(ExecutedEventable.class);
        GraphPath path = mock(GraphPath.class);

        this.crawler.getGraph().addState(first);
        this.crawler.getGraph().addState(second);

        doReturn(uri).when(first).getUri();
        doReturn(first).when(path).getStartVertex();
        doReturn(first).when(firstToSecond).getOrigin();
        doReturn(List.of(firstToSecond)).when(path).getEdgeList();
        doReturn(executedEvent).when(firstToSecond).getExecutedEvent();
        doReturn(event).when(executedEvent).getEvent();
        doReturn(second).when(this.browser).getCurrentBrowserState();

        this.crawler.walk(path);

        verify(this.browser, times(1)).navigateTo(uri);
    }
}
