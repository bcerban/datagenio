package com.datagenio.crawler;

import com.datagenio.crawler.api.Context;

import java.net.URI;

public class CrawlContext implements Context {

    private final int requestTimeout;
    private final int crawlTimeout;
    private final int crawlDepth;
    private final String rootUrl;
    private final String outputDirName;
    private final boolean verbose;
    private final boolean printScreen;

    public CrawlContext(String rootUrl, String outputDirName) {
        this.rootUrl = rootUrl;
        this.requestTimeout = REQUEST_TIMEOUT;
        this.crawlTimeout = 0;
        this.outputDirName = outputDirName;
        this.crawlDepth = NO_MAX_DEPTH;
        this.verbose = false;
        this.printScreen = false;
    }

    public CrawlContext(String rootUrl, String outputDirName, boolean verbose) {
        this.rootUrl = rootUrl;
        this.requestTimeout = REQUEST_TIMEOUT;
        this.crawlTimeout = 0;
        this.outputDirName = outputDirName;
        this.crawlDepth = NO_MAX_DEPTH;
        this.verbose = verbose;
        this.printScreen = false;
    }

    public CrawlContext(String rootUrl, String outputDirName, boolean verbose, boolean printScreen) {
        this.rootUrl = rootUrl;
        this.requestTimeout = REQUEST_TIMEOUT;
        this.crawlTimeout = 0;
        this.outputDirName = outputDirName;
        this.crawlDepth = NO_MAX_DEPTH;
        this.verbose = verbose;
        this.printScreen = printScreen;
    }

    public CrawlContext(String rootUrl, String outputDirName, boolean verbose, boolean printScreen, int crawlDepth) {
        this.rootUrl = rootUrl;
        this.requestTimeout = REQUEST_TIMEOUT;
        this.crawlTimeout = 0;
        this.outputDirName = outputDirName;
        this.crawlDepth = crawlDepth;
        this.verbose = verbose;
        this.printScreen = printScreen;
    }

    public CrawlContext(String rootUrl, String outputDirName, boolean verbose, int crawlTimeout) {
        this.rootUrl = rootUrl;
        this.requestTimeout = REQUEST_TIMEOUT;
        this.crawlTimeout = crawlTimeout;
        this.outputDirName = outputDirName;
        this.crawlDepth = NO_MAX_DEPTH;
        this.verbose = verbose;
        this.printScreen = false;
    }

    public CrawlContext(String rootUrl, String outputDirName, boolean verbose, int crawlTimeout, int requestTimeout) {
        this.rootUrl = rootUrl;
        this.requestTimeout = requestTimeout;
        this.crawlTimeout = crawlTimeout;
        this.outputDirName = outputDirName;
        this.crawlDepth = NO_MAX_DEPTH;
        this.verbose = verbose;
        this.printScreen = false;
    }

    public CrawlContext(String rootUrl, String outputDirName, boolean verbose, int crawlTimeout, int requestTimeout, int crawlDepth) {
        this.rootUrl = rootUrl;
        this.requestTimeout = requestTimeout;
        this.crawlTimeout = crawlTimeout;
        this.outputDirName = outputDirName;
        this.crawlDepth = crawlDepth;
        this.verbose = verbose;
        this.printScreen = false;
    }

    public String getRootUrl() {
        return rootUrl;
    }

    public URI getRootUri() {
        return URI.create(rootUrl);
    }

    public int getRequestTimeout() {
        return requestTimeout;
    }

    public int getCrawlTimeout() {
        return crawlTimeout;
    }

    public int getCrawlDepth() {
        return crawlDepth;
    }

    public String getOutputDirName() {
        return outputDirName;
    }

    public boolean isVerbose() {
        return verbose;
    }

    public boolean isPrintScreen() {
        return printScreen;
    }
}
