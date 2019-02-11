package com.datagenio.storageapi;

import org.neo4j.graphdb.*;

import java.util.Collection;
import java.util.Map;

public interface Connection {
    GraphDatabaseService create(String name);

    Node addNode(GraphDatabaseService graph, Label label, Map<String, Object> properties) throws StorageException;
    Relationship addEdge(GraphDatabaseService graph, Node from, Node to, RelationshipType relType, Map<String, Object> properties) throws StorageException;

    Node findNode(GraphDatabaseService graph, Label label, Map<String, Object> properties) throws StorageException;
    Collection<Map<String, Object>> findNodesAsMap(GraphDatabaseService graph, Label label) throws StorageException;
    ResourceIterator<Node> findNodes(GraphDatabaseService graph, Label label, Map<String, Object> properties) throws StorageException;
    Collection<Node> findNodes(GraphDatabaseService graph, Label label, String query) throws StorageException;

    Collection<Map<String, Object>> findEdges(GraphDatabaseService graph, RelationshipType relationshipType) throws StorageException;
    Collection<Map<String, Object>> execute(GraphDatabaseService graph, String query) throws StorageException;
    Collection<Map<String, Object>> execute(GraphDatabaseService graph, String query, String key) throws StorageException;

    void disconnectFrom(GraphDatabaseService graph);
}
