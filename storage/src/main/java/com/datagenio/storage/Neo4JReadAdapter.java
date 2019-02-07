package com.datagenio.storage;

import com.datagenio.model.api.WebFlowGraph;
import com.datagenio.context.Configuration;
import com.datagenio.storage.api.Connection;
import com.datagenio.storage.api.ReadAdapter;
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
