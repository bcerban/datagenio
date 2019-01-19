package com.datagenio.crawler;

import com.datagenio.crawler.api.*;
import com.datagenio.crawler.exception.*;
import com.datagenio.crawler.model.EventFlowGraphImpl;
import com.datagenio.crawler.model.ExecutedEvent;
import com.datagenio.crawler.model.Transition;
import com.datagenio.crawler.util.ScreenShotSaver;
import com.datagenio.crawler.util.SiteBoundChecker;
import com.datagenio.databank.api.InputBuilder;
import org.jgrapht.GraphPath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.Map;

public class SimpleCrawler implements com.datagenio.crawler.api.Crawler {

    private static Logger logger = LoggerFactory.getLogger(SimpleCrawler.class);

    private CrawlContext context;
    private Browser browser;
    private EventFlowGraph graph;
    private InputBuilder inputBuilder;

    public SimpleCrawler(CrawlContext context, Browser browser, InputBuilder inputBuilder) {
        this.context = context;
        this.browser = browser;
        this.inputBuilder = inputBuilder;
    }

    public CrawlContext getContext() {
        return context;
    }

    public Browser getBrowser() {
        return browser;
    }

    public void setBrowser(Browser browser) {
        this.browser = browser;
    }

    public EventFlowGraph getGraph() {
        if (this.graph == null) {
            this.graph = new EventFlowGraphImpl();
        }
        return this.graph;
    }

    public static Logger getLogger() {
        return logger;
    }

    public EventFlowGraph crawl() {
        logger.debug("Begin crawling {}...", this.context.getRootUrl());

        try {
            this.initCrawl(URI.create(this.context.getRootUrl()));
            while (!this.getGraph().getCurrentState().isFinished()) {
                State current = this.getGraph().getCurrentState();
                Eventable event = current.getNextEventToFire();

                try {
                    Map<String, String> inputs = this.inputBuilder.buildInputs(event.getSource());
                    State newState = this.executeEvent(event, inputs);

                    if (this.getGraph().isNewState(newState)) {
                        this.getGraph().addStateAsCurrent(newState);
                        this.persistState(newState);
                    }

                    // Transition added regardless, as this is a multigraph
                    this.getGraph().addTransition(new Transition(current, newState, new ExecutedEvent(event, inputs)));

                } catch (UnsupportedEventTypeException| EventTriggerException e) {
                    logger.info("Tried to crawl invalid event with ID '{}' from {}", event.getIdentifier(), current.getIdentifier());
                } catch (OutOfBoundsException e) {
                    logger.info(e.getMessage());
                    this.browser.backOrClose();
                }

                if (this.getGraph().getCurrentState().isFinished()) {
                    boolean relocated = relocateFrom(this.getGraph().getCurrentState());
                    if (!relocated) {
                        break;
                    }
                }
            }
        } catch (UncrawlableStateException|BrowserException e) {
            logger.info("Crawl aborted due to exception.", e);
        } finally {
            this.handleClosing();
        }

        logger.debug("Crawl finished!");
        return this.getGraph();
    }

    private State executeEvent(Eventable event, Map<String, String> inputs) throws UnsupportedEventTypeException, OutOfBoundsException, EventTriggerException {
        this.browser.triggerEvent(event, inputs);
        State newState = this.browser.getCurrentBrowserState();

        if (SiteBoundChecker.isOutOfBounds(newState.getUri(), this.context)) {
            throw new OutOfBoundsException("Trying to access " + newState.getUri().toString());
        }

        return newState;
    }

    private void initCrawl(URI root) throws UncrawlableStateException {
        try {
            this.browser.navigateTo(root);
            State initial = this.browser.getCurrentBrowserState();
            this.getGraph().addStateAsCurrent(initial);
            this.getGraph().setRoot(initial);
            this.persistState(initial);
        } catch (BrowserException e) {
            logger.debug("Exception happened while trying to initialize EventFlowGraph. Error: {}", e.getMessage());
            throw new UncrawlableStateException(e);
        }
    }

    public boolean relocateFrom(State current) throws UncrawlableStateException {
        boolean relocated = false;

        State next = this.getGraph().findNearestUnfinishedStateFrom(this.getGraph().getRoot());
        if (next != null) {
            try {
                var path = this.getGraph().findPath(this.getGraph().getRoot(), next);
                this.walk(path);
                this.getGraph().setCurrentState(next);
                relocated = true;
            } catch (UncrawlablePathException e) {
                logger.info("Exception encountered while trying to walk from root.", e);
            }
        }

        return relocated;
    }

    public void walk(GraphPath<State, Transitionable> path) throws UncrawlablePathException {
        try {
            // Reset browser to site root
            this.browser.navigateTo(path.getStartVertex().getUri());

            // Walk to destination
            State previous = path.getStartVertex();
            var edges = path.getEdgeList();
            for (Transitionable edge : edges) {
                this.getGraph().setCurrentState(edge.getOrigin());
                this.browser.triggerEvent(edge.getExecutedEvent().getEvent(), edge.getExecutedEvent().getDataInputs());

                // Check a new state is reached after event execution
                State current = this.browser.getCurrentBrowserState();
                if (previous.equals(current)) {
                    throw new UncrawlablePathException("Stopped crawling at state " + current.toString());
                }

                previous = current;
            }
        } catch (BrowserException|UnsupportedEventTypeException|EventTriggerException|UncrawlableStateException e) {
            throw new UncrawlablePathException("Stopped path crawling due to unexpected exception.", e);
        }
    }

    private void persistState(State state) {
        //TODO: add state persistence
        try {
            if (this.context.isPrintScreen()) {
                ScreenShotSaver.saveScreenShot(this.browser.getScreenShotBytes(), state.getIdentifier(), this.context.getOutputDirName());
            }
        } catch (PersistenceException e) {
            logger.info(e.getMessage(), e);
        }

    }

    private void handleClosing() {
        try {
            logger.info("Trying to close browser sessions.");
            this.browser.quit();
        } catch (BrowserException e) {
            logger.info("Browser closing failed.", e);
        }
    }
}
