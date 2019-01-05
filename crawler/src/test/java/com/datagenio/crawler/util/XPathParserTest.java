package com.datagenio.crawler.util;

import com.datagenio.crawler.util.XPathParser;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class XPathParserTest {

    private Element button1;
    private Element button2;
    private Document parent;

    @Before
    public void setUp() {
        String html = "<html><head><title>Test html document</title></head>"
                + "<body>"
                + "<span><button id=\"button1\">Click me first!</button></span>"
                + "<span><button id=\"button2\">Click me next!</button></span>"
                + "</body></html>";

        this.parent = Jsoup.parse(html);
        this.button1 = this.parent.getElementById("button1");
        this.button2 = this.parent.getElementById("button2");
    }

    @Test
    public void testGetXpathForElementWithSibling() {
        String expectedPath = "/html/body/span[1]/button";
        assertEquals(expectedPath, XPathParser.getXPathFor(this.button1));
    }
}
