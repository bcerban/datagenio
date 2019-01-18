package com.datagenio.storage.connection;

import com.datagenio.model.api.WebState;
import com.datagenio.storage.Labels;
import com.datagenio.storage.Properties;
import com.datagenio.storage.api.Connection;
import com.datagenio.storage.exception.StorageException;
import org.neo4j.graphdb.*;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.graphdb.schema.IndexDefinition;
import org.neo4j.graphdb.schema.Schema;

import java.io.File;
import java.net.URI;
import java.util.Map;

public class EmbeddedConnection implements Connection {

    // TODO: This is for testing only!
    private static String DEFAULT_OUTPUT_DIR = "/Users/Bettina/Developer/datagenio-output";

    public static String STORAGE_DIRECTORY = "data";
    public static long AVAILABILITY_TIMEOUT = 3000;
    private static EmbeddedConnection connection;

    private File outputDirectory;
    private GraphDatabaseFactory databaseFactory;
    private GraphDatabaseService webFlowGraph;

    public static EmbeddedConnection get() {
        if (connection == null) {
            connection = new EmbeddedConnection(DEFAULT_OUTPUT_DIR);
        }

        return connection;
    }

    private EmbeddedConnection(String outputDirectoryName) {
        this.databaseFactory = new GraphDatabaseFactory();

        this.outputDirectory = new File(outputDirectoryName);
        if (outputDirectory.exists()) {
            outputDirectory.mkdir();
        }
    }

    @Override
    public GraphDatabaseService connectTo(String name) {
        if (webFlowGraph == null || !webFlowGraph.isAvailable(AVAILABILITY_TIMEOUT)) {
            File databaseFile = new File(outputDirectory, generateDatabaseName(name));
            webFlowGraph = databaseFactory.newEmbeddedDatabase(databaseFile);
            registerShutdownHook(webFlowGraph);
            addIndexOnProperty(webFlowGraph, Label.label(Labels.WEB_STATE), Properties.IDENTIFICATION);
        }

        return webFlowGraph;
    }

    @Override
    public Node addNode(Label label) throws StorageException {
        Node node;

        try (Transaction tx = getWebFlowGraph().beginTx()) {
            node = getWebFlowGraph().createNode(label);
            tx.success();
        }

        if (node != null) {
            return node;
        }

        throw new StorageException("Couldn't create node.");
    }

    @Override
    public Node addNode(Label label, Map<String, Object> properties) throws StorageException {
        Node node;

        try (Transaction tx = getWebFlowGraph().beginTx()) {
            Node tmp = getWebFlowGraph().createNode(label);
            properties.forEach((k, v) -> tmp.setProperty(k, v));
            tx.success();

            node = tmp;
        }

        if (node != null) {
            return node;
        }

        throw new StorageException("Couldn't create node.");
    }

    @Override
    public Relationship addEdge(Node from, Node to, RelationshipType relType) throws StorageException {
        Relationship edge;
        try (Transaction tx = getWebFlowGraph().beginTx()) {
            edge = from.createRelationshipTo(to, relType);
            tx.success();
        }

        if (edge != null) {
            return edge;
        }

        throw new StorageException("Couldn't create relationship.");
    }

    @Override
    public Relationship addEdge(Node from, Node to, RelationshipType relType, Map<String, Object> properties) throws StorageException {
        Relationship edge;
        try (Transaction tx = getWebFlowGraph().beginTx()) {
            Relationship tmp = from.createRelationshipTo(to, relType);
            properties.forEach((k,v) -> tmp.setProperty(k, v));
            tx.success();

            edge = tmp;
        }

        if (edge != null) {
            return edge;
        }

        throw new StorageException("Couldn't create relationship.");
    }

    @Override
    public Node findStateNode(WebState state) throws StorageException {
        Node node;
        try (Transaction tx = getWebFlowGraph().beginTx()) {
            node = getWebFlowGraph().findNode(Label.label(Labels.WEB_STATE), Properties.IDENTIFICATION, state.getIdentifier());
            tx.success();
        }

        return node;
    }

    @Override
    public void shutDown() {
        webFlowGraph.shutdown();
    }

    private GraphDatabaseService getWebFlowGraph() throws StorageException {
        if (webFlowGraph == null || !webFlowGraph.isAvailable(AVAILABILITY_TIMEOUT)) {
            throw new StorageException("No database connection established.");
        }

        return webFlowGraph;
    }

    private String generateDatabaseName(String site) {
        URI siteUri = URI.create(site);
        return STORAGE_DIRECTORY + "/" + siteUri.getHost().toLowerCase().replaceAll("[^a-zA-Z0-9]", "");
    }

    private void addIndexOnProperty(GraphDatabaseService graph, Label label, String property) {
        try (Transaction tx = graph.beginTx()) {
            Schema schema = graph.schema();
            IndexDefinition indexDefinition = schema.indexFor(label)
                    .on(property)
                    .create();
            tx.success();
        }
    }

    private static void registerShutdownHook(final GraphDatabaseService graphDb) {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                graphDb.shutdown();
            }
        });
    }

}
