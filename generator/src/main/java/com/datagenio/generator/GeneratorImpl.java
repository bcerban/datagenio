package com.datagenio.generator;

import com.datagenio.context.Context;
import com.datagenio.crawler.api.Crawler;
import com.datagenio.generator.api.FormattedWriter;
import com.datagenio.generator.api.Generator;
import com.datagenio.generator.api.GraphConverter;
import com.datagenio.generator.util.FormattedWriterFactory;
import com.datagenio.model.WebFlowGraph;
import com.datagenio.model.WebTransition;
import com.datagenio.model.request.AbstractRequest;
import com.datagenio.storageapi.ReadAdapter;
import com.datagenio.storageapi.WriteAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class GeneratorImpl implements Generator {
    private static Logger logger = LoggerFactory.getLogger(GeneratorImpl.class);

    private Context context;
    private GraphConverter converter;
    private Crawler crawler;
    private ReadAdapter readAdapter;
    private WriteAdapter writeAdapter;

    public GeneratorImpl(Context context, Crawler crawler, GraphConverter converter, ReadAdapter readAdapter, WriteAdapter writeAdapter) {
        this.context = context;
        this.crawler = crawler;
        this.converter = converter;
        this.readAdapter = readAdapter;
        this.writeAdapter = writeAdapter;
    }

    public ReadAdapter getReadAdapter() {
        return readAdapter;
    }

    public void setReadAdapter(ReadAdapter readAdapter) {
        this.readAdapter = readAdapter;
    }

    public WriteAdapter getWriteAdapter() {
        return writeAdapter;
    }

    public void setWriteAdapter(WriteAdapter writeAdapter) {
        this.writeAdapter = writeAdapter;
    }

    @Override
    public WebFlowGraph buildWebModel() {
        var eventModel = crawler.crawl();
        var webModel =  converter.convert(eventModel, loadWebModel());

        logger.info("Saving generated graph...");
        writeAdapter.saveCombined(eventModel, webModel);

        return webModel;
    }

    @Override
    public WebFlowGraph loadWebModel() {
        if (context.isContinueExistingModel()) {
            return readAdapter.loadWebModel();
        }

        return new WebFlowGraph();
    }

    @Override
    public void generateDataset(WebFlowGraph webModel) {
        logger.info("Beginning data set generation...");

        FormattedWriter writer = FormattedWriterFactory.get(context);
        List<AbstractRequest> requests = new ArrayList<>();
        webModel.getTransitions().forEach(t -> {
            int weight = getTransitionWeight(t);
            while (weight > 0) {
                requests.addAll(t.getAbstractRequests());
                weight--;
            }
        });
        writer.formatAndWrite(requests);

        logger.info("Data set generation finished.");
    }

    private int getTransitionWeight(WebTransition transition) {
        var contextWeight = context.getTransitionWeights()
                .stream()
                .filter(w -> w.getTransitionId().equals(transition.getIdentifier()))
                .findFirst();

        if (contextWeight.isPresent()) return contextWeight.get().getWeight();
        return context.getDefaultTransitionWeight();
    }
}
