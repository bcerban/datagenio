package com.datagenio.crawler.util;

import com.datagenio.context.Context;
import com.datagenio.crawler.api.EventFlowGraph;
import com.datagenio.storageapi.ReadAdapter;
import com.datagenio.storageapi.WriteAdapter;

public class ModelPersistor {

    private ReadAdapter readAdapter;
    private WriteAdapter writeAdapter;

    public ModelPersistor(Context context) {
        readAdapter = context.getReadAdapter();
        writeAdapter = context.getWriteAdapter();
    }

    public EventFlowGraph loadEventFlowGraph() {
        return readAdapter.loadEventModel();
    }
}
