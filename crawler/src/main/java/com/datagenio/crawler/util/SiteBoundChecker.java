package com.datagenio.crawler.util;

import com.datagenio.context.Context;

import java.net.URI;

public class SiteBoundChecker {

    public static boolean isOutOfBounds(URI uri, Context context) {
        if (uri == null || uri.getHost() == null) {
            return true;
        }

        URI root = URI.create(context.getRootUrl());
        String current = uri.getHost().toLowerCase();
        return !current.endsWith(root.getHost().toLowerCase());
    }

    public static boolean isOutOfBounds(URI uri, URI root) {
        return !uri.getHost().toLowerCase().endsWith(root.getHost().toLowerCase());
    }
}
