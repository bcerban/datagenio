package com.datagenio.generator;

import com.datagenio.context.Context;
import com.datagenio.crawler.api.Crawler;
import com.datagenio.crawler.api.EventFlowGraph;
import com.datagenio.databank.InputBuilderFactory;
import com.datagenio.databank.api.InputBuilder;
import com.datagenio.generator.api.GraphConverter;
import com.datagenio.generator.api.RequestFormatter;
import com.datagenio.generator.util.DataSetWriter;
import com.datagenio.generator.util.RequestFormatterFactory;
import com.datagenio.model.api.AbstractHttpRequest;
import com.datagenio.model.api.WebFlowGraph;
import com.datagenio.model.api.WebTransition;
import com.datagenio.storageapi.ReadAdapter;
import com.datagenio.storageapi.WriteAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Generator {
    private static Logger logger = LoggerFactory.getLogger(Generator.class);

    private Context context;
    private GraphConverter converter;
    private Crawler crawler;
    private ReadAdapter readAdapter;
    private WriteAdapter writeAdapter;
    private InputBuilder inputBuilder;
    private RequestFormatter formatter;

    public Generator(Context context, Crawler crawler, GraphConverter converter, ReadAdapter readAdapter, WriteAdapter writeAdapter) {
        this.context = context;
        this.crawler = crawler;
        this.converter = converter;
        this.readAdapter = readAdapter;
        this.writeAdapter = writeAdapter;
        this.inputBuilder = InputBuilderFactory.get();
        this.formatter = RequestFormatterFactory.get();
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

    public EventFlowGraph crawlSite() {
        var graph = crawler.crawl();

        logger.info("Saving generated graph...");
        writeAdapter.save(graph);

        return graph;
    }

    public WebFlowGraph generateWebModel() {
        var eventModel = crawler.crawl();
        var webModel =  converter.convert(eventModel);

        logger.info("Saving generated graph...");
        writeAdapter.saveCombined(eventModel, webModel);

        return webModel;
    }

    public void generateDataset(WebFlowGraph webModel) {
        logger.info("Beginning data set generation...");

        DataSetWriter writer = new DataSetWriter(context);
        webModel.getTransitions().forEach(transition -> {
            List<String> lines = generateTransitionData(transition);
            try {
                writer.writeLines(lines);
            } catch (FileNotFoundException e) { }
        });

        logger.info("Data set generation finished.");
    }

    public List<String> generateTransitionData(WebTransition transition) {
        List<String> lines = new ArrayList<>();
        transition.getAbstractRequests().forEach(request -> {
            lines.add(getPopulatedRequest(request));
        });

        return lines;
    }

    private String getPopulatedRequest(AbstractHttpRequest request) {
        Map<String, String> inputs = inputBuilder.buildInputs(request);
        return formatter.format(request, inputs);
    }
}
