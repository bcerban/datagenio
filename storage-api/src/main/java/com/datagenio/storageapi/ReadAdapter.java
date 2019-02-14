package com.datagenio.storageapi;

import com.datagenio.crawler.api.EventFlowGraph;
import com.datagenio.model.WebFlowGraph;

public interface ReadAdapter {

    WebFlowGraph loadWebModel();
    EventFlowGraph loadEventModel();
}
