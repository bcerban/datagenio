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

    private Connection connection;
    private GraphDatabaseService eventGraphService;
    private GraphDatabaseService webFlowGraphService;

    public Neo4JWriteAdapter(Configuration configuration, Connection connection) {
        this.connection = connection;
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
                Node through = connection.addNode(
                        eventGraphService,
                        Label.label(Labels.EVENT),
                        buildEventProperties(transition.getExecutedEvent().getEvent())
                );

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
            } catch (StorageException e) {
                logger.info(
                        "Transition between {} and {} not added due to unexpected exception.",
                        transition.getOrigin().getIdentifier(),
                        transition.getDestination().getIdentifier(), e
                );
            }
        });
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
//        properties.put(Properties.EXECUTED_EVENT, transition.getExecutedEvent());
//        properties.put(Properties.CONCRETE_REQUESTS, transition.getRequests());
        return properties;
    }

    private Map<String, Object> buildEventProperties(Eventable eventable) {
        Map<String, Object> properties = new HashMap<>();
        properties.put(Properties.IDENTIFICATION, eventable.getIdentifier());
        properties.put(Properties.XPATH, eventable.getXpath());
        properties.put(Properties.EVENT_TYPE, eventable.getEventType().toString());
        properties.put(Properties.ELEMENT, eventable.getSource().toString());
        return properties;
    }
}
