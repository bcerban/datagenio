package com.datagenio.storage.api;

import com.datagenio.storage.exception.StorageException;
import org.neo4j.graphdb.*;

import java.util.Map;

public interface Connection {
    GraphDatabaseService connectToWebFlowGraph(String name);
    GraphDatabaseService connectToEventGraph(String name);
    Node addNode(Label label) throws StorageException;
    Node addNode(Label label, Map<String, Object> properties) throws StorageException;
    Node findWebNode(Map<String, Object> properties) throws StorageException;
    ResourceIterator<Node> findWebNodes(Map<String, Object> properties) throws StorageException;
    Node findEventNode(Map<String, Object> properties) throws StorageException;
    ResourceIterator<Node> findEventNodes(Map<String, Object> properties) throws StorageException;
    Relationship addEdge(Node from, Node to, RelationshipType relType) throws StorageException;
    Relationship addEdge(Node from, Node to, RelationshipType relType, Map<String, Object> properties) throws StorageException;
}
