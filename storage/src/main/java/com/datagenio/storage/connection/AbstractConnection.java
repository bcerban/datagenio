package com.datagenio.storage.connection;

import com.datagenio.storage.api.Connection;

import java.net.URI;

public abstract class AbstractConnection implements Connection {

    protected String getDatabaseName(String site) {
        URI siteUri = URI.create(site);
        return siteUri.getHost().toLowerCase().replaceAll("[^a-zA-Z0-9]", "") + ".db";
    }
}
