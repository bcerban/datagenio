package com.datagenio.storage;

import com.datagenio.crawler.api.EventFlowGraph;
import com.datagenio.crawler.api.State;
import com.datagenio.crawler.api.Transitionable;
import com.datagenio.model.api.WebFlowGraph;
import com.datagenio.model.api.WebState;
import com.datagenio.model.api.WebTransition;
import com.datagenio.storage.api.Connection;
import com.datagenio.storage.api.WriteAdapter;
import com.datagenio.storage.connection.ConnectionResolver;
import com.datagenio.storage.exception.StorageException;
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

    public Neo4JWriteAdapter() {
        connection = ConnectionResolver.get();
    }

    @Override
    public void save(WebFlowGraph graph) {
        connection.connectToWebFlowGraph(graph.getRoot().getUrl().toString());
        addWebStates(graph.getStates());
        addWebTransitions(graph.getTransitions());
    }

    @Override
    public void save(EventFlowGraph graph) {
        connection.connectToEventGraph(graph.getRoot().getUri().toString());
        addEventStates(graph.getStates());
        addEventTransitions(graph.getTransitions());
    }

    private void addWebStates(Collection<WebState> states) {
        states.forEach((state) -> {
            try {
                connection.addNode(Label.label(Labels.WEB_STATE), buildStateProperties(state));
            } catch (StorageException e) {
                logger.info("Failed to save state: " + e.getMessage(), e);
            }
        });
    }

    private void addEventStates(Collection<State> states) {
        states.forEach((state) -> {
            try {
                connection.addNode(Label.label(Labels.EVENT_STATE), buildStateProperties(state));
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
            try {
                connection.addEdge(
                        findEventNode(transition.getOrigin()),
                        findEventNode(transition.getDestination()),
                        Relationships.EVENT_TRANSITION, buildTransitionProperties(transition)
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

    private Node findWebNode(WebState state) throws StorageException {
        Map<String, Object> properties = new HashMap<>();
        properties.put(Properties.IDENTIFICATION, state.getIdentifier());
        return connection.findWebNode(properties);
    }

    private Node findEventNode(State state) throws StorageException {
        Map<String, Object> properties = new HashMap<>();
        properties.put(Properties.IDENTIFICATION, state.getIdentifier());
        return connection.findEventNode(properties);
    }

    private Map<String, Object> buildTransitionProperties(WebTransition transition) {
        Map<String, Object> properties = new HashMap<>();
        properties.put(Properties.ABSTRACT_REQUESTS, transition.getAbstractRequests());
        properties.put(Properties.CONCRETE_REQUESTS, transition.getConcreteRequests());
        return properties;
    }

    private Map<String, Object> buildTransitionProperties(Transitionable transition) {
        Map<String, Object> properties = new HashMap<>();
        properties.put(Properties.EXECUTED_EVENT, transition.getExecutedEvent());
        properties.put(Properties.CONCRETE_REQUESTS, transition.getRequests());
        return properties;
    }

    @Override
    public void update(WebFlowGraph graph) {

    }

    @Override
    public void delete(WebFlowGraph graph) {

    }
}
