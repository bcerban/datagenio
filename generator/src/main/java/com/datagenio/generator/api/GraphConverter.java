package com.datagenio.generator.api;

import com.datagenio.crawler.api.EventFlowGraph;
import com.datagenio.model.api.WebFlowGraph;

public interface GraphConverter {

    WebFlowGraph convert(EventFlowGraph eventFlowGraph);
}
