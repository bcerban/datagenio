package com.datagenio.storage.connection;

import com.datagenio.storage.api.Connection;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.neo4j.graphdb.GraphDatabaseService;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.*;

public class EmbeddedConnectionTest {

    private static String TEST_URL = "http://test.com";

    private Connection connection;

    @Before
    public void setUp() {
        connection = EmbeddedConnection.get();
    }

    @After
    public void tearDown() {
        File dbOuput = new File(EmbeddedConnection.DEFAULT_OUTPUT_DIR + "/" + EmbeddedConnection.STORAGE_DIRECTORY);
        if (dbOuput.exists()) {
            try {
                FileUtils.deleteDirectory(dbOuput);
            } catch (IOException e) { }
        }
    }

    @Test
    public void testConnectTo() {
        assertTrue(connection.connectToWebFlowGraph(TEST_URL) instanceof GraphDatabaseService);
    }
}
