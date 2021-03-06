package com.datagenio.storage;

import com.datagenio.crawler.api.EventFlowGraph;
import com.datagenio.crawler.api.Eventable;
import com.datagenio.crawler.api.State;
import com.datagenio.crawler.api.Transitionable;
import com.datagenio.crawler.model.EventFlowGraphImpl;
import com.datagenio.crawler.model.ExecutedEvent;
import com.datagenio.databank.util.XPathParser;
import com.datagenio.model.WebFlowGraph;
import com.datagenio.model.WebState;
import com.datagenio.model.WebTransition;
import com.datagenio.context.Configuration;
import com.datagenio.model.exception.InvalidTransitionException;
import com.datagenio.storage.translator.*;
import com.datagenio.storageapi.*;
import org.neo4j.graphdb.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

public class Neo4JReadAdapter implements ReadAdapter {

    private static Logger logger = LoggerFactory.getLogger(Neo4JReadAdapter.class);

    private Connection connection;
    private GraphDatabaseService combinedGraph;
    private Translator<WebState, Map<String, Object>> webStateTranslator;
    private Translator<State, Map<String, Object>> eventStateTranslator;
    private Translator<WebTransition, Map<String, Object>> webTransitionTranslator;
    private Translator<Transitionable, Map<String, Object>> eventTransitionTranslator;
    private Translator<Eventable, Map<String, Object>> eventableTranslator;

    public Neo4JReadAdapter(Configuration configuration, Connection connection) {
        webStateTranslator = new WebStateTranslator();
        webTransitionTranslator = new WebTransitionTranslator();
        eventStateTranslator = new EventStateTranslator();
        eventableTranslator = new EventTranslator();
        eventTransitionTranslator = new EventTransitionTranslator();

        this.connection = connection;
        combinedGraph = this.connection.create(configuration.getRootUrl());
    }

    @Override
    public WebFlowGraph loadWebModel() {
        var webModel = new WebFlowGraph();

        try {
            Collection<Map<String, Object>> webNodes = connection.findNodesAsMap(combinedGraph, Label.label(Labels.WEB_STATE));
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
                    var transition = webTransitionTranslator.translateFrom((Map<String, Object>)edge.get("properties(rel)"));
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
            Collection<Map<String, Object>> eventStateNodes = connection.findNodesAsMap(combinedGraph, Label.label(Labels.EVENT_STATE));
            eventStateNodes.forEach(stateNode -> {
                var state = eventStateTranslator.translateFrom(stateNode);

                Collection<Eventable> unfiredEvents = findUnfiredEventsFor(state).stream()
                        .map(unfiredEventNode -> {
                            var translatedEvent = eventableTranslator.translateFrom(unfiredEventNode);

                            // The source has to be updated because later xpath lookup will use source structure
                            try {
                                translatedEvent.setSource(XPathParser.getChildByXpath(state.getDocument(), translatedEvent.getXpath()));
                            } catch (NoSuchElementException e) {}

                            return translatedEvent;
                        }).collect(Collectors.toList());

                Collection<Eventable> firedEvents = findFiredEventsFor(state).stream()
                        .map(firedEventNode -> {
                            var translatedEvent = eventableTranslator.translateFrom(firedEventNode);

                            // The source has to be updated because later xpath lookup will use source structure
                            try {
                                translatedEvent.setSource(XPathParser.getChildByXpath(state.getDocument(), translatedEvent.getXpath()));
                            } catch (NoSuchElementException e) {}

                            return translatedEvent;
                        }).collect(Collectors.toList());

                state.setUnfiredEventables(unfiredEvents);
                unfiredEvents.addAll(firedEvents);
                state.setEventables(unfiredEvents);

                eventModel.addState(state);
                eventModel.addEvents(firedEvents);

                if (state.isRoot()) {
                    eventModel.setRoot(state);
                }
            });

            Collection<Map<String, Object>> edges = getEventTransitionData();
            edges.forEach(edge -> {
                var event = eventModel.findEvent((String)edge.get("e." + Properties.IDENTIFICATION));
                if (event != null) {
                    var transition = eventTransitionTranslator.translateFrom(edge);

                    transition.setOrigin(eventModel.findById((String)edge.get("origin." + Properties.IDENTIFICATION)));
                    transition.setDestination(eventModel.findById((String)edge.get("dest." + Properties.IDENTIFICATION)));

                    // TODO: add data inputs
                    transition.setExecutedEvent(new ExecutedEvent(event));
                    eventModel.addTransition(transition);
                }
            });
        } catch (StorageException e) {
            logger.info("Failure while loading web graph.", e);
        }

        return eventModel;
    }

    private Collection<Map<String, Object>> findUnfiredEventsFor(State state) {
        try {
            String query = String.format(
                    "MATCH (e:%s)-[:%s]-(s:%s {identifier: '%s'}) RETURN properties(e)",
                    Label.label(Labels.EVENT).toString(),
                    Relationships.NON_EXECUTED_EVENT.toString(),
                    Label.label(Labels.EVENT_STATE).toString(),
                    state.getIdentifier()
            );
            return connection.execute(combinedGraph, query, "properties(e)");
        } catch (StorageException e) {
            return new ArrayList<>();
        }
    }

    private Collection<Map<String, Object>> findFiredEventsFor(State state) {
        try {
            String query = String.format(
                    "MATCH (e:%s)-[:%s]-(s:%s {identifier: '%s'}) RETURN properties(e)",
                    Label.label(Labels.EVENT).toString(),
                    Relationships.EXECUTED_EVENT.toString(),
                    Label.label(Labels.EVENT_STATE).toString(),
                    state.getIdentifier()
            );
            return connection.execute(combinedGraph, query, "properties(e)");
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
