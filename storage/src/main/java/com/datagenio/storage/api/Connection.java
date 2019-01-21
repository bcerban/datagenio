package com.datagenio.storage.api;

import com.datagenio.storage.exception.StorageException;
import org.neo4j.graphdb.*;

import java.util.Map;

public interface Connection {
    GraphDatabaseService createWebFlowGraph(String name);
    GraphDatabaseService createEventGraph(String name);

    Node addNode(GraphDatabaseService graph, Label label, Map<String, Object> properties) throws StorageException;
    Relationship addEdge(GraphDatabaseService graph, Node from, Node to, RelationshipType relType, Map<String, Object> properties) throws StorageException;

    Node findNode(GraphDatabaseService graph, Label label, Map<String, Object> properties) throws StorageException;
    ResourceIterator<Node> findNodes(GraphDatabaseService graph, Label label, Map<String, Object> properties) throws StorageException;

    void disconnectFrom(GraphDatabaseService graph);
}
