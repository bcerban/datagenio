package com.datagenio.storage.api;

import com.datagenio.crawler.api.EventFlowGraph;
import com.datagenio.model.api.WebFlowGraph;

public interface WriteAdapter {
    void save(WebFlowGraph graph);
    void save(EventFlowGraph graph);
}