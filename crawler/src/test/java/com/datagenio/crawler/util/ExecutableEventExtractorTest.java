package com.datagenio.crawler.util;

import com.datagenio.crawler.api.Eventable;
import com.datagenio.crawler.api.ExtractionRule;
import com.datagenio.crawler.api.State;
import com.datagenio.crawler.rule.TagRule;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

public class ExecutableEventExtractorTest {

    private ExtractionRule rule;
    private State origin;
    private ExecutableEventExtractor extractor;
    private Element root;

    @Before
    public void setUp() {
        String html = "<html><head><title>Test html document</title></head>"
                + "<body>"
                + "<span><button id=\"button-id\">Click me!</button></span>"
                + "<span><a id=\"anchor-id\" href=\"#\"></a></span>"
                + "<span><img id=\"img-id\" src=\"/avatar.jpg\" alt=\"Avatar\"></span>"
                + "<form id=\"form-id\"><input type=\"text\" /></form>"
                + "</body></html>";

        this.root = Jsoup.parse(html);
        this.rule = new TagRule(List.of("a", "button", "form"));
        this.origin = mock(State.class);
        this.extractor = new ExecutableEventExtractor(List.of(this.rule));
    }

    @Test
    public void testGetRules() {
        assertEquals(List.of(this.rule), this.extractor.getRules());
    }

    @Test
    public void testSetRules() {
        var rules = new ArrayList<ExtractionRule>();
        this.extractor.setRules(rules);
        assertEquals(rules, this.extractor.getRules());
    }

    @Test
    public void testExtract() {
        var eventables = this.extractor.extract(this.origin, this.root);
        assertTrue(eventables.size() == 3);
        assertTrue(eventables.stream()
                .filter(e -> e.getEventIdentifier().equals("button-id") && e.getEventType() == Eventable.EventType.CLICK)
                .findFirst()
                .isPresent()
        );

        assertTrue(eventables.stream()
                .filter(e -> e.getEventIdentifier().equals("anchor-id") && e.getEventType() == Eventable.EventType.CLICK)
                .findFirst()
                .isPresent()
        );

        assertTrue(eventables.stream()
                .filter(e -> e.getEventIdentifier().equals("form-id") && e.getEventType() == Eventable.EventType.SUBMIT)
                .findFirst()
                .isPresent()
        );
    }

}
