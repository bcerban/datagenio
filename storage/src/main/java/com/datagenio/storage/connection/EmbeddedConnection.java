package com.datagenio.storage.connection;

import com.datagenio.storageapi.Properties;
import com.datagenio.storageapi.StorageException;
import org.neo4j.graphdb.*;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.graphdb.schema.IndexDefinition;
import org.neo4j.graphdb.schema.Schema;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;

public class EmbeddedConnection extends AbstractConnection {
    public static String STORAGE_DIRECTORY = "data";
    public static String PREFIX_COMBINED = "combined";
    public static long AVAILABILITY_TIMEOUT = 3000;

    private File outputDirectory;
    private GraphDatabaseFactory databaseFactory;
    private GraphDatabaseService dbService;

    public EmbeddedConnection(String outputDirectoryName, GraphDatabaseFactory databaseFactory) {
        this.databaseFactory = databaseFactory;
        this.outputDirectory = new File(outputDirectoryName);

        if (outputDirectory.exists()) {
            outputDirectory.mkdir();
        }
    }

    @Override
    public GraphDatabaseService create(String name) {
        if (dbService == null) {
            File databaseFile = new File(outputDirectory, generateDatabaseName(name, PREFIX_COMBINED));
            dbService = databaseFactory.newEmbeddedDatabase(databaseFile);
            registerShutdownHook(dbService);
        }

        return dbService;
    }

    @Override
    public Node addNode(GraphDatabaseService graph, Label label, Map<String, Object> properties) throws StorageException {
        validateConnection(graph);
        try (Transaction tx = graph.beginTx()) {
            Node node = graph.createNode(label);
            properties.forEach((k, v) -> node.setProperty(k, v));
            tx.success();

            return node;
        } catch (Exception e) {
            throw new StorageException("Couldn't add node.", e);
        }
    }

    @Override
    public Relationship addEdge(GraphDatabaseService graph, Node from, Node to, RelationshipType relType, Map<String, Object> properties) throws StorageException {
        validateConnection(graph);

        try (Transaction tx = graph.beginTx()) {
            Relationship edge = from.createRelationshipTo(to, relType);
            properties.forEach((k,v) -> edge.setProperty(k, v));
            tx.success();

            return edge;
        } catch (Exception e) {
            throw new StorageException("Couldn't create relationship.");
        }
    }

    @Override
    public void disconnectFrom(GraphDatabaseService graph) {
        try {
            validateConnection(graph);
            graph.shutdown();
        } catch (StorageException e) { }
    }

    @Override
    public Node findNode(GraphDatabaseService graph, Label label, Map<String, Object> properties) throws StorageException {
        validateConnection(graph);

        try (Transaction tx = graph.beginTx()) {
            Optional<Node> maybeNode = graph.findNodes(label, properties)
                    .stream().findFirst();
            tx.success();

            return maybeNode.get();
        } catch (Exception e) {
            throw new StorageException("Node not found.", e);
        }
    }

    @Override
    public Collection<Map<String, Object>> findNodesAsMap(GraphDatabaseService graph, Label label) throws StorageException {
        validateConnection(graph);
        String query = String.format("MATCH (n:%s) RETURN properties(n)", label.toString());
        return execute(graph, query, "properties(n)");
    }

    @Override
    public ResourceIterator<Node> findNodes(GraphDatabaseService graph, Label label, Map<String, Object> properties) throws StorageException {
        validateConnection(graph);

        try (Transaction tx = graph.beginTx()) {
            ResourceIterator<Node> nodes = graph.findNodes(label, properties);
            tx.success();

            return nodes;
        } catch (Exception e) {
            throw new StorageException("Couldn't execute search.", e);
        }
    }

    @Override
    public Collection<Node> findNodes(GraphDatabaseService graph, Label label, String query) throws StorageException {
        validateConnection(graph);
        return executeNodeSearchQuery(graph, query);
    }

    @Override
    public Collection<Map<String, Object>> findEdges(GraphDatabaseService graph, RelationshipType relationshipType) throws StorageException {
        validateConnection(graph);
        String query = String.format(
                "MATCH (origin)-[rel:%s]-(dest) RETURN origin.%s, dest.%s, rel",
                relationshipType.toString(),
                Properties.IDENTIFICATION,
                Properties.IDENTIFICATION
        );
        return execute(graph, query);
    }

    @Override
    public Collection<Map<String, Object>> execute(GraphDatabaseService graph, String query) throws StorageException {
        validateConnection(graph);

        var results = new ArrayList<Map<String, Object>>();
        try (Transaction tx = graph.beginTx()) {
            var queryResult = graph.execute(query);

            while(queryResult.hasNext()) {
                results.add(queryResult.next());
            }
            tx.success();
        } catch (Exception e) {
            throw new StorageException("Couldn't execute query.", e);
        }

        return results;
    }

    @Override
    public Collection<Map<String, Object>> execute(GraphDatabaseService graph, String query, String key) throws StorageException {
        validateConnection(graph);

        var results = new ArrayList<Map<String, Object>>();
        try (Transaction tx = graph.beginTx()) {
            var queryResult = graph.execute(query);

            while(queryResult.hasNext()) {
                results.add((Map<String, Object>)queryResult.next().get(key));
            }
            tx.success();
        } catch (Exception e) {
            throw new StorageException("Couldn't execute query.", e);
        }

        return results;
    }

    private Collection<Node> executeNodeSearchQuery(GraphDatabaseService graph, String query) {
        var nodes = new ArrayList<Node>();
        try (Transaction tx = graph.beginTx()) {
            Result result = graph.execute(query);

            while(result.hasNext()) {
                var row = result.next();
                if (row.containsKey("n")) {
                    nodes.add((Node)row.get("n"));
                }
            }
            tx.success();
        } catch (Exception e) { }

        return nodes;
    }

    private void validateConnection(GraphDatabaseService graph) throws StorageException {
        if (graph == null || !graph.isAvailable(AVAILABILITY_TIMEOUT)) {
            throw new StorageException("No database connection established.");
        }
    }

    private String generateDatabaseName(String site, String prefix) {
        return STORAGE_DIRECTORY + "/" + prefix + "/" + getDatabaseName(site);
    }

    private void addIndexOnProperty(GraphDatabaseService graph, Label label, String property) {
        try (Transaction tx = graph.beginTx()) {
            Schema schema = graph.schema();

            if (!hasIndex(schema, label, property)) {
                schema.indexFor(label).on(property).create();
            }

            tx.success();
        }
    }

    private boolean hasIndex(Schema schema, Label label, String property) {
        boolean hasIndex = false;
        var iterator = schema.getIndexes().iterator();

        while (iterator.hasNext() && !hasIndex) {
            IndexDefinition index = iterator.next();

            if (!index.isNodeIndex()) continue;

            boolean isForLabel = false;
            var labelIterator = index.getLabels().iterator();
            while (labelIterator.hasNext() && !isForLabel) {
                var currentLabel = labelIterator.next();
                if (currentLabel.equals(label)) {
                    isForLabel = true;
                }
            }

            if (!isForLabel) continue;

            boolean isForProperty = false;
            var propertyIterator = index.getPropertyKeys().iterator();
            while (propertyIterator.hasNext() && !isForProperty) {
                var currentProperty = propertyIterator.next();
                if (currentProperty.equals(property)) {
                    isForProperty = true;
                }
            }

            if (isForProperty) hasIndex = true;
        }

        return hasIndex;
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
