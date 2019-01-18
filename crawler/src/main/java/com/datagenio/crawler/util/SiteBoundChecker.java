package com.datagenio.crawler.util;

import com.datagenio.crawler.CrawlContext;

import java.net.URI;

public class SiteBoundChecker {

    public static boolean isOutOfBounds(URI uri, CrawlContext context) {
        URI root = URI.create(context.getRootUrl());
        String current = uri.getHost().toLowerCase();
        return !current.endsWith(root.getHost().toLowerCase());
    }
}
