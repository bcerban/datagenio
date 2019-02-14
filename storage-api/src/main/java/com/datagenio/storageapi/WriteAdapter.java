package com.datagenio.storageapi;

import com.datagenio.crawler.api.EventFlowGraph;
import com.datagenio.model.WebFlowGraph;

public interface WriteAdapter {
    void saveCombined(EventFlowGraph eventFlowGraph, WebFlowGraph webFlowGraph);
    void save(WebFlowGraph graph);
    void save(EventFlowGraph graph);
}
