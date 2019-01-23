package com.datagenio.crawler.rule;

import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Tag;
import org.junit.Before;
import org.junit.Test;


import static org.junit.Assert.*;

public class AnchorRuleTest {

    private static final String TEST_URL = "http://test.com";

    private AnchorRule rule;

    @Before
    public void setUp() {
        rule = new AnchorRule();
    }

    @Test
    public void testNotAnchor() {
        Element element = new Element(Tag.valueOf("button"), TEST_URL);
        assertFalse(rule.matches(element));
    }

    @Test
    public void testMailTo() {
        Attributes attributes = new Attributes();
        attributes.put(AnchorRule.HREF, "mailto:test@test.com");
        Element element = new Element(Tag.valueOf(AnchorRule.A_TAG), TEST_URL, attributes);

        assertFalse(rule.matches(element));
    }

    @Test
    public void testOk() {
        Attributes attributes = new Attributes();
        attributes.put(AnchorRule.HREF, "#");
        Element element = new Element(Tag.valueOf(AnchorRule.A_TAG), TEST_URL, attributes);

        assertTrue(rule.matches(element));
    }
}
