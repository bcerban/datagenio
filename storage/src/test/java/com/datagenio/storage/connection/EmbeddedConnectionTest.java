package com.datagenio.storage.connection;

import com.datagenio.storage.api.Connection;
import org.junit.Before;
import org.junit.Test;
import org.neo4j.graphdb.GraphDatabaseService;

import static org.junit.Assert.*;

public class EmbeddedConnectionTest {

    private static String TEST_URL = "http://test.com";

    private Connection connection;

    @Before
    public void setUp() {
        connection = EmbeddedConnection.get();
    }

    @Test
    public void testConnectTo() {
        assertTrue(connection.connectTo(TEST_URL) instanceof GraphDatabaseService);
    }
}
