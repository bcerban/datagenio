package com.datagenio.crawler;

import org.junit.Test;

import static org.junit.Assert.*;

public class CrawlContextTest {
    private static String OUTPUT_DIR = "/tmp/test";

    @Test
    public void testDefaultConstructor() {
        var context = new CrawlContext(OUTPUT_DIR);
        assertEquals(OUTPUT_DIR, context.getOutputDirName());
        assertEquals(0, context.getCrawlDepth());
        assertEquals(0, context.getCrawlTimeout());
        assertEquals(CrawlContext.REQUEST_TIMEOUT, context.getRequestTimeout());
        assertEquals(false, context.isVerbose());
    }

    @Test
    public void testConstructorWithVerbosity() {
        var context = new CrawlContext(OUTPUT_DIR, true);
        assertEquals(OUTPUT_DIR, context.getOutputDirName());
        assertEquals(0, context.getCrawlDepth());
        assertEquals(0, context.getCrawlTimeout());
        assertEquals(CrawlContext.REQUEST_TIMEOUT, context.getRequestTimeout());
        assertEquals(true, context.isVerbose());
    }

    @Test
    public void testConstructorWithCrawlTimeout() {
        var context = new CrawlContext(OUTPUT_DIR, true, 120);
        assertEquals(OUTPUT_DIR, context.getOutputDirName());
        assertEquals(0, context.getCrawlDepth());
        assertEquals(120, context.getCrawlTimeout());
        assertEquals(CrawlContext.REQUEST_TIMEOUT, context.getRequestTimeout());
        assertEquals(true, context.isVerbose());
    }

    @Test
    public void testConstructorWithRequestTimeout() {
        var context = new CrawlContext(OUTPUT_DIR, true, 120, 450);
        assertEquals(OUTPUT_DIR, context.getOutputDirName());
        assertEquals(0, context.getCrawlDepth());
        assertEquals(120, context.getCrawlTimeout());
        assertEquals(450, context.getRequestTimeout());
        assertEquals(true, context.isVerbose());
    }

    @Test
    public void testConstructorWithCrawlDepth() {
        var context = new CrawlContext(OUTPUT_DIR, true, 120, 450, 5);
        assertEquals(OUTPUT_DIR, context.getOutputDirName());
        assertEquals(5, context.getCrawlDepth());
        assertEquals(120, context.getCrawlTimeout());
        assertEquals(450, context.getRequestTimeout());
        assertEquals(true, context.isVerbose());
    }
}
