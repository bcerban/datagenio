package com.datagenio.storage;

import com.datagenio.crawler.api.EventFlowGraph;
import com.datagenio.crawler.api.Eventable;
import com.datagenio.crawler.api.State;
import com.datagenio.crawler.api.Transitionable;
import com.datagenio.crawler.model.EventFlowGraphImpl;
import com.datagenio.crawler.model.ExecutedEvent;
import com.datagenio.model.WebFlowGraphImpl;
import com.datagenio.model.api.WebFlowGraph;
import com.datagenio.context.Configuration;
import com.datagenio.model.api.WebState;
import com.datagenio.model.api.WebTransition;
import com.datagenio.model.exception.InvalidTransitionException;
import com.datagenio.storage.translator.*;
import com.datagenio.storageapi.*;
import com.datagenio.storage.connection.ConnectionResolver;
import org.neo4j.graphdb.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

public class Neo4JReadAdapter implements ReadAdapter {

    private static Logger logger = LoggerFactory.getLogger(Neo4JReadAdapter.class);

    private Connection connection;
    private GraphDatabaseService combinedGraph;
    private Translator<WebState, Node> webStateTranslator;
    private Translator<State, Node> eventStateTranslator;
    private Translator<WebTransition, Map<String, Object>> webTransitionTranslator;
    private Translator<Transitionable, Map<String, Object>> eventTransitionTranslator;
    private Translator<Eventable, Node> eventableTranslator;

    public Neo4JReadAdapter(Configuration configuration) {
        webStateTranslator = new WebStateTranslator();
        webTransitionTranslator = new WebTransitionTranslator();
        eventStateTranslator = new EventStateTranslator();
        eventableTranslator = new EventTranslator();
        eventTransitionTranslator = new EventTransitionTranslator();

        connection = ConnectionResolver.get(configuration);
        combinedGraph = connection.create(configuration.get(Configuration.SITE_ROOT_URI));
    }

    @Override
    public WebFlowGraph loadWebModel() {
        WebFlowGraph webModel = new WebFlowGraphImpl();

        try {
            Collection<Node> webNodes = connection.findNodes(combinedGraph, Label.label(Labels.WEB_STATE));
            webNodes.forEach(node -> {
                var state = webStateTranslator.translateFrom(node);
                webModel.addState(state);

                if (state.isRoot()) {
                    webModel.setRoot(state);
                }
            });

            Collection<Map<String, Object>> edges = connection.findEdges(combinedGraph, Relationships.WEB_TRANSITION);
            edges.forEach(edge -> {
                try {
                    var transition = webTransitionTranslator.translateFrom(edge);
                    transition.setOrigin(webModel.findStateById((String)edge.get("origin." + Properties.IDENTIFICATION)));
                    transition.setDestination(webModel.findStateById((String)edge.get("dest." + Properties.IDENTIFICATION)));
                    webModel.addTransition(transition);
                } catch (InvalidTransitionException e) {
                    logger.info("Failed to add transition in web model.", e);
                }
            });
        } catch (StorageException e) {
            logger.info("Failure while loading web graph.", e);
        }

        return webModel;
    }

    @Override
    public EventFlowGraph loadEventModel() {
        EventFlowGraph eventModel = new EventFlowGraphImpl();

        try {
            Collection<Node> eventStateNodes = connection.findNodes(combinedGraph, Label.label(Labels.EVENT_STATE));
            eventStateNodes.forEach(stateNode -> {
                var state = eventStateTranslator.translateFrom(stateNode);
                eventModel.addState(state);

                Collection<Eventable> unfiredEvents = findUnfiredEventsFor(state).stream()
                        .map(unfiredEventNode -> eventableTranslator.translateFrom(unfiredEventNode))
                        .collect(Collectors.toList());

                Collection<Eventable> firedEvents = findFiredEventsFor(state).stream()
                        .map(firedEventNode -> eventableTranslator.translateFrom(firedEventNode))
                        .collect(Collectors.toList());

                state.setUnfiredEventables(unfiredEvents);
                eventModel.addEvents(firedEvents);

                firedEvents.addAll(unfiredEvents);
                state.setEventables(firedEvents);

                if (state.isRoot()) {
                    eventModel.setRoot(state);
                }
            });

            Collection<Map<String, Object>> edges = getEventTransitionData();
            edges.forEach(edge -> {
                var transition = eventTransitionTranslator.translateFrom(edge);

                transition.setOrigin(eventModel.findById((String)edge.get("origin." + Properties.IDENTIFICATION)));
                transition.setOrigin(eventModel.findById((String)edge.get("destination." + Properties.IDENTIFICATION)));

                // TODO: add data inputs
                transition.setExecutedEvent(
                        new ExecutedEvent(eventModel.findEvent((String)edge.get("e." + Properties.IDENTIFICATION)))
                );
                eventModel.addTransition(transition);
            });
        } catch (StorageException e) {
            logger.info("Failure while loading web graph.", e);
        }

        return eventModel;
    }

    private Collection<Node> findUnfiredEventsFor(State state) {
        try {
            String query = String.format(
                    "MATCH (e:%s)-[:%a]-(s:%s {identifier: '%s'}) RETURN e",
                    Label.label(Labels.EVENT).toString(),
                    Relationships.NON_EXECUTED_EVENT.toString(),
                    Label.label(Labels.EVENT_STATE).toString(),
                    state.getIdentifier()
            );
            return connection.findNodes(combinedGraph, Label.label(Labels.EVENT), query);
        } catch (StorageException e) {
            return new ArrayList<>();
        }
    }

    private Collection<Node> findFiredEventsFor(State state) {
        try {
            String query = String.format(
                    "MATCH (e:%s)-[:%a]-(s:%s {identifier: '%s'}) RETURN e",
                    Label.label(Labels.EVENT).toString(),
                    Relationships.EXECUTED_EVENT.toString(),
                    Label.label(Labels.EVENT_STATE).toString(),
                    state.getIdentifier()
            );
            return connection.findNodes(combinedGraph, Label.label(Labels.EVENT), query);
        } catch (StorageException e) {
            return new ArrayList<>();
        }
    }

    private Collection<Map<String, Object>> getEventTransitionData() {
        String query = String.format(
                "MATCH (origin:%s)-[exec:%s]-(e:%s)-[leads:%s]->(dest:%s) RETURN origin.%s, dest.%s, e.%s, exec.%s, exec.%s",
                Label.label(Labels.EVENT_STATE).toString(),
                Relationships.EXECUTED_EVENT.toString(),
                Label.label(Labels.EVENT).toString(),
                Relationships.LEADS_TO.toString(),
                Label.label(Labels.EVENT_STATE).toString(),
                Properties.IDENTIFICATION,
                Properties.IDENTIFICATION,
                Properties.IDENTIFICATION,
                Properties.CONCRETE_REQUESTS,
                Properties.DATA_INPUTS
        );

        try {
            return connection.execute(combinedGraph, query);
        } catch (StorageException e) {
            return new ArrayList<>();
        }
    }
}
