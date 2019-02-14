package com.datagenio.generator.api;

import com.datagenio.model.WebFlowGraph;
import com.datagenio.model.WebTransition;

import java.util.List;

public interface Generator {

    WebFlowGraph generateWebModel();
    void generateDataset(WebFlowGraph webModel);
    List<String> generateTransitionData(WebTransition transition);
}
