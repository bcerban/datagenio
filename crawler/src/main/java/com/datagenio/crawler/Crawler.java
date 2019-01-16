package com.datagenio.crawler;

import com.datagenio.crawler.api.*;
import com.datagenio.crawler.exception.BrowserException;
import com.datagenio.crawler.exception.UncrawlablePathException;
import com.datagenio.crawler.exception.UncrawlableStateException;
import com.datagenio.crawler.exception.UnsupportedEventTypeException;
import com.datagenio.crawler.model.EventFlowGraphImpl;
import com.datagenio.crawler.model.ExecutedEvent;
import com.datagenio.crawler.model.StateImpl;
import com.datagenio.crawler.model.Transition;
import com.datagenio.crawler.util.EventExtractorFactory;
import com.datagenio.databank.api.InputBuilder;
import org.jgrapht.GraphPath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.Map;

public class Crawler {

    private static Logger logger = LoggerFactory.getLogger(Crawler.class);

    private CrawlContext context;
    private Browser browser;
    private EventableExtractor extractor;
    private EventFlowGraph graph;
    private InputBuilder inputBuilder;

    public Crawler(CrawlContext context, Browser browser, InputBuilder inputBuilder) {
        this.context = context;
        this.browser = browser;
        this.extractor = EventExtractorFactory.get();
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

    public EventFlowGraph crawl(String rootUrl) {
        logger.debug("Begin crawling {}...", rootUrl);

        try {
            this.initCrawl(URI.create(rootUrl));
            while (!this.getGraph().getCurrentState().isFinished()) {
                State current = this.getGraph().getCurrentState();
                Eventable event = current.getNextEventToFire();

                try {
                    Map<String, String> inputs = this.inputBuilder.buildInputs(event.getSource());
                    this.browser.triggerEvent(event, inputs);
                    State newState = this.browser.getCurrentBrowserState();

                    if (this.getGraph().isNewState(newState)) {
                        this.getGraph().addStateAsCurrent(newState);
                        this.getGraph().addTransition(new Transition(current, newState, new ExecutedEvent(event, inputs)));
                    }
                } catch (UnsupportedEventTypeException e) {
                    logger.info("Removing event from crawl because its type is invalid. Event ID: {}", event.getIdentifier());
                    this.getGraph().getCurrentState().getEventables().remove(event);
                }

                if (this.getGraph().getCurrentState().isFinished()) {
                    boolean relocated = relocateFrom(this.getGraph().getCurrentState());
                    if (!relocated) {
                        break;
                    }
                }
            }
        } catch (UncrawlableStateException e) { }

        logger.debug("Crawl finished!");
        return this.getGraph();
    }

    private void initCrawl(URI root) throws UncrawlableStateException {
        try {
            this.browser.navigateTo(root);
            State initial = new StateImpl(root, this.browser.getDOM(), this.extractor);
            this.getGraph().addStateAsCurrent(initial);
            this.getGraph().setRoot(initial);
        } catch (BrowserException e) {
            logger.debug("Exception happened while trying to initialize EventFlowGraph. Error: {}", e.getMessage());
            throw new UncrawlableStateException(e);
        }
    }

    public boolean relocateFrom(State current) throws UncrawlableStateException {
        boolean relocated = false;

        State next = this.getGraph().findNearestUnfinishedStateFrom(current);
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
        } catch (BrowserException|UnsupportedEventTypeException e) {
            throw new UncrawlablePathException("Stopped path crawling due to unexpected exception.", e);
        }
    }
}
