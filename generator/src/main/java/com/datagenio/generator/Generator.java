package com.datagenio.generator;

import com.datagenio.crawler.api.Crawler;
import com.datagenio.crawler.api.EventFlowGraph;
import com.datagenio.generator.api.GraphConverter;
import com.datagenio.model.api.WebFlowGraph;
import com.datagenio.storage.api.ReadAdapter;
import com.datagenio.storage.api.WriteAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Generator {
    private static Logger logger = LoggerFactory.getLogger(Generator.class);

    private GraphConverter converter;
    private Crawler crawler;
    private ReadAdapter readAdapter;
    private WriteAdapter writeAdapter;

    public Generator(Crawler crawler, GraphConverter converter, ReadAdapter readAdapter, WriteAdapter writeAdapter) {
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

    }
}
