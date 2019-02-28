package com.datagenio.crawler;

import com.datagenio.context.Context;
import com.datagenio.context.DatagenioException;
import com.datagenio.context.EventInput;
import com.datagenio.crawler.api.*;
import com.datagenio.crawler.exception.*;
import com.datagenio.crawler.model.EventFlowGraphImpl;
import com.datagenio.crawler.model.ExecutedEvent;
import com.datagenio.crawler.model.Transition;
import com.datagenio.crawler.util.HtmlSaver;
import com.datagenio.crawler.util.ScreenShotSaver;
import com.datagenio.crawler.util.SiteBoundChecker;
import com.datagenio.databank.api.InputBuilder;
import com.datagenio.storageapi.ReadAdapter;
import org.jgrapht.GraphPath;
import org.openqa.selenium.WebDriverException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.*;

public class PersistentCrawler implements com.datagenio.crawler.api.Crawler {

    private static Logger logger = LoggerFactory.getLogger(PersistentCrawler.class);

    private Context context;
    private Browser browser;
    private EventFlowGraph graph;
    private InputBuilder inputBuilder;
    private ReadAdapter readAdapter;

    public PersistentCrawler(Context context, Browser browser, InputBuilder inputBuilder, ReadAdapter readAdapter) {
        this.context = context;
        this.browser = browser;
        this.inputBuilder = inputBuilder;
        this.readAdapter = readAdapter;

        if (context.isContinueExistingModel()) {
            graph = readAdapter.loadEventModel();
            updateGraphFromContext();
        }
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
        if (graph == null) graph = new EventFlowGraphImpl();
        return graph;
    }

    @Override
    public EventFlowGraph crawl() {
        try {
            logger.debug("Begin crawling {}...", context.getRootUrl());
            setUp();

            while (!getGraph().getCurrentState().isFinished()) {
                State current = getGraph().getCurrentState();
                Eventable event = current.getNextEventToFire();

                if (shouldSkip(event)) {
                    addSuspectedTransition(current, event);
                } else {
                    crawlState(current, event);
                }

                if (reachedMaxGraphSize()) break;

                if (getGraph().getCurrentState().isFinished()) {
                    boolean relocated = relocateFrom(getGraph().getCurrentState());
                    if (!relocated) break;
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
    }

    private void crawlState(State current, Eventable event) throws UncrawlableStateException, BrowserException {
        try {
            List<EventInput> inputs = event.requiresInput() ? inputBuilder.buildInputs(event) : new ArrayList<>();
            State newState = executeEvent(event, inputs);

            if (getGraph().isNewState(newState)) {
                getGraph().addStateAsCurrent(newState);
                saveStateScreenShot(newState);
                saveStateHtml(newState);
            } else {
                newState = getGraph().find(newState);
            }

            event.setStatus(Eventable.Status.SUCCEEDED);

            // Add transition only if new for state and event
            if (graph.findTransitions(event, newState).size() == 0) {
                var transition = new Transition(current, newState, new ExecutedEvent(event, inputs));
                transition.setRequests(getRequestsForEvent(event));
                transition.setStatus(Transitionable.Status.TRAVERSED);
                getGraph().addTransition(transition);
            }
        } catch (UnsupportedEventTypeException | EventTriggerException | WebDriverException e) {
            logger.info("Tried to crawl invalid event with ID '{}' from {}", event.getEventIdentifier(), current.getIdentifier());
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
        return getGraph().isRegistered(event) && !hasManualInputs(event);
    }

    private boolean hasManualInputs(Eventable eventable) {
        return context.getEventInputs().stream()
                .filter(i -> i.getEventId().equals(eventable.getId()))
                .count() > 0;
    }

    private void addSuspectedTransition(State state, Eventable event) {
        if (graph.findTransitions(event, state).size() > 0) return;

        try {
            // find event destination in graph and set tentative transition
            var transition = getGraph().findTransitions(event);
            var suspectedTransition = new Transition(state, transition.getDestination(), transition.getExecutedEvent());
            suspectedTransition.setStatus(Transitionable.Status.SUSPECTED);
            suspectedTransition.setRequests(transition.getRequests());
            getGraph().addTransition(suspectedTransition);
        } catch (InvalidTransitionException e) { }

    }

    private Collection<RemoteRequest> getRequestsForEvent(Eventable event) {
        // Save har
        return browser.getCapturedRequests(context.getRootUri(), event.getId(), context.getOutputDirName());
    }

    private State executeEvent(Eventable event, List<EventInput> inputs) throws UnsupportedEventTypeException, OutOfBoundsException, EventTriggerException {
        try {
            browser.triggerEvent(event, inputs);
            State newState = browser.getCurrentBrowserState();

            if (SiteBoundChecker.isOutOfBounds(newState.getUri(), context)) {
                throw new OutOfBoundsException("Trying to access " + newState.getUri().toString());
            }

            var stateEvents = new ArrayList<Eventable>();
            newState.getEventables().forEach(e -> {
                var graphEvent = graph.getEvent(e);
                if (graphEvent != null) {
                    stateEvents.add(graphEvent);
                } else {
                    stateEvents.add(e);
                }
            });
            newState.setEventables(stateEvents);
            newState.setUnfiredEventables(stateEvents);

            return newState;
        } catch (BrowserException e) {
            throw new EventTriggerException(e.getMessage(), e);
        }
    }

    private void setUp() throws UncrawlableStateException {
        if (context.isContinueExistingModel()) {
            boolean foundUnfinishedState = relocateFrom(getGraph().getRoot());
            if (!foundUnfinishedState) {
                throw new UncrawlableStateException("No unfinished states found in model.");
            }
        } else {
            initCrawl();
        }
    }

    private void initCrawl() throws UncrawlableStateException {
        try {
            var root = URI.create(context.getRootUrl());
            browser.navigateTo(root);
            State initial = browser.getCurrentBrowserState();
            getGraph().addStateAsCurrent(initial);
            getGraph().setRoot(initial);
            saveStateScreenShot(initial);
            saveStateHtml(initial);
        } catch (BrowserException| DatagenioException e) {
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
            browser.navigateTo(path.getStartVertex().getUri(), false);

            // Walk to destination
            State previous = path.getStartVertex();
            var edges = path.getEdgeList();
            for (Transitionable edge : edges) {
                getGraph().setCurrentState(edge.getOrigin());
                browser.triggerEvent(edge.getExecutedEvent().getEvent(), edge.getExecutedEvent().getDataInputs(), false);

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

    private void updateGraphFromContext() {
        context.getEventInputs().forEach(eventInput -> applyEventInput(eventInput));
    }

    private void applyEventInput(EventInput eventInput) {
        try {
            Eventable eventable = graph.findEvent(eventInput.getEventId());
            List<State> states = graph.getStates(eventable);
            states.forEach(s -> {
                s.markEventAsUnfired(eventable);
                List<Transitionable> transitions = graph.findTransitions(eventable, s);
                transitions.forEach(t -> graph.removeTransition(t));
            });
        } catch (Exception e) { }
    }

    private void saveStateScreenShot(State state) {
        if (context.isPrintScreen()) {
            var bytes = browser.getScreenShotBytes();

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        state.setScreenShot(ScreenShotSaver.saveScreenShot(bytes, state.getIdentifier(), context.getOutputDirName()));
                    } catch (PersistenceException e) {
                        logger.info(e.getMessage(), e);
                    }
                }
            }).start();
        }
    }

    private void saveStateHtml(State state) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    state.setDocumentFilePath(HtmlSaver.saveHtml(state.getDocument().toString(), state.getIdentifier(), context.getOutputDirName()));
                } catch (PersistenceException e) {
                    logger.info(e.getMessage(), e);
                }
            }
        }).start();
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
