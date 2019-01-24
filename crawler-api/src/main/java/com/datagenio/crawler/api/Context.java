package com.datagenio.crawler.api;

import java.net.URI;

public interface Context {
    int REQUEST_TIMEOUT = 300;
    int NO_MAX_DEPTH = 0;

    String getRootUrl();
    URI getRootUri();
    int getRequestTimeout();
    int getCrawlTimeout();
    int getCrawlDepth();
    String getOutputDirName();
    boolean isVerbose();
    boolean isPrintScreen();
}
