package com.datagenio.crawler;

import com.datagenio.context.Context;
import com.datagenio.crawler.api.*;
import com.datagenio.crawler.exception.*;
import com.datagenio.databank.api.InputBuilder;
import com.datagenio.storageapi.ReadAdapter;
import org.jgrapht.GraphPath;
import org.junit.Before;
import org.junit.Test;

import java.net.URI;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class PersistentCrawlerTest {

    private static String OUTPUT_DIR = "/tmp/test";
    private static String ROOT_URL = "http://test.com";

    private Browser browser;
    private Context context;
    private PersistentCrawler crawler;
    private InputBuilder inputBuilder;
    private ReadAdapter readAdapter;

    @Before
    public void setUp() {
        browser = mock(Browser.class);
        inputBuilder = mock(InputBuilder.class);
        readAdapter = mock(ReadAdapter.class);

        context = new Context();
        context.setRootUrl(ROOT_URL);
        context.setOutputDirName(OUTPUT_DIR);
        crawler = new PersistentCrawler(context, browser, inputBuilder, readAdapter);
    }

    @Test
    public void testGetContext() {
        assertEquals(context, crawler.getContext());
    }

    @Test
    public void testGetBrowser() {
        assertEquals(browser, crawler.getBrowser());
    }

    @Test
    public void testSetBrowser() {
        var newBrowser = mock(Browser.class);
        crawler.setBrowser(newBrowser);
        assertEquals(newBrowser, crawler.getBrowser());
    }

    @Test
    public void testGetLogger() {
        assertNotNull(PersistentCrawler.getLogger());
    }

    @Test
    public void testRelocateNoNearestState() throws UncrawlableStateException {
        State state = mock(State.class);
        EventFlowGraph graph = mock(EventFlowGraph.class);
        PersistentCrawler mockCrawler = mock(PersistentCrawler.class);

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
        PersistentCrawler mockCrawler = mock(PersistentCrawler.class);

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
        PersistentCrawler mockCrawler = mock(PersistentCrawler.class);

        doCallRealMethod().when(mockCrawler).relocateFrom(state);
        doReturn(graph).when(mockCrawler).getGraph();
        doReturn(state).when(graph).findNearestUnfinishedStateFrom(any());
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
        doThrow(new BrowserException("")).when(browser).navigateTo(uri);

        crawler.walk(path);
    }

    @Test(expected = UncrawlablePathException.class)
    public void testWalkUnsupportedType() throws UncrawlablePathException, BrowserException, UnsupportedEventTypeException, EventTriggerException {
        URI uri = URI.create(ROOT_URL);
        State first = mock(State.class);
        Transitionable firstToSecond = mock(Transitionable.class);
        Eventable event = mock(Eventable.class);
        ExecutedEventable executedEvent = mock(ExecutedEventable.class);
        GraphPath path = mock(GraphPath.class);

        crawler.getGraph().addState(first);

        doReturn(uri).when(first).getUri();
        doReturn(first).when(path).getStartVertex();
        doReturn(first).when(firstToSecond).getOrigin();
        doReturn(List.of(firstToSecond)).when(path).getEdgeList();
        doReturn(executedEvent).when(firstToSecond).getExecutedEvent();
        doReturn(event).when(executedEvent).getEvent();
        doThrow(new UnsupportedEventTypeException("")).when(browser).triggerEvent(any(), any());

        crawler.walk(path);

        verify(browser, times(1)).navigateTo(uri);
    }

    @Test(expected = UncrawlablePathException.class)
    public void testWalkSameState() throws UncrawlablePathException, BrowserException, UnsupportedEventTypeException {
        URI uri = URI.create(ROOT_URL);
        State first = mock(State.class);
        Transitionable firstToSecond = mock(Transitionable.class);
        Eventable event = mock(Eventable.class);
        ExecutedEventable executedEvent = mock(ExecutedEventable.class);
        GraphPath path = mock(GraphPath.class);

        crawler.getGraph().addState(first);

        doReturn(uri).when(first).getUri();
        doReturn(first).when(path).getStartVertex();
        doReturn(first).when(firstToSecond).getOrigin();
        doReturn(List.of(firstToSecond)).when(path).getEdgeList();
        doReturn(executedEvent).when(firstToSecond).getExecutedEvent();
        doReturn(event).when(executedEvent).getEvent();
        doReturn(first).when(browser).getCurrentBrowserState();

        crawler.walk(path);

        verify(browser, times(1)).navigateTo(uri);
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

        crawler.getGraph().addState(first);
        crawler.getGraph().addState(second);

        doReturn(uri).when(first).getUri();
        doReturn(first).when(path).getStartVertex();
        doReturn(first).when(firstToSecond).getOrigin();
        doReturn(List.of(firstToSecond)).when(path).getEdgeList();
        doReturn(executedEvent).when(firstToSecond).getExecutedEvent();
        doReturn(event).when(executedEvent).getEvent();
        doReturn(second).when(browser).getCurrentBrowserState();

        crawler.walk(path);

        verify(browser, times(1)).navigateTo(uri);
    }
}
