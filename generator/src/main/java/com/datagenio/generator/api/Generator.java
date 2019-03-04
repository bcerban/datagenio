package com.datagenio.generator.api;

import com.datagenio.model.WebFlowGraph;
import com.datagenio.model.WebTransition;

import java.util.List;

public interface Generator {

    WebFlowGraph buildWebModel();
    WebFlowGraph loadWebModel();
    void generateDataset(WebFlowGraph webModel);
}
