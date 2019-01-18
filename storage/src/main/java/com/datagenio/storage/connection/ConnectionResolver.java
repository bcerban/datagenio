package com.datagenio.storage.connection;

import com.datagenio.storage.api.Connection;

public class ConnectionResolver {

    public static Connection get() {
        // TODO: make configurable from context
        return EmbeddedConnection.get();
    }
}
