package com.datagenio.storage.connection;

import com.datagenio.context.Configuration;
import com.datagenio.storageapi.Connection;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;

public class ConnectionResolver {

    public static Connection get(Configuration configuration) {
        return buildEmbeddedConnection(configuration);
    }

    private static Connection buildEmbeddedConnection(Configuration configuration) {
        return new EmbeddedConnection(configuration.getOutputDirName(), new GraphDatabaseFactory());
    }
}
