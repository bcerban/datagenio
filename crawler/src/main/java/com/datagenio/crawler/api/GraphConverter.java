package com.datagenio.crawler.api;

import com.datagenio.model.api.WebFlowGraph;

public interface GraphConverter {

    WebFlowGraph convert(EventFlowGraph eventFlowGraph);
}
