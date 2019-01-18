package com.datagenio.storage;

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
        connection.connectTo(graph.getRoot().getUrl().toString());
        addStates(graph.getStates());
        addTransitions(graph.getTransitions());
    }

    private void addStates(Collection<WebState> states) {
        states.forEach((state) -> {
            try {
                connection.addNode(Label.label(Labels.WEB_STATE), buildStateProperties(state));
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

    private void addTransitions(Collection<WebTransition> transitions) {
        transitions.forEach((transition) -> {
            try {
                Node from = connection.findStateNode(transition.getOrigin());
                Node to = connection.findStateNode(transition.getDestination());
                connection.addEdge(from, to, Relationships.WEB_TRANSITION, buildTransitionProperties(transition));
            } catch (StorageException e) {
                logger.info(
                        "Transition between {} and {} not added due to unexpected exception.",
                        transition.getOrigin().getIdentifier(),
                        transition.getDestination().getIdentifier()
                );
            }
        });
    }

    private Map<String, Object> buildTransitionProperties(WebTransition transition) {
        Map<String, Object> properties = new HashMap<>();
        properties.put(Properties.ABSTRACT_REQUESTS, transition.getAbstractRequests());
        properties.put(Properties.CONCRETE_REQUESTS, transition.getConcreteRequests());
        return properties;
    }

    @Override
    public void update(WebFlowGraph graph) {

    }

    @Override
    public void delete(WebFlowGraph graph) {

    }
}
