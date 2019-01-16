package com.datagenio.crawler;

import com.datagenio.crawler.api.Browser;
import com.datagenio.crawler.api.EventFlowGraph;
import com.datagenio.crawler.api.EventableExtractor;
import com.datagenio.crawler.api.State;
import com.datagenio.crawler.exception.BrowserException;
import com.datagenio.crawler.exception.UncrawlableStateException;
import com.datagenio.crawler.model.EventFlowGraphImpl;
import com.datagenio.crawler.model.StateImpl;
import com.datagenio.crawler.util.EventExtractorFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;

public class Crawler {

    private static Logger logger = LoggerFactory.getLogger(Crawler.class);

    private CrawlContext context;
    private Browser browser;
    private EventableExtractor extractor;
    private EventFlowGraph graph;

    public Crawler(CrawlContext context, Browser browser) {
        this.context = context;
        this.browser = browser;
        this.extractor = EventExtractorFactory.get();
    }

    public CrawlContext getContext() {
        return context;
    }

    public Browser getBrowser() {
        return browser;
    }

    public void setBrowser(Browser browser) {
        this.browser = browser;
    }

    public EventFlowGraph getGraph() {
        if (this.graph == null) {
            this.graph = new EventFlowGraphImpl();
        }
        return this.graph;
    }

    public static Logger getLogger() {
        return logger;
    }

    public EventFlowGraph crawl(String rootUrl) {
        logger.debug("Begin crawling {}...", rootUrl);

        try {
            this.initCrawl(URI.create(rootUrl));
        } catch (UncrawlableStateException e) {

        }

        return this.getGraph();
    }

    private void initCrawl(URI root) throws UncrawlableStateException {
        try {
            this.browser.navigateTo(root);
            State initial = new StateImpl(root, this.browser.getDOM(), this.extractor);
            this.getGraph().addStateAsCurrent(initial);
        } catch (BrowserException e) {
            logger.debug("Exception happened while trying to initialize EventFlowGraph. Error: {}", e.getMessage());
            throw new UncrawlableStateException(e);
        }
    }
}
