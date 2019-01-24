package com.datagenio.storage.connection;

import com.datagenio.storage.api.Labels;
import com.datagenio.storage.Relationships;
import com.datagenio.storage.api.Connection;
import com.datagenio.storage.exception.StorageException;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.neo4j.graphdb.*;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.graphdb.schema.IndexCreator;
import org.neo4j.graphdb.schema.IndexDefinition;
import org.neo4j.graphdb.schema.Schema;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class EmbeddedConnectionTest {

    private static String TEST_OUTPUT_DIR = "/tmp/test";
    private static String TEST_URL = "http://test.com";
    private static String TEST_LABEL = "test_node";

    private Transaction tx;
    private GraphDatabaseFactory databaseFactory;
    private Connection connection;

    @Before
    public void setUp() {
        tx = mock(Transaction.class);
        databaseFactory = mock(GraphDatabaseFactory.class);
        connection = new EmbeddedConnection(TEST_OUTPUT_DIR, databaseFactory);
    }

    @After
    public void tearDown() {
        File dbOuput = new File(TEST_OUTPUT_DIR + "/" + EmbeddedConnection.STORAGE_DIRECTORY);
        if (dbOuput.exists()) {
            try {
                FileUtils.deleteDirectory(dbOuput);
            } catch (IOException e) { }
        }
    }

    @Test
    public void testCreate() {
//        IndexDefinition[] indexDefinitions = {};
//        Schema schema = mock(Schema.class);
//        doReturn(Arrays.asList(indexDefinitions)).when(schema).getIndexes();
//
//        IndexCreator indexCreator = mock(IndexCreator.class);
//        doReturn(indexCreator).when(schema).indexFor(any());
//        doReturn(indexCreator).when(indexCreator).on(any());

        var database = mock(GraphDatabaseService.class);
//        doReturn(schema).when(database).schema();
        doReturn(database).when(databaseFactory).newEmbeddedDatabase(any());
        doReturn(tx).when(database).beginTx();

        assertTrue(connection.create(TEST_URL) instanceof GraphDatabaseService);
    }

    @Test
    public void testAddNode() throws StorageException {
        var database = mock(GraphDatabaseService.class);
        var node = mock(Node.class);
        var label = Label.label(TEST_LABEL);

        doReturn(true).when(database).isAvailable(EmbeddedConnection.AVAILABILITY_TIMEOUT);
        doReturn(tx).when(database).beginTx();
        doReturn(node).when(database).createNode(label);
        connection.addNode(database, Label.label(TEST_LABEL), new HashMap<>());

        verify(tx, times(1)).success();
    }

    @Test(expected = StorageException.class)
    public void testAddNodeWithException() throws StorageException {
        var database = mock(GraphDatabaseService.class);
        var label = Label.label(TEST_LABEL);

        doReturn(true).when(database).isAvailable(EmbeddedConnection.AVAILABILITY_TIMEOUT);
        doReturn(tx).when(database).beginTx();
        doThrow(new ConstraintViolationException("Test")).when(database).createNode(label);
        connection.addNode(database, Label.label(TEST_LABEL), new HashMap<>());

        verify(tx, times(0)).success();
    }

    @Test
    public void testAddEdge() throws StorageException {
        var database = mock(GraphDatabaseService.class);
        var from = mock(Node.class);
        var to = mock(Node.class);
        var edge = mock(Relationship.class);

        doReturn(true).when(database).isAvailable(EmbeddedConnection.AVAILABILITY_TIMEOUT);
        doReturn(tx).when(database).beginTx();
        doReturn(edge).when(from).createRelationshipTo(to, Relationships.EXECUTED_EVENT);
        connection.addEdge(database, from, to, Relationships.EXECUTED_EVENT, new HashMap<>());

        verify(tx, times(1)).success();
    }

    @Test(expected = StorageException.class)
    public void testAddEdgeWithException() throws StorageException {
        var database = mock(GraphDatabaseService.class);
        var from = mock(Node.class);
        var to = mock(Node.class);

        doReturn(true).when(database).isAvailable(EmbeddedConnection.AVAILABILITY_TIMEOUT);
        doReturn(tx).when(database).beginTx();
        doThrow(new ConstraintViolationException("Test")).when(from).createRelationshipTo(to, Relationships.EXECUTED_EVENT);
        connection.addEdge(database, from, to, Relationships.EXECUTED_EVENT, new HashMap<>());

        verify(tx, times(0)).success();
    }

    @Test
    public void testDisconnectFrom() {
        var database = mock(GraphDatabaseService.class);
        doReturn(true).when(database).isAvailable(EmbeddedConnection.AVAILABILITY_TIMEOUT);

        connection.disconnectFrom(database);
        verify(database, times(1)).shutdown();
    }

    @Test
    public void testFindNode() throws StorageException {
        var database = mock(GraphDatabaseService.class);
        var iterator = mock(ResourceIterator.class);
        var node = mock(Node.class);

        doReturn(true).when(database).isAvailable(EmbeddedConnection.AVAILABILITY_TIMEOUT);
        doReturn(tx).when(database).beginTx();
        doReturn(iterator).when(database).findNodes(any(), any());
        doReturn(List.of(node).stream()).when(iterator).stream();

        assertTrue(connection.findNode(database, Label.label(Labels.WEB_STATE), new HashMap<>()) instanceof Node);
        verify(tx, times(1)).success();
    }

    @Test(expected = StorageException.class)
    public void testFindNodeWithException() throws StorageException {
        var database = mock(GraphDatabaseService.class);

        doReturn(true).when(database).isAvailable(EmbeddedConnection.AVAILABILITY_TIMEOUT);
        doReturn(tx).when(database).beginTx();
        doThrow(new NotFoundException("Test")).when(database).findNodes(any(), any());

        assertTrue(connection.findNode(database, Label.label(Labels.WEB_STATE), new HashMap<>()) instanceof Node);
        verify(tx, times(0)).success();
    }

    @Test
    public void testFindNodes() throws StorageException {
        var database = mock(GraphDatabaseService.class);
        var iterator = mock(ResourceIterator.class);

        doReturn(true).when(database).isAvailable(EmbeddedConnection.AVAILABILITY_TIMEOUT);
        doReturn(tx).when(database).beginTx();
        doReturn(iterator).when(database).findNodes(any(), any());

        connection.findNodes(database, Label.label(Labels.EVENT_STATE), new HashMap<>());
        verify(tx, times(1)).success();
    }

    @Test(expected = StorageException.class)
    public void testFindNodesWithException() throws StorageException {
        var database = mock(GraphDatabaseService.class);

        doReturn(true).when(database).isAvailable(EmbeddedConnection.AVAILABILITY_TIMEOUT);
        doReturn(tx).when(database).beginTx();
        doThrow(new NotFoundException("Test")).when(database).findNodes(any(), any());

        connection.findNodes(database, Label.label(Labels.EVENT_STATE), new HashMap<>());
        verify(tx, times(0)).success();
    }
}
