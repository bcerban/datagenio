package com.datagenio.storage;

import com.datagenio.model.api.WebFlowGraph;
import com.datagenio.context.Configuration;
import com.datagenio.storageapi.Connection;
import com.datagenio.storageapi.ReadAdapter;
import com.datagenio.storage.connection.ConnectionResolver;

public class Neo4JReadAdapter implements ReadAdapter {

    private Connection connection;

    public Neo4JReadAdapter(Configuration configuration) {
        connection = ConnectionResolver.get(configuration);
    }

    @Override
    public WebFlowGraph readWebFloGraph(String site) {
        return null;
    }
}
