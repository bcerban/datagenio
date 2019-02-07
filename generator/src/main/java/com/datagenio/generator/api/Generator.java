package com.datagenio.generator.api;

import com.datagenio.model.api.WebFlowGraph;
import com.datagenio.model.api.WebTransition;

import java.util.List;

public interface Generator {

    WebFlowGraph generateWebModel();
    void generateDataset(WebFlowGraph webModel);
    List<String> generateTransitionData(WebTransition transition);
}
