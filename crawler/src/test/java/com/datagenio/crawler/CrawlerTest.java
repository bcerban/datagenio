package com.datagenio.crawler;

import com.datagenio.crawler.api.Browser;
import com.datagenio.crawler.api.EventFlowGraph;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class CrawlerTest {

    private static String OUTPUT_DIR = "/tmp/test";
    private static String ROOT_URL = "http://test.com";

    private Browser browser;
    private CrawlContext context;
    private Crawler crawler;


    @Before
    public void setUp() {
        this.browser = mock(Browser.class);
        this.context = new CrawlContext(OUTPUT_DIR);
        this.crawler = new Crawler(this.context, this.browser);
    }

    @Test
    public void testGetContext() {
        assertEquals(this.context, this.crawler.getContext());
    }

    @Test
    public void testGetBrowser() {
        assertEquals(this.browser, this.crawler.getBrowser());
    }

    @Test
    public void testSetBrowser() {
        var newBrowser = mock(Browser.class);
        this.crawler.setBrowser(newBrowser);
        assertEquals(newBrowser, this.crawler.getBrowser());
    }

    @Test
    public void testGetLogger() {
        assertNotNull(Crawler.getLogger());
    }

    @Test
    public void testCrawl() {
        String html = "<html><head><title>Test html document</title></head>"
                + "<body><span><button id=\"button-id\">Click me!</button></span>"
                + "<span><img src=\"/avatar.jpg\" alt=\"Avatar\"></span></body></html>";
        Document document = Jsoup.parse(html);
        when(this.browser.getDOM()).thenReturn(document);

        var graph = this.crawler.crawl(ROOT_URL);
        assertTrue(graph instanceof EventFlowGraph);
    }
}
