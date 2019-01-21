package com.datagenio.storage.connection;

import com.datagenio.storage.api.Configuration;
import com.datagenio.storage.api.Connection;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;

public class ConnectionResolver {

    public static Connection get(Configuration configuration) {
        return buildEmbeddedConnection(configuration);
    }

    private static Connection buildEmbeddedConnection(Configuration configuration) {
        return new EmbeddedConnection(configuration.get(Configuration.OUTPUT_DIRECTORY_NAME), new GraphDatabaseFactory());
    }
}
