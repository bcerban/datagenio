package com.datagenio.model.api;

import org.apache.http.HttpRequest;

public interface RequestAbstractor {
    AbstractHttpRequest process(HttpRequest request);
}
