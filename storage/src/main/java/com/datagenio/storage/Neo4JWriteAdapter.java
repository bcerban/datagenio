package com.datagenio.storage;

import com.datagenio.crawler.api.EventFlowGraph;
import com.datagenio.crawler.api.Eventable;
import com.datagenio.crawler.api.State;
import com.datagenio.crawler.api.Transitionable;
import com.datagenio.model.api.WebFlowGraph;
import com.datagenio.model.api.WebState;
import com.datagenio.model.api.WebTransition;
import com.datagenio.storage.api.*;
import com.datagenio.storage.exception.StorageException;
import com.google.gson.Gson;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class Neo4JWriteAdapter implements WriteAdapter {

    private static Logger logger = LoggerFactory.getLogger(Neo4JWriteAdapter.class);

    private Gson gson;
    private Connection connection;
    private Configuration configuration;
    private GraphDatabaseService eventGraphService;
    private GraphDatabaseService webFlowGraphService;

    public Neo4JWriteAdapter(Configuration configuration, Connection connection, Gson gson) {
        this.gson = gson;
        this.connection = connection;
        this.configuration = configuration;
        eventGraphService = connection.createEventGraph(configuration.get(Configuration.SITE_ROOT_URI));
        webFlowGraphService = connection.createWebFlowGraph(configuration.get(Configuration.SITE_ROOT_URI));
    }

    @Override
    public void save(WebFlowGraph graph) {
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
                connection.addNode(webFlowGraphService, Label.label(Labels.WEB_STATE), buildStateProperties(state));
            } catch (StorageException e) {
                logger.info("Failed to save state: " + e.getMessage(), e);
            }
        });
    }

    private void addEventStates(Collection<State> states) {
        states.forEach((state) -> {
            try {
                logger.info("Saving state {}.", state.getIdentifier());
                connection.addNode(eventGraphService, Label.label(Labels.EVENT_STATE), buildStateProperties(state));
            } catch (StorageException e) {
                logger.info("Failed to save state: " + e.getMessage(), e);
            }
        });
    }

    private Map<String, Object> buildStateProperties(WebState state) {
        Map<String, Object> properties = new HashMap<>();
        properties.put(Properties.IDENTIFICATION, state.getIdentifier());
        return properties;
    }

    private Map<String, Object> buildStateProperties(State state) {
        Map<String, Object> properties = new HashMap<>();
        properties.put(Properties.IDENTIFICATION, state.getIdentifier());
        properties.put(Properties.IS_ROOT, state.isRoot() ? "True" : "False");

        if (state.getScreenShot() != null) {
            properties.put(Properties.SCREEN_SHOT_PATH, state.getScreenShot().getAbsolutePath());
        }

        return properties;
    }

    private void addWebTransitions(Collection<WebTransition> transitions) {
        transitions.forEach((transition) -> {
            try {
                connection.addEdge(
                        webFlowGraphService,
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
                eventGraphService,
                findEventNode(transition.getOrigin()),
                through,
                Relationships.EXECUTED_EVENT,
                buildTransitionProperties(transition)
        );

        connection.addEdge(
                eventGraphService,
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

        Node node = connection.addNode(eventGraphService, Label.label(Labels.EVENT), eventProperties);
        if (!addAsJson) saveTransitionRequestsAsNodes(transition, node);

        return node;
    }

    private void saveTransitionRequestsAsNodes(Transitionable transition, Node from) {
        transition.getRequests().forEach(request -> {
            var properties = new HashMap<String, Object>();
            properties.put(Properties.REQUEST_JSON, gson.toJson(request));
            try {
                Node requestNode = connection.addNode(eventGraphService, Label.label(Labels.REQUEST), properties);
                connection.addEdge(eventGraphService, from, requestNode, Relationships.HAS_REQUEST, new HashMap<>());
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
        return connection.findNode(webFlowGraphService, Label.label(Labels.WEB_STATE), properties);
    }

    private Node findEventNode(State state) throws StorageException {
        Map<String, Object> properties = new HashMap<>();
        properties.put(Properties.IDENTIFICATION, state.getIdentifier());
        return connection.findNode(eventGraphService, Label.label(Labels.EVENT_STATE), properties);
    }

    private Map<String, Object> buildTransitionProperties(WebTransition transition) {
        Map<String, Object> properties = new HashMap<>();
//        properties.put(Properties.ABSTRACT_REQUESTS, transition.getAbstractRequests());
//        properties.put(Properties.CONCRETE_REQUESTS, transition.getConcreteRequests());
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
