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
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

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
        if (graph == null) {
            graph = new EventFlowGraphImpl();
        }
        return graph;
    }

    public static Logger getLogger() {
        return logger;
    }

    public EventFlowGraph crawl() {
        logger.debug("Begin crawling {}...", context.getRootUrl());

        try {
            initCrawl(URI.create(context.getRootUrl()));
            while (!getGraph().getCurrentState().isFinished()) {
                State current = getGraph().getCurrentState();
                Eventable event = current.getNextEventToFire();

                try {
                    Map<String, String> inputs = inputBuilder.buildInputs(event.getSource());
                    State newState = executeEvent(event, inputs);

                    if (getGraph().isNewState(newState)) {
                        getGraph().addStateAsCurrent(newState);
                        saveStateScreenShot(newState);
                    } else {
                        newState = getGraph().find(newState);
                    }

                    // Transition added regardless, as this is a multigraph
                    var transition = new Transition(current, newState, new ExecutedEvent(event, inputs));
                    transition.setRequests(getRequestsForEvent(event, newState));
                    getGraph().addTransition(transition);

                } catch (UnsupportedEventTypeException| EventTriggerException e) {
                    logger.info("Tried to crawl invalid event with ID '{}' from {}", event.getIdentifier(), current.getIdentifier());
                } catch (OutOfBoundsException e) {
                    logger.info(e.getMessage());
                    browser.backOrClose();
                }

                if (this.getGraph().getCurrentState().isFinished()) {
                    boolean relocated = relocateFrom(getGraph().getCurrentState());
                    if (!relocated) {
                        break;
                    }
                }
            }
        } catch (UncrawlableStateException|BrowserException e) {
            logger.info("Crawl aborted due to exception.", e);
        } finally {
            handleClosing();
        }

        logger.debug("Crawl finished!");
        return getGraph();
    }

    private Collection<RemoteRequest> getRequestsForEvent(Eventable event, State state) {
        // Save har
        String fileName = state.getIdentifier() + "-" + event.getIdentifier().replaceAll("/", ".");
        return browser.getCapturedRequests(context.getRootUri(), fileName, context.getOutputDirName());
    }

    private Collection<RemoteRequest> getRequestsForEvent(State state) {
        // Save har
        return browser.getCapturedRequests(context.getRootUri(), state.getIdentifier(), context.getOutputDirName());
    }

    private State executeEvent(Eventable event, Map<String, String> inputs) throws UnsupportedEventTypeException, OutOfBoundsException, EventTriggerException {
        browser.triggerEvent(event, inputs);
        State newState = browser.getCurrentBrowserState();

        if (SiteBoundChecker.isOutOfBounds(newState.getUri(), context)) {
            throw new OutOfBoundsException("Trying to access " + newState.getUri().toString());
        }

        return newState;
    }

    private void initCrawl(URI root) throws UncrawlableStateException {
        try {
            browser.navigateTo(root);
            State initial = browser.getCurrentBrowserState();
            getGraph().addStateAsCurrent(initial);
            getGraph().setRoot(initial);
            saveStateScreenShot(initial);

            // TODO: save to init event
            Collection<RemoteRequest> requests = getRequestsForEvent(initial);
            String requestString = requests.stream().map((r) -> r.toString()).collect(Collectors.joining("\n"));
            logger.info("Requests that should be saved to init event: {}", requestString);
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
            browser.navigateTo(path.getStartVertex().getUri());

            // Walk to destination
            State previous = path.getStartVertex();
            var edges = path.getEdgeList();
            for (Transitionable edge : edges) {
                getGraph().setCurrentState(edge.getOrigin());
                browser.triggerEvent(edge.getExecutedEvent().getEvent(), edge.getExecutedEvent().getDataInputs());

                // Check a new state is reached after event execution
                State current = browser.getCurrentBrowserState();
                if (previous.equals(current)) {
                    throw new UncrawlablePathException("Stopped crawling at state " + current.toString());
                }

                previous = current;
            }
        } catch (BrowserException|UnsupportedEventTypeException|EventTriggerException|UncrawlableStateException e) {
            throw new UncrawlablePathException("Stopped path crawling due to unexpected exception.", e);
        }
    }

    private void saveStateScreenShot(State state) {
        try {
            if (context.isPrintScreen()) {
                state.setScreenShot(
                    ScreenShotSaver.saveScreenShot(
                            browser.getScreenShotBytes(),
                            state.getIdentifier(),
                            context.getOutputDirName()
                    )
                );
            }
        } catch (PersistenceException e) {
            logger.info(e.getMessage(), e);
        }
    }

    private void handleClosing() {
        try {
            logger.info("Trying to close browser sessions.");
            browser.quit();
        } catch (BrowserException e) {
            logger.info("Browser closing failed.", e);
        }
    }
}
