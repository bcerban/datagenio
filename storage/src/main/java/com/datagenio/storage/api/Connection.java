package com.datagenio.storage.api;

import com.datagenio.model.api.WebState;
import com.datagenio.storage.exception.StorageException;
import org.neo4j.graphdb.*;

import java.util.Map;

public interface Connection {
    GraphDatabaseService connectTo(String name);
    Node addNode(Label label) throws StorageException;
    Node addNode(Label label, Map<String, Object> properties) throws StorageException;
    Node findStateNode(WebState state) throws StorageException;
    Relationship addEdge(Node from, Node to, RelationshipType relType) throws StorageException;
    Relationship addEdge(Node from, Node to, RelationshipType relType, Map<String, Object> properties) throws StorageException;

    void shutDown();
}
