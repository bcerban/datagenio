package com.datagenio.storageapi;

import com.datagenio.crawler.api.EventFlowGraph;
import com.datagenio.model.api.WebFlowGraph;

public interface ReadAdapter {

    WebFlowGraph loadWebModel();
    EventFlowGraph loadEventModel();
}
