package com.datagenio.crawler;

import org.junit.Test;

import static org.junit.Assert.*;

public class CrawlContextTest {
    private static String ROOT_URL = "http://test.com";
    private static String OUTPUT_DIR = "/tmp/test";

    @Test
    public void testDefaultConstructor() {
        var context = new CrawlContext(ROOT_URL, OUTPUT_DIR);
        assertEquals(ROOT_URL, context.getRootUrl());
        assertEquals(OUTPUT_DIR, context.getOutputDirName());
        assertEquals(0, context.getCrawlDepth());
        assertEquals(0, context.getCrawlTimeout());
        assertEquals(CrawlContext.REQUEST_TIMEOUT, context.getRequestTimeout());
        assertEquals(false, context.isVerbose());
        assertEquals(false, context.isPrintScreen());
    }

    @Test
    public void testConstructorWithVerbosity() {
        var context = new CrawlContext(ROOT_URL, OUTPUT_DIR, true);
        assertEquals(ROOT_URL, context.getRootUrl());
        assertEquals(OUTPUT_DIR, context.getOutputDirName());
        assertEquals(0, context.getCrawlDepth());
        assertEquals(0, context.getCrawlTimeout());
        assertEquals(CrawlContext.REQUEST_TIMEOUT, context.getRequestTimeout());
        assertEquals(true, context.isVerbose());
        assertEquals(false, context.isPrintScreen());
    }

    @Test
    public void testConstructorWithVerbosityAndPrintScreen() {
        var context = new CrawlContext(ROOT_URL, OUTPUT_DIR, true, true);
        assertEquals(ROOT_URL, context.getRootUrl());
        assertEquals(OUTPUT_DIR, context.getOutputDirName());
        assertEquals(0, context.getCrawlDepth());
        assertEquals(0, context.getCrawlTimeout());
        assertEquals(CrawlContext.REQUEST_TIMEOUT, context.getRequestTimeout());
        assertEquals(true, context.isVerbose());
        assertEquals(true, context.isPrintScreen());
    }

    @Test
    public void testConstructorWithCrawlTimeout() {
        var context = new CrawlContext(ROOT_URL, OUTPUT_DIR, true, 120);
        assertEquals(ROOT_URL, context.getRootUrl());
        assertEquals(OUTPUT_DIR, context.getOutputDirName());
        assertEquals(0, context.getCrawlDepth());
        assertEquals(120, context.getCrawlTimeout());
        assertEquals(CrawlContext.REQUEST_TIMEOUT, context.getRequestTimeout());
        assertEquals(true, context.isVerbose());
        assertEquals(false, context.isPrintScreen());
    }

    @Test
    public void testConstructorWithRequestTimeout() {
        var context = new CrawlContext(ROOT_URL, OUTPUT_DIR, true, 120, 450);
        assertEquals(ROOT_URL, context.getRootUrl());
        assertEquals(OUTPUT_DIR, context.getOutputDirName());
        assertEquals(0, context.getCrawlDepth());
        assertEquals(120, context.getCrawlTimeout());
        assertEquals(450, context.getRequestTimeout());
        assertEquals(true, context.isVerbose());
        assertEquals(false, context.isPrintScreen());
    }

    @Test
    public void testConstructorWithCrawlDepth() {
        var context = new CrawlContext(ROOT_URL, OUTPUT_DIR, true, 120, 450, 5);
        assertEquals(ROOT_URL, context.getRootUrl());
        assertEquals(OUTPUT_DIR, context.getOutputDirName());
        assertEquals(5, context.getCrawlDepth());
        assertEquals(120, context.getCrawlTimeout());
        assertEquals(450, context.getRequestTimeout());
        assertEquals(true, context.isVerbose());
        assertEquals(false, context.isPrintScreen());
    }
}
