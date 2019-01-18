package com.datagenio.storage.api;

import com.datagenio.model.api.WebFlowGraph;

public interface WriteAdapter {
    void save(WebFlowGraph graph);
    void update(WebFlowGraph graph);
    void delete(WebFlowGraph graph);
}
