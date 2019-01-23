package com.datagenio.crawler.api;

import java.net.URI;

public interface Context {
    String getRootUrl();
    URI getRootUri();
    int getRequestTimeout();
    int getCrawlTimeout();
    int getCrawlDepth();
    String getOutputDirName();
    boolean isVerbose();
    boolean isPrintScreen();
}
