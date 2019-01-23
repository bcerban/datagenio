package com.datagenio.crawler.rule;

import org.jsoup.nodes.Element;
import org.junit.Before;
import org.junit.Test;

import java.util.Collection;
import java.util.List;

import static org.junit.Assert.*;

public class TagRuleTest {

    private Collection<String> allowedTags;
    private TagRule tagRule;

    @Before
    public void setUp() {
        allowedTags = List.of("button", "form");
        tagRule = new TagRule(allowedTags);
    }

    @Test
    public void testGetAllowedTags() {
        assertEquals(allowedTags, tagRule.getAllowedTags());
    }

    @Test
    public void testSetAllowedTags() {
        var newTags = List.of("input");
        tagRule.setAllowedTags(newTags);
        assertEquals(newTags, tagRule.getAllowedTags());
    }

    @Test
    public void testMatchesButton() {
        Element matched = new Element("button");
        assertTrue(tagRule.matches(matched));
    }

    @Test
    public void testMatchesForm() {
        Element matched = new Element("form");
        assertTrue(tagRule.matches(matched));
    }

    @Test
    public void testMatchesFalse() {
        Element notMatched = new Element("div");
        assertFalse(tagRule.matches(notMatched));
    }
}
