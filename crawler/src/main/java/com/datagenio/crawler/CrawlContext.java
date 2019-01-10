package com.datagenio.crawler;

public class CrawlContext {
    public static int REQUEST_TIMEOUT = 300;

    private final int requestTimeout;
    private final int crawlTimeout;
    private final int crawlDepth;
    private final String outputDirName;
    private final boolean verbose;

    public CrawlContext(String outputDirName) {
        this.requestTimeout = REQUEST_TIMEOUT;
        this.crawlTimeout = 0;
        this.outputDirName = outputDirName;
        this.crawlDepth = 0;
        this.verbose = false;
    }

    public CrawlContext(String outputDirName, boolean verbose) {
        this.requestTimeout = REQUEST_TIMEOUT;
        this.crawlTimeout = 0;
        this.outputDirName = outputDirName;
        this.crawlDepth = 0;
        this.verbose = verbose;
    }

    public CrawlContext(String outputDirName, boolean verbose, int crawlTimeout) {
        this.requestTimeout = REQUEST_TIMEOUT;
        this.crawlTimeout = crawlTimeout;
        this.outputDirName = outputDirName;
        this.crawlDepth = 0;
        this.verbose = verbose;
    }

    public CrawlContext(String outputDirName, boolean verbose, int crawlTimeout, int requestTimeout) {
        this.requestTimeout = requestTimeout;
        this.crawlTimeout = crawlTimeout;
        this.outputDirName = outputDirName;
        this.crawlDepth = 0;
        this.verbose = verbose;
    }

    public CrawlContext(String outputDirName, boolean verbose, int crawlTimeout, int requestTimeout, int crawlDepth) {
        this.requestTimeout = requestTimeout;
        this.crawlTimeout = crawlTimeout;
        this.outputDirName = outputDirName;
        this.crawlDepth = crawlDepth;
        this.verbose = verbose;
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
}
