package com.datagenio.generator;

import com.datagenio.crawler.Crawler;
import com.datagenio.crawler.api.EventFlowGraph;
import com.datagenio.crawler.api.GraphConverter;
import com.datagenio.model.api.WebFlowGraph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Generator {
    private static Logger logger = LoggerFactory.getLogger(Generator.class);

    private GraphConverter converter;
    private Crawler crawler;

    public Generator(Crawler crawler, GraphConverter converter) {
        this.crawler = crawler;
        this.converter = converter;
    }

    public WebFlowGraph generateWebModel(String rootUrl, String outputDirectory) {
        EventFlowGraph eventGraph = this.crawler.crawl(rootUrl);
        return this.converter.convert(eventGraph);
    }

    public void generateDataset(WebFlowGraph webModel) {

    }
}
