package com.datagenio.crawler;

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
        this.allowedTags = List.of("a", "button", "form");
        this.tagRule = new TagRule(this.allowedTags);
    }

    @Test
    public void testGetAllowedTags() {
        assertEquals(this.allowedTags, this.tagRule.getAllowedTags());
    }

    @Test
    public void testSetAllowedTags() {
        var newTags = List.of("input");
        this.tagRule.setAllowedTags(newTags);
        assertEquals(newTags, this.tagRule.getAllowedTags());
    }

    @Test
    public void testMatchesTrue() {
        Element matched = new Element("button");
        assertTrue(this.tagRule.matches(matched));
    }

    @Test
    public void testMatchesFalse() {
        Element notMatched = new Element("div");
        assertFalse(this.tagRule.matches(notMatched));
    }
}
