package com.datagenio.crawler;

import com.datagenio.crawler.api.Eventable;
import com.datagenio.crawler.api.ExtractionRule;
import com.datagenio.crawler.api.State;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.junit.Before;
import org.junit.Test;
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
    public void testExtract() {
        var eventables = this.extractor.extract(this.origin, this.root);
        assertTrue(eventables.size() == 3);
        assertTrue(eventables.stream()
                .filter(e -> e.getIdentifier().equals("button-id") && e.getEventType() == Eventable.EventType.click)
                .findFirst()
                .isPresent()
        );

        assertTrue(eventables.stream()
                .filter(e -> e.getIdentifier().equals("anchor-id") && e.getEventType() == Eventable.EventType.click)
                .findFirst()
                .isPresent()
        );

        assertTrue(eventables.stream()
                .filter(e -> e.getIdentifier().equals("form-id") && e.getEventType() == Eventable.EventType.submit)
                .findFirst()
                .isPresent()
        );
    }

}
