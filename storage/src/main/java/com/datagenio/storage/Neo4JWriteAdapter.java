package com.datagenio.storage;

import com.datagenio.context.Configuration;
import com.datagenio.crawler.api.EventFlowGraph;
import com.datagenio.crawler.api.Eventable;
import com.datagenio.crawler.api.State;
import com.datagenio.crawler.api.Transitionable;
import com.datagenio.storageapi.*;
import com.datagenio.model.api.WebFlowGraph;
import com.datagenio.model.api.WebState;
import com.datagenio.model.api.WebTransition;
import com.datagenio.storageapi.StorageException;
import com.google.gson.Gson;
import org.neo4j.graphdb.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class Neo4JWriteAdapter implements WriteAdapter {

    private static Logger logger = LoggerFactory.getLogger(Neo4JWriteAdapter.class);

    private Gson gson;
    private Connection connection;
    private Configuration configuration;
    private GraphDatabaseService combinedGraph;

    public Neo4JWriteAdapter(Configuration configuration, Connection connection, Gson gson) {
        this.gson = gson;
        this.connection = connection;
        this.configuration = configuration;
        combinedGraph = connection.create(configuration.get(Configuration.SITE_ROOT_URI));
    }

    @Override
    public void saveCombined(EventFlowGraph eventFlowGraph, WebFlowGraph webFlowGraph) {
        save(eventFlowGraph);
        save(webFlowGraph);
    }

    @Override
    public void save(WebFlowGraph graph) {
        logger.info("Attempting to save {} states and {} transitions.", graph.getStates().size(), graph.getTransitions().size());
        addWebStates(graph.getStates());
        addWebTransitions(graph.getTransitions());
    }

    @Override
    public void save(EventFlowGraph graph) {
        logger.info("Attempting to save {} states and {} transitions.", graph.getStates().size(), graph.getTransitions().size());
        addEventStates(graph.getStates());
        addEventTransitions(graph.getTransitions());
    }

    private void addWebStates(Collection<WebState> states) {
        states.forEach((state) -> {
            try {
                Node stateNode = connection.addNode(combinedGraph, Label.label(Labels.WEB_STATE), buildStateProperties(state));
                connectWebStateToChildNodes(state, stateNode);
            } catch (StorageException e) {
                logger.info("Failed to save state: " + e.getMessage(), e);
            }
        });
    }

    private void connectWebStateToChildNodes(WebState state, Node webStateNode) throws StorageException {
        String query = String.format("n.identifier IN %s", gson.toJson(state.getExternalIds()));
        var nodes = connection.findNodes(combinedGraph, Label.label(Labels.EVENT_STATE), query);

        nodes.forEach(node -> {
            try {
                connection.addEdge(combinedGraph, webStateNode, node, Relationships.ABSTRACTED, new HashMap<>());
            } catch (StorageException e) {
                logger.info("Failed to add child transition for state {}.", state.getIdentifier());
            }
        });
    }

    private void addEventStates(Collection<State> states) {
        states.forEach((state) -> {
            try {
                logger.info("Saving state {}.", state.getIdentifier());
                connection.addNode(combinedGraph, Label.label(Labels.EVENT_STATE), buildStateProperties(state));
            } catch (StorageException e) {
                logger.info("Failed to save state: " + e.getMessage(), e);
            }
        });
    }

    private Map<String, Object> buildStateProperties(WebState state) {
        Map<String, Object> properties = new HashMap<>();
        properties.put(Properties.IDENTIFICATION, state.getIdentifier());
        properties.put(Properties.URL, state.getUrl().toString());
        properties.put(Properties.IS_ROOT, state.isRoot() ? "True" : "False");
        properties.put(Properties.EXTERNAL_IDS, gson.toJson(state.getExternalIds()));
        properties.put(Properties.ABSTRACT_REQUESTS, gson.toJson(state.getRequests()));

        var screenShotFiles = state.getScreenShots()
                .stream()
                .map(file -> file.getAbsolutePath()).collect(Collectors.toList());

        properties.put(Properties.SCREEN_SHOTS, gson.toJson(screenShotFiles));
        return properties;
    }

    private Map<String, Object> buildStateProperties(State state) {
        Map<String, Object> properties = new HashMap<>();
        properties.put(Properties.IDENTIFICATION, state.getIdentifier());
        properties.put(Properties.IS_ROOT, state.isRoot() ? "True" : "False");
        properties.put(Properties.URL, state.getUri().toString());
        properties.put(Properties.STATUS, state.isFinished() ? Properties.FINISHED : Properties.UNFINISHED);

        if (state.hasScreenShot()) {
            properties.put(Properties.SCREEN_SHOT_PATH, state.getScreenShot().getAbsolutePath());
        }

        return properties;
    }

    private void addWebTransitions(Collection<WebTransition> transitions) {
        transitions.forEach((transition) -> {
            logger.info(
                    "Adding transition between {} and {}.",
                    transition.getOrigin().getIdentifier(),
                    transition.getDestination().getIdentifier()
            );

            try {
                connection.addEdge(
                        combinedGraph,
                        findWebNode(transition.getOrigin()),
                        findWebNode(transition.getDestination()),
                        Relationships.WEB_TRANSITION, buildTransitionProperties(transition)
                );
            } catch (StorageException e) {
                logger.info(
                        "Transition between {} and {} not added due to unexpected exception.",
                        transition.getOrigin().getIdentifier(),
                        transition.getDestination().getIdentifier()
                );
            }
        });
    }

    private void addEventTransitions(Collection<Transitionable> transitions) {
        transitions.forEach((transition) -> {
            logger.info(
                    "Adding transition between {} and {} via {}.",
                    transition.getOrigin().getIdentifier(),
                    transition.getDestination().getIdentifier(),
                    transition.getExecutedEvent().getEvent().getIdentifier()
            );

            try {
                addEventTransitionWithRequests(transition);
            } catch (StorageException e) {
                logger.info(
                        "Transition between {} and {} not added due to unexpected exception.",
                        transition.getOrigin().getIdentifier(),
                        transition.getDestination().getIdentifier(), e
                );
            }
        });
    }

    private void addEventTransitionWithRequests(Transitionable transition) throws StorageException {

        Node through = addEventTransitionNode(transition);

        connection.addEdge(
                combinedGraph,
                findEventNode(transition.getOrigin()),
                through,
                Relationships.EXECUTED_EVENT,
                buildTransitionProperties(transition)
        );

        connection.addEdge(
                combinedGraph,
                through,
                findEventNode(transition.getDestination()),
                Relationships.LEADS_TO,
                new HashMap<>()
        );
    }

    private Node addEventTransitionNode(Transitionable transition) throws StorageException {
        var addAsJson = addEventNodeAsJson();
        var eventProperties = buildEventProperties(transition.getExecutedEvent().getEvent());
        if (addAsJson) eventProperties.put(Properties.CONCRETE_REQUESTS, gson.toJson(transition.getRequests()));

        Node node = connection.addNode(combinedGraph, Label.label(Labels.EVENT), eventProperties);
        if (!addAsJson) saveTransitionRequestsAsNodes(transition, node);

        return node;
    }

    private void saveTransitionRequestsAsNodes(Transitionable transition, Node from) {
        transition.getRequests().forEach(request -> {
            var properties = new HashMap<String, Object>();
            properties.put(Properties.REQUEST_JSON, gson.toJson(request));
            try {
                Node requestNode = connection.addNode(combinedGraph, Label.label(Labels.REQUEST), properties);
                connection.addEdge(combinedGraph, from, requestNode, Relationships.HAS_REQUEST, new HashMap<>());

            } catch (StorageException e) {
                logger.info("Failed to add event request or edge.", e);
            }
        });
    }

    private boolean addEventNodeAsJson() {
        return configuration.get(Configuration.REQUEST_SAVE_MODE).equals(Configuration.REQUEST_SAVE_AS_JSON);
    }

    private Node findWebNode(WebState state) throws StorageException {
        Map<String, Object> properties = new HashMap<>();
        properties.put(Properties.IDENTIFICATION, state.getIdentifier());
        return connection.findNode(combinedGraph, Label.label(Labels.WEB_STATE), properties);
    }

    private Node findEventNode(State state) throws StorageException {
        Map<String, Object> properties = new HashMap<>();
        properties.put(Properties.IDENTIFICATION, state.getIdentifier());
        return connection.findNode(combinedGraph, Label.label(Labels.EVENT_STATE), properties);
    }

    private Map<String, Object> buildTransitionProperties(WebTransition transition) {
        Map<String, Object> properties = new HashMap<>();
        properties.put(Properties.ABSTRACT_REQUESTS, gson.toJson(transition.getAbstractRequests()));
        return properties;
    }

    private Map<String, Object> buildTransitionProperties(Transitionable transition) {
        Map<String, Object> properties = new HashMap<>();
        properties.put(Properties.EXECUTED_EVENT_ID, transition.getExecutedEvent().getEvent().getIdentifier());
        properties.put(Properties.STATUS, transition.getStatus().toString());
        return properties;
    }

    private Map<String, Object> buildEventProperties(Eventable eventable) {
        Map<String, Object> properties = new HashMap<>();
        properties.put(Properties.IDENTIFICATION, eventable.getIdentifier());
        properties.put(Properties.XPATH, eventable.getXpath());
        properties.put(Properties.EVENT_TYPE, eventable.getEventType().toString());
        properties.put(Properties.ELEMENT, eventable.getSource().toString());
        properties.put(Properties.STATUS, eventable.getStatus().toString());

        if (eventable.getStatus().equals(Eventable.Status.FAILED)) {
            properties.put(Properties.REASON_FOR_FAILRE, eventable.getReasonForFailure());
        }

        return properties;
    }
}
