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

    private Context context;
    private Browser browser;
    private EventFlowGraph graph;
    private InputBuilder inputBuilder;

    public SimpleCrawler(Context context, Browser browser, InputBuilder inputBuilder) {
        this.context = context;
        this.browser = browser;
        this.inputBuilder = inputBuilder;
        graph = new EventFlowGraphImpl();
    }

    public Context getContext() {
        return context;
    }

    public Browser getBrowser() {
        return browser;
    }

    public void setBrowser(Browser browser) {
        this.browser = browser;
    }

    public static Logger getLogger() {
        return logger;
    }

    @Override
    public EventFlowGraph getGraph() {
        return graph;
    }

    @Override
    public EventFlowGraph crawl() {
        logger.debug("Begin crawling {}...", context.getRootUrl());

        try {
            initCrawl(URI.create(context.getRootUrl()));

            while (!getGraph().getCurrentState().isFinished()) {
                State current = getGraph().getCurrentState();
                Eventable event = current.getNextEventToFire();

                if (shouldSkip(event)) {
                    addSuspectedTransition(current, event);
                } else {
                    crawlState(current, event);
                }

                if (reachedMaxGraphSize()) {
                    break;
                }

                if (getGraph().getCurrentState().isFinished()) {
                    boolean relocated = relocateFrom(getGraph().getCurrentState());
                    if (!relocated) {
                        break;
                    }
                }
            }
        } catch (Exception e) {
            logger.info("Crawl aborted due to exception.", e);
        } finally {
            handleClosing();
        }

        logger.debug("Crawl finished!");
        return getGraph();
    }

    private boolean reachedMaxGraphSize() {
        if (context.getCrawlDepth() == Context.NO_MAX_DEPTH) return false;

        // TODO: this is temporary. Graph diameter check should take into account disconnected components
        return getGraph().getStates().size() >= context.getCrawlDepth();
//        return getGraph().getGraphDiameter() >= context.getCrawlDepth();
    }

    private void crawlState(State current, Eventable event) throws UncrawlableStateException, BrowserException {
        try {
            Map<String, String> inputs = inputBuilder.buildInputs(event.getSource());
            State newState = executeEvent(event, inputs);

            if (getGraph().isNewState(newState)) {
                getGraph().addStateAsCurrent(newState);
                saveStateScreenShot(newState);
            } else {
                newState = getGraph().find(newState);
            }

            event.setStatus(Eventable.Status.SUCCEEDED);

            // Transition added regardless, as this is a multigraph
            var transition = new Transition(current, newState, new ExecutedEvent(event, inputs));
            transition.setRequests(getRequestsForEvent(event, newState));
            transition.setStatus(Transitionable.Status.TRAVERSED);
            getGraph().addTransition(transition);

        } catch (UnsupportedEventTypeException | EventTriggerException e) {
            logger.info("Tried to crawl invalid event with ID '{}' from {}", event.getIdentifier(), current.getIdentifier());
            event.setStatus(Eventable.Status.FAILED);
            event.setReasonForFailure(e.getMessage());
        } catch (OutOfBoundsException e) {
            logger.info(e.getMessage());
            event.setStatus(Eventable.Status.FAILED);
            event.setReasonForFailure(e.getMessage());
            browser.backOrClose();
        }
    }

    private boolean shouldSkip(Eventable event) {
        // Skip navigation events if they have already been executed from some other state
        return getGraph().isRegistered(event);
    }

    private void addSuspectedTransition(State state, Eventable event) {
        try {
            // find event destination in graph and set tentative transition
            var transition = getGraph().findTransition(event);
            var suspectedTransition = new Transition(state, transition.getDestination(), transition.getExecutedEvent());
            suspectedTransition.setStatus(Transitionable.Status.SUSPECTED);
            suspectedTransition.setRequests(transition.getRequests());
            getGraph().addTransition(suspectedTransition);
        } catch (InvalidTransitionException e) { }

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
        try {
            browser.triggerEvent(event, inputs);
            State newState = browser.getCurrentBrowserState();

            if (SiteBoundChecker.isOutOfBounds(newState.getUri(), context)) {
                throw new OutOfBoundsException("Trying to access " + newState.getUri().toString());
            }

            return newState;
        } catch (BrowserException e) {
            throw new EventTriggerException(e.getMessage(), e);
        }
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

        State next = getGraph().findNearestUnfinishedStateFrom(getGraph().getRoot());
        if (next != null) {
            try {
                var path = getGraph().findPath(getGraph().getRoot(), next);
                walk(path);
                getGraph().setCurrentState(next);
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
