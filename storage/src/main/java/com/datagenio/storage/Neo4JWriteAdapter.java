package com.datagenio.storage;

import com.datagenio.context.Configuration;
import com.datagenio.crawler.api.EventFlowGraph;
import com.datagenio.crawler.api.Eventable;
import com.datagenio.crawler.api.State;
import com.datagenio.crawler.api.Transitionable;
import com.datagenio.model.WebFlowGraph;
import com.datagenio.model.WebState;
import com.datagenio.model.WebTransition;
import com.datagenio.storage.translator.*;
import com.datagenio.storageapi.*;
import com.datagenio.storageapi.StorageException;
import com.google.gson.Gson;
import org.neo4j.graphdb.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Neo4JWriteAdapter implements WriteAdapter {

    private static Logger logger = LoggerFactory.getLogger(Neo4JWriteAdapter.class);

    private Gson gson;
    private Connection connection;
    private Configuration configuration;
    private GraphDatabaseService combinedGraph;

    private Translator<Eventable, Map<String, Object>> eventableTranslator;
    private Translator<State, Map<String, Object>> eventStateTranslator;
    private Translator<Transitionable, Map<String, Object>> eventTransitionTranslator;
    private Translator<WebState, Map<String, Object>> webStateTranslator;
    private Translator<WebTransition, Map<String, Object>> webTransitionTranslator;

    public Neo4JWriteAdapter(Configuration configuration, Connection connection, Gson gson) {
        this.gson = gson;
        this.connection = connection;
        this.configuration = configuration;
        combinedGraph = connection.create(configuration.getRootUrl());

        eventableTranslator = new EventTranslator();
        eventStateTranslator = new EventStateTranslator();
        eventTransitionTranslator = new EventTransitionTranslator();
        webStateTranslator = new WebStateTranslator();
        webTransitionTranslator = new WebTransitionTranslator();
    }

    @Override
    public void saveCombined(EventFlowGraph eventFlowGraph, WebFlowGraph webFlowGraph) {
        save(eventFlowGraph);
        save(webFlowGraph);
    }

    @Override
    public void save(WebFlowGraph graph) {
        logger.info("Attempting to save {} states and {} transitions.", graph.getStates().size(), graph.getTransitions().size());
        saveWebStates(graph.getStates());
        addWebTransitions(graph.getTransitions());
    }

    @Override
    public void save(EventFlowGraph graph) {
        logger.info("Attempting to save {} states and {} transitions.", graph.getStates().size(), graph.getTransitions().size());
        saveEventStates(graph.getStates());
        addEventTransitions(graph.getTransitions());
    }

    private void saveWebStates(Collection<WebState> states) {
        states.forEach((state) -> {
            try {
                logger.info("Saving state {}.", state.getIdentifier());

                Node stateNode = addOrUpdate(state);
                connectWebStateToChildNodes(state, stateNode);
            } catch (StorageException e) {
                logger.info("Failed to save state: " + e.getMessage(), e);
            }
        });
    }

    private void connectWebStateToChildNodes(WebState state, Node webStateNode) throws StorageException {
        String query = String.format(
                "MATCH (n:%s) WHERE n.identifier IN %s RETURN n",
                Label.label(Labels.EVENT_STATE).toString(), gson.toJson(state.getExternalIds())
        );
        var nodes = connection.findNodes(combinedGraph, Label.label(Labels.EVENT_STATE), query);

        nodes.forEach(node -> {
            try {
                connection.addEdge(combinedGraph, webStateNode, node, Relationships.ABSTRACTED, new HashMap<>());
            } catch (StorageException e) {
                logger.info("Failed to add child transition for state {}.", state.getIdentifier());
            }
        });
    }

    private void saveEventStates(Collection<State> states) {
        states.forEach((state) -> {
            try {
                logger.info("Saving state {}.", state.getIdentifier());

                Node stateNode = addOrUpdate(state);

                /**
                 * Unfired event should be deleted and re-added, since:
                 * 1. It is faster than searching for each one
                 * 2. It is more efficient than looking for the ones that were executed and deleting those
                 */
                deleteUnfiredEvents(state);
                state.getUnfiredEventables().forEach(event -> {

                    Node eventNode = addEventNode(event);
                    if (eventNode != null) {
                        try {
                            connection.addEdge(combinedGraph, stateNode, eventNode, Relationships.NON_EXECUTED_EVENT, new HashMap<>());
                        } catch (StorageException e) {
                            logger.info("Failed to save unfired event transition from {}: {}", state.getIdentifier(), e.getMessage(), e);
                        }
                    }
                });
            } catch (StorageException e) {
                logger.info("Failed to save state: " + e.getMessage(), e);
            }
        });
    }

    private Node addOrUpdate(State state) throws StorageException {
        var properties = buildStateProperties(state);
        Node stateNode;

        try {
            stateNode = connection.findNode(combinedGraph, Label.label(Labels.EVENT_STATE), Map.of(Properties.IDENTIFICATION, state.getIdentifier()));
            // update properties...
            String sets = properties.keySet().stream()
                    .map(prop -> "s." + prop + " = \"" + properties.get(prop).toString().replaceAll("\"", "'") + "\"")
                    .collect(Collectors.joining(", "));

            String query = String.format(
                    "MATCH (s:%s {%s: \"%s\"}) SET %s RETURN s.%s",
                    Labels.EVENT_STATE,
                    Properties.IDENTIFICATION,
                    state.getIdentifier(),
                    sets,
                    Properties.IDENTIFICATION
            );
            connection.execute(combinedGraph, query);
        } catch (StorageException e) {
            stateNode = connection.addNode(combinedGraph, Label.label(Labels.EVENT_STATE), properties);
        }

        return stateNode;
    }

    private Node addOrUpdate(WebState state) throws StorageException {
        var properties = buildStateProperties(state);
        Node stateNode;

        try {
            stateNode = connection.findNode(combinedGraph, Label.label(Labels.WEB_STATE), Map.of(Properties.IDENTIFICATION, state.getIdentifier()));
            // update properties...
            String sets = properties.keySet().stream()
                    .map(prop -> "s." + prop + " = \"" + properties.get(prop).toString().replaceAll("\"", "'") + "\"")
                    .collect(Collectors.joining(", "));

            String query = String.format(
                    "MATCH (s:%s {%s: \"%s\"}) SET %s RETURN s.%s",
                    Labels.WEB_STATE,
                    Properties.IDENTIFICATION,
                    state.getIdentifier(),
                    sets,
                    Properties.IDENTIFICATION
            );
            connection.execute(combinedGraph, query);
        } catch (StorageException e) {
            stateNode = connection.addNode(combinedGraph, Label.label(Labels.WEB_STATE), properties);
        }

        return stateNode;
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
                    transition.getExecutedEvent().getEvent().getId()
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
                findEventStateNode(transition.getOrigin()),
                through,
                Relationships.EXECUTED_EVENT,
                buildTransitionProperties(transition)
        );

        connection.addEdge(
                combinedGraph,
                through,
                findEventStateNode(transition.getDestination()),
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

    private Node addEventNode(Eventable event) {
        Node eventNode = null;
        try {
            eventNode = connection.addNode(combinedGraph, Label.label(Labels.EVENT), buildEventProperties(event));
        } catch (StorageException e) {
            logger.info("Failed to save event: " + e.getMessage(), e);
        }

        return eventNode;
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
        return configuration.getRequestSaveMode().equals(Configuration.REQUEST_SAVE_AS_JSON);
    }

    private void deleteUnfiredEvents(State state) {
        String query = String.format(
                "MATCH (p:%s {%s: \"%s\"})-[r:%s]->(e:%s) DETACH DELETE e",
                Labels.EVENT_STATE,
                Properties.IDENTIFICATION,
                state.getIdentifier(),
                Relationships.NON_EXECUTED_EVENT,
                Labels.EVENT
        );

        try {
            connection.execute(combinedGraph, query);
        } catch (StorageException e) {
            logger.info("Events for state {} could not be deleted.", state.getIdentifier(), e);
        }
    }

    private Node findWebNode(WebState state) throws StorageException {
        Map<String, Object> properties = new HashMap<>();
        properties.put(Properties.IDENTIFICATION, state.getIdentifier());
        return connection.findNode(combinedGraph, Label.label(Labels.WEB_STATE), properties);
    }

    private Node findEventStateNode(State state) throws StorageException {
        Map<String, Object> properties = new HashMap<>();
        properties.put(Properties.IDENTIFICATION, state.getIdentifier());
        return connection.findNode(combinedGraph, Label.label(Labels.EVENT_STATE), properties);
    }

    private Node findEventNode(Eventable event) throws StorageException {
        Map<String, Object> properties = new HashMap<>();
        properties.put(Properties.IDENTIFICATION, event.getId());
        return connection.findNode(combinedGraph, Label.label(Labels.EVENT), properties);
    }

    private Map<String, Object> buildStateProperties(WebState state) {
        return webStateTranslator.buildProperties(state);
    }

    private Map<String, Object> buildStateProperties(State state) {
        return eventStateTranslator.buildProperties(state);
    }

    private Map<String, Object> buildTransitionProperties(WebTransition transition) {
        return webTransitionTranslator.buildProperties(transition);
    }

    private Map<String, Object> buildTransitionProperties(Transitionable transition) {
        return eventTransitionTranslator.buildProperties(transition);
    }

    private Map<String, Object> buildEventProperties(Eventable eventable) {
        return eventableTranslator.buildProperties(eventable);
    }
}
