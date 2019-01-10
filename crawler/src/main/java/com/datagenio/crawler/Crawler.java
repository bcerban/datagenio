package com.datagenio.crawler;

import com.datagenio.crawler.api.EventFlowGraph;
import org.apache.commons.lang.NotImplementedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Crawler {

    private static Logger logger = LoggerFactory.getLogger(Crawler.class);

    private CrawlContext context;

    public Crawler(CrawlContext context) {
        this.context = context;
    }

    public CrawlContext getContext() {
        return context;
    }

    public static Logger getLogger() {
        return logger;
    }

    public EventFlowGraph crawl(String rootUrl) {
        throw new NotImplementedException();
    }
}
