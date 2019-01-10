package com.datagenio.model.api;

import org.apache.http.HttpRequest;

public interface RequestAbstractor {
    AbstractHTTPRequest process(HttpRequest request);
}
