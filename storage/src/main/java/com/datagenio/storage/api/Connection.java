package com.datagenio.storage.api;

import com.datagenio.storage.exception.StorageException;
import org.neo4j.graphdb.*;

import java.util.Collection;
import java.util.Map;

public interface Connection {
    GraphDatabaseService create(String name);

    Node addNode(GraphDatabaseService graph, Label label, Map<String, Object> properties) throws StorageException;
    Relationship addEdge(GraphDatabaseService graph, Node from, Node to, RelationshipType relType, Map<String, Object> properties) throws StorageException;

    Node findNode(GraphDatabaseService graph, Label label, Map<String, Object> properties) throws StorageException;
    ResourceIterator<Node> findNodes(GraphDatabaseService graph, Label label, Map<String, Object> properties) throws StorageException;
    Collection<Node> findNodes(GraphDatabaseService graph, Label label, String where) throws StorageException;

    void disconnectFrom(GraphDatabaseService graph);
}
